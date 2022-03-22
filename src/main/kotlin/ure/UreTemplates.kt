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
private val androRegionLabel = "Andro Build Template"

fun injectAndroLibBuildTemplate(outputPath: Path) = injectBuildRegion(androRegionLabel, androLibResPath, outputPath)
fun injectAndroAppBuildTemplate(outputPath: Path) = injectBuildRegion(androRegionLabel, androAppResPath, outputPath)

fun checkAndroBuildTemplates(vararg buildFiles: Path) {
    val libMR = RESOURCES.readAndMatchUre(androLibResPath, ureWithRegion(androRegionLabel)) ?: error("No match $androLibResPath")
    val appMR = RESOURCES.readAndMatchUre(androAppResPath, ureWithRegion(androRegionLabel)) ?: error("No match $androAppResPath")
    val appABT = appMR["region"]
    val libABT = libMR["region"]
    check(appABT == libABT) { "Templates in app and lib have to be the same! ${appABT.length} ${libABT.length}" }
    println("OK. Templates in template-andro are the same.")
    for (file in buildFiles) {
        val abt by SYSTEM.readAndMatchUre(file, ureWithRegion(androRegionLabel)) ?: error("No match $file")
        check(abt == appABT) { "Template in $file was modified." }
        println("OK. Template in $file is correct.")
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

