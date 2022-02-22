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
import pl.mareklangiewicz.utils.*
import java.io.*

internal data class SourceFunDefinition(
    val taskName: String,
    val sourcePath: Path,
    val outputPath: Path,
    val transform: (String) -> String?
)

open class SourceFunExtension {

    internal val definitions = mutableListOf<SourceFunDefinition>()

    fun def(taskName: String, sourcePath: Path, outputPath: Path, transform: (String) -> String?) {
        definitions.add(SourceFunDefinition(taskName, sourcePath, outputPath, transform))
    }
}

class SourceFunPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val extension = project.extensions.create("sourceFun", SourceFunExtension::class.java)

        project.afterEvaluate {// FIXME: is afterEvaluate appropriate here??
            for (def in extension.definitions) project.tasks.register<SourceFunTask>(def.taskName) {
                addSource(def.sourcePath)
                setOutput(def.outputPath)
                transform(def.transform)
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

    fun visit(action: FileVisitDetails.() -> Unit) {
        visitProperty.set { action() }
        visitProperty.finalizeValue()
    }

    fun visitPath(action: (inPath: Path, outPath: Path) -> Unit) = visit {
        val dir = outputDir.get()
        val inFile = file
        val relPath = path
        logger.quiet("SourceFunTask: processing $relPath")
        val outFile = dir.file(relPath).asFile
        if (isDirectory) outFile.mkdirs() else action(inFile.toOkioPath(), outFile.toOkioPath())
    }

    fun transformPath(transform: (Path) -> String?) = visitPath { inPath, outPath ->
        transform(inPath)?.let { SYSTEM.writeUtf8(outPath, it) }
    }

    fun transform(transform: (String) -> String?) = transformPath { transform(SYSTEM.readUtf8(it)) }
}

abstract class SourceRegexTask : SourceFunTask() {
    @get:Input
    abstract val match: Property<String>
    @get:Input
    abstract val replace: Property<String>
    init { transform { it.replace(Regex(match.get()), replace.get()) } }
}
