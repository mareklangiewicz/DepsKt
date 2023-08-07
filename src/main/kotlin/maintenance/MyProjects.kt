package pl.mareklangiewicz.maintenance

import io.github.typesafegithub.workflows.yaml.toYaml
import kotlinx.coroutines.flow.*
import okio.*
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import pl.mareklangiewicz.io.filterExt
import pl.mareklangiewicz.io.findAllFiles
import pl.mareklangiewicz.io.readUtf8
import pl.mareklangiewicz.io.writeUtf8
import pl.mareklangiewicz.kommand.*
import pl.mareklangiewicz.kommand.CliPlatform.Companion.SYS
import pl.mareklangiewicz.kommand.github.*
import pl.mareklangiewicz.ure.*

suspend fun checkMyDWorkflowsInMyProjects(onlyPublic: Boolean, log: (Any?) -> Unit = ::println) =
    fetchMyProjectsNameS(onlyPublic)
        .mapFilterLocalDWorkflowsProjectsPathS()
        .collect { SYSTEM.checkMyDWorkflowsInProject(it, verbose = true, log = log) }


suspend fun injectMyDWorkflowsToMyProjects(onlyPublic: Boolean, log: (Any?) -> Unit = ::println) =
    fetchMyProjectsNameS(onlyPublic)
        .mapFilterLocalDWorkflowsProjectsPathS(log = log)
        .collect { SYSTEM.injectDefaultWorkflowsToProject(it, log = log) }

private fun Flow<String>.mapFilterLocalDWorkflowsProjectsPathS(
    localSystem: FileSystem = SYSTEM,
    log: (Any?) -> Unit = ::println,
) = mapFilterLocalKotlinProjectsPathS(localSystem) {
    val isGradleRootProject = exists(it / "settings.gradle.kts") || exists(it / "settings.gradle")
    if (!isGradleRootProject) log("Ignoring dworkflows in non-gradle project: $it")
    // FIXME_maybe: Change when I have dworkflows for non-gradle projects
    isGradleRootProject
}

/** @receiver Flow of projects names. */
internal fun Flow<String>.mapFilterLocalKotlinProjectsPathS(
    localSystem: FileSystem = SYSTEM,
    alsoFilter: suspend FileSystem.(Path) -> Boolean = { true }
) = map { PathToMyKotlinProjects / it }
    .filter { localSystem.exists(it) }
    .filter { localSystem.alsoFilter(it) }



fun checkAllKnownRegionsInAllMyProjects(log: (Any?) -> Unit = ::println) =
    checkAllKnownRegionsInMyProjects(*MyOssKotlinProjects.toTypedArray(), log = log)
fun checkAllKnownRegionsInMyProjects(vararg names: String, log: (Any?) -> Unit = ::println) =
    checkAllKnownRegionsInProjects(*names.map { PathToMyKotlinProjects / it }.toTypedArray(), log = log)

fun checkAllKnownRegionsInProjects(vararg projects: Path, log: (Any?) -> Unit = ::println) = projects.forEach {
    log("Check all known regions in project: $it")
    SYSTEM.checkAllKnownRegionsInAllFoundFiles(it, verbose = true, log = log)
}

fun injectAllKnownRegionsToAllMyProjects(log: (Any?) -> Unit = ::println) =
    injectAllKnownRegionsToMyProjects(*MyOssKotlinProjects.toTypedArray(), log = log)
fun injectAllKnownRegionsToMyProjects(vararg names: String, log: (Any?) -> Unit = ::println) =
    injectAllKnownRegionsToProjects(*names.map { PathToMyKotlinProjects / it }.toTypedArray(), log = log)

fun injectAllKnownRegionsToProjects(vararg projects: Path, log: (Any?) -> Unit = ::println) = projects.forEach {
    log("Inject all known regions to project: $it")
    SYSTEM.injectAllKnownRegionsToAllFoundFiles(it, log = log)
}

internal val PathToMyKotlinProjects = "/home/marek/code/kotlin".toPath()

@Suppress("IdentifierGrammar")
internal suspend fun fetchMyProjectsNameS(onlyPublic: Boolean = true): Flow<String> =
    ghLangaraRepoList(onlyPublic = onlyPublic)
        .outputFields("name")
        .reduced { stdout }
        .exec(SYS)

internal suspend fun fetchMyProjectsNames(onlyPublic: Boolean = true, sorted: Boolean = true): List<String> =
    fetchMyProjectsNameS(onlyPublic).toList().let { if (sorted) it.sorted() else it }


@Deprecated("Use KommandLine instead of hardcoding different lists or projects/repos")
private val MyOssKotlinProjects = listOf(
    "AbcdK",
    "AutomateK",
    "CoEdges",
    "DepsKt",
    "dbus-kotlin",
    "KommandLine",
    "kthreelhu",
    "MyBlog",
    "MyHub",
    "MyIntent",
    "MyIntentSample",
    "MyScripts",
    "MyStolenPlaygrounds",
    "RecyclerUi",
    "reproducers",
    "RxEdges",
    "RxMock",
    "SMokK",
    "SandboxUi",
    "SourceFun",
    "StructuredNotes",
    "TixyPlayground",
    "TupleK",
    "UPue",
    "USpek",
    "uspek-js-playground",
    "UWidgets",
)
