package pl.mareklangiewicz.ure

import okio.FileSystem.Companion.RESOURCES
import okio.FileSystem.Companion.SYSTEM
import okio.Path
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

fun checkRootBuildTemplate(buildFile: Path) {
    val rootMR = RESOURCES.readAndMatchUre(rootResPath, ureWithRegion(rootRegionLabel)) ?: error("No match $rootResPath")
    val regionInRoot = rootMR["region"]
    val region by SYSTEM.readAndMatchUre(buildFile, ureWithRegion(rootRegionLabel)) ?: error("No match $buildFile")
    check(region == regionInRoot) { "Template: [$rootRegionLabel] in $buildFile was modified." }
    println("OK. Template: [$rootRegionLabel] in $buildFile is correct.")
}

fun checkKotlinModuleBuildTemplates(vararg buildFiles: Path) {

    // first check if all our template files are consistent:
    val mppLibMR = RESOURCES.readAndMatchUre(mppLibResPath, ureWithRegion(kotlinModuleRegionLabel)) ?: error("No match $mppLibResPath")
    val androLibMR = RESOURCES.readAndMatchUre(androLibResPath, ureWithRegion(kotlinModuleRegionLabel)) ?: error("No match $androLibResPath")
    val androAppMR = RESOURCES.readAndMatchUre(androAppResPath, ureWithRegion(kotlinModuleRegionLabel)) ?: error("No match $androAppResPath")
    val regionInMppLib = mppLibMR["region"]
    val regionInAndroLib = androLibMR["region"]
    val regionInAndroApp = androAppMR["region"]
    check(regionInMppLib == regionInAndroLib) { "Templates in mpp lib and andro lib have to be the same! ${regionInMppLib.length} ${regionInAndroLib.length}" }
    check(regionInAndroLib == regionInAndroApp) { "Templates in andro lib and andro app have to be the same! ${regionInAndroLib.length} ${regionInAndroApp.length}" }
    println("OK. Templates in template-mpp and template-andro are the same.")

    // now check templates in given files
    for (file in buildFiles) {
        val region by SYSTEM.readAndMatchUre(file, ureWithRegion(kotlinModuleRegionLabel)) ?: error("No match $file")
        check(region == regionInMppLib) { "Template: [$kotlinModuleRegionLabel] in $file was modified." }
        println("OK. Template: [$kotlinModuleRegionLabel] in $file is correct.")
    }
    println("OK. Checked. All templates look good.")
}

fun checkMppModuleBuildTemplates(vararg buildFiles: Path) {

    // first check if all our template files are consistent:
    val mppLibMR = RESOURCES.readAndMatchUre(mppLibResPath, ureWithRegion(mppModuleRegionLabel)) ?: error("No match $mppLibResPath")
    // TODO: check with mppAppResPath - when I add app mpp module
    val regionInMppLib = mppLibMR["region"]

    // now check templates in given files
    for (file in buildFiles) {
        val region by SYSTEM.readAndMatchUre(file, ureWithRegion(mppModuleRegionLabel)) ?: error("No match $file")
        check(region == regionInMppLib) { "Template: [$mppModuleRegionLabel] in $file was modified." }
        println("OK. Template: [$mppModuleRegionLabel] in $file is correct.")
    }
    println("OK. Checked. All templates look good.")
}

fun checkAndroBuildTemplates(vararg buildFiles: Path) {
    val libMR = RESOURCES.readAndMatchUre(androLibResPath, ureWithRegion(androModuleRegionLabel)) ?: error("No match $androLibResPath")
    val appMR = RESOURCES.readAndMatchUre(androAppResPath, ureWithRegion(androModuleRegionLabel)) ?: error("No match $androAppResPath")
    val regionInLib = libMR["region"]
    val regionInApp = appMR["region"]
    check(regionInLib == regionInApp) { "Templates in andro lib and andro app have to be the same! ${regionInLib.length} ${regionInApp.length}" }
    println("OK. Templates in template-andro are the same.")
    for (file in buildFiles) {
        val region by SYSTEM.readAndMatchUre(file, ureWithRegion(androModuleRegionLabel)) ?: error("No match $file")
        check(region == regionInLib) { "Template: [$androModuleRegionLabel] in $file was modified." }
        println("OK. Template: [$androModuleRegionLabel] in $file is correct.")
    }
    println("OK. Checked. All templates look good.")
}

private fun ureWithRegion(regionLabel: String) = ure {
    1 of ureWhateva().withName("before")
    1 of ureRegion(ureWhateva(), ir(regionLabel)).withName("region")
    1 of ureWhateva(reluctant = false).withName("after")
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

