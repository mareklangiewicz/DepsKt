package pl.mareklangiewicz.maintenance

import kotlinx.coroutines.flow.*
import okio.*
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import pl.mareklangiewicz.kommand.*
import pl.mareklangiewicz.kommand.CliPlatform.Companion.SYS
import pl.mareklangiewicz.kommand.github.*
import pl.mareklangiewicz.ure.*

suspend fun checkMyDWorkflowsInMyProjects(onlyPublic: Boolean, log: (Any?) -> Unit = ::println) =
    fetchMyProjectsNameS(onlyPublic)
        .mapFilterLocalDWorkflowsProjectsPathS(log = log)
        .collect { SYSTEM.checkMyDWorkflowsInProject(it, verbose = true, log = log) }


suspend fun injectMyDWorkflowsToMyProjects(onlyPublic: Boolean, log: (Any?) -> Unit = ::println) =
    fetchMyProjectsNameS(onlyPublic)
        .mapFilterLocalDWorkflowsProjectsPathS(log = log)
        .collect { SYSTEM.injectDWorkflowsToProject(it, log = log) }

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



suspend fun checkAllKnownRegionsInMyProjects(onlyPublic: Boolean = false, log: (Any?) -> Unit = ::println) =
    fetchMyProjectsNameS(onlyPublic)
        .mapFilterLocalKotlinProjectsPathS()
        .collect {
            log("Check all known regions in project: $it")
            SYSTEM.checkAllKnownRegionsInAllFoundFiles(it, verbose = true, log = log)
        }

suspend fun injectAllKnownRegionsToMyProjects(onlyPublic: Boolean = false, log: (Any?) -> Unit = ::println) =
    fetchMyProjectsNameS(onlyPublic)
        .mapFilterLocalKotlinProjectsPathS()
        .collect {
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

