@file:Suppress("MemberVisibilityCanBePrivate", "PackageDirectoryMismatch")

package pl.mareklangiewicz.sourcefun

import kotlin.properties.*
import kotlin.reflect.*
import okio.*
import org.gradle.api.*
import org.gradle.api.tasks.*

@Deprecated("Use SourceFunRegistering (via sourceFun { val taskName by reg {...} })")
internal data class SourceFunDefinition(
  val taskName: String,
  val sourcePath: Path,
  val outputPath: Path,
  val taskGroup: String? = null,
  val transform: Pair<Path, Path>.(String) -> String?,
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
  fun def(taskName: String, sourcePath: Path, outputPath: Path, transform: Pair<Path, Path>.(String) -> String?) {
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

    // this is only needed for deprecated definitions list. so it will be removed sooner or later
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

