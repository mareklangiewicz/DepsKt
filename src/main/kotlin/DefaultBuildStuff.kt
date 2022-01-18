import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.ScriptHandlerScope
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories

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


fun ScriptHandlerScope.defaultAndroBuildScript() {
    repositories {
        defaultRepositories(withGradle = true)
    }
    dependencies {
        defaultAndroBuildScriptDeps()
    }
}


fun DependencyHandler.defaultAndroBuildScriptDeps(
) {
    add("classpath", Deps.kotlinGradlePlugin)
    add("classpath", Deps.androidGradlePlugin)
}

fun defaultVerName(major: Int = 0, minor: Int = 0, patch: Int = 1, patchLen: Int = 2) =
    "$major.$minor." + patch.toString().padStart(patchLen, '0')

