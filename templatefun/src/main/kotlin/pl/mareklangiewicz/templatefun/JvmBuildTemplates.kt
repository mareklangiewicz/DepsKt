package pl.mareklangiewicz.templatefun

import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.dsl.*
import org.gradle.api.plugins.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.gradle.kotlin.dsl.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.defaults.*

// region [[JVM Module Build Template]]

/**
 * Only for very standard small jvm libs. In most cases it's better to not use this function.
 *
 * These ignoreXXX flags are hacky, but needed. see [jvmOnlyDefault] kdoc for details.
 */
fun Project.defaultBuildTemplateForBasicJvmLib(
  lib: Lib = gradle.extLib,
  ignoreCompose: Boolean = false, // so user have to explicitly say THAT he wants to ignore compose settings here.
  ignoreAndroTarget: Boolean = false, // so user have to explicitly say THAT he wants to ignore android target.
  addJvmDependencies: DependencyHandlerScope.() -> Unit = {},
): Unit = context(lib.info, lib.flags) {
  require(ignoreCompose || lib.compose == null) { "defaultBuildTemplateForBasicJvmLib can NOT configure compose stuff" }
  require(ignoreAndroTarget || lib.andro == null) { "defaultBuildTemplateForBasicJvmLib can NOT configure android target" }
  repositories { context(lib.repos) { addRepos() } }
  defaultGroupAndVerAndDescription(lib)
  extensions.configure<KotlinJvmProjectExtension> {
    // The whole opt-out: the jvm/testing flags are in scope and NOTHING else is. There is no
    // ignoreCompose/ignoreAndroTarget to forward any more, because there is nothing to ignore —
    // `compose` and `andro` are not reachable from [LibFlags] at all.
    jvmOnlyDefault(addJvmDependencies = addJvmDependencies)
  }
  configurations.checkVerSync(warnOnly = true)
  tasks.defaultKotlinCompileOptions(jvmTargetVer = null) // jvmVer is set in fun jvmDefault using jvmToolchain
  tasks.defaultTestsOptions(onJvmUseJUnitPlatform = lib.flags.withTestJUnit5)
  if (plugins.hasPlugin("com.vanniktech.maven.publish")) defaultPublishing()
  else println("JVM Module ${name}: publishing (and signing) disabled")
}


/**
 * Only for very standard small jvm libs. In most cases it's better to not use this function.
 *
 * MIGRATED to the sibling model ([LibFlags]) — the first entry point to move. Both
 * `ignoreCompose` and `ignoreAndroTarget`, and both `require`s they guarded, are GONE:
 *
 * ```
 * require(ignoreCompose || compose == null) { "jvmOnlyDefault can NOT configure compose stuff" }
 * require(ignoreAndroTarget || settings.andro == null) { "jvmOnlyDefault can NOT configure android target" }
 * ```
 *
 * The old kdoc called the flags "hacky, but needed because we want to inject this code also to such
 * build files, where plugins for compose and/or android are not applied at all". That need came
 * entirely from NESTING: a caller holding `LibSettings` could not hand over the jvm flags without
 * also handing over `compose` and `andro`, so the only way to say "those do not apply here" was a
 * boolean plus a runtime check. As a sibling, [LibFlags] carries no `compose` and no `andro`
 * at all — this function cannot configure them because it cannot NAME them, and the caller opts out
 * by simply not opening those scopes. A runtime `require` became the absence of a parameter.
 */
context(flags: LibFlags)
fun KotlinJvmProjectExtension.jvmOnlyDefault(
  addJvmDependencies: DependencyHandlerScope.() -> Unit = {},
) = with(flags) {
  withJvmVer?.let { jvmToolchain(it.toInt()) } // works for jvm and android
  project.dependencies.apply {
    val scope = DependencyHandlerScope.of(this)
    if (withKotlinxHtml) add("implementation", KotlinX.html)
    add("testImplementation", Kotlin.test)
    if (withTestUSpekX) add("testImplementation", Langiewicz.uspekx)
    if (withTestJUnit4) add("testImplementation", JUnit.junit)
    if (withTestJUnit5) {
      add("testImplementation", Org.JUnit.Jupiter.junit_jupiter_engine)
      add("testRuntimeOnly", Org.JUnit.Platform.junit_platform_launcher)
    }
    if (withTestUSpekX) {
      add("testImplementation", Langiewicz.uspekx)
      if (withTestJUnit4) add("testImplementation", Langiewicz.uspekx_junit4)
      if (withTestJUnit5) add("testImplementation", Langiewicz.uspekx_junit5)
    }
    if (withTestGoogleTruth) add("testImplementation", Com.Google.Truth.truth)
    if (withTestMockitoKotlin) add("testImplementation", Org.Mockito.Kotlin.mockito_kotlin)
    scope.addJvmDependencies()
  }
}

// endregion [[JVM Module Build Template]]

// region [[JVM App Build Template]]

fun Project.defaultBuildTemplateForBasicJvmApp(
  lib: Lib = gradle.extLib,
  ignoreCompose: Boolean = false, // so user have to explicitly say THAT he wants to ignore compose settings here.
  ignoreAndroTarget: Boolean = false, // so user have to explicitly say THAT he wants to ignore android target.
  addJvmDependencies: DependencyHandlerScope.() -> Unit = {},
): Unit = context(lib.info, lib.flags) {
  defaultBuildTemplateForBasicJvmLib(lib, ignoreCompose, ignoreAndroTarget, addJvmDependencies)
  extensions.configure<JavaApplication> {
    mainClass.set(lib.info.run { "$appMainPackage.$appMainClass" })
  }
  dropRelocatedComposeRuntimeStubs()
}

/**
 * TODO_later: delete this whole fun when the compose runtime relocation settles upstream.
 *
 * AndroidX Compose Runtime 1.9.0 (2025-08-13) announced:
 * "androidx.compose.runtime:runtime will now support additional platforms. Support for desktop, iOS
 * and native targets has been upstreamed from JetBrains' Compose Multiplatform project, and will be
 * released through Google Maven moving forward. No other Compose artifacts are affected."
 * [androidx release notes](https://developer.android.com/jetpack/androidx/releases/compose-runtime#1.9.0)
 *
 * As of Compose Multiplatform 1.12.0 the JetBrains side of that move is still published, but EMPTY:
 * `org.jetbrains.compose.runtime:runtime` and `:runtime-saveable` carry 0 class files, while the
 * androidx artifacts of the very same basename carry 742 and 22. Both families end up on
 * runtimeClasspath, and the `application` plugin's distribution tasks flatten everything into one
 * `lib/` dir -- where `runtime-saveable-desktop-1.12.0.jar` collides with itself and `distTar` fails
 * with "is a duplicate but no duplicate handling strategy has been set".
 *
 * Dropping the two empty stubs by name is deliberate, and better than a `duplicatesStrategy`:
 * - a duplicates policy resolves by COPY ORDERING, and would silently absorb the next collision too,
 *   including one where both jars actually have classes;
 * - these excludes become plain no-ops once upstream stops publishing the stubs, so this rots safely.
 *
 * Note "No other Compose artifacts are affected" is measurably still true: foundation, ui and
 * animation keep their classes under `org.jetbrains.compose`. Expect to revisit when they follow.
 * No upstream ticket tracks this particular collision (searched 2026-09-16); the androidx release
 * note above is the authoritative statement of the move.
 */
private fun Project.dropRelocatedComposeRuntimeStubs() {
  configurations.named("runtimeClasspath") {
    exclude(group = "org.jetbrains.compose.runtime", module = "runtime")
    exclude(group = "org.jetbrains.compose.runtime", module = "runtime-saveable")
  }
}


// endregion [[JVM App Build Template]]
