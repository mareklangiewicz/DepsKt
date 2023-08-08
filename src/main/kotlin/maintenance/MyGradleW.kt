package pl.mareklangiewicz.maintenance

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okio.*
import okio.FileSystem.Companion.RESOURCES
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import pl.mareklangiewicz.io.*
import pl.mareklangiewicz.kommand.*
import pl.mareklangiewicz.kommand.CliPlatform.Companion.SYS
import pl.mareklangiewicz.kommand.find.*


suspend fun updateGradlewFilesInMyProjects(onlyPublic: Boolean, log: (Any?) -> Unit = ::println) =
    getMyGradleProjectsPathS(onlyPublic).collect {
        updateGradlewFilesInProject(it, log)
    }

fun updateGradlewFilesInProject(path: Path, log: (Any?) -> Unit = ::println) =
    gradlewRelPaths.forEach { gradlewRelPath ->
        val targetPath = path / gradlewRelPath
        val content = RESOURCES.readByteString(gradlewRelPath.withName { "$it.tmpl" })
        val action = if (SYSTEM.exists(targetPath)) "Updating" else "Creating new"
        log("$action gradlew file: $targetPath")
        SYSTEM.writeByteString(targetPath, content)
    }


@OptIn(DelicateKommandApi::class)
private suspend fun findGradleRootProjectS(path: Path): Flow<Path> =
    findTypeRegex(path.toString(), "f", ".*/settings.gradle\\(.kts\\)?")
        .reduced {
            // $ at the end of regex is important to avoid matching generated resource like: settings.gradle.kts.tmpl
            val regex = Regex("/settings\\.gradle(\\.kts)?\$")
            stdout.map { regex.replace(it, "").toPath() }
        }
        .exec(SYS)

val gradlewRelPaths =
    listOf("", ".bat").map { "gradlew$it".toPath() } +
            listOf("jar", "properties").map { "gradle/wrapper/gradle-wrapper.$it".toPath() }

/** @return Full pathS of my gradle rootProjectS (dirs with settings.gradle[.kts] files) */
@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun getMyGradleProjectsPathS(onlyPublic: Boolean = true): Flow<Path> =
    fetchMyProjectsNameS(onlyPublic)
        .mapFilterLocalKotlinProjectsPathS()
        .flatMapConcat(::findGradleRootProjectS)


