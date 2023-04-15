package pl.mareklangiewicz.ure

import okio.*
import okio.FileSystem.Companion.RESOURCES
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import pl.mareklangiewicz.io.*
import pl.mareklangiewicz.kommand.Platform
import pl.mareklangiewicz.kommand.kommand
import pl.mareklangiewicz.maintenance.*
import kotlin.math.absoluteValue
import kotlin.random.Random

const val labelRoot = "Root Build Template"
const val labelKotlinModule = "Kotlin Module Build Template"
const val labelMppModule = "MPP Module Build Template"
const val labelMppApp = "MPP App Build Template"
const val labelJvmApp = "Jvm App Build Template"
const val labelComposeMppModule = "Compose MPP Module Build Template"
const val labelComposeMppApp = "Compose MPP App Build Template"
const val labelAndroCommon = "Andro Common Build Template"
const val labelAndroLib = "Andro Lib Build Template"
const val labelAndroApp = "Andro App Build Template"

private const val pathMppRoot = "template-mpp"
private const val pathDepsKtRoot = ""
private const val pathMppLib = "template-mpp/template-mpp-lib"
private const val pathMppApp = "template-mpp/template-mpp-app"
private const val pathJvmApp = "template-mpp/template-jvm-cli"
private const val pathAndroRoot = "template-andro"
private const val pathAndroLib = "template-andro/template-andro-lib"
private const val pathAndroApp = "template-andro/template-andro-app"

data class RegionInfo(val label: String, val path: Path, val syncedPaths: List<Path>)

val RegionInfo.pathInRes get() = path / "build.gradle.kts.tmpl"
// pathInRes has to have different suffix from "build.gradle.kts" otherwise gradle sometimes tries to run it..
// (even just .kts extension sometimes confuses at least IDE)

fun RegionInfo.pathInSrc(depsKtRootPath: Path = MyDepsKtRootPath) =
    depsKtRootPath / path / "build.gradle.kts"

fun RegionInfo.syncedPathsArrInSrc(depsKtRootPath: Path = MyDepsKtRootPath) =
    syncedPaths.map { depsKtRootPath / it / "build.gradle.kts" }.toTypedArray()

private fun info(label: String, dir: String, vararg syncedDirs: String) =
    RegionInfo(label, dir.toPath(), syncedDirs.toList().map { it.toPath() })

private val regionsInfos = listOf(
    info(labelRoot, pathMppRoot, pathAndroRoot, pathDepsKtRoot),
    info(labelKotlinModule, pathMppLib, pathMppApp, pathJvmApp, pathAndroLib, pathAndroApp, pathDepsKtRoot),
    info(labelMppModule, pathMppLib, pathMppApp),
    info(labelMppApp, pathMppApp),
    info(labelJvmApp, pathJvmApp),
    info(labelComposeMppModule, pathMppLib, pathMppApp),
    info(labelComposeMppApp, pathMppApp),
    info(labelAndroCommon, pathAndroLib, pathAndroApp),
    info(labelAndroLib, pathAndroLib),
    info(labelAndroApp, pathAndroApp),
)

operator fun List<RegionInfo>.get(label: String) = find { it.label == label } ?: error("Unknown region label: $label")

private fun knownRegion(regionLabel: String): String {
    val inputResPath = regionsInfos[regionLabel].pathInRes
    val ureWithRegion = ureWithSpecialRegion(regionLabel)
    val mr = RESOURCES.readAndMatchUre(inputResPath, ureWithRegion) ?: error("No region [$regionLabel] in $inputResPath")
    return mr["region"]
}

private fun knownRegionFullTemplatePath(
    regionLabel: String,
    depsKtRootPath: Path = MyDepsKtRootPath,
) = SYSTEM.canonicalize(regionsInfos[regionLabel].pathInSrc(depsKtRootPath))

fun checkAllKnownRegionsInProject(projectPath: Path, log: (Any?) -> Unit = ::println) = try {
    checkAllKnownRegionsSynced(log = log) // to be sure source of truth is clean
    log("BEGIN: Check all known regions in project:")
    SYSTEM.checkAllKnownRegionsInAllFoundFiles(projectPath, verbose = true, log = log)
    log("END: Check all known regions in project.")
} catch (e: IllegalStateException) {
    log("ERROR: ${e.message}")
}

fun injectAllKnownRegionsInProject(projectPath: Path, log: (Any?) -> Unit = ::println) {
    log("BEGIN: Inject all known regions in project:")
    SYSTEM.injectAllKnownRegionsToAllFoundFiles(projectPath, log = log)
    log("END: Inject all known regions in project.")
}

// This actually is self-check for deps.kt, so it should be in some unit test for deps.kt
// But let's run it every time when checking client regions just to be sure the "source of truth" is consistent.
fun checkAllKnownRegionsSynced(depsKtRootPath: Path = MyDepsKtRootPath, verbose: Boolean = false, log: (Any?) -> Unit = ::println) =
    regionsInfos.forEach {
        SYSTEM.checkKnownRegion(it.label, it.pathInSrc(depsKtRootPath), *it.syncedPathsArrInSrc(depsKtRootPath), verbose = verbose, log = log)
    }

fun injectAllKnownRegionsToSync(depsKtRootPath: Path = MyDepsKtRootPath, log: (Any?) -> Unit = ::println) =
    regionsInfos.forEach {
        SYSTEM.injectKnownRegion(it.label, *it.syncedPathsArrInSrc(depsKtRootPath), addIfNotFound = false, log = log)
    }

fun FileSystem.checkAllKnownRegionsInAllFoundFiles(
    outputTreePath: Path,
    outputFileExt: String = "gradle.kts",
    failIfNotFound: Boolean = false,
    verbose: Boolean = false,
    log: (Any?) -> Unit = ::println,
) {
    val outputPaths = findAllFiles(outputTreePath).filterExt(outputFileExt).toList().toTypedArray()
    for (label in regionsInfos.map { it.label })
        checkKnownRegion(label, *outputPaths, failIfNotFound = failIfNotFound, verbose = verbose, log = log)
}

fun FileSystem.checkKnownRegionInAllFoundFiles(
    regionLabel: String,
    outputTreePath: Path,
    outputFileExt: String = "gradle.kts",
    failIfNotFound: Boolean = false,
    verbose: Boolean = false,
    log: (Any?) -> Unit = ::println,
) {
    val outputPaths = findAllFiles(outputTreePath).filterExt(outputFileExt).toList().toTypedArray()
    checkKnownRegion(regionLabel, *outputPaths, failIfNotFound = failIfNotFound, verbose = verbose, log = log)
}

fun FileSystem.injectAllKnownRegionsToAllFoundFiles(
    outputTreePath: Path,
    outputFileExt: String = "gradle.kts",
    addIfNotFound: Boolean = false,
    log: (Any?) -> Unit = ::println,
) {
    val outputPaths = findAllFiles(outputTreePath).filterExt(outputFileExt).toList().toTypedArray()
    for (label in regionsInfos.map { it.label })
        injectKnownRegion(label, *outputPaths, addIfNotFound = addIfNotFound, log = log)
}

fun FileSystem.injectKnownRegionToAllFoundFiles(
    regionLabel: String,
    outputTreePath: Path,
    outputFileExt: String = "gradle.kts",
    addIfNotFound: Boolean = false,
    log: (Any?) -> Unit = ::println,
) {
    val outputPaths = findAllFiles(outputTreePath).filterExt(outputFileExt).toList().toTypedArray()
    injectKnownRegion(regionLabel, *outputPaths, addIfNotFound = addIfNotFound, log = log)
}

// by "special" we mean region with label wrapped in squared brackets
// the promise is: all special regions with some label should contain exactly the same content (synced)
private fun ureWithSpecialRegion(regionLabel: String) = ure {
    1 of ureWhateva().withName("before")
    1 of ureRegion(ureWhateva(), ir("\\[$regionLabel\\]")).withName("region")
    1 of ureWhateva(reluctant = false).withName("after")
}

fun FileSystem.checkKnownRegion(
    regionLabel: String,
    vararg outputPaths: Path,
    failIfNotFound: Boolean = true,
    verbose: Boolean = false,
    log: (Any?) -> Unit = ::println,
) = outputPaths.forEach { path ->
    val hint = "Try sth like: ideap diff ${knownRegionFullTemplatePath(regionLabel)} ${canonicalize(path)}"
    checkRegion(regionLabel, knownRegion(regionLabel), path, failIfNotFound, verbose, hint.takeIf { verbose }, log = log)
}

private fun FileSystem.checkRegion(
    regionLabel: String,
    regionExpected: String,
    outputPath: Path,
    failIfNotFound: Boolean = true,
    verbose: Boolean = false,
    verboseCheckFailedHint: String? = null,
    log: (Any?) -> Unit = ::println,
) {
    val ureWithRegion = ureWithSpecialRegion(regionLabel)
    require(ureWithRegion.compile().matches(regionExpected)) { "regionExpected doesn't match region [$regionLabel]" }
    val region by readAndMatchUre(outputPath, ureWithRegion)
        ?: if (failIfNotFound) error("Region [$regionLabel] not found in $outputPath") else return
    check(region.trimEnd('\n') == regionExpected.trimEnd('\n')) {
        if (verbose) {
            log("Region: [$regionLabel] in File: $outputPath was modified.")
            verboseCheckFailedHint?.let { log(it) }
        }
        "Region: [$regionLabel] in File: $outputPath was modified."
    }
    if (verbose) log("OK [$regionLabel] in $outputPath")
}

fun FileSystem.injectKnownRegion(
    regionLabel: String,
    vararg outputPaths: Path,
    addIfNotFound: Boolean = true,
    log: (Any?) -> Unit = ::println,
) = injectCustomRegion(regionLabel, knownRegion(regionLabel), *outputPaths, addIfNotFound = addIfNotFound, log = log)

fun FileSystem.injectCustomRegion(
    regionLabel: String,
    region: String,
    vararg outputPaths: Path,
    addIfNotFound: Boolean = true,
    log: (Any?) -> Unit = ::println,
) = outputPaths.forEach { outputPath ->
    val regex = ureWithSpecialRegion(regionLabel).compile()
    processFile(outputPath, outputPath) { output ->
        val outputMR = regex.matchEntire(output)
        if (outputMR == null) {
            log("Inject [$regionLabel] to $outputPath - No match.")
            if (addIfNotFound) {
                log("Adding new region at the end.")
                output + "\n\n" + region.trimEnd()
            } else null
        } else {
            val before by outputMR
            val after by outputMR
            val newAfter = if (after.isNotEmpty() && region.last() != '\n') "\n" + after else after
            val newRegion = if (newAfter.isEmpty()) region.trimEnd() else region
            val newOutput = before + newRegion + newAfter
            val summary = if (newOutput == output) "No changes." else "Changes detected (len ${output.length}->${newOutput.length})"
            log("Inject [$regionLabel] to $outputPath - $summary")
            newOutput
        }
    }
}


fun downloadTmpFile(
    url: String,
    name: String = "tmp${Random.nextLong().absoluteValue}.txt",
    dir: Path = "build".toPath(),
): Path = Platform.SYS.run {
    val path = dir / name
    // TODO: Add curl to KommandLine library, then use it here
    kommand("curl", "-o", path.toString(), url)()
    path
}

fun downloadAndInjectFileToSpecialRegion(
    inFileUrl: String,
    outFilePath: Path,
    outFileRegionLabel: String,
) {
    val inFilePath = downloadTmpFile(inFileUrl)
    val regionContent = SYSTEM.readUtf8(inFilePath)
    val markBefore = "// region [$outFileRegionLabel]\n"
    val markAfter = "// endregion [$outFileRegionLabel]\n"
    val region = "$markBefore\n$regionContent\n$markAfter"
    SYSTEM.injectCustomRegion(outFileRegionLabel, region, outFilePath)
}



// TODO_someday: scan imports and add imports from template-android/lib/build.gradle.kts if needed

