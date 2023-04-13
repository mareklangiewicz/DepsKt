package pl.mareklangiewicz.maintenance

import io.github.typesafegithub.workflows.actions.actions.CheckoutV3
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV3
import io.github.typesafegithub.workflows.actions.gradle.GradleBuildActionV2
import io.github.typesafegithub.workflows.domain.JobOutputs
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.JobBuilder
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow

private val myFork = expr { "${github.repository_owner} == 'langara'" }

private val myEnv = listOf(
    "signing_keyId", "signing_password", "signing_key",
    "ossrhUsername", "ossrhPassword", "sonatypeStagingProfileId"
)
    .map { "MYKOTLIBS_$it" }
    .associateWith { expr("secrets.$it") } as LinkedHashMap<String, String>


internal val MyWorkflowDNames = listOf("dbuild", "drelease")

/**
 * @dname name of both: workflow, and file name in .github/workflows (without .yml extension)
 * hacky "d" prefix in all recognized names is mostly to avoid clashing with other workflows.
 * (if I add it to existing repos/forks) (and it means "default")
 */
internal fun defaultWorkflow(dname: String) = when (dname) {
    "dbuild" -> defaultBuildWorkflow()
    "drelease" -> defaultReleaseWorkflow()
    else -> error("Unknown default workflow dname: $dname")
}

private fun defaultBuildWorkflow(runners: List<RunnerType> = listOf(RunnerType.UbuntuLatest)) =
    workflow(
        name = "dbuild",
        on = listOf(
            Push(branches = listOf("master", "main")),
            PullRequest(),
        ),
        env = myEnv,
    ) {
        runners.forEach { runnerType ->
            job(
                id = "build-for-${runnerType::class.simpleName}",
                runsOn = runnerType,
            ) {
                uses(CheckoutV3())
                usesJdk()
                usesGradleBuild()
            }
        }
    }

private fun defaultReleaseWorkflow() =
    workflow(
        name = "drelease",
        on = listOf(Push(tags = listOf("v*.*.*"))),
        env = myEnv,
    ) {
        job(
            id = "release",
            runsOn = RunnerType.UbuntuLatest,
        ) {
            uses(CheckoutV3())
            usesJdk()
            usesGradleBuild()
            uses(
                name = "Publish to Sonatype",
                action = GradleBuildActionV2(
                    arguments = "publishToSonatype closeAndReleaseSonatypeStagingRepository",
                )
            )
            // TODO_someday: something like
            // github-workflows-kt/.github/workflows/release.main.kts#L49
            // github-workflows-kt/buildSrc/src/main/kotlin/buildsrc/tasks/AwaitMavenCentralDeployTask.kt
        }
    }

private fun JobBuilder<JobOutputs.EMPTY>.usesJdk(
    version: String? = "17", // fixme_maybe: take from DepsNew.ver...? [Deps Selected]
    distribution: SetupJavaV3.Distribution = SetupJavaV3.Distribution.Zulu, // fixme_later: which dist?
) = uses(
        name = "Set up JDK",
        action = SetupJavaV3(
            javaVersion = version,
            distribution = distribution
        )
    )

private fun JobBuilder<JobOutputs.EMPTY>.usesGradleBuild() =
    uses(name = "Build", action = GradleBuildActionV2(arguments = "build"))

