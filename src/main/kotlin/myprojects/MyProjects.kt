package pl.mareklangiewicz.myprojects

import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import pl.mareklangiewicz.ure.*

fun checkAllKnownRegionsInMyProjects() = checkAllKnownRegionsInProjects(*MyKotlinProjects.toTypedArray())
fun checkAllKnownRegionsInProjects(vararg projectDirs: String) = projectDirs.forEach {
    println("Check all known regions in project: $it")
    SYSTEM.checkAllKnownRegionsInAllFoundFiles(PathToMyKotlinProjects / it, verbose = true)
}

fun injectAllKnownRegionsToMyProjects() = injectAllKnownRegionsToProjects(*MyKotlinProjects.toTypedArray())
fun injectAllKnownRegionsToProjects(vararg projectDirs: String) = projectDirs.forEach {
    println("Inject all known regions to project: $it")
    SYSTEM.injectAllKnownRegionsToAllFoundFiles(PathToMyKotlinProjects / it)
}

internal val PathToMyKotlinProjects = "/home/marek/code/kotlin".toPath()

internal val MyKotlinProjects = listOf(
    "AbcdK",
    "AutomateK",
    "CoEdges",
    "dbus-kotlin",
    "IPShareK",
    "Koder",
    "kokpit667",
    "KommandLine",
    "kthreelhu",
    "KWSocket",
    "MyBlog",
    "MyScripts",
    "MyStolenPlaygrounds",
    "RxDebugBridge",
    "RxEdges",
    "RxMock",
    "SMokK",
    "SourceFun",
    "TixyPlayground",
    "TupleK",
    "upue",
    "USpek",
    "uspek-js-playground",
    "uspek-painters",
    "UWidgets",
)
