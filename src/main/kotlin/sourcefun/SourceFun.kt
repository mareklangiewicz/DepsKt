package pl.mareklangiewicz.sourcefun

import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import java.io.*

internal data class SourceFunDefinition(
    val taskName: String,
    val sourceDir: String,
    val outputDir: String,
    val transform: (String) -> String?
)

open class SourceFunExtension {

    internal val definitions = mutableListOf<SourceFunDefinition>()

    fun def(taskName: String, sourceDir: String, outputDir: String, transform: (String) -> String?) {
        definitions.add(SourceFunDefinition(taskName, sourceDir, outputDir, transform))
    }
}

class SourceFunPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val extension = project.extensions.create("sourceFun", SourceFunExtension::class.java)

        project.afterEvaluate {// FIXME: is afterEvaluate appropriate here??
            for (def in extension.definitions) project.tasks.register<SourceFunTask>(def.taskName) {
                source(def.sourceDir)
                outputDir.set(project.file(def.outputDir))
                transform(def.transform)
            }
        }
    }
}

abstract class SourceFunTask : SourceTask() {

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Internal
    internal abstract val visitProperty: Property<Action<FileVisitDetails>>

    @TaskAction
    fun execute() { source.visit(visitProperty.get()) }

    fun visit(action: FileVisitDetails.() -> Unit) {
        visitProperty.set { action() }
        visitProperty.finalizeValue()
    }

    fun visitFile(action: (inFile: File, outFile: File) -> Unit) = visit {
        val dir = outputDir.get()
        val inFile = file
        val relPath = path
        logger.quiet("SourceFunTask: processing $relPath")
        val outFile = dir.file(relPath).asFile
        if (isDirectory) outFile.mkdirs() else action(inFile, outFile)
    }

    fun transformFile(transform: (File) -> String?) {
        visitFile { inFile, outFile -> transform(inFile)?.let { outFile.writeText(it) } }
    }

    fun transform(transform: (String) -> String?) = transformFile { transform(it.readText()) }
}

abstract class SourceRegexTask : SourceFunTask() {
    @get:Input
    abstract val match: Property<String>
    @get:Input
    abstract val replace: Property<String>
    init { transform { it.replace(Regex(match.get()), replace.get()) } }
}
