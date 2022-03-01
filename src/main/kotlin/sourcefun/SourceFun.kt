@file:Suppress("MemberVisibilityCanBePrivate", "PackageDirectoryMismatch")

package pl.mareklangiewicz.sourcefun

import okio.*
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toOkioPath
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import pl.mareklangiewicz.ure.*
import java.time.*
import java.time.format.*

internal data class SourceFunDefinition(
    val taskName: String,
    val sourcePath: Path,
    val outputPath: Path,
    val taskGroup: String? = null,
    val transform: Path.(String) -> String?
)

open class SourceFunExtension {

    internal val definitions = mutableListOf<SourceFunDefinition>()

    var grp: String? = null // very hacky - TODO_later: experiment and probably refactor

    fun def(taskName: String, sourcePath: Path, outputPath: Path, transform: Path.(String) -> String?) {
        definitions.add(SourceFunDefinition(taskName, sourcePath, outputPath, grp, transform))
    }
}

class SourceFunPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val extension = project.extensions.create("sourceFun", SourceFunExtension::class.java)

        project.afterEvaluate {// FIXME: is afterEvaluate appropriate here??
            for (def in extension.definitions) project.tasks.register<SourceFunTask>(def.taskName) {
                addSource(def.sourcePath)
                setOutput(def.outputPath)
                setTransformFun(def.transform)
                def.taskGroup?.let { group = it }
            }
        }
    }
}

abstract class SourceFunTask : SourceTask() {

    @get:OutputDirectory
    protected abstract val outputDirProperty: DirectoryProperty

    @get:Internal
    protected abstract val taskActionProperty: Property<(source: FileTree, output: Directory) -> Unit>

    fun setOutput(path: Path) = outputDirProperty.set(path.toFile())

    fun setTaskAction(action: (srcTree: FileTree, outDir: Directory) -> Unit) {
        taskActionProperty.set(action)
        taskActionProperty.finalizeValue()
    }

    @TaskAction
    fun taskAction() = taskActionProperty.get()(source, outputDirProperty.get())
}

fun SourceFunTask.setVisitFun(action: FileVisitDetails.(outDir: Directory) -> Unit) {
    setTaskAction { srcTree, outDir -> srcTree.visit { action(outDir) } }
}

fun SourceFunTask.setVisitPathFun(action: (inPath: Path, outPath: Path) -> Unit) {
    setVisitFun { outDir ->
        if (isDirectory) return@setVisitFun
        logger.quiet("SourceFunTask: processing $path")
        action(file.toOkioPath(), outDir.file(path).asFile.toOkioPath())
    }
}

fun SourceFunTask.setTransformPathFun(transform: (Path) -> String?) = setVisitPathFun { inPath, outPath ->
    transform(inPath)?.let { SYSTEM.writeUtf8(outPath, it, createParentDir = true) }
}

fun SourceFunTask.setTransformFun(transform: Path.(String) -> String?) {
    setTransformPathFun { it.transform(SYSTEM.readUtf8(it)) }
}

abstract class SourceRegexTask : SourceFunTask() {
    @get:Input
    abstract val match: Property<String>
    @get:Input
    abstract val replace: Property<String>
    init { setTransformFun { it.replace(Regex(match.get()), replace.get()) } }
}

fun SourceTask.addSource(path: Path) { source(path.toFile()) }


@Suppress("UnstableApiUsage")
@UntrackedTask(because = "Git version and build time is external state and can't be tracked.")
abstract class VersionDetailsTask: DefaultTask() {

    @get:OutputDirectory
    abstract val generatedAssetsDir: DirectoryProperty

    @TaskAction
    fun execute(){
        val process = ProcessBuilder("git", "rev-parse",  "HEAD").start()
        val error = process.errorStream.bufferedReader().use { it.readText() }
        check(error.isBlank()) { "GitVersionTask error: $error" }
        val commit = process.inputStream.bufferedReader().use { it.readText() }
        val time = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        generatedAssetsDir.get().run {
            file("version-details/commit").asFile.writeText(commit)
            file("version-details/buildtime").asFile.writeText(time)
        }
    }
}
