package pl.mareklangiewicz.evts

import okio.*
import org.gradle.*
import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.execution.*
import org.gradle.api.initialization.*
import org.gradle.api.invocation.*
import org.gradle.api.logging.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.testing.*
import pl.mareklangiewicz.evts.GradleEvt.BuildEvt.*
import pl.mareklangiewicz.evts.GradleEvt.DependencyResolutionEvt.*
import pl.mareklangiewicz.evts.GradleEvt.ProjectEvaluationEvt.*
import pl.mareklangiewicz.evts.GradleEvt.StdOutEvt.*
import pl.mareklangiewicz.evts.GradleEvt.TaskActionEvt.*
import pl.mareklangiewicz.evts.GradleEvt.TaskExecutionEvt.*
import pl.mareklangiewicz.evts.GradleEvt.TaskExecutionGraphEvt.*
import pl.mareklangiewicz.evts.GradleEvt.TestEvt.*
import pl.mareklangiewicz.evts.GradleEvt.TestOutEvt.*
import java.util.*

sealed class GradleEvt {
  sealed class BuildEvt : GradleEvt() {
    data class BeforeSettings(val settings: Settings) : BuildEvt()

    @Deprecated("This method is not supported when configuration caching is enabled.")
    data class BuildFinished(val result: BuildResult) : BuildEvt()
    data class ProjectsEvaluated(val gradle: Gradle) : BuildEvt()
    data class ProjectsLoaded(val gradle: Gradle) : BuildEvt()
    data class SettingsEvaluated(val settings: Settings) : BuildEvt()
  }

  sealed class ProjectEvaluationEvt : GradleEvt() {
    data class BeforeEvaluate(val project: Project) : ProjectEvaluationEvt()
    data class AfterEvaluate(val project: Project, val state: ProjectState) : ProjectEvaluationEvt()
  }

  sealed class TaskExecutionEvt : GradleEvt() {
    data class BeforeExecute(val task: Task) : TaskExecutionEvt()
    data class AfterExecute(val task: Task, val state: TaskState) : TaskExecutionEvt()
  }

  sealed class TaskExecutionGraphEvt : GradleEvt() {
    data class GraphPopulated(val graph: TaskExecutionGraph) : TaskExecutionGraphEvt()
  }

  sealed class TaskActionEvt : GradleEvt() {
    data class BeforeActions(val task: Task) : TaskActionEvt()
    data class AfterActions(val task: Task) : TaskActionEvt()
  }

  sealed class StdOutEvt : GradleEvt() {
    data class OnOutput(val output: CharSequence?) : StdOutEvt()
  }

  sealed class TestEvt : GradleEvt() {
    data class BeforeSuite(val suite: TestDescriptor?) : TestEvt()
    data class AfterSuite(val suite: TestDescriptor?, val result: TestResult?) : TestEvt()
    data class BeforeTest(val descriptor: TestDescriptor?) : TestEvt()
    data class AfterTest(val descriptor: TestDescriptor?, val result: TestResult?) : TestEvt()
  }

  sealed class TestOutEvt : GradleEvt() {
    data class OnTestOutput(val descriptor: TestDescriptor?, val output: TestOutputEvent?) : TestOutEvt()
  }

  sealed class DependencyResolutionEvt : GradleEvt() {
    data class BeforeResolve(val dependencies: ResolvableDependencies) : DependencyResolutionEvt()
    data class AfterResolve(val dependencies: ResolvableDependencies) : DependencyResolutionEvt()
  }
}

class GradleListener(val pushee: (GradleEvt) -> Unit) : BuildListener, ProjectEvaluationListener,
  TaskExecutionListener, TaskExecutionGraphListener, TaskActionListener, StandardOutputListener, TestListener,
  TestOutputListener, DependencyResolutionListener {

  override fun beforeSettings(settings: Settings) = pushee(BeforeSettings(settings))

  @Deprecated("This method is not supported when configuration caching is enabled.")
  override fun buildFinished(result: BuildResult) = pushee(BuildFinished(result))
  override fun projectsEvaluated(gradle: Gradle) = pushee(ProjectsEvaluated(gradle))
  override fun projectsLoaded(gradle: Gradle) = pushee(ProjectsLoaded(gradle))
  override fun settingsEvaluated(settings: Settings) = pushee(SettingsEvaluated(settings))

  override fun beforeEvaluate(project: Project) = pushee(BeforeEvaluate(project))
  override fun afterEvaluate(project: Project, state: ProjectState) = pushee(AfterEvaluate(project, state))

  override fun beforeExecute(task: Task) = pushee(BeforeExecute(task))
  override fun afterExecute(task: Task, state: TaskState) = pushee(AfterExecute(task, state))

  override fun graphPopulated(graph: TaskExecutionGraph) = pushee(GraphPopulated(graph))

  override fun beforeActions(task: Task) = pushee(BeforeActions(task))
  override fun afterActions(task: Task) = pushee(AfterActions(task))

  override fun onOutput(output: CharSequence?) = pushee(OnOutput(output))

  override fun beforeSuite(suite: TestDescriptor?) = pushee(BeforeSuite(suite))
  override fun afterSuite(suite: TestDescriptor?, result: TestResult?) = pushee(AfterSuite(suite, result))
  override fun beforeTest(descriptor: TestDescriptor?) = pushee(BeforeTest(descriptor))
  override fun afterTest(descriptor: TestDescriptor?, result: TestResult?) = pushee(AfterTest(descriptor, result))

  override fun onOutput(descriptor: TestDescriptor?, output: TestOutputEvent?) =
    pushee(OnTestOutput(descriptor, output))

  override fun beforeResolve(dependencies: ResolvableDependencies) = pushee(BeforeResolve(dependencies))
  override fun afterResolve(dependencies: ResolvableDependencies) = pushee(AfterResolve(dependencies))
}

// TODO_someday: investigate if it can considerably slow down gradle (additional IO during configuration, etc),
// if not: maybe remove deprecation
@Deprecated("Can potentially slow down gradle (e.g. configuration). Use only when actually needed.")
fun Gradle.logSomeEventsToFile(
  file: Path,
  system: FileSystem = FileSystem.SYSTEM,
  filter: (GradleEvt) -> Boolean = { true },
) {
  println("Logging some GradleEvts to file: $file")
  val sink = system.sink(file).buffer()
  lateinit var listener: GradleListener
  listener = GradleListener {
    if (filter(it)) {
      val now = Date()
      sink.writeUtf8("[$now] $it\n")
      sink.flush()
    }
    if (it is BuildFinished) {
      removeListener(listener)
      sink.close()
      // val proj = it.result.gradle?.rootProject?.name // This is problematic: https://github.com/gradle/gradle/issues/16532
      val act = it.result.action
      val fail = it.result.failure
      println("Logging some GradleEvts to $file finished. (act: $act; fail?: $fail)")
    }
  }
  addListener(listener)
}
