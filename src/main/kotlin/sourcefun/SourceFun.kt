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
    protected abstract val outputDir: DirectoryProperty

    @get:Internal
    protected abstract val visitProperty: Property<Action<FileVisitDetails>>

    fun setOutput(path: Path) = outputDir.set(path.toFile())

    @TaskAction
    fun execute() {
        source.visit(visitProperty.get())
    }

    fun setVisitFun(action: FileVisitDetails.() -> Unit) {
        visitProperty.set(action)
        visitProperty.finalizeValue()
    }

    fun setVisitPathFun(action: (inPath: Path, outPath: Path) -> Unit) = setVisitFun {
        val dir = outputDir.get()
        val inFile = file
        val relPath = path
        logger.quiet("SourceFunTask: processing $relPath")
        val outFile = dir.file(relPath).asFile
        if (!isDirectory) action(inFile.toOkioPath(), outFile.toOkioPath())
    }

    fun setTransformPathFun(transform: (Path) -> String?) = setVisitPathFun { inPath, outPath ->
        transform(inPath)?.let { SYSTEM.writeUtf8(outPath, it, createParentDir = true) }
    }

    fun setTransformFun(transform: Path.(String) -> String?) = setTransformPathFun { it.transform(SYSTEM.readUtf8(it)) }
}

abstract class SourceRegexTask : SourceFunTask() {
    @get:Input
    abstract val match: Property<String>
    @get:Input
    abstract val replace: Property<String>
    init { setTransformFun { it.replace(Regex(match.get()), replace.get()) } }
}

fun SourceTask.addSource(path: Path) = source(path.toFile())


@Suppress("UnstableApiUsage")
@UntrackedTask(because = "Git version and build time is external state and can't be tracked.")
abstract class VersionDetailsTask: DefaultTask() {

    @get:OutputFile
    abstract val gitVersionOutputFile: RegularFileProperty

    @get:OutputFile
    abstract val buildTimeOutputFile: RegularFileProperty

    @TaskAction
    fun execute(){
        val process = ProcessBuilder("git", "rev-parse",  "HEAD").start()
        val error = process.errorStream.bufferedReader().use { it.readText() }
        check(error.isBlank()) { "GitVersionTask error: $error" }
        val gitVersion = process.inputStream.bufferedReader().use { it.readText() }
        gitVersionOutputFile.get().asFile.writeText(gitVersion)
        buildTimeOutputFile.get().asFile.writeText(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
    }
}
