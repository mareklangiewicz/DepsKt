@file:Suppress("unused")

package pl.mareklangiewicz.deps

import versNew

object Plugs {

    /**
     * Gradle-Nexus Publish Plugin (turnkey plugin for publishing libs to maven central / sonatype)
     * - [github](https://github.com/gradle-nexus/publish-plugin/)
     * - [github releases](https://github.com/gradle-nexus/publish-plugin/releases)
     */
    val NexusPublish = DepP("io.github.gradle-nexus.publish-plugin", versNew.NexusPublishPlug)

    val KotlinMulti = DepP("org.jetbrains.kotlin.multiplatform", versNew.Kotlin)
    val KotlinJvm = DepP("org.jetbrains.kotlin.jvm", versNew.Kotlin)
    val KotlinJs = DepP("org.jetbrains.kotlin.js", versNew.Kotlin)
    val KotlinAndro = DepP("org.jetbrains.kotlin.android", versNew.Kotlin)

    val MavenPublish = DepP("maven-publish")

    val Signing = DepP("signing")


    /**
     * Gradle Publish Plugin (gradle plugin for publishing gradle plugins)
     * - [plugins gradle org](https://plugins.gradle.org/plugin/com.gradle.plugin-publish)
     * - [plugins gradle org docs](https://plugins.gradle.org/docs/publish-plugin)
     */
    val GradlePublish = DepP("com.gradle.plugin-publish", versNew.GradlePublishPlug)

    /**
     * Gradle Enterprise Plugin (enables integration with Gradle Enterprise and scans.gradle.com)
     * - [gradle org docs](https://docs.gradle.com/enterprise/gradle-plugin/)
     * - [gradle portal](https://plugins.gradle.org/plugin/com.gradle.enterprise)
     */
    val GradleEnterprise = DepP("com.gradle.enterprise", versNew.GradleEnterprisePlug)

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
    val AndroLib = DepP("com.android.library", versNew.AndroPlug)

    /**
     * Android Gradle Plugin
     * - [maven](https://maven.google.com/web/index.html#com.android.tools.build:gradle)
     * - [releases](https://developer.android.com/studio/releases/gradle-plugin)
     * - [andro gradle dsl](https://google.github.io/android-gradle-dsl/)
     */
    val AndroApp = DepP("com.android.application", versNew.AndroPlug)

//    val Compose = Org.JetBrains.Compose.gradle_plugin.withVer(versNew.Compose)
    val Compose = ComposeEdgeGradlePlugin

    /**
     * Dokka Gradle Plugin
     * - [gradle portal](https://plugins.gradle.org/plugin/org.jetbrains.dokka)
     * - [github](https://github.com/Kotlin/dokka)
     * - [github releases](https://github.com/Kotlin/dokka/releases)
     */
    val Dokka = DepP("org.jetbrains.dokka", versNew.DokkaPlug)

    /**
     * Ktor Gradle Plugin
     * [github](https://github.com/ktorio/ktor-build-plugins)
     */
    val Ktor = DepP("io.ktor.plugin", versNew.KtorPlug)

    /**
     * Kotlin Jupyter Gradle Plugin
     * [gradle portal](https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jupyter.api)
     * [github](https://github.com/Kotlin/kotlin-jupyter)
     */
    val KotlinJupyter = DepP("org.jetbrains.kotlin.jupyter.api", versNew.KotlinJupyterPlug)

    /**
     * Osacky Doctor Gradle Plugin
     * - [gradle portal](https://plugins.gradle.org/plugin/com.osacky.doctor)
     * - [github](https://github.com/runningcode/gradle-doctor)
     * - [docs](https://runningcode.github.io/gradle-doctor/)
     */
    val Doctor = DepP("com.osacky.doctor", versNew.OsackyDoctorPlug)

    /**
     * DepsKt Gradle Plugin
     * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
     * - [github](https://github.com/langara/DepsKt)
     */
    val Deps = DepP("pl.mareklangiewicz.deps", versNew.DepsPlug)

    /**
     * DepsKt Gradle Settings Plugin
     * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
     * - [github](https://github.com/langara/DepsKt)
     */
    val DepsSettings = DepP("pl.mareklangiewicz.deps.settings", versNew.DepsPlug)

    /**
     * SourceFun Gradle Plugin
     * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
     * - [github](https://github.com/langara/DepsKt)
     */
    val SourceFun = DepP("pl.mareklangiewicz.sourcefun", versNew.DepsPlug)
}