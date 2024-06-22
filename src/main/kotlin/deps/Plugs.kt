@file:Suppress("unused", "MemberVisibilityCanBePrivate", "PackageDirectoryMismatch")

package pl.mareklangiewicz.deps

import vers

object Plugs {

  /**
   * Gradle-Nexus Publish Plugin (turnkey plugin for publishing libs to maven central / sonatype)
   * - [github](https://github.com/gradle-nexus/publish-plugin/)
   * - [github releases](https://github.com/gradle-nexus/publish-plugin/releases)
   */
  val NexusPublish = DepP("io.github.gradle-nexus.publish-plugin", vers.NexusPublishPlug)

  val KotlinMulti = DepP("org.jetbrains.kotlin.multiplatform", vers.Kotlin)

  /** https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compiler.html#migrating-a-compose-multiplatform-project */
  val KotlinMultiCompose = DepP("org.jetbrains.kotlin.plugin.compose", vers.Kotlin)

  val KotlinJvm = DepP("org.jetbrains.kotlin.jvm", vers.Kotlin)

  val KotlinJs = DepP("org.jetbrains.kotlin.js", vers.Kotlin)

  val KotlinAndro = DepP("org.jetbrains.kotlin.android", vers.Kotlin)

  val MavenPublish = DepP("maven-publish")

  val Signing = DepP("signing")


  /**
   * Gradle Publish Plugin (gradle plugin for publishing gradle plugins)
   * - [plugins gradle org](https://plugins.gradle.org/plugin/com.gradle.plugin-publish)
   * - [plugins gradle org docs](https://plugins.gradle.org/docs/publish-plugin)
   */
  val GradlePublish = DepP("com.gradle.plugin-publish", vers.GradlePublishPlug)

  /**
   * Gradle Develocity Plugin (enables integration with Gradle Develocity and scans.gradle.com)
   * - [gradle org docs](https://docs.gradle.com/develocity/gradle-plugin/)
   * - [gradle portal](https://plugins.gradle.org/plugin/com.gradle.develocity)
   */
  val GradleDevelocity = DepP("com.gradle.develocity", vers.GradleDevelocityPlug)

  /**
   * The builtin Gradle plugin implemented by [org.gradle.api.plugins.ApplicationPlugin].
   *
   * @see org.gradle.api.plugins.ApplicationPlugin
   */
  val JvmApp = DepP("org.gradle.application")

  /**
   * Android Gradle Plugin
   * - [maven](https://maven.google.com/web/index.html#com.android.tools.build:gradle)
   * - [releases](https://developer.android.com/studio/releases/gradle-plugin)
   * - [andro gradle dsl](https://google.github.io/android-gradle-dsl/)
   */
  val AndroLibNoVer = DepP("com.android.library") // needed because .withNoVer() doesn't work in plugins {..}
  val AndroLibStable = AndroLibNoVer.withVers(vers.AndroPlugStable)
  val AndroLibEdge = AndroLibNoVer.withVers(vers.AndroPlugEdge)
  val AndroLib = AndroLibStable

  /**
   * Android Gradle Plugin
   * - [maven](https://maven.google.com/web/index.html#com.android.tools.build:gradle)
   * - [releases](https://developer.android.com/studio/releases/gradle-plugin)
   * - [andro gradle dsl](https://google.github.io/android-gradle-dsl/)
   */
  val AndroAppNoVer = DepP("com.android.application") // needed because .withNoVer() doesn't work in plugins {..}
  val AndroAppStable = AndroAppNoVer.withVers(vers.AndroPlugStable)
  val AndroAppEdge = AndroAppNoVer.withVers(vers.AndroPlugEdge)
  val AndroApp = AndroAppStable

  /**
   * Compose Multiplatform Gradle Plugin
   * [gradle portal](https://plugins.gradle.org/plugin/org.jetbrains.compose)
   * [github](https://github.com/JetBrains/compose-multiplatform/releases)
   * [maven runtime](https://maven.pkg.jetbrains.space/public/p/compose/dev/org/jetbrains/compose/runtime/)
   * [maven runtime](https://maven.pkg.jetbrains.space/public/p/compose/dev/org/jetbrains/compose/runtime/runtime/)
   * [maven ui-js](https://maven.pkg.jetbrains.space/public/p/compose/dev/org/jetbrains/compose/ui/ui-js/)
   */
  val ComposeJb = Org.JetBrains.Compose.gradle_plugin.withVer(vers.ComposeJb)
  val ComposeJbNoVer = ComposeJb.withNoVer() // needed because .withNoVer() doesn't work in plugins {..}
  val ComposeJbStable = ComposeJb.withVer(vers.ComposeJbStable)
  val ComposeJbEdge = ComposeJb.withVer(vers.ComposeJbEdge)


  /**
   * Dokka Gradle Plugin
   * - [gradle portal](https://plugins.gradle.org/plugin/org.jetbrains.dokka)
   * - [github](https://github.com/Kotlin/dokka)
   * - [github releases](https://github.com/Kotlin/dokka/releases)
   */
  val Dokka = DepP("org.jetbrains.dokka", vers.DokkaPlug)

  /**
   * Ktor Gradle Plugin
   * [github](https://github.com/ktorio/ktor-build-plugins)
   */
  val Ktor = DepP("io.ktor.plugin", vers.KtorPlug)

  /**
   * Kotlin Jupyter Gradle Plugin
   * [gradle portal](https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jupyter.api)
   * [github](https://github.com/Kotlin/kotlin-jupyter)
   */
  val KotlinJupyter = DepP("org.jetbrains.kotlin.jupyter.api", vers.KotlinJupyterPlug)

  /**
   * Gradle Shadow Gradle Plugin
   * - [gradle portal](https://github.com/johnrengelman/shadow)
   * - [github john rengelman shadow](https://github.com/johnrengelman/shadow)
   * - [docs](https://github.com/johnrengelman/shadow)
   */
  val GradleShadow = DepP("com.github.johnrengelman.shadow", vers.GradleShadowPlug)

  /**
   * Osacky Doctor Gradle Plugin
   * - [gradle portal](https://plugins.gradle.org/plugin/com.osacky.doctor)
   * - [github](https://github.com/runningcode/gradle-doctor)
   * - [docs](https://runningcode.github.io/gradle-doctor/)
   */
  val Doctor = DepP("com.osacky.doctor", vers.OsackyDoctorPlug)

  /**
   * DepsKt Gradle Plugin
   * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
   * - [github](https://github.com/mareklangiewicz/DepsKt)
   */
  val Deps = DepP("pl.mareklangiewicz.deps", vers.DepsPlug)

  /**
   * DepsKt Gradle Settings Plugin
   * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
   * - [github](https://github.com/mareklangiewicz/DepsKt)
   */
  val DepsSettings = DepP("pl.mareklangiewicz.deps.settings", vers.DepsPlug)

  /**
   * SourceFun Gradle Plugin
   * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
   * - [github](https://github.com/mareklangiewicz/DepsKt)
   */
  val SourceFun = DepP("pl.mareklangiewicz.sourcefun", vers.DepsPlug)
}
