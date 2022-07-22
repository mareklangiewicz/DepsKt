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
    listOf("template-mpp", "template-andro", "sample-sourcefun").map { "deps.kt/$it" } +
    listOf("isolatedground1", "isolatedground2", "isolatedground3", "isolatedkamera").map { "kokpit667/$it" }

private val MyGradlewProjects = MyKotlinProjects - MyNonGradlewProjects + MyGradlewSubProjects

internal fun updateGradlewFilesInAllMyProjects() = updateGradlewFilesInMyProjects(*MyGradlewProjects.toTypedArray())
internal fun updateGradlewFilesInMyProjects(vararg names: String) =
    updateGradlewFilesInProjects(*names.map { PathToMyKotlinProjects / it }.toTypedArray())
fun updateGradlewFilesInProjects(vararg projects: Path) = projects.forEach { projectPath ->
    gradlewRelPaths.forEach { gradlewRelPath ->
        val targetPath = projectPath / gradlewRelPath
        check(SYSTEM.exists(targetPath)) { "Gradlew file does not exist: $targetPath" }
        val content = RESOURCES.readUtf8(gradlewRelPath.withName { "$it.tmpl" })
        println("Updating gradlew file: $targetPath")
        SYSTEM.writeUtf8(targetPath, content)
    }
}





