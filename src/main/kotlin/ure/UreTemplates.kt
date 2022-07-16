package pl.mareklangiewicz.ure

import okio.*
import okio.FileSystem.Companion.RESOURCES
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import pl.mareklangiewicz.io.*

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
private const val pathMppLib = "template-mpp/template-mpp-lib"
private const val pathMppApp = "template-mpp/template-mpp-app"
private const val pathJvmApp = "template-mpp/template-jvm-cli"
private const val pathAndroRoot = "template-andro"
private const val pathAndroLib = "template-andro/template-andro-lib"
private const val pathAndroApp = "template-andro/template-andro-app"

data class RegionInfo(val label: String, val path: Path, val syncedPaths: List<Path>)

private fun info(label: String, dir: String, vararg syncedDirs: String) =
    RegionInfo(label, dir.toPath() / "build.gradle.kts", syncedDirs.toList().map { it.toPath() / "build.gradle.kts" })

// TODO NOW: resPaths should be the same as srcPaths, but in resources!
private val regionsInfos = listOf(
    info(labelRoot, pathMppRoot, pathAndroRoot),
    info(labelKotlinModule, pathMppLib, pathMppApp, pathJvmApp, pathAndroLib, pathAndroApp),
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
    val inputResPath = regionsInfos[regionLabel].path
    val ureWithRegion = ureWithSpecialRegion(regionLabel)
    val mr = RESOURCES.readAndMatchUre(inputResPath, ureWithRegion) ?: error("No region [$regionLabel] in $inputResPath")
    return mr["region"]
}

private fun knownRegionFullTemplatePath(
    regionLabel: String,
    depsKtRootPath: Path = "/home/marek/code/kotlin/deps.kt".toPath()
) = SYSTEM.canonicalize(depsKtRootPath / regionsInfos[regionLabel].path)

fun checkAllKnownRegionsInProject(projectRootPath: Path) = try {
    println("BEGIN: Check all known regions in project:")
    checkAllKnownRegionsSynced() // to be sure source of truth is clean
    SYSTEM.checkAllKnownRegionsInAllFoundFiles(projectRootPath, verbose = true)
    println("END: Check all known regions in project.")
} catch (e: IllegalStateException) {
    println("ERROR: ${e.message}")
}

// This actually is self check for deps.kt, so it should be in some unit test for deps.kt
// But let's run it every time when checking client regions just to be sure the "source of truth" is consistent.
fun checkAllKnownRegionsSynced() = regionsInfos.forEach {
    SYSTEM.checkKnownRegion(it.label, it.path, *it.syncedPaths.toTypedArray(), verbose = true)
}

fun FileSystem.checkAllKnownRegionsInAllFoundFiles(
    outputTreePath: Path,
    outputFileExt: String = "gradle.kts",
    failIfNotFound: Boolean = false,
    verbose: Boolean = false,
) {
    val outputPaths = findAllFiles(outputTreePath).filterExt(outputFileExt).toList().toTypedArray()
    for (label in regionsInfos.map { it.label })
        checkKnownRegion(label, *outputPaths, failIfNotFound = failIfNotFound, verbose = verbose)
}

fun FileSystem.checkKnownRegionInAllFoundFiles(
    regionLabel: String,
    outputTreePath: Path,
    outputFileExt: String = "gradle.kts",
    failIfNotFound: Boolean = false,
    verbose: Boolean = false,
) {
    val outputPaths = findAllFiles(outputTreePath).filterExt(outputFileExt).toList().toTypedArray()
    checkKnownRegion(regionLabel, *outputPaths, failIfNotFound = failIfNotFound, verbose = verbose)
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
) = outputPaths.forEach { path ->
    val hint = "Try sth like: ideap diff ${knownRegionFullTemplatePath(regionLabel)} ${canonicalize(path)}"
    checkRegion(regionLabel, knownRegion(regionLabel), path, failIfNotFound, verbose, hint.takeIf { verbose })
}

private fun FileSystem.checkRegion(
    regionLabel: String,
    regionExpected: String,
    outputPath: Path,
    failIfNotFound: Boolean = true,
    verbose: Boolean = false,
    verboseCheckFailedHint: String? = null,
) {
    val ureWithRegion = ureWithSpecialRegion(regionLabel)
    require(ureWithRegion.compile().matches(regionExpected)) { "regionExpected doesn't match region [$regionLabel]" }
    val region by readAndMatchUre(outputPath, ureWithRegion)
        ?: if (failIfNotFound) error("Region [$regionLabel] not found in $outputPath") else return
    check(region.trimEnd('\n') == regionExpected.trimEnd('\n')) {
        if (verbose) {
            println("Region: [$regionLabel] in File: $outputPath was modified.")
            verboseCheckFailedHint?.let { println(it) }
        }
        "Region: [$regionLabel] in File: $outputPath was modified."
    }
    if (verbose) println("OK [$regionLabel] in $outputPath")
}

fun injectKnownRegion(regionLabel: String, vararg outputPaths: Path) = outputPaths.forEach { outputPath ->
    val region = knownRegion(regionLabel)
    val regex = ureWithSpecialRegion(regionLabel).compile()
    SYSTEM.processFile(outputPath, outputPath) { output ->
        val outputMR = regex.matchEntire(output)
        if (outputMR == null) {
            println("Inject [$regionLabel] to $outputPath - No match. Adding new region at the end.")
            output + "\n\n" + region.trimEnd()
        } else {
            val before by outputMR
            val after by outputMR
            val newAfter = if (after.isNotEmpty() && region.last() != '\n') "\n" + after else after
            val newRegion = if (newAfter.isEmpty()) region.trimEnd() else region
            val newOutput = before + newRegion + newAfter
            val summary = if (newOutput == output) "No changes." else "Changes detected (len ${output.length}->${newOutput.length})"
            println("Inject [$regionLabel] to $outputPath - $summary")
            newOutput
        }
    }
}

// TODO_someday: scan imports and add imports from template-android/lib/build.gradle.kts if needed

