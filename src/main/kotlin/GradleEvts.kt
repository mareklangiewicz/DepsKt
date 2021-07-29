@file:OptIn(ExperimentalFileSystem::class)

import GradleEvt.BuildEvt.BeforeSettings
import GradleEvt.BuildEvt.BuildFinished
import GradleEvt.BuildEvt.ProjectsEvaluated
import GradleEvt.BuildEvt.ProjectsLoaded
import GradleEvt.BuildEvt.SettingsEvaluated
import GradleEvt.DependencyResolutionEvt.AfterResolve
import GradleEvt.DependencyResolutionEvt.BeforeResolve
import GradleEvt.ProjectEvaluationEvt.AfterEvaluate
import GradleEvt.ProjectEvaluationEvt.BeforeEvaluate
import GradleEvt.StdOutEvt.OnOutput
import GradleEvt.TaskActionEvt.AfterActions
import GradleEvt.TaskActionEvt.BeforeActions
import GradleEvt.TaskExecutionEvt.AfterExecute
import GradleEvt.TaskExecutionEvt.BeforeExecute
import GradleEvt.TaskExecutionGraphEvt.GraphPopulated
import GradleEvt.TestEvt.AfterSuite
import GradleEvt.TestEvt.AfterTest
import GradleEvt.TestEvt.BeforeSuite
import GradleEvt.TestEvt.BeforeTest
import GradleEvt.TestOutEvt.OnTestOutput
import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.Path
import okio.buffer
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.Task
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.execution.TaskActionListener
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.execution.TaskExecutionGraphListener
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.StandardOutputListener
import org.gradle.api.tasks.TaskState
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestOutputEvent
import org.gradle.api.tasks.testing.TestOutputListener
import org.gradle.api.tasks.testing.TestResult
import java.util.Date

sealed class GradleEvt {
    sealed class BuildEvt : GradleEvt() {
        data class BeforeSettings(val settings: Settings): BuildEvt()
        data class BuildFinished(val result: BuildResult): BuildEvt()
        data class ProjectsEvaluated(val gradle: Gradle): BuildEvt()
        data class ProjectsLoaded(val gradle: Gradle): BuildEvt()
        data class SettingsEvaluated(val settings: Settings): BuildEvt()
    }
    sealed class ProjectEvaluationEvt : GradleEvt() {
        data class BeforeEvaluate(val project: Project): ProjectEvaluationEvt()
        data class AfterEvaluate(val project: Project, val state: ProjectState): ProjectEvaluationEvt()
    }
    sealed class TaskExecutionEvt : GradleEvt() {
        data class BeforeExecute(val task: Task): TaskExecutionEvt()
        data class AfterExecute(val task: Task, val state: TaskState): TaskExecutionEvt()
    }
    sealed class TaskExecutionGraphEvt : GradleEvt() {
        data class GraphPopulated(val graph: TaskExecutionGraph): TaskExecutionGraphEvt()
    }
    sealed class TaskActionEvt : GradleEvt() {
        data class BeforeActions(val task: Task): TaskActionEvt()
        data class AfterActions(val task: Task): TaskActionEvt()
    }
    sealed class StdOutEvt : GradleEvt() {
        data class OnOutput(val output: CharSequence?): StdOutEvt()
    }
    sealed class TestEvt : GradleEvt() {
        data class BeforeSuite(val suite: TestDescriptor?): TestEvt()
        data class AfterSuite(val suite: TestDescriptor?, val result: TestResult?): TestEvt()
        data class BeforeTest(val descriptor: TestDescriptor?): TestEvt()
        data class AfterTest(val descriptor: TestDescriptor?, val result: TestResult?): TestEvt()
    }
    sealed class TestOutEvt : GradleEvt() {
        data class OnTestOutput(val descriptor: TestDescriptor?, val output: TestOutputEvent?): TestOutEvt()
    }
    sealed class DependencyResolutionEvt : GradleEvt() {
        data class BeforeResolve(val dependencies: ResolvableDependencies): DependencyResolutionEvt()
        data class AfterResolve(val dependencies: ResolvableDependencies): DependencyResolutionEvt()
    }
}

class GradleListener(val pushee: (GradleEvt) -> Unit) : BuildListener, ProjectEvaluationListener,
    TaskExecutionListener, TaskExecutionGraphListener, TaskActionListener, StandardOutputListener, TestListener,
    TestOutputListener, DependencyResolutionListener {

    override fun beforeSettings(settings: Settings) = pushee(BeforeSettings(settings))
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

    override fun onOutput(descriptor: TestDescriptor?, output: TestOutputEvent?) = pushee(OnTestOutput(descriptor, output))

    override fun beforeResolve(dependencies: ResolvableDependencies) = pushee(BeforeResolve(dependencies))
    override fun afterResolve(dependencies: ResolvableDependencies) = pushee(AfterResolve(dependencies))
}

fun Gradle.logSomeEventsToFile(
    file: Path,
    system: FileSystem = FileSystem.SYSTEM,
    filter: (GradleEvt) -> Boolean = { true }
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
            val proj = it.result.gradle?.rootProject?.name
            val act = it.result.action
            val fail = it.result.failure
            println("Build finished. (root: $proj; act: $act; fail?: $fail)")
        }
    }
    addListener(listener)
}
