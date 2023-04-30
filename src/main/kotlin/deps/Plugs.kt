package pl.mareklangiewicz.deps

object Plugs {

    /**
     * Gradle-Nexus Publish Plugin (turnkey plugin for publishing libs to maven central / sonatype)
     * - [github](https://github.com/gradle-nexus/publish-plugin/)
     * - [github releases](https://github.com/gradle-nexus/publish-plugin/releases)
     */
    val NexusPublish = DepP("io.github.gradle-nexus.publish-plugin", VersNew.NexusPublishPlug)

    val KotlinMulti = DepP("org.jetbrains.kotlin.multiplatform", VersNew.Kotlin)
    val KotlinJvm = DepP("org.jetbrains.kotlin.jvm", VersNew.Kotlin)
    val KotlinJs = DepP("org.jetbrains.kotlin.js", VersNew.Kotlin)
    val KotlinAndro = DepP("org.jetbrains.kotlin.android", VersNew.Kotlin)

    val MavenPublish = DepP("maven-publish")

    val Signing = DepP("signing")


    /**
     * Gradle Publish Plugin (gradle plugin for publishing gradle plugins)
     * - [plugins gradle org](https://plugins.gradle.org/plugin/com.gradle.plugin-publish)
     * - [plugins gradle org docs](https://plugins.gradle.org/docs/publish-plugin)
     */
    val GradlePublish = DepP("com.gradle.plugin-publish", VersNew.GradlePublishPlug)

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
    val AndroLib = DepP("com.android.library", VersNew.AndroPlug)

    /**
     * Android Gradle Plugin
     * - [maven](https://maven.google.com/web/index.html#com.android.tools.build:gradle)
     * - [releases](https://developer.android.com/studio/releases/gradle-plugin)
     * - [andro gradle dsl](https://google.github.io/android-gradle-dsl/)
     */
    val AndroApp = DepP("com.android.application", VersNew.AndroPlug)

    val Compose = Org.JetBrains.Compose.gradle_plugin.withVer(VersNew.Compose)

    /**
     * Dokka Gradle Plugin
     * - [gradle portal](https://plugins.gradle.org/plugin/org.jetbrains.dokka)
     * - [github](https://github.com/Kotlin/dokka)
     * - [github releases](https://github.com/Kotlin/dokka/releases)
     */
    val Dokka = DepP("org.jetbrains.dokka", VersNew.DokkaPlug)


    /**
     * Osacky Doctor Gradle Plugin
     * - [gradle portal](https://plugins.gradle.org/plugin/com.osacky.doctor)
     * - [github](https://github.com/runningcode/gradle-doctor)
     * - [docs](https://runningcode.github.io/gradle-doctor/)
     */
    val Doctor = DepP("com.osacky.doctor", VersNew.OsackyDoctorPlug)

    /**
     * DepsKt Gradle Plugin
     * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
     * - [github](https://github.com/langara/DepsKt)
     */
    val Deps = DepP("pl.mareklangiewicz.deps", VersNew.DepsPlug)

    /**
     * DepsKt Gradle Settings Plugin
     * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
     * - [github](https://github.com/langara/DepsKt)
     */
    val DepsSettings = DepP("pl.mareklangiewicz.deps.settings", VersNew.DepsPlug)

    /**
     * SourceFun Gradle Plugin
     * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
     * - [github](https://github.com/langara/DepsKt)
     */
    val SourceFun = DepP("pl.mareklangiewicz.sourcefun", VersNew.DepsPlug)
}