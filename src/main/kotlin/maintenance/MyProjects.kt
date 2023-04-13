package pl.mareklangiewicz.maintenance

import io.github.typesafegithub.workflows.yaml.toYaml
import okio.*
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import pl.mareklangiewicz.io.filterExt
import pl.mareklangiewicz.io.findAllFiles
import pl.mareklangiewicz.io.readUtf8
import pl.mareklangiewicz.io.writeUtf8
import pl.mareklangiewicz.ure.*

fun checkAllWorkflowsInAllMyProjects(log: (Any?) -> Unit = ::println) =
    checkAllWorkflowsInMyProjects(*MyKotlinProjects.toTypedArray(), log = log)
fun checkAllWorkflowsInMyProjects(vararg names: String, log: (Any?) -> Unit = ::println) =
    checkAllWorkflowsInProjects(*names.map { PathToMyKotlinProjects / it }.toTypedArray(), log = log)

fun checkAllWorkflowsInProjects(vararg projects: Path, log: (Any?) -> Unit = ::println) = projects.forEach {
    log("Check all workflows in project: $it")
    SYSTEM.checkAllFoundWorkflowsInProject(it, verbose = true, log = log)
}

fun FileSystem.checkAllFoundWorkflowsInProject(
    projectPath: Path,
    yamlFilesPath: Path = projectPath / ".github" / "workflows",
    yamlFilesExt: String = "yml",
    failIfUnknownWorkflowFound: Boolean = false,
    failIfKnownWorkflowNotFound: Boolean = false,
    verbose: Boolean = false,
    log: (Any?) -> Unit = ::println,
) {
    val yamlFiles = findAllFiles(yamlFilesPath, maxDepth = 1).filterExt(yamlFilesExt)
    val yamlNames = yamlFiles.map { it.name.substringBeforeLast('.') }
    for (dname in MyWorkflowDNames) {
        if (dname !in yamlNames) {
            val summary = "Workflow $dname not found."
            if (verbose) log("ERR project:${projectPath.name}: $summary")
            if (failIfKnownWorkflowNotFound) error(summary)
        }
    }

    for (file in yamlFiles) {
        val dname = file.name.substringBeforeLast('.')
        val contentExpected = try { defaultWorkflow(dname).toYaml() }
        catch (e: IllegalStateException) {
            if (failIfUnknownWorkflowFound) throw e
            else { if (verbose) log(e.message); continue }
        }
        val contentActual = readUtf8(file)
        check(contentExpected == contentActual) {
            val summary = "Workflow $dname was modified."
            if (verbose) log("ERR project:${projectPath.name}: $summary")
            summary
        }
        if (verbose) log("OK project:${projectPath.name} workflow:$dname")
    }
}



fun injectDefaultWorkflowsToAllMyProjects(log: (Any?) -> Unit = ::println) =
    injectDefaultWorkflowsToMyProjects(*MyKotlinProjects.toTypedArray(), log = log)
fun injectDefaultWorkflowsToMyProjects(vararg names: String, log: (Any?) -> Unit = ::println) =
    injectDefaultWorkflowsToProjects(*names.map { PathToMyKotlinProjects / it }.toTypedArray(), log = log)

fun injectDefaultWorkflowsToProjects(vararg projects: Path, log: (Any?) -> Unit = ::println) = projects.forEach {
    log("Inject default workflows to project: $it")
    SYSTEM.injectDefaultWorkflowsToProject(it, log = log)
}

fun FileSystem.injectDefaultWorkflowsToProject(
    projectPath: Path,
    yamlFilesPath: Path = projectPath / ".github" / "workflows",
    yamlFilesExt: String = "yml",
    log: (Any?) -> Unit = ::println,
) {
    for (dname in MyWorkflowDNames) {
        val file = yamlFilesPath / "$dname.$yamlFilesExt"
        val contentOld = readUtf8(file)
        val contentNew = defaultWorkflow(dname).toYaml()
        SYSTEM.writeUtf8(file, contentNew)
        val summary =
            if (contentNew == contentOld) "No changes."
            else "Changes detected (len ${contentOld.length}->${contentNew.length})"
        log("Inject workflow to project:${projectPath.name} dname:$dname - $summary")
    }
}



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
