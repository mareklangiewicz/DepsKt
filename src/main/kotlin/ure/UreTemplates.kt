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

fun injectRootBuildTemplate(outputPath: Path) = injectBuildRegion(rootRegionLabel, rootResPath, outputPath)
fun injectKotlinModuleBuildTemplate(outputPath: Path) = injectBuildRegion(kotlinModuleRegionLabel, mppLibResPath, outputPath)
fun injectMppModuleBuildTemplate(outputPath: Path) = injectBuildRegion(mppModuleRegionLabel, mppLibResPath, outputPath)
fun injectAndroModuleBuildTemplate(outputPath: Path) = injectBuildRegion(androModuleRegionLabel, androLibResPath, outputPath)

fun checkRootBuildTemplate(buildFile: Path) = checkSomeBuildTemplates(rootRegionLabel, rootResPath, buildFile)
fun checkKotlinModuleBuildTemplates(vararg buildFiles: Path) = checkSomeBuildTemplates(kotlinModuleRegionLabel, mppLibResPath, *buildFiles)
fun checkMppModuleBuildTemplates(vararg buildFiles: Path) = checkSomeBuildTemplates(mppModuleRegionLabel, mppLibResPath, *buildFiles)
fun checkAndroModuleBuildTemplates(vararg buildFiles: Path) = checkSomeBuildTemplates(androModuleRegionLabel, androLibResPath, *buildFiles)

fun checkSomeBuildTemplates(regionLabel: String, srcResPath: Path, vararg buildFiles: Path) {
    println("Checking [$regionLabel] template...")
    checkAllBuildRegionsSync() // to be sure source of truth is clean
    val region by RESOURCES.readAndMatchUre(srcResPath, ureWithRegion(regionLabel)) ?: error("No match $srcResPath")
    for (path in buildFiles) SYSTEM.checkBuildRegion(regionLabel, region, path)
    println("OK. Checked [$regionLabel]. All look good.")
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

private fun FileSystem.checkBuildRegion(regionLabel: String, regionExpected: String, outputPath: Path) {
    val ureWithBuildRegion = ureWithRegion(regionLabel)
    require(ureWithBuildRegion.compile().matches(regionExpected)) { "regionExpected doesn't match ureWithBuildRegion(regionLabel)" }
    val region by readAndMatchUre(outputPath, ureWithBuildRegion) ?: error("No match $outputPath")
    val outputFullPath = canonicalize(outputPath)
    check(region == regionExpected) { "Region: [$regionLabel] in File: $outputFullPath was modified." }
    println("OK. Region: [$regionLabel] in File: $outputFullPath is correct.")
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
        println("Inject template from:$inputResPath to:$outputPath (len ${output.length}->${newOutput.length}).")
        newOutput
    }
}

// TODO_someday: scan imports and add imports from template-android/lib/build.gradle.kts if needed

