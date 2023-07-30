package pl.mareklangiewicz.maintenance

import okio.*
import okio.FileSystem.Companion.RESOURCES
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import pl.mareklangiewicz.io.*

val gradlewRelPaths =
    listOf("", ".bat").map { "gradlew$it".toPath() } +
        listOf("jar", "properties").map { "gradle/wrapper/gradle-wrapper.$it".toPath() }

private val MyNonGradlewProjects = listOf("MyScripts", "uspek-js-playground", "uspek-painters")
private val MyGradlewSubProjects =
    listOf("template-mpp", "template-andro", "sample-sourcefun").map { "DepsKt/$it" } +
        listOf("isolatedground1", "isolatedground2", "isolatedground3", "isolatedkamera").map { "kokpit667/isolated/$it" }

private val MyGradlewProjects = MyOssKotlinProjects - MyNonGradlewProjects + MyGradlewSubProjects

fun updateGradlewFilesInAllMyProjects(log: (Any?) -> Unit = ::println) =
    updateGradlewFilesInMyProjects(*MyGradlewProjects.toTypedArray(), log = log)
fun updateGradlewFilesInMyProjects(vararg names: String, log: (Any?) -> Unit = ::println) =
    updateGradlewFilesInProjects(*names.map { PathToMyKotlinProjects / it }.toTypedArray(), log = log)

fun updateGradlewFilesInProjects(vararg projects: Path, log: (Any?) -> Unit = ::println) = projects.forEach { projectPath ->
    gradlewRelPaths.forEach { gradlewRelPath ->
        val targetPath = projectPath / gradlewRelPath
        check(SYSTEM.exists(targetPath)) { "Gradlew file does not exist: $targetPath" }
        val content = RESOURCES.readByteString(gradlewRelPath.withName { "$it.tmpl" })
        log("Updating gradlew file: $targetPath")
        SYSTEM.writeByteString(targetPath, content)
    }
}





