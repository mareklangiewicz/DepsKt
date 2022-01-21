package pl.mareklangiewicz.deps

import okio.FileSystem.Companion.RESOURCES
import okio.FileSystem.Companion.SYSTEM
import okio.Path
import okio.Path.Companion.toPath
import pl.mareklangiewicz.ure.*

private val appABTResPath = "andro-app-build-template".toPath()
private val libABTResPath = "andro-lib-build-template".toPath()

private val ureWithABT = ure {
    1 of ureWhateva().withName("before")
    1 of ureRegion(ureWhateva(), ir("Andro Build Template")).withName("abt")
    1 of ureWhateva(reluctant = false).withName("after")
}


private fun injectAndroBuildTemplate(inputResPath: Path, outputPath: Path) {
    val inputMR = RESOURCES.sourceMatchUre(inputResPath, ureWithABT) ?: error("No match $inputResPath")
    val abt by inputMR
    SYSTEM.processFile(outputPath) { output ->
        val outputMR = ureWithABT.compile().matchEntire(output) ?: error("No match $outputPath")
        val before by outputMR
        val after by outputMR
        before + abt + after
    }
}

fun injectAndroAppBuildTemplate(outputPath: Path) = injectAndroBuildTemplate(appABTResPath, outputPath)
fun injectAndroLibBuildTemplate(outputPath: Path) = injectAndroBuildTemplate(libABTResPath, outputPath)

fun checkAndroBuildTemplates() {
    val appMR = RESOURCES.sourceMatchUre(appABTResPath, ureWithABT) ?: error("No match $appABTResPath")
    val libMR = RESOURCES.sourceMatchUre(libABTResPath, ureWithABT) ?: error("No match $libABTResPath")
    check(appMR["abt"] == libMR["abt"]) { "Templates in app and lib have to be the same!" }
}

// TODO NOW: scan imports and add imports from template-android/lib/build.gradle.kts if needed

