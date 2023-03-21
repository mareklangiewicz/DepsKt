package pl.mareklangiewicz.maintenance

import okio.*
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import pl.mareklangiewicz.ure.*

fun checkAllKnownRegionsInAllMyProjects(log: (Any?) -> Unit = ::println) =
    checkAllKnownRegionsInMyProjects(*MyKotlinProjects.toTypedArray(), log = log)
fun checkAllKnownRegionsInMyProjects(vararg names: String, log: (Any?) -> Unit = ::println) =
    checkAllKnownRegionsInProjects(*names.map { PathToMyKotlinProjects / it }.toTypedArray(), log = log)

fun checkAllKnownRegionsInProjects(vararg projects: Path, log: (Any?) -> Unit = ::println) = projects.forEach {
    log("Check all known regions in project: $it")
    SYSTEM.checkAllKnownRegionsInAllFoundFiles(it, verbose = true, log = log)
}

fun injectAllKnownRegionsToAllMyProjects(log: (Any?) -> Unit = ::println) =
    injectAllKnownRegionsToMyProjects(*MyKotlinProjects.toTypedArray(), log = log)
fun injectAllKnownRegionsToMyProjects(vararg names: String, log: (Any?) -> Unit = ::println) =
    injectAllKnownRegionsToProjects(*names.map { PathToMyKotlinProjects / it }.toTypedArray(), log = log)

fun injectAllKnownRegionsToProjects(vararg projects: Path, log: (Any?) -> Unit = ::println) = projects.forEach {
    log("Inject all known regions to project: $it")
    SYSTEM.injectAllKnownRegionsToAllFoundFiles(it, log = log)
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
