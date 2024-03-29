@file:Suppress("unused", "MemberVisibilityCanBePrivate")

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
     * Gradle Enterprise Plugin (enables integration with Gradle Enterprise and scans.gradle.com)
     * - [gradle org docs](https://docs.gradle.com/enterprise/gradle-plugin/)
     * - [gradle portal](https://plugins.gradle.org/plugin/com.gradle.enterprise)
     */
    val GradleEnterprise = DepP("com.gradle.enterprise", vers.GradleEnterprisePlug)

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

    val Compose = Org.JetBrains.Compose.gradle_plugin.withVer(vers.Compose)

    val ComposeEdge = Compose.withVer(vers.ComposeEdge)

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
     * Osacky Doctor Gradle Plugin
     * - [gradle portal](https://plugins.gradle.org/plugin/com.osacky.doctor)
     * - [github](https://github.com/runningcode/gradle-doctor)
     * - [docs](https://runningcode.github.io/gradle-doctor/)
     */
    val Doctor = DepP("com.osacky.doctor", vers.OsackyDoctorPlug)

    /**
     * DepsKt Gradle Plugin
     * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
     * - [github](https://github.com/langara/DepsKt)
     */
    val Deps = DepP("pl.mareklangiewicz.deps", vers.DepsPlug)

    /**
     * DepsKt Gradle Settings Plugin
     * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
     * - [github](https://github.com/langara/DepsKt)
     */
    val DepsSettings = DepP("pl.mareklangiewicz.deps.settings", vers.DepsPlug)

    /**
     * SourceFun Gradle Plugin
     * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
     * - [github](https://github.com/langara/DepsKt)
     */
    val SourceFun = DepP("pl.mareklangiewicz.sourcefun", vers.DepsPlug)
}