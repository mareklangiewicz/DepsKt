@file:Suppress("MemberVisibilityCanBePrivate", "PackageDirectoryMismatch")

package pl.mareklangiewicz.sourcefun

import okio.*
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toOkioPath
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import pl.mareklangiewicz.io.*
import java.time.*
import java.time.format.*
import kotlin.properties.*
import kotlin.reflect.*

internal data class SourceFunDefinition(
    val taskName: String,
    val sourcePath: Path,
    val outputPath: Path,
    val taskGroup: String? = null,
    val transform: Path.(String) -> String?,
)

class SourceFunRegistering(val project: Project, val configuration: SourceFunTask.() -> Unit) {

    operator fun provideDelegate(thisObj: SourceFunExtension?, property: KProperty<*>):
        ReadOnlyProperty<SourceFunExtension?, TaskProvider<SourceFunTask>> {
        val taskProvider = project.tasks.register(property.name, SourceFunTask::class.java, configuration)
        return ReadOnlyProperty { _, _ -> taskProvider }
    }
}

open class SourceFunExtension {

    internal val definitions = mutableListOf<SourceFunDefinition>()

    var grp: String? = null

    @Deprecated("Use val taskName by reg { ... }", replaceWith = ReplaceWith("val taskName by reg { ... }"))
    fun def(taskName: String, sourcePath: Path, outputPath: Path, transform: Path.(String) -> String?) {
        definitions.add(SourceFunDefinition(taskName, sourcePath, outputPath, grp, transform))
    }

    fun Project.reg(group: String? = grp, configuration: SourceFunTask.() -> Unit) = SourceFunRegistering(project) {
        group?.let { this.group = it }
        configuration()
    }
}

class SourceFunPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val extension = project.extensions.create("sourceFun", SourceFunExtension::class.java)

        project.afterEvaluate {// FIXME: is afterEvaluate appropriate here??
            for (def in extension.definitions) project.tasks.register(def.taskName, SourceFunTask::class.java) { task ->
                task.addSource(def.sourcePath)
                task.setOutput(def.outputPath)
                task.setTransformFun(def.transform)
                def.taskGroup?.let { task.group = it }
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

fun SourceTask.addSource(path: Path) {
    source(path.toFile())
}

var SourceFunTask.src: Path
    get() = error("src is write only")
    set(value) = setSource(value.toFile())

var SourceFunTask.out: Path
    get() = error("out is write only")
    set(value) = setOutput(value)


fun SourceFunTask.setVisitFun(action: FileVisitDetails.(outDir: Directory) -> Unit) {
    setTaskAction { srcTree, outDir -> srcTree.visit { it.action(outDir) } }
}

fun SourceFunTask.setVisitPathFun(action: (inPath: Path, outPath: Path) -> Unit) {
    setVisitFun { outDir ->
        if (isDirectory) return@setVisitFun
        val inPath = file.toOkioPath()
        val outPath = outDir.file(path).asFile.toOkioPath()
        logger.info("src: $inPath") // printing absolute path is good because it's then clickable in the IDE
        logger.info("out: $outPath")
        action(inPath, outPath)
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

    init {
        setTransformFun { it.replace(Regex(match.get()), replace.get()) }
    }
}

@Suppress("UnstableApiUsage")
@UntrackedTask(because = "Git version and build time is external state and can't be tracked.")
abstract class VersionDetailsTask : DefaultTask() {

    @get:OutputDirectory
    abstract val generatedAssetsDir: DirectoryProperty

    @TaskAction
    fun execute() {
        val process = ProcessBuilder("git", "rev-parse", "HEAD").start()
        val error = process.errorStream.bufferedReader().use { it.readText() }
        check(error.isBlank()) { "GitVersionTask error: $error" }
        val commit = process.inputStream.bufferedReader().use { it.readText() }
        val time = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        generatedAssetsDir.dir("version-details").get().run {
            project.mkdir(this)
            file("commit").asFile.writeText(commit)
            file("buildtime").asFile.writeText(time)
        }
    }
}
