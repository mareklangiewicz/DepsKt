@file:Suppress("unused")

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
    AndroidX.Compose.Compiler.compiler.group -> checkWith(Vers.ComposeCompilerAx, warnOnly)
    Org.JetBrains.Compose.Compiler.compiler.group -> checkWith(Vers.ComposeCompilerJb, warnOnly)
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
  if (version != expectedVer.ver) {
    val msg = "Dependency $group:$name:$version not synced with $expectedVer"
    if (warnOnly) println("WARNING: $msg") else error(msg)
    // TODO_someday: better warning? But we don't have access to gradle logger here?
  }
}

object Vers {

  /**
   * Manually selected kotlin version. Have to be working with current compose multiplatform and compose andro.
   * - [compose kotlin compatibility](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compatibility-and-versioning.html#kotlin-compatibility)
   * - [releases github](https://github.com/JetBrains/kotlin/releases)
   * - [compiler Ax dev repo table](https://androidx.dev/storage/compose-compiler/repository)
   * - [compiler Jb space maven](https://maven.pkg.jetbrains.space/public/p/compose/dev/org/jetbrains/compose/compiler/compiler/)
   */
  val Kotlin20 = Ver("2.0.0")
  val Kotlin19 = Ver("1.9.24")
  val Kotlin = Kotlin20


  // Compose compilers build by Google (AndroidX "Ax", aka Jetpack Compiler)

  val ComposeCompilerAxStable = AndroidX.Compose.Compiler.compiler.verStable
  val ComposeCompilerAxFor1920 = Ver("1.5.4-dev-k1.9.20-50f08dfa4b4") // this ver is prepared for 1.9.20
  val ComposeCompilerAxFor1921 = Ver("1.5.6-dev-k1.9.21-3eed341308a") // this ver is prepared for 1.9.21
  val ComposeCompilerAxFor1924 = Ver("1.5.14-dev-k1.9.24-50022def4af") // this ver is prepared for 1.9.24
  val ComposeCompilerAxFor200B1 = Ver("1.5.6-dev-k2.0.0-Beta1-06a03be2b42") // this ver is prepared for 2.0.0-Beta1
  val ComposeCompilerAxFor200B2 = Ver("1.5.8-dev-k2.0.0-Beta2-99ed868a0f8") // this ver is prepared for 2.0.0-Beta2
  val ComposeCompilerAxFor200B3 = Ver("1.5.9-dev-k2.0.0-Beta3-7c5ec6895a0") // this ver is prepared for 2.0.0-Beta3
  val ComposeCompilerAxFor200B4 = Ver("1.5.11-dev-k2.0.0-Beta4-21f5e479a96") // this ver is prepared for 2.0.0-Beta4
  val ComposeCompilerAxFor200B5 = Ver("1.5.11-dev-k2.0.0-Beta5-b5a216d0ac6") // this ver is prepared for 2.0.0-Beta5
  val ComposeCompilerAxFor200RC1 = Ver("1.5.13-dev-k2.0.0-RC1-50f08dfa4b4"	) // this ver is prepared for 2.0.0-RC1

  /** Selected Compose Compiler version. Should always be kept compatible with the selected Kotlin version. */
  @Deprecated("Usually it's better to let compose plugin (mpp or andro) select default compose compiler.")
  val ComposeCompilerAx = ComposeCompilerAxFor200RC1

  // Compose compilers built by JetBrains ("Jb")

  // https://maven.pkg.jetbrains.space/public/p/compose/dev/org/jetbrains/compose/compiler/compiler/
  val ComposeCompilerJbStable = Org.JetBrains.Compose.Compiler.compiler.verStable
  val ComposeCompilerJbFor1922 = Ver("1.5.8-beta01")
  val ComposeCompilerJbFor200B1 = Ver("1.5.4-dev1-kt2.0.0-Beta1")
  val ComposeCompilerJbFor200B2 = Ver("1.5.6-dev1-kt2.0.0-Beta2")
  val ComposeCompilerJbFor200B3 = Ver("1.5.6-dev2-kt2.0.0-Beta3")
  val ComposeCompilerJbFor200B4 = Ver("1.5.9-kt-2.0.0-Beta4")
  val ComposeCompilerJbFor200RC1 = Ver("1.5.11-kt-2.0.0-RC1")
  val ComposeCompilerJbFor200 = Ver("1.5.14")

  @Deprecated("Usually it's better to let compose plugin (mpp or andro) select default compose compiler.")
  val ComposeCompilerJb = ComposeCompilerJbFor200


  // https://github.com/JetBrains/compose-multiplatform/releases
  val ComposeEdge = Ver("1.6.10-dev1646")

  /** Selected ComposeMultiplatform version. Should always be kept compatible with the selected Kotlin version. */
  // val Compose = Org.JetBrains.Compose.gradle_plugin.ver!!
  val Compose = ComposeEdge


  /** Selected ComposeAndroid version. Should always be kept compatible with the selected Kotlin version. */
  val ComposeAndro = AndroidX.Compose.Runtime.runtime.ver!!

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
  val GradleDevelocityPlug = Ver("3.17.4")

  @Deprecated("Use GradleDevelocityPlug")
  val GradleEnterprisePlug = GradleDevelocityPlug


  /**
   * Android Gradle Plugin
   * - [andro gradle api releases](https://developer.android.com/reference/tools/gradle-api)
   * - [maven](https://maven.google.com/web/index.html#com.android.tools.build:gradle)
   * - [releases](https://developer.android.com/studio/releases/gradle-plugin)
   */
  val AndroPlugStable = Ver("8.5.0-beta02")
  val AndroPlugEdge = Ver("8.6.0-alpha02")
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
  val KtorPlug = Io.Ktor.server.ver!! // version will probably always be synced with the server version.

  /**
   * Kotlin Jupyter Gradle Plugin
   * [gradle portal](https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jupyter.api)
   * [github](https://github.com/Kotlin/kotlin-jupyter)
   */
  val KotlinJupyterPlug = Ver("0.12.0-226")

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
  val DepsPlug = Ver("0.3.13")

  /**
   * SourceFun Gradle Plugin
   * - [github](https://github.com/mareklangiewicz/SourceFun)
   * - [plugins gradle sourcefun](https://plugins.gradle.org/plugin/pl.mareklangiewicz.sourcefun)
   * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
   */
  val SourceFunPlug = Ver("0.4.07")

  val JvmDefaultVer = "21"


  val Gradle5 = Ver("5.6.4")
  val Gradle6 = Ver("6.8.3")
  val Gradle7 = Ver("7.6.4")
  val Gradle8 = Ver("8.7")

  /**
   * Gradle - just a reference - not so useful in typical usecases
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
  val AndroSdkMin = 26
  val AndroSdkCompile = 34
  val AndroSdkTarget = 34

  /**
   * This runner looks like working correctly these years (with "gradle unified test platform).
   * I had many crazy issues with instrumented testing, so better not to change too much.
   * Make sure gradle.properties DOESN'T change this to false: android.experimental.androidTest.useUnifiedTestPlatform=true
   * [andro testing docs](https://developer.android.com/training/testing/instrumented-tests#set-testing)
   */
  val AndroTestRunner = "androidx.test.runner.AndroidJUnitRunner"

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
