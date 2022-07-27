package pl.mareklangiewicz.maintenance

import okio.*
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import pl.mareklangiewicz.ure.*

internal fun checkAllKnownRegionsInAllMyProjects() = checkAllKnownRegionsInMyProjects(*MyKotlinProjects.toTypedArray())
internal fun checkAllKnownRegionsInMyProjects(vararg names: String) =
    checkAllKnownRegionsInProjects(*names.map { PathToMyKotlinProjects / it }.toTypedArray())
fun checkAllKnownRegionsInProjects(vararg projects: Path) = projects.forEach {
    println("Check all known regions in project: $it")
    SYSTEM.checkAllKnownRegionsInAllFoundFiles(it, verbose = true)
}

internal fun injectAllKnownRegionsToAllMyProjects() = injectAllKnownRegionsToMyProjects(*MyKotlinProjects.toTypedArray())
internal fun injectAllKnownRegionsToMyProjects(vararg names: String) =
    injectAllKnownRegionsToProjects(*names.map { PathToMyKotlinProjects / it }.toTypedArray())
fun injectAllKnownRegionsToProjects(vararg projects: Path) = projects.forEach {
    println("Inject all known regions to project: $it")
    SYSTEM.injectAllKnownRegionsToAllFoundFiles(it)
}

internal val PathToMyKotlinProjects = "/home/marek/code/kotlin".toPath()

internal val MyKotlinProjects = listOf(
    "AbcdK",
    "AreaKim",
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
