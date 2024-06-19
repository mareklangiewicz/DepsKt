@file:Suppress("unused", "ConstPropertyName", "PackageDirectoryMismatch")

package pl.mareklangiewicz.deps

import org.gradle.api.artifacts.*


fun ConfigurationContainer.checkVerSync(warnOnly: Boolean = false) = configureEach { it.checkVerSync(warnOnly) }
fun Configuration.checkVerSync(warnOnly: Boolean = false) {
  withDependencies { it.checkVerSync(warnOnly) }
}

fun DependencySet.checkVerSync(warnOnly: Boolean = false) = configureEach { it.checkVerSync(warnOnly) }
fun Dependency.checkVerSync(warnOnly: Boolean = false) {
  when (group) {
    "org.jetbrains.kotlin" -> checkWith(Vers.Kotlin, warnOnly)
    "androidx.compose.ui", "androidx.compose.animation", "androidx.compose.foundation",
    "androidx.compose.material",
    -> checkWith(Vers.ComposeAndro, warnOnly)

    "androidx.compose.runtime" -> when (name) {
      "runtime-livedata", "runtime", "runtime-rxjava2", "runtime-rxjava3",
      "runtime-saveable",
      -> checkWith(Vers.ComposeAndro, warnOnly)
    }
  }
}

private fun Dependency.checkWith(expectedVer: Ver, warnOnly: Boolean) {
  if (version != expectedVer.str) {
    val msg = "Dependency $group:$name:$version not synced with $expectedVer"
    if (warnOnly) println("WARNING: $msg") else error(msg)
    // TODO_someday: better warning? But we don't have access to gradle logger here?
  }
}

object Vers {

  /** [releases github](https://github.com/JetBrains/kotlin/releases) */
  val Kotlin20 = Org.JetBrains.Kotlin.stdlib.verLast
  val Kotlin19 = Ver("1.9.24")
  val Kotlin = Kotlin20


  // https://github.com/JetBrains/compose-multiplatform/releases
  val ComposeJbEdge = Org.JetBrains.Compose.gradle_plugin.verLast
  val ComposeJbStable = Org.JetBrains.Compose.gradle_plugin.verLastStable

  val ComposeJb = ComposeJbEdge


  /** Selected ComposeAndroid version. Should always be kept compatible with the selected Kotlin version. */
  val ComposeAndro = AndroidX.Compose.Runtime.runtime.verLast

  /**
   * Gradle-Nexus Publish Plugin (turnkey plugin for publishing libs to maven central / sonatype)
   * - [github](https://github.com/gradle-nexus/publish-plugin/)
   * - [github releases](https://github.com/gradle-nexus/publish-plugin/releases)
   */
  val NexusPublishPlug = Ver("2.0.0")

  /**
   * Gradle Publish Plugin (gradle plugin for publishing gradle plugins)
   * - [plugins gradle org](https://plugins.gradle.org/plugin/com.gradle.plugin-publish)
   * - [plugins gradle org docs](https://plugins.gradle.org/docs/publish-plugin)
   */
  val GradlePublishPlug = Ver("1.2.1")

  /**
   * Gradle Develocity Plugin (enables integration with Gradle Develocity and scans.gradle.com)
   * - [gradle org docs](https://docs.gradle.com/develocity/gradle-plugin/)
   * - [gradle portal](https://plugins.gradle.org/plugin/com.gradle.develocity)
   */
  val GradleDevelocityPlug = Ver("3.17.5")


  /**
   * Android Gradle Plugin
   * - [andro gradle api releases](https://developer.android.com/reference/tools/gradle-api)
   * - [maven](https://maven.google.com/web/index.html#com.android.tools.build:gradle)
   * - [releases](https://developer.android.com/studio/releases/gradle-plugin)
   */
  val AndroPlugEdge = Com.Android.Tools.Build.gradle.verLast
  val AndroPlugStable = Com.Android.Tools.Build.gradle.verLastStable
  val AndroPlug = AndroPlugEdge

  /**
   * Dokka Gradle Plugin
   * - [gradle portal](https://plugins.gradle.org/plugin/org.jetbrains.dokka)
   * - [github](https://github.com/Kotlin/dokka)
   * - [github releases](https://github.com/Kotlin/dokka/releases)
   */
  val DokkaPlug = Ver("1.9.20")


  /**
   * Ktor Gradle Plugin
   * [github](https://github.com/ktorio/ktor-build-plugins)
   */
  val KtorPlug = Io.Ktor.server.verLast // version will probably always be synced with the server version.

  /**
   * Kotlin Jupyter Gradle Plugin
   * [gradle portal](https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jupyter.api)
   * [github](https://github.com/Kotlin/kotlin-jupyter)
   */
  val KotlinJupyterPlug = Ver("0.12.0-236")

  /**
   * Gradle Shadow Gradle Plugin
   * - [gradle portal](https://github.com/johnrengelman/shadow)
   * - [github john rengelman shadow](https://github.com/johnrengelman/shadow)
   * - [docs](https://github.com/johnrengelman/shadow)
   */
  val GradleShadowPlug = Ver("8.1.1")

  /**
   * Osacky Doctor Gradle Plugin
   * - [gradle portal](https://plugins.gradle.org/plugin/com.osacky.doctor)
   * - [github](https://github.com/runningcode/gradle-doctor)
   * - [docs](https://runningcode.github.io/gradle-doctor/)
   */
  val OsackyDoctorPlug = Ver("0.10.0")

  /**
   * DepsKt Gradle Plugin
   * - [github](https://github.com/mareklangiewicz/DepsKt)
   * - [plugins gradle deps](https://plugins.gradle.org/plugin/pl.mareklangiewicz.deps)
   * - [plugins gradle deps settings](https://plugins.gradle.org/plugin/pl.mareklangiewicz.deps.settings)
   * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
   */
  val DepsPlug = Ver(0, 3, 21) // TODO make sure it's always synced with myLibDetails(version)

  /**
   * SourceFun Gradle Plugin
   * - [github](https://github.com/mareklangiewicz/SourceFun)
   * - [plugins gradle sourcefun](https://plugins.gradle.org/plugin/pl.mareklangiewicz.sourcefun)
   * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
   */
  val SourceFunPlug = Ver("0.4.09")

  const val JvmDefaultVer = "21"


  val Gradle5 = Ver("5.6.4")
  val Gradle6 = Ver("6.8.3")
  val Gradle7 = Ver("7.6.4")
  val Gradle8 = Ver("8.8")

  /**
   * Gradle - just a reference - not so useful in typical use cases
   * - [gradle releases](https://gradle.org/releases/)
   * - [gradle versions](https://services.gradle.org/versions)
   * - [gradle versions current](https://services.gradle.org/versions/current)
   * - [gradle versions rel candidate](https://services.gradle.org/versions/release-candidate)
   */
  val Gradle = Gradle8


  /**
   * - [dashboards](https://developer.android.com/about/dashboards/index.html)
   * - [build numbers](https://source.android.com/setup/start/build-numbers)
   */
  const val AndroSdkMin = 26
  const val AndroSdkCompile = 34
  const val AndroSdkTarget = 34

  /**
   * This runner looks like working correctly these years (with gradle unified test platform).
   * I had many crazy issues with instrumented testing, so better not to change too much.
   * Make sure gradle.properties DOESN'T change this to false: android.experimental.androidTest.useUnifiedTestPlatform=true
   * [andro testing docs](https://developer.android.com/training/testing/instrumented-tests#set-testing)
   */
  const val AndroTestRunner = "androidx.test.runner.AndroidJUnitRunner"

  /**
   * - [releases](https://developer.android.com/tools/releases/build-tools)
   */
  @Deprecated("Deprecated with android gradle plugin 3.0.0 or higher")
  val AndroBuildTools = Ver("34.0.0")

  /**
   * - [revisions](https://developer.android.com/topic/libraries/support-library/revisions.html)
   */
  @Deprecated("Use androidx")
  val AndroSupportLibrary = Ver("28.0.0")

}
