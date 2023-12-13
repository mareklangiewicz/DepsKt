@file:Suppress("unused")

package pl.mareklangiewicz.deps

import vers
import org.gradle.api.artifacts.*


fun ConfigurationContainer.checkVerSync(warnOnly: Boolean = false) = configureEach { it.checkVerSync(warnOnly) }
fun Configuration.checkVerSync(warnOnly: Boolean = false) { withDependencies { it.checkVerSync(warnOnly) } }
fun DependencySet.checkVerSync(warnOnly: Boolean = false) = configureEach { it.checkVerSync(warnOnly) }
fun Dependency.checkVerSync(warnOnly: Boolean = false) {
    when (group) {
        "org.jetbrains.kotlin" -> checkWith(vers.Kotlin, warnOnly)
        "androidx.compose.compiler" -> checkWith(vers.ComposeCompiler, warnOnly)
        "androidx.compose.ui", "androidx.compose.animation", "androidx.compose.foundation",
        "androidx.compose.material" -> checkWith(vers.ComposeAndro, warnOnly)

        "androidx.compose.runtime" -> when (name) {
            "runtime-livedata", "runtime", "runtime-rxjava2", "runtime-rxjava3",
            "runtime-saveable" -> checkWith(vers.ComposeAndro, warnOnly)
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

object Vers {

    /**
     * Manually selected kotlin version. Have to be working with current compose multiplatform and compose andro.
     * - [compose kotlin compatibility](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compatibility-and-versioning.html#kotlin-compatibility)
     * - [releases github](https://github.com/JetBrains/kotlin/releases)
     * - [compiler dev repo table](https://androidx.dev/storage/compose-compiler/repository)
     */
    val Kotlin = Ver("2.0.0-Beta1")

    val ComposeCompilerFromAXStable = AndroidX.Compose.Compiler.compiler.verStable!!
    val ComposeCompilerFor1921 = Ver("1.5.6-dev-k1.9.21-3eed341308a") // this ver is prepared for 1.9.21
    val ComposeCompilerFor200B1 = Ver("1.5.6-dev-k2.0.0-Beta1-06a03be2b42") // this ver is prepared for 2.0.0-Beta1

    /** Selected Compose Compiler version. Should always be kept compatible with the selected Kotlin version. */
    val ComposeCompiler = ComposeCompilerFor200B1

    // https://github.com/JetBrains/compose-multiplatform/releases
    val ComposeEdge = Ver("1.6.0-dev1334")

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
    val NexusPublishPlug = Ver("1.3.0", 0)

    /**
     * Gradle Publish Plugin (gradle plugin for publishing gradle plugins)
     * - [plugins gradle org](https://plugins.gradle.org/plugin/com.gradle.plugin-publish)
     * - [plugins gradle org docs](https://plugins.gradle.org/docs/publish-plugin)
     */
    val GradlePublishPlug = Ver("1.2.1", 0)

    /**
     * Gradle Enterprise Plugin (enables integration with Gradle Enterprise and scans.gradle.com)
     * - [gradle org docs](https://docs.gradle.com/enterprise/gradle-plugin/)
     * - [gradle portal](https://plugins.gradle.org/plugin/com.gradle.enterprise)
     */
    val GradleEnterprisePlug = Ver("3.16", 0)

    /**
     * Android Gradle Plugin
     * - [andro gradle api releases](https://developer.android.com/reference/tools/gradle-api)
     * - [maven](https://maven.google.com/web/index.html#com.android.tools.build:gradle)
     * - [releases](https://developer.android.com/studio/releases/gradle-plugin)
     */
    val AndroPlugStable = Ver("8.2.0")
    val AndroPlugEdge = Ver("8.3.0-alpha17")
    val AndroPlug = AndroPlugEdge

    /**
     * Dokka Gradle Plugin
     * - [gradle portal](https://plugins.gradle.org/plugin/org.jetbrains.dokka)
     * - [github](https://github.com/Kotlin/dokka)
     * - [github releases](https://github.com/Kotlin/dokka/releases)
     */
    val DokkaPlug = Ver("1.9.10", 0)


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
    val KotlinJupyterPlug = Ver("0.12.0-97")

    /**
     * Osacky Doctor Gradle Plugin
     * - [gradle portal](https://plugins.gradle.org/plugin/com.osacky.doctor)
     * - [github](https://github.com/runningcode/gradle-doctor)
     * - [docs](https://runningcode.github.io/gradle-doctor/)
     */
    val OsackyDoctorPlug = Ver("0.9.1", 0)

    /**
     * DepsKt Gradle Plugin
     * - [plugins gradle search mareklangiewicz](https://plugins.gradle.org/search?term=pl.mareklangiewicz)
     * - [github](https://github.com/langara/DepsKt)
     */
    val DepsPlug = Ver("0.2.68")

    val JvmDefaultVer = "21" // I had terrible issues with "16" (andro compose project)


    val Gradle5 = Ver("5.6.4", 0)
    val Gradle6 = Ver("6.8.3", 0)
    val Gradle7 = Ver("7.6.1", 0)
    val Gradle8 = Ver("8.5", 0)

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
    val AndroBuildTools = Ver("34.0.0", 0)

    /**
     * - [revisions](https://developer.android.com/topic/libraries/support-library/revisions.html)
     */
    @Deprecated("Use androidx")
    val AndroSupportLibrary = Ver("28.0.0", 0)

}