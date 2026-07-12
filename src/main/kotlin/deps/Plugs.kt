@file:Suppress("unused", "MemberVisibilityCanBePrivate", "PackageDirectoryMismatch")

package pl.mareklangiewicz.deps

import vers

object Plugs {

  /**
   * Gradle-Nexus Publish Plugin (turnkey plugin for publishing libs to maven central / sonatype)
   * - [github](https://github.com/gradle-nexus/publish-plugin/)
   * - [github releases](https://github.com/gradle-nexus/publish-plugin/releases)
   */
  val NexusPublishNoVer = DepP("io.github.gradle-nexus.publish-plugin")
  val NexusPublish = NexusPublishNoVer.withVer(vers.NexusPublishPlug)

  val KotlinMultiNoVer = DepP("org.jetbrains.kotlin.multiplatform")
  val KotlinMulti = KotlinMultiNoVer.withVer(vers.Kotlin)

  /** https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compiler.html#migrating-a-compose-multiplatform-project */
  val KotlinMultiComposeNoVer = DepP("org.jetbrains.kotlin.plugin.compose")
  val KotlinMultiCompose = KotlinMultiComposeNoVer.withVer(vers.Kotlin)

  val KotlinJvmNoVer = DepP("org.jetbrains.kotlin.jvm")
  val KotlinJvm = KotlinJvmNoVer.withVer(vers.Kotlin)

  val KotlinJsNoVer = DepP("org.jetbrains.kotlin.js")
  val KotlinJs = KotlinJsNoVer.withVer(vers.Kotlin)

  @Deprecated("https://developer.android.com/build/migrate-to-built-in-kotlin", ReplaceWith(""))
  val KotlinAndroNoVer = DepP("org.jetbrains.kotlin.android")
  @Deprecated("https://developer.android.com/build/migrate-to-built-in-kotlin", ReplaceWith(""))
  val KotlinAndro = KotlinAndroNoVer.withVer(vers.Kotlin)

  val MavenPublish = DepP("maven-publish")

  val Signing = DepP("signing")

  /**
   * Vannik Tech Gradle Maven Publish Plugin (recommended by JetBrains for publishing KMP libs)
   *
   * Gradle plugin that creates a publish task to automatically upload all of your Java,
   * Kotlin or Android libraries to any Maven instance.
   * - [jetbrains howto](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-publish-libraries.html)
   * - [github maven central docs](https://vanniktech.github.io/gradle-maven-publish-plugin/central/)
   * - [github](https://github.com/vanniktech/gradle-maven-publish-plugin)
   * - [github releases](https://github.com/vanniktech/gradle-maven-publish-plugin/releases)
   */
  val VannikPublishNoVer = DepP("com.vanniktech.maven.publish")
  val VannikPublish = VannikPublishNoVer.withVer(vers.VannikPublishPlug)

  /**
   * Vannik Tech Gradle Maven Publish Plugin Base (same as VannikPublish but without default config)
   */
  val VannikPublishBaseNoVer = DepP("com.vanniktech.maven.publish.base")
  val VannikPublishBase = VannikPublishBaseNoVer.withVer(vers.VannikPublishPlug)

  /**
   * Gradle Publish Plugin (gradle plugin for publishing gradle plugins)
   * - [plugins gradle org](https://plugins.gradle.org/plugin/com.gradle.plugin-publish)
   * - [plugins gradle org docs](https://plugins.gradle.org/docs/publish-plugin)
   */
  val GradlePublishNoVer = DepP("com.gradle.plugin-publish")
  val GradlePublish = GradlePublishNoVer.withVer(vers.GradlePublishPlug)

  /**
   * Gradle Develocity Plugin (enables integration with Gradle Develocity and scans.gradle.com)
   * - [gradle org docs](https://docs.gradle.com/develocity/gradle-plugin/)
   * - [gradle portal](https://plugins.gradle.org/plugin/com.gradle.develocity)
   */
  val GradleDevelocityNoVer = DepP("com.gradle.develocity")
  val GradleDevelocity = GradleDevelocityNoVer.withVer(vers.GradleDevelocityPlug)

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
  val AndroLibStable = AndroLibNoVer.withVer(vers.AndroPlugStable)
  val AndroLibEdge = AndroLibNoVer.withVer(vers.AndroPlugEdge)
  val AndroLib = AndroLibNoVer.withVer(vers.AndroPlug)

  val AndroKmpNoVer = DepP("com.android.kotlin.multiplatform.library")
  val AndroKmpStable = AndroKmpNoVer.withVer(vers.AndroPlugStable)
  val AndroKmpEdge = AndroKmpNoVer.withVer(vers.AndroPlugEdge)
  val AndroKmp = AndroKmpNoVer.withVer(vers.AndroPlug)

  /**
   * Android Gradle Plugin
   * - [maven](https://maven.google.com/web/index.html#com.android.tools.build:gradle)
   * - [releases](https://developer.android.com/studio/releases/gradle-plugin)
   * - [andro gradle dsl](https://google.github.io/android-gradle-dsl/)
   */
  val AndroAppNoVer = DepP("com.android.application") // needed because .withNoVer() doesn't work in plugins {..}
  val AndroAppStable = AndroAppNoVer.withVer(vers.AndroPlugStable)
  val AndroAppEdge = AndroAppNoVer.withVer(vers.AndroPlugEdge)
  val AndroApp = AndroAppNoVer.withVer(vers.AndroPlug)

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
  val DokkaNoVer = DepP("org.jetbrains.dokka")
  val Dokka = DokkaNoVer.withVer(vers.DokkaPlug)

  /**
   * Ktor Gradle Plugin
   * [github](https://github.com/ktorio/ktor-build-plugins)
   */
  val KtorNoVer = DepP("io.ktor.plugin")
  val Ktor = KtorNoVer.withVer(vers.KtorPlug)

  /**
   * Kotlin Jupyter Gradle Plugin
   * [gradle portal](https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jupyter.api)
   * [github](https://github.com/Kotlin/kotlin-jupyter)
   */
  val KotlinJupyterNoVer = DepP("org.jetbrains.kotlin.jupyter.api")
  val KotlinJupyter = KotlinJupyterNoVer.withVer(vers.KotlinJupyterPlug)

  /**
   * Gradle Shadow Gradle Plugin
   * - [gradle portal](https://github.com/johnrengelman/shadow)
   * - [github john rengelman shadow](https://github.com/johnrengelman/shadow)
   * - [docs](https://github.com/johnrengelman/shadow)
   */
  val GradleShadowNoVer = DepP("com.github.johnrengelman.shadow")
  val GradleShadow = GradleShadowNoVer.withVer(vers.GradleShadowPlug)

  /**
   * Osacky Doctor Gradle Plugin
   * - [gradle portal](https://plugins.gradle.org/plugin/com.osacky.doctor)
   * - [github](https://github.com/runningcode/gradle-doctor)
   * - [docs](https://runningcode.github.io/gradle-doctor/)
   */
  val DoctorNoVer = DepP("com.osacky.doctor")
  val Doctor = DoctorNoVer.withVer(vers.OsackyDoctorPlug)

  /**
   * DepsKt Gradle Plugin
   * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
   * - [github](https://github.com/mareklangiewicz/DepsKt)
   */
  val DepsNoVer = DepP("pl.mareklangiewicz.deps")
  val Deps = DepsNoVer.withVer(vers.DepsPlug)

  /**
   * DepsKt Gradle Settings Plugin
   * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
   * - [github](https://github.com/mareklangiewicz/DepsKt)
   */
  val DepsSettingsNoVer = DepP("pl.mareklangiewicz.deps.settings")
  val DepsSettings = DepsSettingsNoVer.withVer(vers.DepsPlug)

  /**
   * SourceFun Gradle Plugin
   * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
   * - [github](https://github.com/mareklangiewicz/DepsKt)
   */
  val SourceFunNoVer = DepP("pl.mareklangiewicz.sourcefun")
  val SourceFun = SourceFunNoVer.withVer(vers.SourceFunPlug)
}
