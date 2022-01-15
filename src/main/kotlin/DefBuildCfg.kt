import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.*

fun ScriptHandlerScope.defaultAndroBuildScript() {
    repositories {
        defaultRepositories(withGradle = true)
    }
    dependencies {
        classpath(Deps.kotlinGradlePlugin)
        classpath(Deps.androidGradlePlugin)
    }
}

fun RepositoryHandler.defaultRepositories(
    withGradle: Boolean = false,
    withGoogle: Boolean = true,
    withMavenCentral: Boolean = true,
    withKotlinX: Boolean = true,
    withJitpack: Boolean = true,
) {
    if (withGradle) gradlePluginPortal()
    if (withGoogle) google()
    if (withMavenCentral) mavenCentral()
    if (withKotlinX) maven(url = "https://kotlin.bintray.com/kotlinx")
    if (withJitpack) maven(Repos.jitpack)
}