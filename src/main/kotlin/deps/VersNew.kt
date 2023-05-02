package pl.mareklangiewicz.deps

import versNew
import org.gradle.api.artifacts.*


fun ConfigurationContainer.checkVerSync(warnOnly: Boolean = false) = configureEach { it.checkVerSync(warnOnly) }
fun Configuration.checkVerSync(warnOnly: Boolean = false) { withDependencies { it.checkVerSync(warnOnly) } }
fun DependencySet.checkVerSync(warnOnly: Boolean = false) = configureEach { it.checkVerSync(warnOnly) }
fun Dependency.checkVerSync(warnOnly: Boolean = false) {
    when (group) {
        "org.jetbrains.kotlin" -> checkWith(versNew.Kotlin, warnOnly)
        "androidx.compose.compiler" -> checkWith(versNew.ComposeCompiler, warnOnly)
        "androidx.compose.ui", "androidx.compose.animation", "androidx.compose.foundation",
        "androidx.compose.material" -> checkWith(versNew.ComposeAndro, warnOnly)

        "androidx.compose.runtime" -> when (name) {
            "runtime-livedata", "runtime", "runtime-rxjava2", "runtime-rxjava3",
            "runtime-saveable" -> checkWith(versNew.ComposeAndro, warnOnly)
        }
    }
}

private fun Dependency.checkWith(expectedVer: Ver, warnOnly: Boolean) {
    if(version != expectedVer.ver) {
        val msg = "Dependency $group:$name:$version not synced with $expectedVer"
        if (warnOnly) println("WARNING: $msg") else error(msg)
        // TODO_someday: better warning? But we don't have access to gradle logger here?
    }
}

object VersNew {

    /**
     * Manually selected kotlin version. Have to be working with current compose multiplatform and compose andro.
     * - [compose kotlin compatibility](https://github.com/JetBrains/compose-multiplatform/blob/master/VERSIONING.md#kotlin-compatibility)
     */
    val Kotlin = Ver("1.8.20")

    /** Selected Compose Multiplatform version. Should always be kept compatible with selected Kotlin version. */
    val Compose = Org.JetBrains.Compose.gradle_plugin.verStable!!

    /** Selected Compose Compiler version. Should always be kept compatible with selected Kotlin version. */
    val ComposeCompiler = AndroidX.Compose.Compiler.compiler.verStable!!

    /** Selected Compose Android version. Should always be kept compatible with selected Kotlin version. */
    val ComposeAndro = AndroidX.Compose.Runtime.runtime.verStable!!

    /**
     * Gradle-Nexus Publish Plugin (turnkey plugin for publishing libs to maven central / sonatype)
     * - [github](https://github.com/gradle-nexus/publish-plugin/)
     * - [github releases](https://github.com/gradle-nexus/publish-plugin/releases)
     */
    val NexusPublishPlug = Ver("1.3.0", 0)

    /**
     * Gradle Publish Plugin (gradle plugin for publishing gradle plugins)
     * - [plugins gradle org](https://plugins.gradle.org/plugin/com.gradle.plugin-publish)
     * - [plugins gradle org docs](https://plugins.gradle.org/docs/publish-plugin)
     */
    val GradlePublishPlug = Ver("1.2.0", 0)

    /**
     * Android Gradle Plugin
     * - [maven](https://maven.google.com/web/index.html#com.android.tools.build:gradle)
     * - [releases](https://developer.android.com/studio/releases/gradle-plugin)
     * - [andro gradle dsl](https://google.github.io/android-gradle-dsl/)
     */
    val AndroPlug = Ver("8.2.0-alpha01")

    /**
     * Dokka Gradle Plugin
     * - [gradle portal](https://plugins.gradle.org/plugin/org.jetbrains.dokka)
     * - [github](https://github.com/Kotlin/dokka)
     * - [github releases](https://github.com/Kotlin/dokka/releases)
     */
    val DokkaPlug = Ver("1.8.10", 0)

    /**
     * Osacky Doctor Gradle Plugin
     * - [gradle portal](https://plugins.gradle.org/plugin/com.osacky.doctor)
     * - [github](https://github.com/runningcode/gradle-doctor)
     * - [docs](https://runningcode.github.io/gradle-doctor/)
     */
    val OsackyDoctorPlug = Ver("0.8.1", 0)

    /**
     * DepsKt Gradle Plugin
     * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
     * - [github](https://github.com/langara/DepsKt)
     */
    val DepsPlug = Ver("0.2.36")

    val JvmDefaultVer = "17" // I had terrible issues with "16" (andro compose project)


    val Gradle5 = Ver("5.6.4", 0)
    val Gradle6 = Ver("6.8.3", 0)
    val Gradle7 = Ver("7.6.1", 0)
    val Gradle8 = Ver("8.1.1", 0)

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
    val AndroSdkTarget = 33

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
    val AndroBuildTools = Ver("34.0.0", 0)

    /**
     * - [revisions](https://developer.android.com/topic/libraries/support-library/revisions.html)
     */
    @Deprecated("Use androidx")
    val AndroSupportLibrary = Ver("28.0.0", 0)

}