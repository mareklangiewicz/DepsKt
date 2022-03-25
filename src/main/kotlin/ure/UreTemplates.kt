package pl.mareklangiewicz.ure

import okio.*
import okio.FileSystem.Companion.RESOURCES
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import pl.mareklangiewicz.io.*

private val rootResPath = "template-root-build".toPath()
private val mppLibResPath = "template-mpp-lib-build".toPath()
private val androLibResPath = "template-andro-lib-build".toPath()
private val androAppResPath = "template-andro-app-build".toPath()

private val rootRegionLabel = "Root Build Template"
private val kotlinModuleRegionLabel = "Kotlin Module Build Template"
private val mppModuleRegionLabel = "MPP Module Build Template"
private val androModuleRegionLabel = "Andro Module Build Template"

fun injectRootBuildTemplate(vararg outputPaths: Path) = injectBuildRegionToAll(rootRegionLabel, rootResPath, *outputPaths)
fun injectKotlinModuleBuildTemplate(vararg outputPaths: Path) = injectBuildRegionToAll(kotlinModuleRegionLabel, mppLibResPath, *outputPaths)
fun injectMppModuleBuildTemplate(vararg outputPaths: Path) = injectBuildRegionToAll(mppModuleRegionLabel, mppLibResPath, *outputPaths)
fun injectAndroModuleBuildTemplate(vararg outputPaths: Path) = injectBuildRegionToAll(androModuleRegionLabel, androLibResPath, *outputPaths)

fun checkRootBuildTemplate(buildFile: Path) = checkSomeBuildTemplates(rootRegionLabel, rootResPath, buildFile)
fun checkKotlinModuleBuildTemplates(vararg buildFiles: Path) = checkSomeBuildTemplates(kotlinModuleRegionLabel, mppLibResPath, *buildFiles)
fun checkMppModuleBuildTemplates(vararg buildFiles: Path) = checkSomeBuildTemplates(mppModuleRegionLabel, mppLibResPath, *buildFiles)
fun checkAndroModuleBuildTemplates(vararg buildFiles: Path) = checkSomeBuildTemplates(androModuleRegionLabel, androLibResPath, *buildFiles)

fun checkSomeBuildTemplates(regionLabel: String, srcResPath: Path, vararg buildFiles: Path) {
    println("BEGIN: Check [$regionLabel]:")
    checkAllBuildRegionsSync() // to be sure source of truth is clean
    val region by RESOURCES.readAndMatchUre(srcResPath, ureWithRegion(regionLabel)) ?: error("No match $srcResPath")
    for (path in buildFiles) SYSTEM.checkBuildRegion(regionLabel, region, path, verbose = true)
    println("END: Check [$regionLabel].")
}

private fun ureWithRegion(regionLabel: String) = ure {
    1 of ureWhateva().withName("before")
    1 of ureRegion(ureWhateva(), ir(regionLabel)).withName("region")
    1 of ureWhateva(reluctant = false).withName("after")
}

// This actually is self check for deps.kt so it should be in some unit test for deps.kt
// But let's run it every time when checking client regions just to be sure the "source of truth" is consistent.
private fun checkAllBuildRegionsSync() = RESOURCES.run {
    checkBuildRegionsSync(rootRegionLabel, rootResPath, rootResPath, ) // TODO_later: add other root files to check sync
    checkBuildRegionsSync(kotlinModuleRegionLabel, mppLibResPath, mppLibResPath, androLibResPath, androAppResPath) // TODO_later: add other mpp modules to check sync
    checkBuildRegionsSync(mppModuleRegionLabel, mppLibResPath, mppLibResPath) // TODO_later: add other mpp modules to check sync
    checkBuildRegionsSync(androModuleRegionLabel, androLibResPath, androLibResPath, androAppResPath)
}
private fun FileSystem.checkBuildRegionsSync(regionLabel: String, inputPath: Path, vararg outputPaths: Path) {
    val mr = readAndMatchUre(inputPath, ureWithRegion(regionLabel)) ?: error("No region [$regionLabel] in ${canonicalize(inputPath)}")
    val region by mr
    for (path in outputPaths) checkBuildRegion(regionLabel, region, path)
}

private fun FileSystem.checkBuildRegion(regionLabel: String, regionExpected: String, outputPath: Path, verbose: Boolean = false) {
    val ureWithBuildRegion = ureWithRegion(regionLabel)
    require(ureWithBuildRegion.compile().matches(regionExpected)) { "regionExpected doesn't match ureWithBuildRegion(regionLabel)" }
    val region by readAndMatchUre(outputPath, ureWithBuildRegion) ?: error("No match $outputPath")
    check(region == regionExpected) { "ERROR Region: [$regionLabel] in File: $outputPath was modified.".also { println(it) } }
    if (verbose) println("OK [$regionLabel] in $outputPath")
}

private fun injectBuildRegionToAll(regionLabel: String, inputResPath: Path, vararg outputPaths: Path) {
    for (path in outputPaths) injectBuildRegion(regionLabel, inputResPath, path)
}

private fun injectBuildRegion(regionLabel: String, inputResPath: Path, outputPath: Path) {
    val ureWithBuildRegion = ureWithRegion(regionLabel)
    val inputMR = RESOURCES.readAndMatchUre(inputResPath, ureWithBuildRegion) ?: error("No match $inputResPath")
    val region by inputMR
    SYSTEM.processFile(outputPath, outputPath) { output ->
        val outputMR = ureWithBuildRegion.compile().matchEntire(output) ?: error("No match $outputPath")
        val before by outputMR
        val after by outputMR
        val newOutput = before + region + after
        val summary = if (newOutput == output) "No changes." else "Changes detected (len ${output.length}->${newOutput.length})"
        println("Inject [$regionLabel] to $outputPath - $summary")
        newOutput
    }
}

// TODO_someday: scan imports and add imports from template-android/lib/build.gradle.kts if needed
