@file:Suppress(
    "unused",
    "SpellCheckingInspection",
    "GrazieInspection",
    "ClassName",
    "PackageDirectoryMismatch",
    "MemberVisibilityCanBePrivate",
)

package pl.mareklangiewicz.deps

import versNew

// region [Deps Data Structures]

/**
 * Stable -> 0
 * ReleaseCandidate -> 100
 * Milestone -> 120
 * EarlyAccessProgram -> 140
 * Beta -> 200
 * Alpha -> 300
 * Development -> 320
 * Preview -> 400
 * Snapshot -> 500
 *
 * Other positive numbers are allowed if really necessary.
 */
@JvmInline
value class Instability(val instability: Int)

infix fun Instability?.moreStableThan(other: Instability?): Boolean {
    val left = this?.instability ?: Int.MAX_VALUE
    val right = other?.instability ?: Int.MAX_VALUE
    return left < right
}

private fun detectInstability(version: String): Instability? = null // FIXME

data class Ver(val ver: String, val instability: Instability? = detectInstability(ver)) {
    // TODO: instability detection
    constructor(ver: String, instability: Int): this(ver, Instability(instability))
    constructor(major: Int, minor: Int, patch: Int, patchLength: Int = 2, suffix: String = ""):
            this("$major.$minor.${patch.toString().padStart(patchLength, '0')}$suffix")
}

/** versions should always be sorted from oldest to newest */
data class Dep(val group: String, val name: String, val vers: List<Ver>): CharSequence {
    constructor(group: String, name: String, vararg vers: Ver): this(group, name, vers.toList())
    val ver: Ver? get() = vers.lastOrNull()
    val mvn: String get() = ver?.ver?.let { "$group:$name:$it" } ?: "$group:$name"
    override fun toString() = mvn
    override val length get() = mvn.length
    override fun get(index: Int) = mvn[index]
    override fun subSequence(startIndex: Int, endIndex: Int) = mvn.subSequence(startIndex, endIndex)
}

fun DepP(pluginId: String, vararg vers: Ver) = Dep(pluginId, "$pluginId.gradle.plugin", *vers)

fun Dep.withNoVer() = copy(vers = emptyList())
fun Dep.withVer(ver: Ver) = copy(vers = listOf(ver))
fun Dep.withVer(verName: String, verInstability: Int? = null) = withVer(Ver(verName, verInstability?.let(::Instability)))
fun Dep.withVers(vararg vers: Ver) = copy(vers = vers.toList())
fun Dep.withVers(maxInstability: Instability) = copy(vers = vers.filter { !(maxInstability moreStableThan it.instability) })
val Dep.verStable get() = vers.lastOrNull { it.instability?.instability == 0 }

// endregion [Deps Data Structures]


// region [Deps Selected]

/**
 * - [releases](https://github.com/JetBrains/kotlin/releases)
 * - [release details](https://kotlinlang.org/docs/releases.html#release-details)
 * - [changelog](https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md)
 * - compatibility with compose
 *     - [developer android com](https://developer.android.com/jetpack/androidx/releases/compose-kotlin)
 *     - [androidx dev compose compiler compatibility](https://androidx.dev/storage/compose-compiler/repository)
 *     - [jetbrains/compose/ComposeCompilerCompatibility.kt](https://github.com/JetBrains/compose-multiplatform/blob/master/gradle-plugins/compose/src/main/kotlin/org/jetbrains/compose/ComposeCompilerCompatibility.kt)
 */
typealias Kotlin = Org.JetBrains.Kotlin

/**
 * - KotlinX some libs links
 *     - [date time](https://github.com/Kotlin/kotlinx-datetime)
 *     - [coroutines](https://github.com/Kotlin/kotlinx.coroutines/releases)
 *     - [serialization](https://github.com/Kotlin/kotlinx.serialization/releases)
 *     - [atomic fu](https://github.com/Kotlin/kotlinx.atomicfu/releases)
 *     - [html](https://github.com/Kotlin/kotlinx.html/releases)
 *     - [node js](https://github.com/Kotlin/kotlinx-nodejs)
 */
typealias KotlinX = Org.JetBrains.KotlinX

/**
 * - [github](https://github.com/langara)
 * - [github repositories](https://github.com/langara?tab=repositories)
 * - [repo1 maven](https://repo1.maven.org/maven2/pl/mareklangiewicz/)
 */
typealias Langiewicz = Pl.MarekLangiewicz

/**
 * - [github](https://github.com/JetBrains/compose-multiplatform)
 * - [github releases](https://github.com/JetBrains/compose-multiplatform/releases)
 * - [github changelog](https://github.com/JetBrains/compose-multiplatform/blob/master/CHANGELOG.md)
 * - [maven space](https://maven.pkg.jetbrains.space/public/p/compose/dev/org/jetbrains/compose/)
 * - [maven space plugin](https://maven.pkg.jetbrains.space/public/p/compose/dev/org/jetbrains/compose/compose-gradle-plugin/)
 */
typealias Compose = Org.JetBrains.Compose
val ComposeEdgeGradlePlugin get() = Compose.gradle_plugin.withVer(Ver("1.5.10-dev1177")) // FIXME: auto generate from jetbrains space or deprecate?
//val ComposeEdgeGradlePlugin get() = Compose.gradle_plugin

val ComposeCompiler get() = AndroidX.Compose.Compiler.compiler.withVer(versNew.ComposeCompiler)


/**
 * - [releases](https://developer.android.com/jetpack/androidx/releases/compose)
 * - [releases compatibility](https://developer.android.com/jetpack/androidx/releases/compose-kotlin#pre-release_kotlin_compatibility)
 * - [compiler dev repo table](https://androidx.dev/storage/compose-compiler/repository)
 * - [compiler mvnrepository releases](https://mvnrepository.com/artifact/org.jetbrains.compose.compiler/compiler)
 */
typealias ComposeAndro = AndroidX.Compose

/**
 * - [accompanist docs](https://google.github.io/accompanist/)
 * - [accompanist sonatype search](https://central.sonatype.com/search?namespace=com.google.accompanist)
 */
typealias GoogleAccompanist = Com.Google.Accompanist

/**
 * Android related kdoc links
 * TODO: better places for links (auto injecting into generated objects?)
 * - [google maven index](https://maven.google.com/web/index.html)
 * - [androidx versions](https://developer.android.com/jetpack/androidx/versions)
 * - [constraint layout](https://developer.android.com/develop/ui/views/layout/constraint-layout)
 * - [navigation](https://developer.android.com/jetpack/androidx/releases/navigation)
 * - [material components](https://github.com/material-components/material-components-android/releases)
 * - [browser helper](https://github.com/GoogleChrome/android-browser-helper)
 * - [flexbox layout](https://github.com/google/flexbox-layout/releases)
 * - [percent layout](https://developer.android.com/jetpack/androidx/releases/percentlayout)
 * - [lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle)
 * - [camera](https://developer.android.com/jetpack/androidx/releases/camera)
 * - [room](https://developer.android.com/jetpack/androidx/releases/room)
 * - [auto fill](https://developer.android.com/jetpack/androidx/releases/autofill)
 * - [test](https://developer.android.com/jetpack/androidx/releases/test)
 * - [rx binding](https://github.com/JakeWharton/RxBinding)
 * - [rx android](https://github.com/ReactiveX/RxAndroid/releases)
 * - [google services plugin](https://developers.google.com/android/guides/google-services-plugin)
 * - [google play services setup](https://developers.google.com/android/guides/setup)
 * - [google play services releases](https://developers.google.com/android/guides/releases)
 * - [firebase crashlytics get started](https://firebase.google.com/docs/crashlytics/get-started?platform=android)
 * - [firebase release notes andro](https://firebase.google.com/support/release-notes/android)
 * - [firebase setup andro bom](https://firebase.google.com/docs/android/setup#add-sdks)
 * - [picasso](https://github.com/square/picasso)
 * - [leak canary](https://github.com/square/leakcanary/releases)
 * - [mockito releases](https://github.com/mockito/mockito/releases)
 * - [mockito core sonatype](https://central.sonatype.com/search?q=mockito-core&namespace=org.mockito)
 * - [mockito android sonatype](https://central.sonatype.com/search?q=mockito-android&namespace=org.mockito)
 * - [robolectric getting started](https://robolectric.org/getting-started/)
 * - [realm android install](https://docs.mongodb.com/realm/sdk/android/install/)
 * - [rsocket kotlin github](https://github.com/rsocket/rsocket-kotlin)
 * - [rsocket kotlin github releases](https://github.com/rsocket/rsocket-kotlin/releases)
 * - [splitties github](https://github.com/LouisCAD/Splitties)
 * - [splitties github releases](https://github.com/rsocket/rsocket-kotlin/releases)
 */
val AndroLinks: Nothing get() = error("Dont' use AndroLinks in code. It's only for links in kdoc comment.")


/**
 *
 * Other kdoc links
 * TODO: better places for links (auto injecting into generated objects?)
 * - [rx java releases](https://github.com/ReactiveX/RxJava/releases)
 * - [rx kotlin releases](https://github.com/ReactiveX/RxKotlin/releases)
 * - [rx relay](https://github.com/JakeWharton/RxRelay)
 * - [retrofit](https://github.com/square/retrofit)
 * - [okhttp](https://github.com/square/okhttp)
 * - [okio](https://square.github.io/okio)
 * - [mockito kotlin releases](https://github.com/nhaarman/mockito-kotlin/releases)
 * - [junit4 releases](https://github.com/junit-team/junit4/releases)
 * - [junit5 jupiter releases](https://github.com/junit-team/junit5/releases)
 * - [google truth releases](https://github.com/google/truth/releases)
 * - [ktor maven space](https://maven.pkg.jetbrains.space/public/p/ktor/eap/io/ktor/ktor/)
 * - [ktor github](https://github.com/ktorio/ktor)
 * - [ktor github](https://github.com/ktorio/ktor)
 * - [ktor github](https://github.com/ktorio/ktor)
 * - [ktor github releases](https://github.com/ktorio/ktor/releases)
 */
val OtherLinks: Nothing get() = error("Don't use OtherLinks in code. It's only for links in kdoc comment.")

// endregion [Deps Selected]


// region [Deps Generated]

object AndroidX {
    object Activity {
        val activity = Dep("androidx.activity", "activity", Ver("1.8.1", 0), Ver("1.9.0-alpha01", 300))
        val compose = Dep("androidx.activity", "activity-compose", Ver("1.8.1", 0), Ver("1.9.0-alpha01", 300))
        val ktx = Dep("androidx.activity", "activity-ktx", Ver("1.8.1", 0), Ver("1.9.0-alpha01", 300))
    }
    object Annotation {
        val annotation = Dep("androidx.annotation", "annotation", Ver("1.7.0", 0))
        val experimental = Dep("androidx.annotation", "annotation-experimental", Ver("1.3.1", 0), Ver("1.4.0-alpha01", 300))
    }
    object AppCompat {
        val appcompat = Dep("androidx.appcompat", "appcompat", Ver("1.6.1", 0), Ver("1.7.0-alpha03", 300))
        val resources = Dep("androidx.appcompat", "appcompat-resources", Ver("1.6.1", 0), Ver("1.7.0-alpha03", 300))
    }
    object AppSearch {
        val appsearch = Dep("androidx.appsearch", "appsearch", Ver("1.1.0-alpha03", 300))
        val builtin_types = Dep("androidx.appsearch", "appsearch-builtin-types", Ver("1.1.0-alpha03", 300))
        val compiler = Dep("androidx.appsearch", "appsearch-compiler", Ver("1.1.0-alpha03", 300))
        val local_storage = Dep("androidx.appsearch", "appsearch-local-storage", Ver("1.1.0-alpha03", 300))
        val platform_storage = Dep("androidx.appsearch", "appsearch-platform-storage", Ver("1.1.0-alpha03", 300))
    }
    object Arch {
        object Core {
            val common = Dep("androidx.arch.core", "core-common", Ver("2.2.0", 0))
            val runtime = Dep("androidx.arch.core", "core-runtime", Ver("2.2.0", 0))
            val testing = Dep("androidx.arch.core", "core-testing", Ver("2.2.0", 0))
        }
    }
    object AsyncLayoutInflater {
        val appcompat = Dep("androidx.asynclayoutinflater", "asynclayoutinflater-appcompat", Ver("1.1.0-alpha01", 300))
        val asynclayoutinflater = Dep("androidx.asynclayoutinflater", "asynclayoutinflater", Ver("1.0.0", 0), Ver("1.1.0-alpha01", 300))
    }
    object AutoFill {
        val autofill = Dep("androidx.autofill", "autofill", Ver("1.1.0", 0), Ver("1.2.0-beta01", 200), Ver("1.3.0-alpha01", 300))
    }
    object Benchmark {
        val common = Dep("androidx.benchmark", "benchmark-common", Ver("1.2.1", 0))
        val gradle_plugin = Dep("androidx.benchmark", "benchmark-gradle-plugin", Ver("1.2.1", 0))
        val junit4 = Dep("androidx.benchmark", "benchmark-junit4", Ver("1.2.1", 0))
        val macro = Dep("androidx.benchmark", "benchmark-macro", Ver("1.2.1", 0))
        val macro_junit4 = Dep("androidx.benchmark", "benchmark-macro-junit4", Ver("1.2.1", 0))
    }
    object Biometric {
        val biometric = Dep("androidx.biometric", "biometric", Ver("1.1.0", 0), Ver("1.2.0-alpha05", 300))
        val ktx = Dep("androidx.biometric", "biometric-ktx", Ver("1.2.0-alpha05", 300))
    }
    object Browser {
        val browser = Dep("androidx.browser", "browser", Ver("1.7.0", 0), Ver("1.8.0-beta01", 200))
    }
    object Camera {
        val camera2 = Dep("androidx.camera", "camera-camera2", Ver("1.3.0", 0), Ver("1.4.0-alpha02", 300))
        val core = Dep("androidx.camera", "camera-core", Ver("1.3.0", 0), Ver("1.4.0-alpha02", 300))
        val extensions = Dep("androidx.camera", "camera-extensions", Ver("1.3.0", 0), Ver("1.4.0-alpha02", 300))
        val lifecycle = Dep("androidx.camera", "camera-lifecycle", Ver("1.3.0", 0), Ver("1.4.0-alpha02", 300))
        val mlkit_vision = Dep("androidx.camera", "camera-mlkit-vision", Ver("1.3.0-beta02", 200), Ver("1.4.0-alpha02", 300))
        val video = Dep("androidx.camera", "camera-video", Ver("1.3.0", 0), Ver("1.4.0-alpha02", 300))
        val view = Dep("androidx.camera", "camera-view", Ver("1.3.0", 0), Ver("1.4.0-alpha02", 300))
    }
    object Car {
        object App {
            val app = Dep("androidx.car.app", "app", Ver("1.2.0", 0), Ver("1.4.0-rc01", 100))
            val automotive = Dep("androidx.car.app", "app-automotive", Ver("1.2.0", 0), Ver("1.4.0-rc01", 100))
            val projected = Dep("androidx.car.app", "app-projected", Ver("1.2.0", 0), Ver("1.4.0-rc01", 100))
            val testing = Dep("androidx.car.app", "app-testing", Ver("1.2.0", 0), Ver("1.4.0-rc01", 100))
        }
    }
    object CardView {
        val cardview = Dep("androidx.cardview", "cardview", Ver("1.0.0", 0))
    }
    object Collection {
        val collection = Dep("androidx.collection", "collection", Ver("1.3.0", 0), Ver("1.4.0-beta02", 200))
        val ktx = Dep("androidx.collection", "collection-ktx", Ver("1.3.0", 0), Ver("1.4.0-beta02", 200))
    }
    object Compose {
        val bom = Dep("androidx.compose", "compose-bom", Ver("2023.10.01", 0))
        object Animation {
            val animation = Dep("androidx.compose.animation", "animation", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val core = Dep("androidx.compose.animation", "animation-core", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val graphics = Dep("androidx.compose.animation", "animation-graphics", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
        }
        object Compiler {
            val compiler = Dep("androidx.compose.compiler", "compiler", Ver("1.5.5", 0))
        }
        object Foundation {
            val foundation = Dep("androidx.compose.foundation", "foundation", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val layout = Dep("androidx.compose.foundation", "foundation-layout", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
        }
        object Material {
            val icons_core = Dep("androidx.compose.material", "material-icons-core", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val icons_extended = Dep("androidx.compose.material", "material-icons-extended", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val material = Dep("androidx.compose.material", "material", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val ripple = Dep("androidx.compose.material", "material-ripple", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
        }
        object Material3 {
            val material3 = Dep("androidx.compose.material3", "material3", Ver("1.1.2", 0), Ver("1.2.0-alpha12", 300))
            val window_size_class = Dep("androidx.compose.material3", "material3-window-size-class", Ver("1.1.2", 0), Ver("1.2.0-alpha12", 300))
        }
        object Runtime {
            val dispatch = Dep("androidx.compose.runtime", "runtime-dispatch", Ver("1.0.0-alpha12", 300))
            val livedata = Dep("androidx.compose.runtime", "runtime-livedata", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val runtime = Dep("androidx.compose.runtime", "runtime", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val rxjava2 = Dep("androidx.compose.runtime", "runtime-rxjava2", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val rxjava3 = Dep("androidx.compose.runtime", "runtime-rxjava3", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val saveable = Dep("androidx.compose.runtime", "runtime-saveable", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val tracing = Dep("androidx.compose.runtime", "runtime-tracing", Ver("1.0.0-beta01", 200))
        }
        object Ui {
            val geometry = Dep("androidx.compose.ui", "ui-geometry", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val graphics = Dep("androidx.compose.ui", "ui-graphics", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val test = Dep("androidx.compose.ui", "ui-test", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val test_junit4 = Dep("androidx.compose.ui", "ui-test-junit4", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val test_manifest = Dep("androidx.compose.ui", "ui-test-manifest", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val text = Dep("androidx.compose.ui", "ui-text", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val text_google_fonts = Dep("androidx.compose.ui", "ui-text-google-fonts", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val tooling = Dep("androidx.compose.ui", "ui-tooling", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val tooling_data = Dep("androidx.compose.ui", "ui-tooling-data", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val tooling_preview = Dep("androidx.compose.ui", "ui-tooling-preview", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val ui = Dep("androidx.compose.ui", "ui", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val unit = Dep("androidx.compose.ui", "ui-unit", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val util = Dep("androidx.compose.ui", "ui-util", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
            val viewbinding = Dep("androidx.compose.ui", "ui-viewbinding", Ver("1.5.4", 0), Ver("1.6.0-beta02", 200))
        }
    }
    object Concurrent {
        val futures = Dep("androidx.concurrent", "concurrent-futures", Ver("1.1.0", 0), Ver("1.2.0-alpha02", 300))
        val futures_ktx = Dep("androidx.concurrent", "concurrent-futures-ktx", Ver("1.1.0", 0), Ver("1.2.0-alpha02", 300))
    }
    object ConstraintLayout {
        val compose = Dep("androidx.constraintlayout", "constraintlayout-compose", Ver("1.0.1", 0), Ver("1.1.0-alpha13", 300))
        val constraintlayout = Dep("androidx.constraintlayout", "constraintlayout", Ver("2.1.4", 0), Ver("2.2.0-alpha13", 300))
    }
    object ContentPager {
        val contentpager = Dep("androidx.contentpager", "contentpager", Ver("1.0.0", 0))
    }
    object CoordinatorLayout {
        val coordinatorlayout = Dep("androidx.coordinatorlayout", "coordinatorlayout", Ver("1.2.0", 0), Ver("1.3.0-alpha02", 300))
    }
    object Core {
        val animation = Dep("androidx.core", "core-animation", Ver("1.0.0-rc01", 100))
        val animation_testing = Dep("androidx.core", "core-animation-testing", Ver("1.0.0-rc01", 100))
        val core = Dep("androidx.core", "core", Ver("1.12.0", 0), Ver("1.13.0-alpha02", 300))
        val google_shortcuts = Dep("androidx.core", "core-google-shortcuts", Ver("1.1.0", 0), Ver("1.2.0-alpha01", 300))
        val ktx = Dep("androidx.core", "core-ktx", Ver("1.12.0", 0), Ver("1.13.0-alpha02", 300))
        val performance = Dep("androidx.core", "core-performance", Ver("1.0.0-beta02", 200))
        val remoteviews = Dep("androidx.core", "core-remoteviews", Ver("1.0.0", 0))
        val role = Dep("androidx.core", "core-role", Ver("1.0.0", 0), Ver("1.1.0-rc01", 100))
        val splashscreen = Dep("androidx.core", "core-splashscreen", Ver("1.0.1", 0), Ver("1.1.0-alpha02", 300))
        object Uwb {
            val rxjava3 = Dep("androidx.core.uwb", "uwb-rxjava3", Ver("1.0.0-alpha07", 300))
            val uwb = Dep("androidx.core.uwb", "uwb", Ver("1.0.0-alpha07", 300))
        }
    }
    object CursorAdapter {
        val cursoradapter = Dep("androidx.cursoradapter", "cursoradapter", Ver("1.0.0", 0))
    }
    object CustomView {
        val customview = Dep("androidx.customview", "customview", Ver("1.1.0", 0), Ver("1.2.0-alpha02", 300))
        val poolingcontainer = Dep("androidx.customview", "customview-poolingcontainer", Ver("1.0.0", 0))
    }
    object DataStore {
        val core = Dep("androidx.datastore", "datastore-core", Ver("1.0.0", 0), Ver("1.1.0-alpha07", 300))
        val core_okio = Dep("androidx.datastore", "datastore-core-okio", Ver("1.1.0-alpha07", 300))
        val datastore = Dep("androidx.datastore", "datastore", Ver("1.0.0", 0), Ver("1.1.0-alpha07", 300))
        val preferences = Dep("androidx.datastore", "datastore-preferences", Ver("1.0.0", 0), Ver("1.1.0-alpha07", 300))
        val preferences_core = Dep("androidx.datastore", "datastore-preferences-core", Ver("1.0.0", 0), Ver("1.1.0-alpha07", 300))
        val preferences_rxjava2 = Dep("androidx.datastore", "datastore-preferences-rxJava2", Ver("1.0.0", 0), Ver("1.1.0-alpha07", 300))
        val preferences_rxjava3 = Dep("androidx.datastore", "datastore-preferences-rxJava3", Ver("1.0.0", 0), Ver("1.1.0-alpha07", 300))
        val rxjava2 = Dep("androidx.datastore", "datastore-rxJava2", Ver("1.0.0", 0), Ver("1.1.0-alpha07", 300))
        val rxjava3 = Dep("androidx.datastore", "datastore-rxJava3", Ver("1.0.0", 0), Ver("1.1.0-alpha07", 300))
    }
    object DocumentFile {
        val documentfile = Dep("androidx.documentfile", "documentfile", Ver("1.0.1", 0), Ver("1.1.0-alpha01", 300))
    }
    object Draganddrop {
        val draganddrop = Dep("androidx.draganddrop", "draganddrop", Ver("1.0.0", 0))
    }
    object DrawerLayout {
        val drawerlayout = Dep("androidx.drawerlayout", "drawerlayout", Ver("1.2.0", 0))
    }
    object DynamicAnimation {
        val dynamicanimation = Dep("androidx.dynamicanimation", "dynamicanimation", Ver("1.0.0", 0), Ver("1.1.0-alpha03", 300))
        val ktx = Dep("androidx.dynamicanimation", "dynamicanimation-ktx", Ver("1.0.0-alpha03", 300))
    }
    object Emoji {
        val appcompat = Dep("androidx.emoji", "emoji-appcompat", Ver("1.1.0", 0), Ver("1.2.0-alpha03", 300))
        val bundled = Dep("androidx.emoji", "emoji-bundled", Ver("1.1.0", 0), Ver("1.2.0-alpha03", 300))
        val emoji = Dep("androidx.emoji", "emoji", Ver("1.1.0", 0), Ver("1.2.0-alpha03", 300))
    }
    object Emoji2 {
        val bundled = Dep("androidx.emoji2", "emoji2-bundled", Ver("1.4.0", 0))
        val emoji2 = Dep("androidx.emoji2", "emoji2", Ver("1.4.0", 0))
        val views = Dep("androidx.emoji2", "emoji2-views", Ver("1.4.0", 0))
        val views_helper = Dep("androidx.emoji2", "emoji2-views-helper", Ver("1.4.0", 0))
    }
    object Enterprise {
        val feedback = Dep("androidx.enterprise", "enterprise-feedback", Ver("1.1.0", 0))
        val feedback_testing = Dep("androidx.enterprise", "enterprise-feedback-testing", Ver("1.1.0", 0))
    }
    object Exifinterface {
        val exifinterface = Dep("androidx.exifinterface", "exifinterface", Ver("1.3.6", 0))
    }
    object Fragment {
        val fragment = Dep("androidx.fragment", "fragment", Ver("1.6.2", 0), Ver("1.7.0-alpha07", 300))
        val ktx = Dep("androidx.fragment", "fragment-ktx", Ver("1.6.2", 0), Ver("1.7.0-alpha07", 300))
        val testing = Dep("androidx.fragment", "fragment-testing", Ver("1.6.2", 0), Ver("1.7.0-alpha07", 300))
    }
    object Games {
        val activity = Dep("androidx.games", "games-activity", Ver("2.0.2", 0), Ver("3.0.0-beta01", 200))
        val controller = Dep("androidx.games", "games-controller", Ver("2.0.1", 0))
        val frame_pacing = Dep("androidx.games", "games-frame-pacing", Ver("2.1.0", 0))
        val performance_tuner = Dep("androidx.games", "games-performance-tuner", Ver("1.6.0", 0), Ver("2.0.0-alpha07", 300))
        val text_input = Dep("androidx.games", "games-text-input", Ver("2.0.0", 0), Ver("3.0.0-beta01", 200))
    }
    object Glance {
        val appwidget = Dep("androidx.glance", "glance-appwidget", Ver("1.0.0", 0))
        val glance = Dep("androidx.glance", "glance", Ver("1.0.0", 0))
        val wear_tiles = Dep("androidx.glance", "glance-wear-tiles", Ver("1.0.0-alpha05", 300))
    }
    object GraphIcs {
        val core = Dep("androidx.graphics", "graphics-core", Ver("1.0.0-alpha05", 300))
    }
    object GridLayout {
        val gridlayout = Dep("androidx.gridlayout", "gridlayout", Ver("1.0.0", 0), Ver("1.1.0-beta01", 200))
    }
    object Health {
        val services_client = Dep("androidx.health", "health-services-client", Ver("1.0.0-rc01", 100), Ver("1.1.0-alpha01", 300))
        object Connect {
            val client = Dep("androidx.health.connect", "connect-client", Ver("1.1.0-alpha06", 300))
        }
    }
    object Heifwriter {
        val heifwriter = Dep("androidx.heifwriter", "heifwriter", Ver("1.0.0", 0), Ver("1.1.0-alpha02", 300))
    }
    object Hilt {
        val compiler = Dep("androidx.hilt", "hilt-compiler", Ver("1.1.0", 0))
        val navigation_compose = Dep("androidx.hilt", "hilt-navigation-compose", Ver("1.1.0", 0))
        val navigation_fragment = Dep("androidx.hilt", "hilt-navigation-fragment", Ver("1.1.0", 0))
        val work = Dep("androidx.hilt", "hilt-work", Ver("1.1.0", 0))
    }
    object Input {
        val motionprediction = Dep("androidx.input", "input-motionprediction", Ver("1.0.0-beta03", 200))
    }
    object Interpolator {
        val interpolator = Dep("androidx.interpolator", "interpolator", Ver("1.0.0", 0))
    }
    object JavaScriptengine {
        val javascriptengine = Dep("androidx.javascriptengine", "javascriptengine", Ver("1.0.0-beta01", 200))
    }
    object Leanback {
        val grid = Dep("androidx.leanback", "leanback-grid", Ver("1.0.0-alpha03", 300))
        val leanback = Dep("androidx.leanback", "leanback", Ver("1.0.0", 0), Ver("1.1.0-rc02", 100), Ver("1.2.0-alpha04", 300))
        val paging = Dep("androidx.leanback", "leanback-paging", Ver("1.1.0-alpha11", 300))
        val preference = Dep("androidx.leanback", "leanback-preference", Ver("1.0.0", 0), Ver("1.1.0-rc01", 100), Ver("1.2.0-alpha04", 300))
        val tab = Dep("androidx.leanback", "leanback-tab", Ver("1.1.0-beta01", 200))
    }
    object Lifecycle {
        val common = Dep("androidx.lifecycle", "lifecycle-common", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val common_java8 = Dep("androidx.lifecycle", "lifecycle-common-java8", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val compiler = Dep("androidx.lifecycle", "lifecycle-compiler", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val extensions = Dep("androidx.lifecycle", "lifecycle-extensions", Ver("2.2.0", 0))
        val livedata = Dep("androidx.lifecycle", "lifecycle-livedata", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val livedata_ktx = Dep("androidx.lifecycle", "lifecycle-livedata-ktx", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val process = Dep("androidx.lifecycle", "lifecycle-process", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val reactivestreams = Dep("androidx.lifecycle", "lifecycle-reactivestreams", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val reactivestreams_ktx = Dep("androidx.lifecycle", "lifecycle-reactivestreams-ktx", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val runtime = Dep("androidx.lifecycle", "lifecycle-runtime", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val runtime_compose = Dep("androidx.lifecycle", "lifecycle-runtime-compose", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val runtime_ktx = Dep("androidx.lifecycle", "lifecycle-runtime-ktx", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val runtime_testing = Dep("androidx.lifecycle", "lifecycle-runtime-testing", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val service = Dep("androidx.lifecycle", "lifecycle-service", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val viewmodel = Dep("androidx.lifecycle", "lifecycle-viewmodel", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val viewmodel_compose = Dep("androidx.lifecycle", "lifecycle-viewmodel-compose", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val viewmodel_ktx = Dep("androidx.lifecycle", "lifecycle-viewmodel-ktx", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
        val viewmodel_savedstate = Dep("androidx.lifecycle", "lifecycle-viewmodel-savedstate", Ver("2.6.2", 0), Ver("2.7.0-rc01", 100))
    }
    object Loader {
        val loader = Dep("androidx.loader", "loader", Ver("1.1.0", 0))
    }
    object LocalBroadcastManager {
        val localbroadcastmanager = Dep("androidx.localbroadcastmanager", "localbroadcastmanager", Ver("1.1.0", 0))
    }
    object Media {
        val media = Dep("androidx.media", "media", Ver("1.7.0", 0))
    }
    object Media2 {
        val common = Dep("androidx.media2", "media2-common", Ver("1.2.1", 0), Ver("1.3.0-beta01", 200))
        val exoplayer = Dep("androidx.media2", "media2-exoplayer", Ver("1.2.1", 0), Ver("1.3.0-beta01", 200))
        val player = Dep("androidx.media2", "media2-player", Ver("1.2.1", 0), Ver("1.3.0-beta01", 200))
        val session = Dep("androidx.media2", "media2-session", Ver("1.2.1", 0), Ver("1.3.0-beta01", 200))
        val widget = Dep("androidx.media2", "media2-widget", Ver("1.2.1", 0), Ver("1.3.0-beta01", 200))
    }
    object Media3 {
        val cast = Dep("androidx.media3", "media3-cast", Ver("1.2.0", 0))
        val common = Dep("androidx.media3", "media3-common", Ver("1.2.0", 0))
        val database = Dep("androidx.media3", "media3-database", Ver("1.2.0", 0))
        val datasource = Dep("androidx.media3", "media3-datasource", Ver("1.2.0", 0))
        val datasource_cronet = Dep("androidx.media3", "media3-datasource-cronet", Ver("1.2.0", 0))
        val datasource_okhttp = Dep("androidx.media3", "media3-datasource-okhttp", Ver("1.2.0", 0))
        val datasource_rtmp = Dep("androidx.media3", "media3-datasource-rtmp", Ver("1.2.0", 0))
        val decoder = Dep("androidx.media3", "media3-decoder", Ver("1.2.0", 0))
        val exoplayer = Dep("androidx.media3", "media3-exoplayer", Ver("1.2.0", 0))
        val exoplayer_dash = Dep("androidx.media3", "media3-exoplayer-dash", Ver("1.2.0", 0))
        val exoplayer_hls = Dep("androidx.media3", "media3-exoplayer-hls", Ver("1.2.0", 0))
        val exoplayer_ima = Dep("androidx.media3", "media3-exoplayer-ima", Ver("1.2.0", 0))
        val exoplayer_rtsp = Dep("androidx.media3", "media3-exoplayer-rtsp", Ver("1.2.0", 0))
        val exoplayer_workmanager = Dep("androidx.media3", "media3-exoplayer-workmanager", Ver("1.2.0", 0))
        val extractor = Dep("androidx.media3", "media3-extractor", Ver("1.2.0", 0))
        val session = Dep("androidx.media3", "media3-session", Ver("1.2.0", 0))
        val test_utils = Dep("androidx.media3", "media3-test-utils", Ver("1.2.0", 0))
        val test_utils_robolectric = Dep("androidx.media3", "media3-test-utils-robolectric", Ver("1.2.0", 0))
        val transformer = Dep("androidx.media3", "media3-transformer", Ver("1.2.0", 0))
        val ui = Dep("androidx.media3", "media3-ui", Ver("1.2.0", 0))
        val ui_leanback = Dep("androidx.media3", "media3-ui-leanback", Ver("1.2.0", 0))
    }
    object MediaRouter {
        val mediarouter = Dep("androidx.mediarouter", "mediarouter", Ver("1.6.0", 0), Ver("1.7.0-alpha01", 300))
    }
    object Metrics {
        val performance = Dep("androidx.metrics", "metrics-performance", Ver("1.0.0-alpha04", 300))
    }
    object Multidex {
        val instrumentation = Dep("androidx.multidex", "multidex-instrumentation", Ver("2.0.0", 0))
        val multidex = Dep("androidx.multidex", "multidex", Ver("2.0.1", 0))
    }
    object Navigation {
        val common = Dep("androidx.navigation", "navigation-common", Ver("2.7.5", 0))
        val common_ktx = Dep("androidx.navigation", "navigation-common-ktx", Ver("2.7.5", 0))
        val compose = Dep("androidx.navigation", "navigation-compose", Ver("2.7.5", 0))
        val dynamic_features_fragment = Dep("androidx.navigation", "navigation-dynamic-features-fragment", Ver("2.7.5", 0))
        val fragment = Dep("androidx.navigation", "navigation-fragment", Ver("2.7.5", 0))
        val fragment_ktx = Dep("androidx.navigation", "navigation-fragment-ktx", Ver("2.7.5", 0))
        val runtime = Dep("androidx.navigation", "navigation-runtime", Ver("2.7.5", 0))
        val runtime_ktx = Dep("androidx.navigation", "navigation-runtime-ktx", Ver("2.7.5", 0))
        val safe_args_generator = Dep("androidx.navigation", "navigation-safe-args-generator", Ver("2.7.5", 0))
        val safe_args_gradle_plugin = Dep("androidx.navigation", "navigation-safe-args-gradle-plugin", Ver("2.7.5", 0))
        val testing = Dep("androidx.navigation", "navigation-testing", Ver("2.7.5", 0))
        val ui = Dep("androidx.navigation", "navigation-ui", Ver("2.7.5", 0))
        val ui_ktx = Dep("androidx.navigation", "navigation-ui-ktx", Ver("2.7.5", 0))
    }
    object Paging {
        val common = Dep("androidx.paging", "paging-common", Ver("3.2.1", 0), Ver("3.3.0-alpha02", 300))
        val common_ktx = Dep("androidx.paging", "paging-common-ktx", Ver("3.2.1", 0), Ver("3.3.0-alpha02", 300))
        val compose = Dep("androidx.paging", "paging-compose", Ver("3.2.1", 0), Ver("3.3.0-alpha02", 300))
        val guava = Dep("androidx.paging", "paging-guava", Ver("3.2.1", 0), Ver("3.3.0-alpha02", 300))
        val runtime = Dep("androidx.paging", "paging-runtime", Ver("3.2.1", 0), Ver("3.3.0-alpha02", 300))
        val runtime_ktx = Dep("androidx.paging", "paging-runtime-ktx", Ver("3.2.1", 0), Ver("3.3.0-alpha02", 300))
        val rxjava2 = Dep("androidx.paging", "paging-rxjava2", Ver("3.2.1", 0), Ver("3.3.0-alpha02", 300))
        val rxjava2_ktx = Dep("androidx.paging", "paging-rxjava2-ktx", Ver("3.2.1", 0), Ver("3.3.0-alpha02", 300))
        val rxjava3 = Dep("androidx.paging", "paging-rxjava3", Ver("3.2.1", 0), Ver("3.3.0-alpha02", 300))
        val testing = Dep("androidx.paging", "paging-testing", Ver("3.2.1", 0), Ver("3.3.0-alpha02", 300))
    }
    object Palette {
        val ktx = Dep("androidx.palette", "palette-ktx", Ver("1.0.0", 0))
        val palette = Dep("androidx.palette", "palette", Ver("1.0.0", 0))
    }
    object PercentLayout {
        val percentlayout = Dep("androidx.percentlayout", "percentlayout", Ver("1.0.0", 0))
    }
    object Preference {
        val ktx = Dep("androidx.preference", "preference-ktx", Ver("1.2.1", 0))
        val preference = Dep("androidx.preference", "preference", Ver("1.2.1", 0))
    }
    object Print {
        val print = Dep("androidx.print", "print", Ver("1.0.0", 0), Ver("1.1.0-beta01", 200))
    }
    object Recommendation {
        val recommendation = Dep("androidx.recommendation", "recommendation", Ver("1.0.0", 0))
    }
    object RecyclerView {
        val recyclerview = Dep("androidx.recyclerview", "recyclerview", Ver("1.3.2", 0), Ver("1.4.0-alpha01", 300))
        val selection = Dep("androidx.recyclerview", "recyclerview-selection", Ver("1.1.0", 0), Ver("1.2.0-alpha01", 300))
    }
    object Remotecallback {
        val processor = Dep("androidx.remotecallback", "remotecallback-processor", Ver("1.0.0-alpha02", 300))
        val remotecallback = Dep("androidx.remotecallback", "remotecallback", Ver("1.0.0-alpha02", 300))
    }
    object Room {
        val common = Dep("androidx.room", "room-common", Ver("2.6.1", 0))
        val compiler = Dep("androidx.room", "room-compiler", Ver("2.6.1", 0))
        val guava = Dep("androidx.room", "room-guava", Ver("2.6.1", 0))
        val ktx = Dep("androidx.room", "room-ktx", Ver("2.6.1", 0))
        val paging = Dep("androidx.room", "room-paging", Ver("2.6.1", 0))
        val paging_guava = Dep("androidx.room", "room-paging-guava", Ver("2.6.1", 0))
        val paging_rxjava2 = Dep("androidx.room", "room-paging-rxjava2", Ver("2.6.1", 0))
        val paging_rxjava3 = Dep("androidx.room", "room-paging-rxjava3", Ver("2.6.1", 0))
        val runtime = Dep("androidx.room", "room-runtime", Ver("2.6.1", 0))
        val rxjava2 = Dep("androidx.room", "room-rxjava2", Ver("2.6.1", 0))
        val rxjava3 = Dep("androidx.room", "room-rxjava3", Ver("2.6.1", 0))
        val testing = Dep("androidx.room", "room-testing", Ver("2.6.1", 0))
    }
    object Savedstate {
        val ktx = Dep("androidx.savedstate", "savedstate-ktx", Ver("1.2.1", 0))
        val savedstate = Dep("androidx.savedstate", "savedstate", Ver("1.2.1", 0))
    }
    object Security {
        val app_authenticator = Dep("androidx.security", "security-app-authenticator", Ver("1.0.0-alpha02", 300))
        val app_authenticator_testing = Dep("androidx.security", "security-app-authenticator-testing", Ver("1.0.0-alpha01", 300))
        val crypto = Dep("androidx.security", "security-crypto", Ver("1.0.0", 0), Ver("1.1.0-alpha06", 300))
        val crypto_ktx = Dep("androidx.security", "security-crypto-ktx", Ver("1.1.0-alpha06", 300))
        val identity_credential = Dep("androidx.security", "security-identity-credential", Ver("1.0.0-alpha03", 300))
    }
    object ShareTarget {
        val sharetarget = Dep("androidx.sharetarget", "sharetarget", Ver("1.2.0", 0))
    }
    object Slice {
        val builders = Dep("androidx.slice", "slice-builders", Ver("1.0.0", 0), Ver("1.1.0-alpha02", 300))
        val builders_ktx = Dep("androidx.slice", "slice-builders-ktx", Ver("1.0.0-alpha08", 300))
        val core = Dep("androidx.slice", "slice-core", Ver("1.0.0", 0), Ver("1.1.0-alpha02", 300))
        val view = Dep("androidx.slice", "slice-view", Ver("1.0.0", 0), Ver("1.1.0-alpha02", 300))
    }
    object SlidingpaneLayout {
        val slidingpanelayout = Dep("androidx.slidingpanelayout", "slidingpanelayout", Ver("1.2.0", 0))
    }
    object Sqlite {
        val framework = Dep("androidx.sqlite", "sqlite-framework", Ver("2.4.0", 0))
        val ktx = Dep("androidx.sqlite", "sqlite-ktx", Ver("2.4.0", 0))
        val sqlite = Dep("androidx.sqlite", "sqlite", Ver("2.4.0", 0))
    }
    object Startup {
        val runtime = Dep("androidx.startup", "startup-runtime", Ver("1.1.1", 0), Ver("1.2.0-alpha02", 300))
    }
    object SwiperefreshLayout {
        val swiperefreshlayout = Dep("androidx.swiperefreshlayout", "swiperefreshlayout", Ver("1.1.0", 0), Ver("1.2.0-alpha01", 300))
    }
    object Test {
        val core = Dep("androidx.test", "core", Ver("1.5.0", 0), Ver("1.6.0-alpha02", 300))
        val core_ktx = Dep("androidx.test", "core-ktx", Ver("1.5.0", 0), Ver("1.6.0-alpha02", 300))
        val monitor = Dep("androidx.test", "monitor", Ver("1.6.1", 0), Ver("1.7.0-alpha02", 300))
        val orchestrator = Dep("androidx.test", "orchestrator", Ver("1.4.2", 0), Ver("1.5.0-alpha01", 300))
        val rules = Dep("androidx.test", "rules", Ver("1.5.0", 0), Ver("1.6.0-alpha01", 300))
        val runner = Dep("androidx.test", "runner", Ver("1.5.2", 0), Ver("1.6.0-alpha04", 300))
        object Espresso {
            val accessibility = Dep("androidx.test.espresso", "espresso-accessibility", Ver("3.5.1", 0), Ver("3.6.0-alpha01", 300))
            val contrib = Dep("androidx.test.espresso", "espresso-contrib", Ver("3.5.1", 0), Ver("3.6.0-alpha01", 300))
            val core = Dep("androidx.test.espresso", "espresso-core", Ver("3.5.1", 0), Ver("3.6.0-alpha01", 300))
            val device = Dep("androidx.test.espresso", "espresso-device", Ver("1.0.0-alpha06", 300))
            val idling_resource = Dep("androidx.test.espresso", "espresso-idling-resource", Ver("3.5.1", 0), Ver("3.6.0-alpha01", 300))
            val intents = Dep("androidx.test.espresso", "espresso-intents", Ver("3.5.1", 0), Ver("3.6.0-alpha01", 300))
            val remote = Dep("androidx.test.espresso", "espresso-remote", Ver("3.5.1", 0), Ver("3.6.0-alpha01", 300))
            val web = Dep("androidx.test.espresso", "espresso-web", Ver("3.5.1", 0), Ver("3.6.0-alpha01", 300))
            object Idling {
                val concurrent = Dep("androidx.test.espresso.idling", "idling-concurrent", Ver("3.5.1", 0), Ver("3.6.0-alpha01", 300))
                val net = Dep("androidx.test.espresso.idling", "idling-net", Ver("3.5.1", 0), Ver("3.6.0-alpha01", 300))
            }
        }
        object Ext {
            val junit = Dep("androidx.test.ext", "junit", Ver("1.1.5", 0), Ver("1.2.0-alpha01", 300))
            val junit_gtest = Dep("androidx.test.ext", "junit-gtest", Ver("1.0.0-alpha02", 300))
            val junit_ktx = Dep("androidx.test.ext", "junit-ktx", Ver("1.1.5", 0), Ver("1.2.0-alpha01", 300))
            val truth = Dep("androidx.test.ext", "truth", Ver("1.5.0", 0), Ver("1.6.0-alpha01", 300))
        }
        object Services {
            val test_services = Dep("androidx.test.services", "test-services", Ver("1.4.2", 0), Ver("1.5.0-alpha01", 300))
        }
        object UiAutoMator {
            val uiautomator = Dep("androidx.test.uiautomator", "uiautomator", Ver("2.2.0", 0), Ver("2.3.0-alpha05", 300))
        }
    }
    object TextClassifier {
        val textclassifier = Dep("androidx.textclassifier", "textclassifier", Ver("1.0.0-alpha04", 300))
    }
    object Tracing {
        val ktx = Dep("androidx.tracing", "tracing-ktx", Ver("1.2.0", 0), Ver("1.3.0-alpha02", 300))
        val perfetto = Dep("androidx.tracing", "tracing-perfetto", Ver("1.0.0", 0))
        val tracing = Dep("androidx.tracing", "tracing", Ver("1.2.0", 0), Ver("1.3.0-alpha02", 300))
    }
    object Transition {
        val ktx = Dep("androidx.transition", "transition-ktx", Ver("1.4.1", 0), Ver("1.5.0-alpha05", 300))
        val transition = Dep("androidx.transition", "transition", Ver("1.4.1", 0), Ver("1.5.0-alpha05", 300))
    }
    object Tv {
        val foundation = Dep("androidx.tv", "tv-foundation", Ver("1.0.0-alpha10", 300))
        val material = Dep("androidx.tv", "tv-material", Ver("1.0.0-alpha10", 300))
    }
    object TvProvider {
        val tvprovider = Dep("androidx.tvprovider", "tvprovider", Ver("1.0.0", 0), Ver("1.1.0-alpha01", 300))
    }
    object Vectordrawable {
        val animated = Dep("androidx.vectordrawable", "vectordrawable-animated", Ver("1.1.0", 0))
        val seekable = Dep("androidx.vectordrawable", "vectordrawable-seekable", Ver("1.0.0-beta01", 200))
        val vectordrawable = Dep("androidx.vectordrawable", "vectordrawable", Ver("1.1.0", 0), Ver("1.2.0-beta01", 200))
    }
    object Versionedparcelable {
        val versionedparcelable = Dep("androidx.versionedparcelable", "versionedparcelable", Ver("1.1.1", 0), Ver("1.2.0-beta01", 200))
    }
    object ViewPager {
        val viewpager = Dep("androidx.viewpager", "viewpager", Ver("1.0.0", 0), Ver("1.1.0-alpha01", 300))
    }
    object ViewPager2 {
        val viewpager2 = Dep("androidx.viewpager2", "viewpager2", Ver("1.0.0", 0), Ver("1.1.0-beta02", 200))
    }
    object Wear {
        val input = Dep("androidx.wear", "wear-input", Ver("1.1.0", 0), Ver("1.2.0-alpha02", 300))
        val input_testing = Dep("androidx.wear", "wear-input-testing", Ver("1.1.0", 0), Ver("1.2.0-alpha02", 300))
        val ongoing = Dep("androidx.wear", "wear-ongoing", Ver("1.0.0", 0), Ver("1.1.0-alpha01", 300))
        val phone_interactions = Dep("androidx.wear", "wear-phone-interactions", Ver("1.0.1", 0), Ver("1.1.0-alpha03", 300))
        val remote_interactions = Dep("androidx.wear", "wear-remote-interactions", Ver("1.0.0", 0), Ver("1.1.0-alpha01", 300))
        val wear = Dep("androidx.wear", "wear", Ver("1.3.0", 0), Ver("1.4.0-alpha01", 300))
        object Compose {
            val foundation = Dep("androidx.wear.compose", "compose-foundation", Ver("1.2.1", 0), Ver("1.3.0-beta01", 200))
            val material = Dep("androidx.wear.compose", "compose-material", Ver("1.2.1", 0), Ver("1.3.0-beta01", 200))
            val navigation = Dep("androidx.wear.compose", "compose-navigation", Ver("1.2.1", 0), Ver("1.3.0-beta01", 200))
        }
        object Tiles {
            val material = Dep("androidx.wear.tiles", "tiles-material", Ver("1.2.0", 0), Ver("1.3.0-alpha03", 300))
            val renderer = Dep("androidx.wear.tiles", "tiles-renderer", Ver("1.2.0", 0), Ver("1.3.0-alpha03", 300))
            val testing = Dep("androidx.wear.tiles", "tiles-testing", Ver("1.2.0", 0), Ver("1.3.0-alpha03", 300))
            val tiles = Dep("androidx.wear.tiles", "tiles", Ver("1.2.0", 0), Ver("1.3.0-alpha03", 300))
        }
        object Watchface {
            val complications_data_source = Dep("androidx.wear.watchface", "watchface-complications-data-source", Ver("1.2.0", 0))
            val complications_data_source_ktx = Dep("androidx.wear.watchface", "watchface-complications-data-source-ktx", Ver("1.2.0", 0))
            val complications_rendering = Dep("androidx.wear.watchface", "watchface-complications-rendering", Ver("1.2.0", 0))
            val editor = Dep("androidx.wear.watchface", "watchface-editor", Ver("1.2.0", 0))
            val watchface = Dep("androidx.wear.watchface", "watchface", Ver("1.2.0", 0))
        }
    }
    object WebKit {
        val webkit = Dep("androidx.webkit", "webkit", Ver("1.9.0", 0), Ver("1.10.0-alpha01", 300))
    }
    object Window {
        val java = Dep("androidx.window", "window-java", Ver("1.2.0", 0), Ver("1.3.0-alpha01", 300))
        val rxjava2 = Dep("androidx.window", "window-rxjava2", Ver("1.2.0", 0), Ver("1.3.0-alpha01", 300))
        val rxjava3 = Dep("androidx.window", "window-rxjava3", Ver("1.2.0", 0), Ver("1.3.0-alpha01", 300))
        val testing = Dep("androidx.window", "window-testing", Ver("1.2.0", 0), Ver("1.3.0-alpha01", 300))
        val window = Dep("androidx.window", "window", Ver("1.2.0", 0), Ver("1.3.0-alpha01", 300))
    }
    object Work {
        val gcm = Dep("androidx.work", "work-gcm", Ver("2.9.0", 0))
        val multiprocess = Dep("androidx.work", "work-multiprocess", Ver("2.9.0", 0))
        val runtime = Dep("androidx.work", "work-runtime", Ver("2.9.0", 0))
        val runtime_ktx = Dep("androidx.work", "work-runtime-ktx", Ver("2.9.0", 0))
        val rxjava2 = Dep("androidx.work", "work-rxjava2", Ver("2.9.0", 0))
        val rxjava3 = Dep("androidx.work", "work-rxjava3", Ver("2.9.0", 0))
        val testing = Dep("androidx.work", "work-testing", Ver("2.9.0", 0))
    }
}
object App {
    object Cash {
        object Copper {
            val flow = Dep("app.cash.copper", "copper-flow", Ver("1.0.0", 0))
            val rx2 = Dep("app.cash.copper", "copper-rx2", Ver("1.0.0", 0))
            val rx3 = Dep("app.cash.copper", "copper-rx3", Ver("1.0.0", 0))
        }
        object Licensee {
            val gradle_plugin = Dep("app.cash.licensee", "licensee-gradle-plugin", Ver("1.8.0", 0))
        }
        object Molecule {
            val gradle_plugin = Dep("app.cash.molecule", "molecule-gradle-plugin", Ver("1.3.1", 0))
            val runtime = Dep("app.cash.molecule", "molecule-runtime", Ver("1.3.1", 0))
        }
        object Turbine {
            val turbine = Dep("app.cash.turbine", "turbine", Ver("1.0.0", 0))
        }
    }
}
object Co {
    object TouchLab {
        val kermit = Dep("co.touchlab", "kermit", Ver("2.0.2", 0))
        val kermit_bugsnag = Dep("co.touchlab", "kermit-bugsnag", Ver("2.0.2", 0))
        val kermit_bugsnag_test = Dep("co.touchlab", "kermit-bugsnag-test", Ver("1.1.3", 0), Ver("1.2.0-M2", 120))
        val kermit_crashlytics = Dep("co.touchlab", "kermit-crashlytics", Ver("2.0.2", 0))
        val kermit_crashlytics_test = Dep("co.touchlab", "kermit-crashlytics-test", Ver("1.1.3", 0), Ver("1.2.0-M2", 120))
        val kermit_gradle_plugin = Dep("co.touchlab", "kermit-gradle-plugin", Ver("1.2.3", 0))
        val kermit_test = Dep("co.touchlab", "kermit-test", Ver("2.0.2", 0))
        val stately_common = Dep("co.touchlab", "stately-common", Ver("2.0.5", 0))
        val stately_concurrency = Dep("co.touchlab", "stately-concurrency", Ver("2.0.5", 0))
        val stately_iso_collections = Dep("co.touchlab", "stately-iso-collections", Ver("2.0.5", 0))
        val stately_isolate = Dep("co.touchlab", "stately-isolate", Ver("2.0.5", 0))
    }
}
object Com {
    object Android {
        object Billingclient {
            val billing = Dep("com.android.billingclient", "billing", Ver("2.0.3", 0), Ver("6.1.0", 0))
            val billing_ktx = Dep("com.android.billingclient", "billing-ktx", Ver("6.1.0", 0))
        }
        object Installreferrer {
            val installreferrer = Dep("com.android.installreferrer", "installreferrer", Ver("2.2", 0))
        }
        object Tools {
            val desugar_jdk_libs = Dep("com.android.tools", "desugar_jdk_libs", Ver("2.0.4", 0))
            val r8 = Dep("com.android.tools", "r8", Ver("8.1.72", 0))
            object Build {
                val gradle = Dep("com.android.tools.build", "gradle", Ver("2.3.0", 0), Ver("2.3.3", 0), Ver("2.4.0-alpha7", 300), Ver("2.5.0-alpha-preview-02", 400), Ver("8.1.4", 0), Ver("8.2.0-rc03", 100), Ver("8.3.0-alpha15", 300))
            }
        }
    }
    object ApolloGraphQl {
        object Apollo3 {
            val apollo_adapters = Dep("com.apollographql.apollo3", "apollo-adapters", Ver("3.8.2", 0), Ver("4.0.0-beta.2", 200))
            val apollo_api = Dep("com.apollographql.apollo3", "apollo-api", Ver("3.8.2", 0), Ver("4.0.0-beta.2", 200))
            val apollo_ast = Dep("com.apollographql.apollo3", "apollo-ast", Ver("3.8.2", 0), Ver("4.0.0-beta.2", 200))
            val apollo_http_cache = Dep("com.apollographql.apollo3", "apollo-http-cache", Ver("3.8.2", 0), Ver("4.0.0-beta.2", 200))
            val apollo_idling_resource = Dep("com.apollographql.apollo3", "apollo-idling-resource", Ver("3.8.2", 0), Ver("4.0.0-beta.2", 200))
            val apollo_mockserver = Dep("com.apollographql.apollo3", "apollo-mockserver", Ver("3.8.2", 0), Ver("4.0.0-beta.2", 200))
            val apollo_normalized_cache = Dep("com.apollographql.apollo3", "apollo-normalized-cache", Ver("3.8.2", 0), Ver("4.0.0-beta.2", 200))
            val apollo_normalized_cache_sqlite = Dep("com.apollographql.apollo3", "apollo-normalized-cache-sqlite", Ver("3.8.2", 0), Ver("4.0.0-beta.2", 200))
            val apollo_runtime = Dep("com.apollographql.apollo3", "apollo-runtime", Ver("3.8.2", 0), Ver("4.0.0-beta.2", 200))
            val apollo_testing_support = Dep("com.apollographql.apollo3", "apollo-testing-support", Ver("3.8.2", 0), Ver("4.0.0-beta.2", 200))
        }
    }
    object GitHub {
        object ChuckerTeam {
            object Chucker {
                val library = Dep("com.github.chuckerteam.chucker", "library", Ver("4.0.0", 0))
                val library_no_op = Dep("com.github.chuckerteam.chucker", "library-no-op", Ver("4.0.0", 0))
            }
        }
    }
    object Google {
        object Accompanist {
            val appcompat_theme = Dep("com.google.accompanist", "accompanist-appcompat-theme", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
            val coil = Dep("com.google.accompanist", "accompanist-coil", Ver("0.15.0", 0))
            val drawablepainter = Dep("com.google.accompanist", "accompanist-drawablepainter", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
            val flowlayout = Dep("com.google.accompanist", "accompanist-flowlayout", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
            val glide = Dep("com.google.accompanist", "accompanist-glide", Ver("0.15.0", 0))
            val imageloading_core = Dep("com.google.accompanist", "accompanist-imageloading-core", Ver("0.15.0", 0))
            val insets = Dep("com.google.accompanist", "accompanist-insets", Ver("0.30.1", 0), Ver("0.31.5-beta", 200))
            val insets_ui = Dep("com.google.accompanist", "accompanist-insets-ui", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
            val navigation_animation = Dep("com.google.accompanist", "accompanist-navigation-animation", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
            val navigation_material = Dep("com.google.accompanist", "accompanist-navigation-material", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
            val pager = Dep("com.google.accompanist", "accompanist-pager", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
            val pager_indicators = Dep("com.google.accompanist", "accompanist-pager-indicators", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
            val permissions = Dep("com.google.accompanist", "accompanist-permissions", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
            val picasso = Dep("com.google.accompanist", "accompanist-picasso", Ver("0.6.2", 0))
            val placeholder = Dep("com.google.accompanist", "accompanist-placeholder", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
            val placeholder_material = Dep("com.google.accompanist", "accompanist-placeholder-material", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
            val swiperefresh = Dep("com.google.accompanist", "accompanist-swiperefresh", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
            val systemuicontroller = Dep("com.google.accompanist", "accompanist-systemuicontroller", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
            val webview = Dep("com.google.accompanist", "accompanist-webview", Ver("0.32.0", 0), Ver("0.33.2-alpha", 300))
        }
        object Ambient {
            object Crossdevice {
                val crossdevice = Dep("com.google.ambient.crossdevice", "crossdevice", Ver("0.1.0-preview01", 400))
            }
        }
        object Android {
            object Fhir {
                val data_capture = Dep("com.google.android.fhir", "data-capture", Ver("1.0.0", 0))
                val engine = Dep("com.google.android.fhir", "engine", Ver("0.1.0-beta05", 200))
                val workflow = Dep("com.google.android.fhir", "workflow", Ver("0.1.0-alpha04", 300))
            }
            object Flexbox {
                val flexbox = Dep("com.google.android.flexbox", "flexbox", Ver("3.0.0", 0))
            }
            object Gms {
                val oss_licenses_plugin = Dep("com.google.android.gms", "oss-licenses-plugin", Ver("0.10.6", 0))
                val play_services_analytics = Dep("com.google.android.gms", "play-services-analytics", Ver("18.0.4", 0))
                val play_services_appset = Dep("com.google.android.gms", "play-services-appset", Ver("16.0.2", 0))
                val play_services_auth = Dep("com.google.android.gms", "play-services-auth", Ver("20.7.0", 0))
                val play_services_auth_api_phone = Dep("com.google.android.gms", "play-services-auth-api-phone", Ver("18.0.1", 0))
                val play_services_auth_blockstore = Dep("com.google.android.gms", "play-services-auth-blockstore", Ver("16.2.0", 0))
                val play_services_awareness = Dep("com.google.android.gms", "play-services-awareness", Ver("19.0.1", 0))
                val play_services_base = Dep("com.google.android.gms", "play-services-base", Ver("18.2.0", 0))
                val play_services_basement = Dep("com.google.android.gms", "play-services-basement", Ver("18.2.0", 0))
                val play_services_cast = Dep("com.google.android.gms", "play-services-cast", Ver("21.3.0", 0))
                val play_services_cast_framework = Dep("com.google.android.gms", "play-services-cast-framework", Ver("21.3.0", 0))
                val play_services_cast_tv = Dep("com.google.android.gms", "play-services-cast-tv", Ver("21.0.0", 0))
                val play_services_cronet = Dep("com.google.android.gms", "play-services-cronet", Ver("18.0.1", 0))
                val play_services_drive = Dep("com.google.android.gms", "play-services-drive", Ver("17.0.0", 0))
                val play_services_fido = Dep("com.google.android.gms", "play-services-fido", Ver("20.1.0", 0))
                val play_services_fitness = Dep("com.google.android.gms", "play-services-fitness", Ver("21.1.0", 0))
                val play_services_games = Dep("com.google.android.gms", "play-services-games", Ver("23.1.0", 0))
                val play_services_gcm = Dep("com.google.android.gms", "play-services-gcm", Ver("17.0.0", 0))
                val play_services_identity = Dep("com.google.android.gms", "play-services-identity", Ver("18.0.1", 0))
                val play_services_instantapps = Dep("com.google.android.gms", "play-services-instantapps", Ver("18.0.1", 0))
                val play_services_location = Dep("com.google.android.gms", "play-services-location", Ver("21.0.1", 0))
                val play_services_maps = Dep("com.google.android.gms", "play-services-maps", Ver("18.2.0", 0))
                val play_services_mlkit_barcode_scanning = Dep("com.google.android.gms", "play-services-mlkit-barcode-scanning", Ver("18.3.0", 0))
                val play_services_mlkit_face_detection = Dep("com.google.android.gms", "play-services-mlkit-face-detection", Ver("17.1.0", 0))
                val play_services_mlkit_image_labeling = Dep("com.google.android.gms", "play-services-mlkit-image-labeling", Ver("16.0.8", 0))
                val play_services_mlkit_image_labeling_custom = Dep("com.google.android.gms", "play-services-mlkit-image-labeling-custom", Ver("16.0.0-beta5", 200))
                val play_services_mlkit_language_id = Dep("com.google.android.gms", "play-services-mlkit-language-id", Ver("17.0.0", 0))
                val play_services_mlkit_text_recognition = Dep("com.google.android.gms", "play-services-mlkit-text-recognition", Ver("19.0.0", 0))
                val play_services_nearby = Dep("com.google.android.gms", "play-services-nearby", Ver("19.0.0", 0))
                val play_services_oss_licenses = Dep("com.google.android.gms", "play-services-oss-licenses", Ver("17.0.1", 0))
                val play_services_panorama = Dep("com.google.android.gms", "play-services-panorama", Ver("17.1.0", 0))
                val play_services_password_complexity = Dep("com.google.android.gms", "play-services-password-complexity", Ver("18.0.1", 0))
                val play_services_pay = Dep("com.google.android.gms", "play-services-pay", Ver("16.4.0", 0))
                val play_services_recaptcha = Dep("com.google.android.gms", "play-services-recaptcha", Ver("17.0.1", 0))
                val play_services_safetynet = Dep("com.google.android.gms", "play-services-safetynet", Ver("18.0.1", 0))
                val play_services_tagmanager = Dep("com.google.android.gms", "play-services-tagmanager", Ver("18.0.4", 0))
                val play_services_tasks = Dep("com.google.android.gms", "play-services-tasks", Ver("18.0.2", 0))
                val play_services_vision = Dep("com.google.android.gms", "play-services-vision", Ver("20.1.3", 0))
                val play_services_wallet = Dep("com.google.android.gms", "play-services-wallet", Ver("19.2.1", 0))
                val play_services_wearable = Dep("com.google.android.gms", "play-services-wearable", Ver("18.1.0", 0))
                val strict_version_matcher_plugin = Dep("com.google.android.gms", "strict-version-matcher-plugin", Ver("1.2.4", 0))
            }
            object Horologist {
                val audio = Dep("com.google.android.horologist", "horologist-audio", Ver("0.5.12", 0))
                val audio_ui = Dep("com.google.android.horologist", "horologist-audio-ui", Ver("0.5.12", 0))
                val composables = Dep("com.google.android.horologist", "horologist-composables", Ver("0.5.12", 0))
                val compose_layout = Dep("com.google.android.horologist", "horologist-compose-layout", Ver("0.5.12", 0))
                val compose_tools = Dep("com.google.android.horologist", "horologist-compose-tools", Ver("0.5.12", 0))
                val datalayer = Dep("com.google.android.horologist", "horologist-datalayer", Ver("0.5.12", 0))
                val media = Dep("com.google.android.horologist", "horologist-media", Ver("0.5.12", 0))
                val media3_backend = Dep("com.google.android.horologist", "horologist-media3-backend", Ver("0.5.12", 0))
                val media_data = Dep("com.google.android.horologist", "horologist-media-data", Ver("0.5.12", 0))
                val media_ui = Dep("com.google.android.horologist", "horologist-media-ui", Ver("0.5.12", 0))
                val network_awareness = Dep("com.google.android.horologist", "horologist-network-awareness", Ver("0.5.12", 0))
                val tiles = Dep("com.google.android.horologist", "horologist-tiles", Ver("0.5.12", 0))
            }
            object Libraries {
                object Places {
                    val places = Dep("com.google.android.libraries.places", "places", Ver("3.3.0", 0))
                }
            }
            object Material {
                val compose_theme_adapter = Dep("com.google.android.material", "compose-theme-adapter", Ver("1.2.1", 0))
                val compose_theme_adapter_3 = Dep("com.google.android.material", "compose-theme-adapter-3", Ver("1.1.1", 0))
                val material = Dep("com.google.android.material", "material", Ver("1.10.0", 0), Ver("1.11.0-rc01", 100), Ver("1.12.0-alpha01", 300))
            }
            object Play {
                val core = Dep("com.google.android.play", "core", Ver("1.10.3", 0))
                val core_ktx = Dep("com.google.android.play", "core-ktx", Ver("1.8.1", 0))
            }
            object Support {
                val wearable = Dep("com.google.android.support", "wearable", Ver("2.9.0", 0))
            }
            object Wearable {
                val wearable = Dep("com.google.android.wearable", "wearable", Ver("2.9.0", 0))
            }
        }
        object AndroidBrowserHelper {
            val androidbrowserhelper = Dep("com.google.androidbrowserhelper", "androidbrowserhelper", Ver("2.5.0", 0))
        }
        object Ar {
            val core = Dep("com.google.ar", "core", Ver("1.40.0", 0))
            object Sceneform {
                val animation = Dep("com.google.ar.sceneform", "animation", Ver("1.17.1", 0))
                val assets = Dep("com.google.ar.sceneform", "assets", Ver("1.17.1", 0))
                val base = Dep("com.google.ar.sceneform", "sceneform-base", Ver("1.17.1", 0))
                val core = Dep("com.google.ar.sceneform", "core", Ver("1.17.1", 0))
                val filament_android = Dep("com.google.ar.sceneform", "filament-android", Ver("1.17.1", 0))
                val plugin = Dep("com.google.ar.sceneform", "plugin", Ver("1.17.1", 0))
                val rendering = Dep("com.google.ar.sceneform", "rendering", Ver("1.17.1", 0))
                object Ux {
                    val sceneform_ux = Dep("com.google.ar.sceneform.ux", "sceneform-ux", Ver("1.17.1", 0))
                }
            }
        }
        object Dagger {
            val android = Dep("com.google.dagger", "dagger-android", Ver("2.48.1", 0))
            val android_processor = Dep("com.google.dagger", "dagger-android-processor", Ver("2.48.1", 0))
            val android_support = Dep("com.google.dagger", "dagger-android-support", Ver("2.48.1", 0))
            val compiler = Dep("com.google.dagger", "dagger-compiler", Ver("2.48.1", 0))
            val dagger = Dep("com.google.dagger", "dagger", Ver("2.48.1", 0))
            val grpc_server = Dep("com.google.dagger", "dagger-grpc-server", Ver("2.48.1", 0))
            val grpc_server_annotations = Dep("com.google.dagger", "dagger-grpc-server-annotations", Ver("2.48.1", 0))
            val grpc_server_processor = Dep("com.google.dagger", "dagger-grpc-server-processor", Ver("2.48.1", 0))
            val gwt = Dep("com.google.dagger", "dagger-gwt", Ver("2.48.1", 0))
            val hilt_android = Dep("com.google.dagger", "hilt-android", Ver("2.48.1", 0))
            val hilt_android_compiler = Dep("com.google.dagger", "hilt-android-compiler", Ver("2.48.1", 0))
            val hilt_android_gradle_plugin = Dep("com.google.dagger", "hilt-android-gradle-plugin", Ver("2.48.1", 0))
            val hilt_android_testing = Dep("com.google.dagger", "hilt-android-testing", Ver("2.48.1", 0))
            val hilt_compiler = Dep("com.google.dagger", "hilt-compiler", Ver("2.48.1", 0))
            val producers = Dep("com.google.dagger", "dagger-producers", Ver("2.48.1", 0))
            val spi = Dep("com.google.dagger", "dagger-spi", Ver("2.48.1", 0))
        }
        object Firebase {
            val analytics = Dep("com.google.firebase", "firebase-analytics", Ver("21.5.0", 0))
            val analytics_ktx = Dep("com.google.firebase", "firebase-analytics-ktx", Ver("21.5.0", 0))
            val appdistribution_gradle = Dep("com.google.firebase", "firebase-appdistribution-gradle", Ver("4.0.1", 0))
            val appindexing = Dep("com.google.firebase", "firebase-appindexing", Ver("20.0.0", 0))
            val auth = Dep("com.google.firebase", "firebase-auth", Ver("22.3.0", 0))
            val auth_ktx = Dep("com.google.firebase", "firebase-auth-ktx", Ver("22.3.0", 0))
            val bom = Dep("com.google.firebase", "firebase-bom", Ver("32.6.0", 0))
            val config = Dep("com.google.firebase", "firebase-config", Ver("21.6.0", 0))
            val config_ktx = Dep("com.google.firebase", "firebase-config-ktx", Ver("21.6.0", 0))
            val crashlytics = Dep("com.google.firebase", "firebase-crashlytics", Ver("18.6.0", 0))
            val crashlytics_gradle = Dep("com.google.firebase", "firebase-crashlytics-gradle", Ver("2.9.9", 0))
            val crashlytics_ktx = Dep("com.google.firebase", "firebase-crashlytics-ktx", Ver("18.6.0", 0))
            val crashlytics_ndk = Dep("com.google.firebase", "firebase-crashlytics-ndk", Ver("18.6.0", 0))
            val database = Dep("com.google.firebase", "firebase-database", Ver("20.3.0", 0))
            val database_ktx = Dep("com.google.firebase", "firebase-database-ktx", Ver("20.3.0", 0))
            val dynamic_links = Dep("com.google.firebase", "firebase-dynamic-links", Ver("21.2.0", 0))
            val dynamic_links_ktx = Dep("com.google.firebase", "firebase-dynamic-links-ktx", Ver("21.2.0", 0))
            val dynamic_module_support = Dep("com.google.firebase", "firebase-dynamic-module-support", Ver("16.0.0-beta03", 200))
            val firestore = Dep("com.google.firebase", "firebase-firestore", Ver("24.9.1", 0))
            val firestore_ktx = Dep("com.google.firebase", "firebase-firestore-ktx", Ver("24.9.1", 0))
            val functions = Dep("com.google.firebase", "firebase-functions", Ver("20.4.0", 0))
            val functions_ktx = Dep("com.google.firebase", "firebase-functions-ktx", Ver("20.4.0", 0))
            val inappmessaging = Dep("com.google.firebase", "firebase-inappmessaging", Ver("20.4.0", 0))
            val inappmessaging_display = Dep("com.google.firebase", "firebase-inappmessaging-display", Ver("20.4.0", 0))
            val inappmessaging_display_ktx = Dep("com.google.firebase", "firebase-inappmessaging-display-ktx", Ver("20.4.0", 0))
            val inappmessaging_ktx = Dep("com.google.firebase", "firebase-inappmessaging-ktx", Ver("20.4.0", 0))
            val messaging = Dep("com.google.firebase", "firebase-messaging", Ver("23.3.1", 0))
            val messaging_directboot = Dep("com.google.firebase", "firebase-messaging-directboot", Ver("23.3.1", 0))
            val messaging_ktx = Dep("com.google.firebase", "firebase-messaging-ktx", Ver("23.3.1", 0))
            val ml_modeldownloader = Dep("com.google.firebase", "firebase-ml-modeldownloader", Ver("24.2.1", 0))
            val ml_modeldownloader_ktx = Dep("com.google.firebase", "firebase-ml-modeldownloader-ktx", Ver("24.2.1", 0))
            val perf = Dep("com.google.firebase", "firebase-perf", Ver("20.5.1", 0))
            val perf_ktx = Dep("com.google.firebase", "firebase-perf-ktx", Ver("20.5.1", 0))
            val perf_plugin = Dep("com.google.firebase", "perf-plugin", Ver("1.4.2", 0))
            val storage = Dep("com.google.firebase", "firebase-storage", Ver("20.3.0", 0))
            val storage_ktx = Dep("com.google.firebase", "firebase-storage-ktx", Ver("20.3.0", 0))
        }
        object Gms {
            val google_services = Dep("com.google.gms", "google-services", Ver("3.1.1", 0), Ver("4.2.0", 0), Ver("4.4.0", 0))
        }
        object Maps {
            object Android {
                val maps_compose = Dep("com.google.maps.android", "maps-compose", Ver("4.3.0", 0))
                val maps_ktx = Dep("com.google.maps.android", "maps-ktx", Ver("5.0.0", 0))
                val maps_rx = Dep("com.google.maps.android", "maps-rx", Ver("1.0.0", 0))
                val maps_utils = Dep("com.google.maps.android", "android-maps-utils", Ver("3.8.0", 0))
                val maps_utils_ktx = Dep("com.google.maps.android", "maps-utils-ktx", Ver("5.0.0", 0))
            }
        }
        object MlKit {
            val barcode_scanning = Dep("com.google.mlkit", "barcode-scanning", Ver("17.2.0", 0))
            val digital_ink_recognition = Dep("com.google.mlkit", "digital-ink-recognition", Ver("18.1.0", 0))
            val entity_extraction = Dep("com.google.mlkit", "entity-extraction", Ver("16.0.0-beta4", 200))
            val face_detection = Dep("com.google.mlkit", "face-detection", Ver("16.1.5", 0))
            val image_labeling = Dep("com.google.mlkit", "image-labeling", Ver("17.0.7", 0))
            val image_labeling_custom = Dep("com.google.mlkit", "image-labeling-custom", Ver("17.0.2", 0))
            val language_id = Dep("com.google.mlkit", "language-id", Ver("17.0.4", 0))
            val linkfirebase = Dep("com.google.mlkit", "linkfirebase", Ver("17.0.0", 0))
            val object_detection = Dep("com.google.mlkit", "object-detection", Ver("17.0.0", 0))
            val object_detection_custom = Dep("com.google.mlkit", "object-detection-custom", Ver("17.0.1", 0))
            val playstore_dynamic_feature_support = Dep("com.google.mlkit", "playstore-dynamic-feature-support", Ver("16.0.0-beta2", 200))
            val pose_detection = Dep("com.google.mlkit", "pose-detection", Ver("17.0.0", 0), Ver("18.0.0-beta3", 200))
            val pose_detection_accurate = Dep("com.google.mlkit", "pose-detection-accurate", Ver("17.0.0", 0), Ver("18.0.0-beta3", 200))
            val segmentation_selfie = Dep("com.google.mlkit", "segmentation-selfie", Ver("16.0.0-beta4", 200))
            val smart_reply = Dep("com.google.mlkit", "smart-reply", Ver("17.0.2", 0))
            val text_recognition = Dep("com.google.mlkit", "text-recognition", Ver("16.0.0", 0))
            val text_recognition_chinese = Dep("com.google.mlkit", "text-recognition-chinese", Ver("16.0.0", 0))
            val text_recognition_devanagari = Dep("com.google.mlkit", "text-recognition-devanagari", Ver("16.0.0", 0))
            val text_recognition_japanese = Dep("com.google.mlkit", "text-recognition-japanese", Ver("16.0.0", 0))
            val text_recognition_korean = Dep("com.google.mlkit", "text-recognition-korean", Ver("16.0.0", 0))
            val translate = Dep("com.google.mlkit", "translate", Ver("17.0.2", 0))
        }
        object Modernstorage {
            val bom = Dep("com.google.modernstorage", "modernstorage-bom", Ver("1.0.0-alpha06", 300))
            val permissions = Dep("com.google.modernstorage", "modernstorage-permissions", Ver("1.0.0-alpha06", 300))
            val photopicker = Dep("com.google.modernstorage", "modernstorage-photopicker", Ver("1.0.0-alpha06", 300))
            val storage = Dep("com.google.modernstorage", "modernstorage-storage", Ver("1.0.0-alpha06", 300))
        }
        object Oboe {
            val oboe = Dep("com.google.oboe", "oboe", Ver("1.8.0", 0))
        }
        object Truth {
            val parent = Dep("com.google.truth", "truth-parent", Ver("1.1.5", 0))
            val truth = Dep("com.google.truth", "truth", Ver("1.1.5", 0))
        }
    }
    object JakeWharton {
        object Confundus {
            val gradle = Dep("com.jakewharton.confundus", "confundus-gradle", Ver("1.0.0", 0))
        }
        object Moshi {
            val shimo = Dep("com.jakewharton.moshi", "shimo", Ver("0.1.1", 0))
        }
        object Picnic {
            val picnic = Dep("com.jakewharton.picnic", "picnic", Ver("0.7.0", 0))
        }
        object Retrofit {
            val retrofit2_kotlinx_serialization_converter = Dep("com.jakewharton.retrofit", "retrofit2-kotlinx-serialization-converter", Ver("1.0.0", 0))
        }
        object RxBinding3 {
            val rxbinding = Dep("com.jakewharton.rxbinding3", "rxbinding", Ver("3.1.0", 0))
            val rxbinding_appcompat = Dep("com.jakewharton.rxbinding3", "rxbinding-appcompat", Ver("3.1.0", 0))
            val rxbinding_core = Dep("com.jakewharton.rxbinding3", "rxbinding-core", Ver("3.1.0", 0))
            val rxbinding_drawerlayout = Dep("com.jakewharton.rxbinding3", "rxbinding-drawerlayout", Ver("3.1.0", 0))
            val rxbinding_leanback = Dep("com.jakewharton.rxbinding3", "rxbinding-leanback", Ver("3.1.0", 0))
            val rxbinding_material = Dep("com.jakewharton.rxbinding3", "rxbinding-material", Ver("3.1.0", 0))
            val rxbinding_recyclerview = Dep("com.jakewharton.rxbinding3", "rxbinding-recyclerview", Ver("3.1.0", 0))
            val rxbinding_slidingpanelayout = Dep("com.jakewharton.rxbinding3", "rxbinding-slidingpanelayout", Ver("3.1.0", 0))
            val rxbinding_swiperefreshlayout = Dep("com.jakewharton.rxbinding3", "rxbinding-swiperefreshlayout", Ver("3.1.0", 0))
            val rxbinding_viewpager = Dep("com.jakewharton.rxbinding3", "rxbinding-viewpager", Ver("3.1.0", 0))
            val rxbinding_viewpager2 = Dep("com.jakewharton.rxbinding3", "rxbinding-viewpager2", Ver("3.1.0", 0))
        }
        object RxBinding4 {
            val rxbinding = Dep("com.jakewharton.rxbinding4", "rxbinding", Ver("4.0.0", 0))
            val rxbinding_appcompat = Dep("com.jakewharton.rxbinding4", "rxbinding-appcompat", Ver("4.0.0", 0))
            val rxbinding_core = Dep("com.jakewharton.rxbinding4", "rxbinding-core", Ver("4.0.0", 0))
            val rxbinding_drawerlayout = Dep("com.jakewharton.rxbinding4", "rxbinding-drawerlayout", Ver("4.0.0", 0))
            val rxbinding_leanback = Dep("com.jakewharton.rxbinding4", "rxbinding-leanback", Ver("4.0.0", 0))
            val rxbinding_material = Dep("com.jakewharton.rxbinding4", "rxbinding-material", Ver("4.0.0", 0))
            val rxbinding_recyclerview = Dep("com.jakewharton.rxbinding4", "rxbinding-recyclerview", Ver("4.0.0", 0))
            val rxbinding_slidingpanelayout = Dep("com.jakewharton.rxbinding4", "rxbinding-slidingpanelayout", Ver("4.0.0", 0))
            val rxbinding_swiperefreshlayout = Dep("com.jakewharton.rxbinding4", "rxbinding-swiperefreshlayout", Ver("4.0.0", 0))
            val rxbinding_viewpager = Dep("com.jakewharton.rxbinding4", "rxbinding-viewpager", Ver("4.0.0", 0))
            val rxbinding_viewpager2 = Dep("com.jakewharton.rxbinding4", "rxbinding-viewpager2", Ver("4.0.0", 0))
        }
        object RxRelay2 {
            val rxrelay = Dep("com.jakewharton.rxrelay2", "rxrelay", Ver("2.1.1", 0))
        }
        object RxRelay3 {
            val rxrelay = Dep("com.jakewharton.rxrelay3", "rxrelay", Ver("3.0.1", 0))
        }
        object Timber {
            val timber = Dep("com.jakewharton.timber", "timber", Ver("5.0.1", 0))
        }
        object Wormhole {
            val gradle = Dep("com.jakewharton.wormhole", "wormhole-gradle", Ver("0.3.1", 0))
        }
    }
    object Louiscad {
        object Splitties {
            val activities = Dep("com.louiscad.splitties", "splitties-activities", Ver("3.0.0", 0))
            val alertdialog = Dep("com.louiscad.splitties", "splitties-alertdialog", Ver("3.0.0", 0))
            val alertdialog_appcompat = Dep("com.louiscad.splitties", "splitties-alertdialog-appcompat", Ver("3.0.0", 0))
            val alertdialog_appcompat_coroutines = Dep("com.louiscad.splitties", "splitties-alertdialog-appcompat-coroutines", Ver("3.0.0", 0))
            val alertdialog_material = Dep("com.louiscad.splitties", "splitties-alertdialog-material", Ver("3.0.0", 0))
            val appctx = Dep("com.louiscad.splitties", "splitties-appctx", Ver("3.0.0", 0))
            val arch_lifecycle = Dep("com.louiscad.splitties", "splitties-arch-lifecycle", Ver("3.0.0", 0))
            val arch_room = Dep("com.louiscad.splitties", "splitties-arch-room", Ver("3.0.0", 0))
            val bitflags = Dep("com.louiscad.splitties", "splitties-bitflags", Ver("3.0.0", 0))
            val bundle = Dep("com.louiscad.splitties", "splitties-bundle", Ver("3.0.0", 0))
            val checkedlazy = Dep("com.louiscad.splitties", "splitties-checkedlazy", Ver("3.0.0", 0))
            val collections = Dep("com.louiscad.splitties", "splitties-collections", Ver("3.0.0", 0))
            val coroutines = Dep("com.louiscad.splitties", "splitties-coroutines", Ver("3.0.0", 0))
            val dimensions = Dep("com.louiscad.splitties", "splitties-dimensions", Ver("3.0.0", 0))
            val exceptions = Dep("com.louiscad.splitties", "splitties-exceptions", Ver("3.0.0", 0))
            val fragmentargs = Dep("com.louiscad.splitties", "splitties-fragmentargs", Ver("3.0.0", 0))
            val fragments = Dep("com.louiscad.splitties", "splitties-fragments", Ver("3.0.0", 0))
            val fun_pack_android_appcompat = Dep("com.louiscad.splitties", "splitties-fun-pack-android-appcompat", Ver("3.0.0", 0))
            val fun_pack_android_appcompat_with_views_dsl = Dep("com.louiscad.splitties", "splitties-fun-pack-android-appcompat-with-views-dsl", Ver("3.0.0", 0))
            val fun_pack_android_base = Dep("com.louiscad.splitties", "splitties-fun-pack-android-base", Ver("3.0.0", 0))
            val fun_pack_android_base_with_views_dsl = Dep("com.louiscad.splitties", "splitties-fun-pack-android-base-with-views-dsl", Ver("3.0.0", 0))
            val fun_pack_android_material_components = Dep("com.louiscad.splitties", "splitties-fun-pack-android-material-components", Ver("3.0.0", 0))
            val fun_pack_android_material_components_with_views_dsl = Dep("com.louiscad.splitties", "splitties-fun-pack-android-material-components-with-views-dsl", Ver("3.0.0", 0))
            val initprovider = Dep("com.louiscad.splitties", "splitties-initprovider", Ver("2.1.1", 0), Ver("3.0.0-beta06", 200))
            val intents = Dep("com.louiscad.splitties", "splitties-intents", Ver("3.0.0", 0))
            val lifecycle_coroutines = Dep("com.louiscad.splitties", "splitties-lifecycle-coroutines", Ver("3.0.0", 0))
            val mainhandler = Dep("com.louiscad.splitties", "splitties-mainhandler", Ver("3.0.0", 0))
            val mainthread = Dep("com.louiscad.splitties", "splitties-mainthread", Ver("3.0.0", 0))
            val material_colors = Dep("com.louiscad.splitties", "splitties-material-colors", Ver("3.0.0", 0))
            val material_lists = Dep("com.louiscad.splitties", "splitties-material-lists", Ver("3.0.0", 0))
            val permissions = Dep("com.louiscad.splitties", "splitties-permissions", Ver("3.0.0", 0))
            val preferences = Dep("com.louiscad.splitties", "splitties-preferences", Ver("3.0.0", 0))
            val resources = Dep("com.louiscad.splitties", "splitties-resources", Ver("3.0.0", 0))
            val snackbar = Dep("com.louiscad.splitties", "splitties-snackbar", Ver("3.0.0", 0))
            val stetho_init = Dep("com.louiscad.splitties", "splitties-stetho-init", Ver("3.0.0", 0))
            val systemservices = Dep("com.louiscad.splitties", "splitties-systemservices", Ver("3.0.0", 0))
            val toast = Dep("com.louiscad.splitties", "splitties-toast", Ver("3.0.0", 0))
            val typesaferecyclerview = Dep("com.louiscad.splitties", "splitties-typesaferecyclerview", Ver("3.0.0", 0))
            val views = Dep("com.louiscad.splitties", "splitties-views", Ver("3.0.0", 0))
            val views_appcompat = Dep("com.louiscad.splitties", "splitties-views-appcompat", Ver("3.0.0", 0))
            val views_cardview = Dep("com.louiscad.splitties", "splitties-views-cardview", Ver("3.0.0", 0))
            val views_coroutines = Dep("com.louiscad.splitties", "splitties-views-coroutines", Ver("3.0.0", 0))
            val views_coroutines_material = Dep("com.louiscad.splitties", "splitties-views-coroutines-material", Ver("3.0.0", 0))
            val views_dsl = Dep("com.louiscad.splitties", "splitties-views-dsl", Ver("3.0.0", 0))
            val views_dsl_appcompat = Dep("com.louiscad.splitties", "splitties-views-dsl-appcompat", Ver("3.0.0", 0))
            val views_dsl_constraintlayout = Dep("com.louiscad.splitties", "splitties-views-dsl-constraintlayout", Ver("3.0.0", 0))
            val views_dsl_coordinatorlayout = Dep("com.louiscad.splitties", "splitties-views-dsl-coordinatorlayout", Ver("3.0.0", 0))
            val views_dsl_material = Dep("com.louiscad.splitties", "splitties-views-dsl-material", Ver("3.0.0", 0))
            val views_dsl_recyclerview = Dep("com.louiscad.splitties", "splitties-views-dsl-recyclerview", Ver("3.0.0", 0))
            val views_material = Dep("com.louiscad.splitties", "splitties-views-material", Ver("3.0.0", 0))
            val views_recyclerview = Dep("com.louiscad.splitties", "splitties-views-recyclerview", Ver("3.0.0", 0))
            val views_selectable = Dep("com.louiscad.splitties", "splitties-views-selectable", Ver("3.0.0", 0))
            val views_selectable_appcompat = Dep("com.louiscad.splitties", "splitties-views-selectable-appcompat", Ver("3.0.0", 0))
            val views_selectable_constraintlayout = Dep("com.louiscad.splitties", "splitties-views-selectable-constraintlayout", Ver("3.0.0", 0))
        }
    }
    object Nhaarman {
        object MockitoKotlin2 {
            val mockito_kotlin = Dep("com.nhaarman.mockitokotlin2", "mockito-kotlin", Ver("2.2.0", 0))
        }
    }
    object Rickclephas {
        object Kmp {
            val nativecoroutines_annotations = Dep("com.rickclephas.kmp", "kmp-nativecoroutines-annotations", Ver("0.13.3", 0), Ver("1.0.0-ALPHA-22", 300))
            val nativecoroutines_compiler = Dep("com.rickclephas.kmp", "kmp-nativecoroutines-compiler", Ver("0.13.3", 0), Ver("1.0.0-ALPHA-22", 300))
            val nativecoroutines_compiler_embeddable = Dep("com.rickclephas.kmp", "kmp-nativecoroutines-compiler-embeddable", Ver("0.13.3", 0), Ver("1.0.0-ALPHA-22", 300))
            val nativecoroutines_core = Dep("com.rickclephas.kmp", "kmp-nativecoroutines-core", Ver("0.13.3", 0), Ver("1.0.0-ALPHA-22", 300))
            val nativecoroutines_gradle_plugin = Dep("com.rickclephas.kmp", "kmp-nativecoroutines-gradle-plugin", Ver("0.13.3", 0), Ver("1.0.0-ALPHA-22", 300))
        }
    }
    object Russhwolf {
        val multiplatform_settings = Dep("com.russhwolf", "multiplatform-settings", Ver("1.1.1", 0))
        val multiplatform_settings_coroutines = Dep("com.russhwolf", "multiplatform-settings-coroutines", Ver("1.1.1", 0))
        val multiplatform_settings_coroutines_native_mt = Dep("com.russhwolf", "multiplatform-settings-coroutines-native-mt", Ver("0.9", 0))
        val multiplatform_settings_datastore = Dep("com.russhwolf", "multiplatform-settings-datastore", Ver("1.1.1", 0))
        val multiplatform_settings_no_arg = Dep("com.russhwolf", "multiplatform-settings-no-arg", Ver("1.1.1", 0))
        val multiplatform_settings_serialization = Dep("com.russhwolf", "multiplatform-settings-serialization", Ver("1.1.1", 0))
        val multiplatform_settings_test = Dep("com.russhwolf", "multiplatform-settings-test", Ver("1.1.1", 0))
    }
    object SquareUp {
        val kotlinpoet = Dep("com.squareup", "kotlinpoet", Ver("1.15.1", 0))
        val kotlinpoet_metadata = Dep("com.squareup", "kotlinpoet-metadata", Ver("1.15.1", 0))
        val kotlinpoet_metadata_specs = Dep("com.squareup", "kotlinpoet-metadata-specs", Ver("1.9.0", 0))
        object LeakCanary {
            val android = Dep("com.squareup.leakcanary", "leakcanary-android", Ver("2.12", 0))
            val android_instrumentation = Dep("com.squareup.leakcanary", "leakcanary-android-instrumentation", Ver("2.12", 0))
            val android_process = Dep("com.squareup.leakcanary", "leakcanary-android-process", Ver("2.12", 0))
            val deobfuscation_gradle_plugin = Dep("com.squareup.leakcanary", "leakcanary-deobfuscation-gradle-plugin", Ver("2.12", 0))
            val object_watcher = Dep("com.squareup.leakcanary", "leakcanary-object-watcher", Ver("2.12", 0))
            val object_watcher_android = Dep("com.squareup.leakcanary", "leakcanary-object-watcher-android", Ver("2.12", 0))
            val plumber_android = Dep("com.squareup.leakcanary", "plumber-android", Ver("2.12", 0))
            val shark = Dep("com.squareup.leakcanary", "shark", Ver("2.12", 0))
            val shark_android = Dep("com.squareup.leakcanary", "shark-android", Ver("2.12", 0))
            val shark_cli = Dep("com.squareup.leakcanary", "shark-cli", Ver("2.12", 0))
            val shark_graph = Dep("com.squareup.leakcanary", "shark-graph", Ver("2.12", 0))
            val shark_hprof = Dep("com.squareup.leakcanary", "shark-hprof", Ver("2.12", 0))
        }
        object Logcat {
            val logcat = Dep("com.squareup.logcat", "logcat", Ver("0.1", 0))
        }
        object Moshi {
            val adapters = Dep("com.squareup.moshi", "moshi-adapters", Ver("1.15.0", 0))
            val kotlin = Dep("com.squareup.moshi", "moshi-kotlin", Ver("1.15.0", 0))
            val kotlin_codegen = Dep("com.squareup.moshi", "moshi-kotlin-codegen", Ver("1.15.0", 0))
            val moshi = Dep("com.squareup.moshi", "moshi", Ver("1.15.0", 0))
        }
        object Okhttp3 {
            val logging_interceptor = Dep("com.squareup.okhttp3", "logging-interceptor", Ver("4.12.0", 0), Ver("5.0.0-alpha.11", 300))
            val mockwebserver = Dep("com.squareup.okhttp3", "mockwebserver", Ver("4.12.0", 0), Ver("5.0.0-alpha.11", 300))
            val mockwebserver3 = Dep("com.squareup.okhttp3", "mockwebserver3", Ver("5.0.0-alpha.11", 300))
            val mockwebserver3_junit4 = Dep("com.squareup.okhttp3", "mockwebserver3-junit4", Ver("5.0.0-alpha.11", 300))
            val mockwebserver3_junit5 = Dep("com.squareup.okhttp3", "mockwebserver3-junit5", Ver("5.0.0-alpha.11", 300))
            val okhttp = Dep("com.squareup.okhttp3", "okhttp", Ver("4.12.0", 0), Ver("5.0.0-alpha.11", 300))
            val okhttp_android = Dep("com.squareup.okhttp3", "okhttp-android", Ver("5.0.0-alpha.11", 300))
            val okhttp_bom = Dep("com.squareup.okhttp3", "okhttp-bom", Ver("4.12.0", 0), Ver("5.0.0-alpha.11", 300))
            val okhttp_brotli = Dep("com.squareup.okhttp3", "okhttp-brotli", Ver("4.12.0", 0), Ver("5.0.0-alpha.11", 300))
            val okhttp_coroutines = Dep("com.squareup.okhttp3", "okhttp-coroutines", Ver("5.0.0-alpha.11", 300))
            val okhttp_dnsoverhttps = Dep("com.squareup.okhttp3", "okhttp-dnsoverhttps", Ver("4.12.0", 0), Ver("5.0.0-alpha.11", 300))
            val okhttp_sse = Dep("com.squareup.okhttp3", "okhttp-sse", Ver("4.12.0", 0), Ver("5.0.0-alpha.11", 300))
            val okhttp_tls = Dep("com.squareup.okhttp3", "okhttp-tls", Ver("4.12.0", 0), Ver("5.0.0-alpha.11", 300))
            val okhttp_urlconnection = Dep("com.squareup.okhttp3", "okhttp-urlconnection", Ver("4.12.0", 0), Ver("5.0.0-alpha.11", 300))
        }
        object Okio {
            val okio = Dep("com.squareup.okio", "okio", Ver("3.6.0", 0))
        }
        object Picasso {
            val picasso = Dep("com.squareup.picasso", "picasso", Ver("2.71828", 0))
            val pollexor = Dep("com.squareup.picasso", "picasso-pollexor", Ver("2.71828", 0))
        }
        object Retrofit2 {
            val adapter_java8 = Dep("com.squareup.retrofit2", "adapter-java8", Ver("2.9.0", 0))
            val adapter_rxjava = Dep("com.squareup.retrofit2", "adapter-rxjava", Ver("2.9.0", 0))
            val adapter_rxjava2 = Dep("com.squareup.retrofit2", "adapter-rxjava2", Ver("2.9.0", 0))
            val adapter_rxjava3 = Dep("com.squareup.retrofit2", "adapter-rxjava3", Ver("2.9.0", 0))
            val converter_gson = Dep("com.squareup.retrofit2", "converter-gson", Ver("2.9.0", 0))
            val converter_jackson = Dep("com.squareup.retrofit2", "converter-jackson", Ver("2.9.0", 0))
            val converter_moshi = Dep("com.squareup.retrofit2", "converter-moshi", Ver("2.9.0", 0))
            val converter_scalars = Dep("com.squareup.retrofit2", "converter-scalars", Ver("2.9.0", 0))
            val converter_simplexml = Dep("com.squareup.retrofit2", "converter-simplexml", Ver("2.9.0", 0))
            val converter_wire = Dep("com.squareup.retrofit2", "converter-wire", Ver("2.9.0", 0))
            val retrofit = Dep("com.squareup.retrofit2", "retrofit", Ver("2.9.0", 0))
            val retrofit_mock = Dep("com.squareup.retrofit2", "retrofit-mock", Ver("2.9.0", 0))
        }
        object SqlDelight {
            val android_driver = Dep("com.squareup.sqldelight", "android-driver", Ver("1.5.5", 0))
            val android_paging3_extensions = Dep("com.squareup.sqldelight", "android-paging3-extensions", Ver("1.5.5", 0))
            val android_paging_extensions = Dep("com.squareup.sqldelight", "android-paging-extensions", Ver("1.5.5", 0))
            val coroutines_extensions = Dep("com.squareup.sqldelight", "coroutines-extensions", Ver("1.5.5", 0))
            val gradle_plugin = Dep("com.squareup.sqldelight", "gradle-plugin", Ver("1.5.5", 0))
            val jdbc_driver = Dep("com.squareup.sqldelight", "jdbc-driver", Ver("1.5.5", 0))
            val native_driver = Dep("com.squareup.sqldelight", "native-driver", Ver("1.5.5", 0))
            val rxjava2_extensions = Dep("com.squareup.sqldelight", "rxjava2-extensions", Ver("1.5.5", 0))
            val rxjava3_extensions = Dep("com.squareup.sqldelight", "rxjava3-extensions", Ver("1.5.5", 0))
            val sqlite_driver = Dep("com.squareup.sqldelight", "sqlite-driver", Ver("1.5.5", 0))
            val sqljs_driver = Dep("com.squareup.sqldelight", "sqljs-driver", Ver("1.5.5", 0))
        }
        object Wire {
            val gradle_plugin = Dep("com.squareup.wire", "wire-gradle-plugin", Ver("4.9.3", 0))
            val grpc_client = Dep("com.squareup.wire", "wire-grpc-client", Ver("4.9.3", 0))
            val runtime = Dep("com.squareup.wire", "wire-runtime", Ver("4.9.3", 0))
        }
    }
}
object Io {
    object Arrow_kt {
        val arrow_core = Dep("io.arrow-kt", "arrow-core", Ver("1.2.1", 0))
        val arrow_fx_coroutines = Dep("io.arrow-kt", "arrow-fx-coroutines", Ver("1.2.1", 0))
        val arrow_fx_stm = Dep("io.arrow-kt", "arrow-fx-stm", Ver("1.2.1", 0))
        val arrow_optics = Dep("io.arrow-kt", "arrow-optics", Ver("1.2.1", 0))
        val arrow_optics_ksp_plugin = Dep("io.arrow-kt", "arrow-optics-ksp-plugin", Ver("1.2.1", 0))
        val arrow_optics_reflect = Dep("io.arrow-kt", "arrow-optics-reflect", Ver("1.2.1", 0))
        val arrow_stack = Dep("io.arrow-kt", "arrow-stack", Ver("1.2.1", 0))
        object Analysis {
            object Kotlin {
                val io_arrow_kt_analysis_kotlin_gradle_plugin = Dep("io.arrow-kt.analysis.kotlin", "io.arrow-kt.analysis.kotlin.gradle.plugin", Ver("2.0.2", 0), Ver("2.0.3-alpha.2", 300))
            }
        }
    }
    object Coil_kt {
        val coil = Dep("io.coil-kt", "coil", Ver("2.5.0", 0))
        val coil_base = Dep("io.coil-kt", "coil-base", Ver("2.5.0", 0))
        val coil_compose = Dep("io.coil-kt", "coil-compose", Ver("2.5.0", 0))
        val coil_compose_base = Dep("io.coil-kt", "coil-compose-base", Ver("2.5.0", 0))
        val coil_gif = Dep("io.coil-kt", "coil-gif", Ver("2.5.0", 0))
        val coil_svg = Dep("io.coil-kt", "coil-svg", Ver("2.5.0", 0))
        val coil_video = Dep("io.coil-kt", "coil-video", Ver("2.5.0", 0))
    }
    object GitHub {
        object JavaEden {
            object Orchid {
                val orchidall = Dep("io.github.javaeden.orchid", "OrchidAll", Ver("v0.5.3", 0), Ver("0.21.1", 0))
                val orchidasciidoc = Dep("io.github.javaeden.orchid", "OrchidAsciidoc", Ver("0.21.1", 0))
                val orchidazure = Dep("io.github.javaeden.orchid", "OrchidAzure", Ver("0.21.1", 0))
                val orchidbible = Dep("io.github.javaeden.orchid", "OrchidBible", Ver("0.21.1", 0))
                val orchidbitbucket = Dep("io.github.javaeden.orchid", "OrchidBitbucket", Ver("0.21.1", 0))
                val orchidblog = Dep("io.github.javaeden.orchid", "OrchidBlog", Ver("v0.3.12", 0), Ver("0.21.1", 0))
                val orchidbsdoc = Dep("io.github.javaeden.orchid", "OrchidBsDoc", Ver("v0.3.12", 0), Ver("0.21.1", 0))
                val orchidchangelog = Dep("io.github.javaeden.orchid", "OrchidChangelog", Ver("v0.3.12", 0), Ver("0.21.1", 0))
                val orchidcopper = Dep("io.github.javaeden.orchid", "OrchidCopper", Ver("0.21.1", 0))
                val orchidcore = Dep("io.github.javaeden.orchid", "OrchidCore", Ver("v0.3.12", 0), Ver("0.21.1", 0))
                val orchiddiagrams = Dep("io.github.javaeden.orchid", "OrchidDiagrams", Ver("0.21.1", 0))
                val orchiddocs = Dep("io.github.javaeden.orchid", "OrchidDocs", Ver("0.21.1", 0))
                val orchideditorial = Dep("io.github.javaeden.orchid", "OrchidEditorial", Ver("v0.5.3", 0), Ver("0.21.1", 0))
                val orchidforms = Dep("io.github.javaeden.orchid", "OrchidForms", Ver("0.21.1", 0))
                val orchidfutureimperfect = Dep("io.github.javaeden.orchid", "OrchidFutureImperfect", Ver("v0.3.11", 0), Ver("0.21.1", 0))
                val orchidgithub = Dep("io.github.javaeden.orchid", "OrchidGithub", Ver("0.21.1", 0))
                val orchidgitlab = Dep("io.github.javaeden.orchid", "OrchidGitlab", Ver("0.21.1", 0))
                val orchidgroovydoc = Dep("io.github.javaeden.orchid", "OrchidGroovydoc", Ver("0.21.1", 0))
                val orchidjavadoc = Dep("io.github.javaeden.orchid", "OrchidJavadoc", Ver("v0.3.12", 0), Ver("0.21.1", 0))
                val orchidkotlindoc = Dep("io.github.javaeden.orchid", "OrchidKotlindoc", Ver("0.21.1", 0))
                val orchidkss = Dep("io.github.javaeden.orchid", "OrchidKSS", Ver("v0.3.12", 0), Ver("0.21.1", 0))
                val orchidlanguagepack = Dep("io.github.javaeden.orchid", "OrchidLanguagePack", Ver("v0.5.3", 0), Ver("0.21.1", 0))
                val orchidnetlify = Dep("io.github.javaeden.orchid", "OrchidNetlify", Ver("0.21.1", 0))
                val orchidnetlifycms = Dep("io.github.javaeden.orchid", "OrchidNetlifyCMS", Ver("0.21.1", 0))
                val orchidpages = Dep("io.github.javaeden.orchid", "OrchidPages", Ver("v0.5.3", 0), Ver("0.21.1", 0))
                val orchidplugindocs = Dep("io.github.javaeden.orchid", "OrchidPluginDocs", Ver("0.21.1", 0))
                val orchidposts = Dep("io.github.javaeden.orchid", "OrchidPosts", Ver("v0.3.11", 0), Ver("0.21.1", 0))
                val orchidpresentations = Dep("io.github.javaeden.orchid", "OrchidPresentations", Ver("v0.5.3", 0), Ver("0.21.1", 0))
                val orchidsearch = Dep("io.github.javaeden.orchid", "OrchidSearch", Ver("0.21.1", 0))
                val orchidsourcedoc = Dep("io.github.javaeden.orchid", "OrchidSourceDoc", Ver("0.21.1", 0))
                val orchidswagger = Dep("io.github.javaeden.orchid", "OrchidSwagger", Ver("0.21.1", 0))
                val orchidswiftdoc = Dep("io.github.javaeden.orchid", "OrchidSwiftdoc", Ver("0.21.1", 0))
                val orchidsyntaxhighlighter = Dep("io.github.javaeden.orchid", "OrchidSyntaxHighlighter", Ver("0.21.1", 0))
                val orchidtaxonomies = Dep("io.github.javaeden.orchid", "OrchidTaxonomies", Ver("0.21.1", 0))
                val orchidtest = Dep("io.github.javaeden.orchid", "OrchidTest", Ver("0.21.1", 0))
                val orchidwiki = Dep("io.github.javaeden.orchid", "OrchidWiki", Ver("v0.5.3", 0), Ver("0.21.1", 0))
                val orchidwritersblocks = Dep("io.github.javaeden.orchid", "OrchidWritersBlocks", Ver("0.21.1", 0))
            }
        }
        object TypeSafeGitHub {
            val github_workflows_kt = Dep("io.github.typesafegithub", "github-workflows-kt", Ver("1.6.0", 0))
        }
    }
    object Insert_koin {
        val koin_android = Dep("io.insert-koin", "koin-android", Ver("3.5.0", 0), Ver("3.5.2-RC1", 100))
        val koin_android_compat = Dep("io.insert-koin", "koin-android-compat", Ver("3.5.0", 0), Ver("3.5.2-RC1", 100))
        val koin_androidx_compose = Dep("io.insert-koin", "koin-androidx-compose", Ver("3.5.0", 0), Ver("3.5.2-RC1", 100))
        val koin_androidx_navigation = Dep("io.insert-koin", "koin-androidx-navigation", Ver("3.5.0", 0), Ver("3.5.2-RC1", 100))
        val koin_androidx_workmanager = Dep("io.insert-koin", "koin-androidx-workmanager", Ver("3.5.0", 0), Ver("3.5.2-RC1", 100))
        val koin_core = Dep("io.insert-koin", "koin-core", Ver("3.5.0", 0), Ver("3.5.2-RC1", 100))
        val koin_ktor = Dep("io.insert-koin", "koin-ktor", Ver("3.5.1", 0), Ver("3.5.2-RC1", 100))
        val koin_logger_slf4j = Dep("io.insert-koin", "koin-logger-slf4j", Ver("3.5.1", 0), Ver("3.5.2-RC1", 100))
        val koin_test = Dep("io.insert-koin", "koin-test", Ver("3.5.0", 0), Ver("3.5.2-RC1", 100))
        val koin_test_junit4 = Dep("io.insert-koin", "koin-test-junit4", Ver("3.5.0", 0), Ver("3.5.2-RC1", 100))
        val koin_test_junit5 = Dep("io.insert-koin", "koin-test-junit5", Ver("3.5.0", 0), Ver("3.5.2-RC1", 100))
    }
    object Kotest {
        val assertions_arrow = Dep("io.kotest", "kotest-assertions-arrow", Ver("4.4.3", 0))
        val assertions_compiler = Dep("io.kotest", "kotest-assertions-compiler", Ver("4.6.4", 0), Ver("5.0.0.M3", 120))
        val assertions_core = Dep("io.kotest", "kotest-assertions-core", Ver("5.8.0", 0))
        val assertions_json = Dep("io.kotest", "kotest-assertions-json", Ver("5.8.0", 0))
        val assertions_jsoup = Dep("io.kotest", "kotest-assertions-jsoup", Ver("4.4.3", 0))
        val assertions_klock = Dep("io.kotest", "kotest-assertions-klock", Ver("4.4.3", 0))
        val assertions_konform = Dep("io.kotest", "kotest-assertions-konform", Ver("4.4.3", 0))
        val assertions_kotlinx_time = Dep("io.kotest", "kotest-assertions-kotlinx-time", Ver("4.4.3", 0))
        val assertions_ktor = Dep("io.kotest", "kotest-assertions-ktor", Ver("4.4.3", 0))
        val assertions_sql = Dep("io.kotest", "kotest-assertions-sql", Ver("5.8.0", 0))
        val core = Dep("io.kotest", "kotest-core", Ver("4.1.3", 0), Ver("4.2.0.RC2", 100))
        val framework_api = Dep("io.kotest", "kotest-framework-api", Ver("5.8.0", 0))
        val framework_datatest = Dep("io.kotest", "kotest-framework-datatest", Ver("5.8.0", 0))
        val plugins_pitest = Dep("io.kotest", "kotest-plugins-pitest", Ver("4.4.3", 0))
        val property = Dep("io.kotest", "kotest-property", Ver("5.8.0", 0))
        val property_arrow = Dep("io.kotest", "kotest-property-arrow", Ver("4.4.3", 0))
        val runner_junit4 = Dep("io.kotest", "kotest-runner-junit4", Ver("5.8.0", 0))
        val runner_junit5 = Dep("io.kotest", "kotest-runner-junit5", Ver("5.8.0", 0))
        object Extensions {
            val kotest_extensions_allure = Dep("io.kotest.extensions", "kotest-extensions-allure", Ver("1.3.0", 0))
            val kotest_extensions_embedded_kafka = Dep("io.kotest.extensions", "kotest-extensions-embedded-kafka", Ver("2.0.0", 0))
            val kotest_extensions_gherkin = Dep("io.kotest.extensions", "kotest-extensions-gherkin", Ver("0.1.0", 0))
            val kotest_extensions_koin = Dep("io.kotest.extensions", "kotest-extensions-koin", Ver("1.3.0", 0))
            val kotest_extensions_mockserver = Dep("io.kotest.extensions", "kotest-extensions-mockserver", Ver("1.3.0", 0))
            val kotest_extensions_pitest = Dep("io.kotest.extensions", "kotest-extensions-pitest", Ver("1.2.0", 0))
            val kotest_extensions_robolectric = Dep("io.kotest.extensions", "kotest-extensions-robolectric", Ver("0.5.0", 0))
            val kotest_extensions_spring = Dep("io.kotest.extensions", "kotest-extensions-spring", Ver("1.1.3", 0))
            val kotest_extensions_testcontainers = Dep("io.kotest.extensions", "kotest-extensions-testcontainers", Ver("2.0.2", 0))
            val kotest_extensions_wiremock = Dep("io.kotest.extensions", "kotest-extensions-wiremock", Ver("2.0.1", 0))
            val kotest_property_arbs = Dep("io.kotest.extensions", "kotest-property-arbs", Ver("2.1.2", 0))
            val kotest_property_datetime = Dep("io.kotest.extensions", "kotest-property-datetime", Ver("1.1.0", 0))
        }
    }
    object Ktor {
        val client_android = Dep("io.ktor", "ktor-client-android", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_apache = Dep("io.ktor", "ktor-client-apache", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_auth = Dep("io.ktor", "ktor-client-auth", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_cio = Dep("io.ktor", "ktor-client-cio", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_content_negotiation = Dep("io.ktor", "ktor-client-content-negotiation", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_content_negotiation_tests = Dep("io.ktor", "ktor-client-content-negotiation-tests", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_core = Dep("io.ktor", "ktor-client-core", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_curl = Dep("io.ktor", "ktor-client-curl", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_darwin = Dep("io.ktor", "ktor-client-darwin", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_encoding = Dep("io.ktor", "ktor-client-encoding", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_gson = Dep("io.ktor", "ktor-client-gson", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_jackson = Dep("io.ktor", "ktor-client-jackson", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_java = Dep("io.ktor", "ktor-client-java", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_jetty = Dep("io.ktor", "ktor-client-jetty", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_json = Dep("io.ktor", "ktor-client-json", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_json_tests = Dep("io.ktor", "ktor-client-json-tests", Ver("2.3.6", 0))
        val client_logging = Dep("io.ktor", "ktor-client-logging", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_mock = Dep("io.ktor", "ktor-client-mock", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_okhttp = Dep("io.ktor", "ktor-client-okhttp", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_resources = Dep("io.ktor", "ktor-client-resources", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_serialization = Dep("io.ktor", "ktor-client-serialization", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val client_tests = Dep("io.ktor", "ktor-client-tests", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val events = Dep("io.ktor", "ktor-events", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val http = Dep("io.ktor", "ktor-http", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val http_cio = Dep("io.ktor", "ktor-http-cio", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val io = Dep("io.ktor", "ktor-io", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val network = Dep("io.ktor", "ktor-network", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val network_tls = Dep("io.ktor", "ktor-network-tls", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val network_tls_certificates = Dep("io.ktor", "ktor-network-tls-certificates", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val resources = Dep("io.ktor", "ktor-resources", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val serialization = Dep("io.ktor", "ktor-serialization", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val serialization_gson = Dep("io.ktor", "ktor-serialization-gson", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val serialization_jackson = Dep("io.ktor", "ktor-serialization-jackson", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val serialization_kotlinx = Dep("io.ktor", "ktor-serialization-kotlinx", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val serialization_kotlinx_cbor = Dep("io.ktor", "ktor-serialization-kotlinx-cbor", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val serialization_kotlinx_json = Dep("io.ktor", "ktor-serialization-kotlinx-json", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val serialization_kotlinx_tests = Dep("io.ktor", "ktor-serialization-kotlinx-tests", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val serialization_kotlinx_xml = Dep("io.ktor", "ktor-serialization-kotlinx-xml", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server = Dep("io.ktor", "ktor-server", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_auth = Dep("io.ktor", "ktor-server-auth", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_auth_jwt = Dep("io.ktor", "ktor-server-auth-jwt", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_auth_ldap = Dep("io.ktor", "ktor-server-auth-ldap", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_auto_head_response = Dep("io.ktor", "ktor-server-auto-head-response", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_caching_headers = Dep("io.ktor", "ktor-server-caching-headers", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_call_id = Dep("io.ktor", "ktor-server-call-id", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_call_logging = Dep("io.ktor", "ktor-server-call-logging", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_cio = Dep("io.ktor", "ktor-server-cio", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_compression = Dep("io.ktor", "ktor-server-compression", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_conditional_headers = Dep("io.ktor", "ktor-server-conditional-headers", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_content_negotiation = Dep("io.ktor", "ktor-server-content-negotiation", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_core = Dep("io.ktor", "ktor-server-core", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_cors = Dep("io.ktor", "ktor-server-cors", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_data_conversion = Dep("io.ktor", "ktor-server-data-conversion", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_default_headers = Dep("io.ktor", "ktor-server-default-headers", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_double_receive = Dep("io.ktor", "ktor-server-double-receive", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_forwarded_header = Dep("io.ktor", "ktor-server-forwarded-header", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_freemarker = Dep("io.ktor", "ktor-server-freemarker", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_host_common = Dep("io.ktor", "ktor-server-host-common", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_hsts = Dep("io.ktor", "ktor-server-hsts", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_html_builder = Dep("io.ktor", "ktor-server-html-builder", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_http_redirect = Dep("io.ktor", "ktor-server-http-redirect", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_jetty = Dep("io.ktor", "ktor-server-jetty", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_jte = Dep("io.ktor", "ktor-server-jte", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_locations = Dep("io.ktor", "ktor-server-locations", Ver("2.3.6", 0))
        val server_method_override = Dep("io.ktor", "ktor-server-method-override", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_metrics = Dep("io.ktor", "ktor-server-metrics", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_metrics_micrometer = Dep("io.ktor", "ktor-server-metrics-micrometer", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_mustache = Dep("io.ktor", "ktor-server-mustache", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_netty = Dep("io.ktor", "ktor-server-netty", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_partial_content = Dep("io.ktor", "ktor-server-partial-content", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_pebble = Dep("io.ktor", "ktor-server-pebble", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_resources = Dep("io.ktor", "ktor-server-resources", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_servlet = Dep("io.ktor", "ktor-server-servlet", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_sessions = Dep("io.ktor", "ktor-server-sessions", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_status_pages = Dep("io.ktor", "ktor-server-status-pages", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_test_host = Dep("io.ktor", "ktor-server-test-host", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_test_suites = Dep("io.ktor", "ktor-server-test-suites", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_thymeleaf = Dep("io.ktor", "ktor-server-thymeleaf", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_tomcat = Dep("io.ktor", "ktor-server-tomcat", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_velocity = Dep("io.ktor", "ktor-server-velocity", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_webjars = Dep("io.ktor", "ktor-server-webjars", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val server_websockets = Dep("io.ktor", "ktor-server-websockets", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val test_dispatcher = Dep("io.ktor", "ktor-test-dispatcher", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val utils = Dep("io.ktor", "ktor-utils", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val websocket_serialization = Dep("io.ktor", "ktor-websocket-serialization", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
        val websockets = Dep("io.ktor", "ktor-websockets", Ver("2.3.6", 0), Ver("3.0.0-beta-1", 200))
    }
    object MockK {
        val android = Dep("io.mockk", "mockk-android", Ver("1.13.8", 0))
        val common = Dep("io.mockk", "mockk-common", Ver("1.12.5", 0))
        val mockk = Dep("io.mockk", "mockk", Ver("1.13.8", 0))
    }
    object Pivotal {
        object Spring {
            object Cloud {
                val spring_cloud_services_starter_circuit_breaker = Dep("io.pivotal.spring.cloud", "spring-cloud-services-starter-circuit-breaker", Ver("2.4.1", 0))
                val spring_cloud_services_starter_config_client = Dep("io.pivotal.spring.cloud", "spring-cloud-services-starter-config-client", Ver("4.0.4", 0))
                val spring_cloud_services_starter_service_registry = Dep("io.pivotal.spring.cloud", "spring-cloud-services-starter-service-registry", Ver("4.0.4", 0))
            }
        }
    }
    object Projectreactor {
        val reactor_test = Dep("io.projectreactor", "reactor-test", Ver("3.6.0", 0))
        object Kotlin {
            val reactor_kotlin_extensions = Dep("io.projectreactor.kotlin", "reactor-kotlin-extensions", Ver("1.2.2", 0))
        }
    }
    object RSocket {
        object Kotlin {
            val rsocket_core = Dep("io.rsocket.kotlin", "rsocket-core", Ver("0.15.4", 0))
            val rsocket_ktor_client = Dep("io.rsocket.kotlin", "rsocket-ktor-client", Ver("0.15.4", 0))
            val rsocket_ktor_server = Dep("io.rsocket.kotlin", "rsocket-ktor-server", Ver("0.15.4", 0))
            val rsocket_transport_ktor = Dep("io.rsocket.kotlin", "rsocket-transport-ktor", Ver("0.15.4", 0))
            val rsocket_transport_ktor_tcp = Dep("io.rsocket.kotlin", "rsocket-transport-ktor-tcp", Ver("0.15.4", 0))
            val rsocket_transport_ktor_websocket = Dep("io.rsocket.kotlin", "rsocket-transport-ktor-websocket", Ver("0.15.4", 0))
            val rsocket_transport_ktor_websocket_client = Dep("io.rsocket.kotlin", "rsocket-transport-ktor-websocket-client", Ver("0.15.4", 0))
            val rsocket_transport_ktor_websocket_server = Dep("io.rsocket.kotlin", "rsocket-transport-ktor-websocket-server", Ver("0.15.4", 0))
            val rsocket_transport_nodejs_tcp = Dep("io.rsocket.kotlin", "rsocket-transport-nodejs-tcp", Ver("0.15.4", 0))
        }
    }
    object ReactiveX {
        object RxJava2 {
            val rxandroid = Dep("io.reactivex.rxjava2", "rxandroid", Ver("2.1.1", 0))
            val rxjava = Dep("io.reactivex.rxjava2", "rxjava", Ver("2.2.21", 0))
            val rxkotlin = Dep("io.reactivex.rxjava2", "rxkotlin", Ver("2.4.0", 0))
        }
        object RxJava3 {
            val rxandroid = Dep("io.reactivex.rxjava3", "rxandroid", Ver("3.0.2", 0))
            val rxjava = Dep("io.reactivex.rxjava3", "rxjava", Ver("3.1.8", 0))
            val rxkotlin = Dep("io.reactivex.rxjava3", "rxkotlin", Ver("3.0.1", 0))
        }
    }
    object Realm {
        val gradle_plugin = Dep("io.realm", "realm-gradle-plugin", Ver("10.17.0", 0))
    }
    object Strikt {
        val arrow = Dep("io.strikt", "strikt-arrow", Ver("0.34.1", 0))
        val bom = Dep("io.strikt", "strikt-bom", Ver("0.34.1", 0))
        val core = Dep("io.strikt", "strikt-core", Ver("0.34.1", 0))
        val gradle = Dep("io.strikt", "strikt-gradle", Ver("0.31.0", 0))
        val jackson = Dep("io.strikt", "strikt-jackson", Ver("0.34.1", 0))
        val java_time = Dep("io.strikt", "strikt-java-time", Ver("0.28.2", 0))
        val mockk = Dep("io.strikt", "strikt-mockk", Ver("0.34.1", 0))
        val protobuf = Dep("io.strikt", "strikt-protobuf", Ver("0.34.1", 0))
        val spring = Dep("io.strikt", "strikt-spring", Ver("0.34.1", 0))
    }
}
object JUnit {
    val junit = Dep("junit", "junit", Ver("4.13.2", 0))
}
object Org {
    object AssertJ {
        val core = Dep("org.assertj", "assertj-core", Ver("3.24.2", 0))
        val db = Dep("org.assertj", "assertj-db", Ver("2.0.2", 0))
        val guava = Dep("org.assertj", "assertj-guava", Ver("3.24.2", 0))
        val joda_time = Dep("org.assertj", "assertj-joda-time", Ver("2.2.0", 0))
        val swing = Dep("org.assertj", "assertj-swing", Ver("3.17.1", 0))
    }
    object Hamcrest {
        val core = Dep("org.hamcrest", "hamcrest-core", Ver("2.2", 0))
        val hamcrest = Dep("org.hamcrest", "hamcrest", Ver("2.2", 0))
        val library = Dep("org.hamcrest", "hamcrest-library", Ver("2.2", 0))
    }
    object Http4k {
        val aws = Dep("org.http4k", "http4k-aws", Ver("5.10.4.0", 0))
        val bom = Dep("org.http4k", "http4k-bom", Ver("5.10.4.0", 0))
        val client_apache = Dep("org.http4k", "http4k-client-apache", Ver("5.10.4.0", 0))
        val client_apache4 = Dep("org.http4k", "http4k-client-apache4", Ver("5.10.4.0", 0))
        val client_apache4_async = Dep("org.http4k", "http4k-client-apache4-async", Ver("5.10.4.0", 0))
        val client_apache_async = Dep("org.http4k", "http4k-client-apache-async", Ver("5.10.4.0", 0))
        val client_jetty = Dep("org.http4k", "http4k-client-jetty", Ver("5.10.4.0", 0))
        val client_okhttp = Dep("org.http4k", "http4k-client-okhttp", Ver("5.10.4.0", 0))
        val client_websocket = Dep("org.http4k", "http4k-client-websocket", Ver("5.10.4.0", 0))
        val cloudnative = Dep("org.http4k", "http4k-cloudnative", Ver("5.10.4.0", 0))
        val contract = Dep("org.http4k", "http4k-contract", Ver("5.10.4.0", 0))
        val core = Dep("org.http4k", "http4k-core", Ver("5.10.4.0", 0))
        val format_argo = Dep("org.http4k", "http4k-format-argo", Ver("5.10.4.0", 0))
        val format_core = Dep("org.http4k", "http4k-format-core", Ver("5.10.4.0", 0))
        val format_gson = Dep("org.http4k", "http4k-format-gson", Ver("5.10.4.0", 0))
        val format_jackson = Dep("org.http4k", "http4k-format-jackson", Ver("5.10.4.0", 0))
        val format_jackson_xml = Dep("org.http4k", "http4k-format-jackson-xml", Ver("5.10.4.0", 0))
        val format_jackson_yaml = Dep("org.http4k", "http4k-format-jackson-yaml", Ver("5.10.4.0", 0))
        val format_klaxon = Dep("org.http4k", "http4k-format-klaxon", Ver("5.10.4.0", 0))
        val format_kotlinx_serialization = Dep("org.http4k", "http4k-format-kotlinx-serialization", Ver("5.10.4.0", 0))
        val format_moshi = Dep("org.http4k", "http4k-format-moshi", Ver("5.10.4.0", 0))
        val format_xml = Dep("org.http4k", "http4k-format-xml", Ver("5.10.4.0", 0))
        val graphql = Dep("org.http4k", "http4k-graphql", Ver("5.10.4.0", 0))
        val incubator = Dep("org.http4k", "http4k-incubator", Ver("5.10.4.0", 0))
        val jsonrpc = Dep("org.http4k", "http4k-jsonrpc", Ver("5.10.4.0", 0))
        val metrics_micrometer = Dep("org.http4k", "http4k-metrics-micrometer", Ver("5.10.4.0", 0))
        val multipart = Dep("org.http4k", "http4k-multipart", Ver("5.10.4.0", 0))
        val opentelemetry = Dep("org.http4k", "http4k-opentelemetry", Ver("5.10.4.0", 0))
        val realtime_core = Dep("org.http4k", "http4k-realtime-core", Ver("5.10.4.0", 0))
        val resilience4j = Dep("org.http4k", "http4k-resilience4j", Ver("5.10.4.0", 0))
        val security_oauth = Dep("org.http4k", "http4k-security-oauth", Ver("5.10.4.0", 0))
        val server_apache = Dep("org.http4k", "http4k-server-apache", Ver("5.10.4.0", 0))
        val server_apache4 = Dep("org.http4k", "http4k-server-apache4", Ver("5.10.4.0", 0))
        val server_jetty = Dep("org.http4k", "http4k-server-jetty", Ver("5.10.4.0", 0))
        val server_ktorcio = Dep("org.http4k", "http4k-server-ktorcio", Ver("5.10.4.0", 0))
        val server_ktornetty = Dep("org.http4k", "http4k-server-ktornetty", Ver("5.10.4.0", 0))
        val server_netty = Dep("org.http4k", "http4k-server-netty", Ver("5.10.4.0", 0))
        val server_ratpack = Dep("org.http4k", "http4k-server-ratpack", Ver("5.10.4.0", 0))
        val server_undertow = Dep("org.http4k", "http4k-server-undertow", Ver("5.10.4.0", 0))
        val serverless_alibaba = Dep("org.http4k", "http4k-serverless-alibaba", Ver("5.10.4.0", 0))
        val serverless_azure = Dep("org.http4k", "http4k-serverless-azure", Ver("5.10.4.0", 0))
        val serverless_gcf = Dep("org.http4k", "http4k-serverless-gcf", Ver("5.10.4.0", 0))
        val serverless_lambda = Dep("org.http4k", "http4k-serverless-lambda", Ver("5.10.4.0", 0))
        val serverless_lambda_runtime = Dep("org.http4k", "http4k-serverless-lambda-runtime", Ver("5.10.4.0", 0))
        val serverless_openwhisk = Dep("org.http4k", "http4k-serverless-openwhisk", Ver("5.10.4.0", 0))
        val serverless_tencent = Dep("org.http4k", "http4k-serverless-tencent", Ver("5.10.4.0", 0))
        val template_core = Dep("org.http4k", "http4k-template-core", Ver("5.10.4.0", 0))
        val template_dust = Dep("org.http4k", "http4k-template-dust", Ver("4.48.0.0", 0))
        val template_freemarker = Dep("org.http4k", "http4k-template-freemarker", Ver("5.10.4.0", 0))
        val template_handlebars = Dep("org.http4k", "http4k-template-handlebars", Ver("5.10.4.0", 0))
        val template_jade4j = Dep("org.http4k", "http4k-template-jade4j", Ver("5.10.4.0", 0))
        val template_pebble = Dep("org.http4k", "http4k-template-pebble", Ver("5.10.4.0", 0))
        val template_thymeleaf = Dep("org.http4k", "http4k-template-thymeleaf", Ver("5.10.4.0", 0))
        val testing_approval = Dep("org.http4k", "http4k-testing-approval", Ver("5.10.4.0", 0))
        val testing_chaos = Dep("org.http4k", "http4k-testing-chaos", Ver("5.10.4.0", 0))
        val testing_hamkrest = Dep("org.http4k", "http4k-testing-hamkrest", Ver("5.10.4.0", 0))
        val testing_kotest = Dep("org.http4k", "http4k-testing-kotest", Ver("5.10.4.0", 0))
        val testing_servirtium = Dep("org.http4k", "http4k-testing-servirtium", Ver("5.10.4.0", 0))
        val testing_strikt = Dep("org.http4k", "http4k-testing-strikt", Ver("5.10.4.0", 0))
        val testing_webdriver = Dep("org.http4k", "http4k-testing-webdriver", Ver("5.10.4.0", 0))
    }
    object JUnit {
        val bom = Dep("org.junit", "junit-bom", Ver("5.10.1", 0))
        object Jupiter {
            val junit_jupiter = Dep("org.junit.jupiter", "junit-jupiter", Ver("5.10.1", 0))
            val junit_jupiter_api = Dep("org.junit.jupiter", "junit-jupiter-api", Ver("5.10.1", 0))
            val junit_jupiter_engine = Dep("org.junit.jupiter", "junit-jupiter-engine", Ver("5.10.1", 0))
            val junit_jupiter_migrationsupport = Dep("org.junit.jupiter", "junit-jupiter-migrationsupport", Ver("5.10.1", 0))
            val junit_jupiter_params = Dep("org.junit.jupiter", "junit-jupiter-params", Ver("5.10.1", 0))
        }
    }
    object JetBrains {
        object Compose {
            val gradle_plugin = Dep("org.jetbrains.compose", "compose-gradle-plugin", Ver("1.5.11", 0), Ver("1.6.0-dev1291", 320))
        }
        object Exposed {
            val core = Dep("org.jetbrains.exposed", "exposed-core", Ver("0.45.0", 0))
            val dao = Dep("org.jetbrains.exposed", "exposed-dao", Ver("0.45.0", 0))
            val jdbc = Dep("org.jetbrains.exposed", "exposed-jdbc", Ver("0.45.0", 0))
        }
        object Kotlin {
            val script_runtime = Dep("org.jetbrains.kotlin", "kotlin-script-runtime", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
            val stdlib = Dep("org.jetbrains.kotlin", "kotlin-stdlib", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
            val stdlib_common = Dep("org.jetbrains.kotlin", "kotlin-stdlib-common", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
            val stdlib_jdk7 = Dep("org.jetbrains.kotlin", "kotlin-stdlib-jdk7", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
            val stdlib_jdk8 = Dep("org.jetbrains.kotlin", "kotlin-stdlib-jdk8", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
            val stdlib_js = Dep("org.jetbrains.kotlin", "kotlin-stdlib-js", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
            val test = Dep("org.jetbrains.kotlin", "kotlin-test", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
            val test_annotations_common = Dep("org.jetbrains.kotlin", "kotlin-test-annotations-common", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
            val test_common = Dep("org.jetbrains.kotlin", "kotlin-test-common", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
            val test_js = Dep("org.jetbrains.kotlin", "kotlin-test-js", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
            val test_js_runner = Dep("org.jetbrains.kotlin", "kotlin-test-js-runner", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
            val test_junit = Dep("org.jetbrains.kotlin", "kotlin-test-junit", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
            val test_junit5 = Dep("org.jetbrains.kotlin", "kotlin-test-junit5", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
            val test_testng = Dep("org.jetbrains.kotlin", "kotlin-test-testng", Ver("1.9.21", 0), Ver("2.0.0-Beta1", 200))
        }
        object KotlinX {
            val atomicfu_gradle_plugin = Dep("org.jetbrains.kotlinx", "atomicfu-gradle-plugin", Ver("0.23.1", 0))
            val cli = Dep("org.jetbrains.kotlinx", "kotlinx-cli", Ver("0.3.6", 0))
            val collections_immutable = Dep("org.jetbrains.kotlinx", "kotlinx-collections-immutable", Ver("0.3.6", 0))
            val collections_immutable_jvm = Dep("org.jetbrains.kotlinx", "kotlinx-collections-immutable-jvm", Ver("0.3.6", 0))
            val coroutines_android = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-android", Ver("1.7.3", 0))
            val coroutines_bom = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-bom", Ver("1.7.3", 0))
            val coroutines_core = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-core", Ver("1.7.3", 0))
            val coroutines_debug = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-debug", Ver("1.7.3", 0))
            val coroutines_guava = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-guava", Ver("1.7.3", 0))
            val coroutines_javafx = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-javafx", Ver("1.7.3", 0))
            val coroutines_jdk8 = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", Ver("1.7.3", 0))
            val coroutines_jdk9 = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk9", Ver("1.7.3", 0))
            val coroutines_play_services = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-play-services", Ver("1.7.3", 0))
            val coroutines_reactive = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-reactive", Ver("1.7.3", 0))
            val coroutines_reactor = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-reactor", Ver("1.7.3", 0))
            val coroutines_rx2 = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-rx2", Ver("1.7.3", 0))
            val coroutines_rx3 = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-rx3", Ver("1.7.3", 0))
            val coroutines_slf4j = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-slf4j", Ver("1.7.3", 0))
            val coroutines_swing = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-swing", Ver("1.7.3", 0))
            val coroutines_test = Dep("org.jetbrains.kotlinx", "kotlinx-coroutines-test", Ver("1.7.3", 0))
            val dataframe = Dep("org.jetbrains.kotlinx", "dataframe", Ver("1727", 0))
            val dataframe_arrow = Dep("org.jetbrains.kotlinx", "dataframe-arrow", Ver("1727", 0))
            val dataframe_core = Dep("org.jetbrains.kotlinx", "dataframe-core", Ver("1727", 0))
            val dataframe_excel = Dep("org.jetbrains.kotlinx", "dataframe-excel", Ver("1727", 0))
            val datetime = Dep("org.jetbrains.kotlinx", "kotlinx-datetime", Ver("0.4.1", 0))
            val html = Dep("org.jetbrains.kotlinx", "kotlinx-html", Ver("0.9.1", 0))
            val io_jvm = Dep("org.jetbrains.kotlinx", "kotlinx-io-jvm", Ver("0.1.16", 0))
            val kotlin_deeplearning_api = Dep("org.jetbrains.kotlinx", "kotlin-deeplearning-api", Ver("0.5.2", 0), Ver("0.6.0-alpha-1", 300))
            val kotlin_deeplearning_onnx = Dep("org.jetbrains.kotlinx", "kotlin-deeplearning-onnx", Ver("0.5.2", 0), Ver("0.6.0-alpha-1", 300))
            val kotlin_deeplearning_visualization = Dep("org.jetbrains.kotlinx", "kotlin-deeplearning-visualization", Ver("0.5.2", 0), Ver("0.6.0-alpha-1", 300))
            val lincheck = Dep("org.jetbrains.kotlinx", "lincheck", Ver("2.23-IDEA-PLUGIN", 0))
            val lincheck_jvm = Dep("org.jetbrains.kotlinx", "lincheck-jvm", Ver("2.23-IDEA-PLUGIN", 0))
            val multik_api = Dep("org.jetbrains.kotlinx", "multik-api", Ver("0.1.1", 0))
            val multik_default = Dep("org.jetbrains.kotlinx", "multik-default", Ver("0.2.2", 0))
            val multik_jvm = Dep("org.jetbrains.kotlinx", "multik-jvm", Ver("0.1.1", 0))
            val multik_native = Dep("org.jetbrains.kotlinx", "multik-native", Ver("0.1.1", 0))
            val nodejs = Dep("org.jetbrains.kotlinx", "kotlinx-nodejs", Ver("0.0.7", 0))
            val reflect_lite = Dep("org.jetbrains.kotlinx", "kotlinx.reflect.lite", Ver("1.1.0", 0))
            val serialization_bom = Dep("org.jetbrains.kotlinx", "kotlinx-serialization-bom", Ver("1.6.1", 0))
            val serialization_cbor = Dep("org.jetbrains.kotlinx", "kotlinx-serialization-cbor", Ver("1.6.1", 0))
            val serialization_core = Dep("org.jetbrains.kotlinx", "kotlinx-serialization-core", Ver("1.6.1", 0))
            val serialization_hocon = Dep("org.jetbrains.kotlinx", "kotlinx-serialization-hocon", Ver("1.6.1", 0))
            val serialization_json = Dep("org.jetbrains.kotlinx", "kotlinx-serialization-json", Ver("1.6.1", 0))
            val serialization_json_okio = Dep("org.jetbrains.kotlinx", "kotlinx-serialization-json-okio", Ver("1.6.1", 0))
            val serialization_properties = Dep("org.jetbrains.kotlinx", "kotlinx-serialization-properties", Ver("1.6.1", 0))
            val serialization_protobuf = Dep("org.jetbrains.kotlinx", "kotlinx-serialization-protobuf", Ver("1.6.1", 0))
        }
        object Kotlin_Wrappers {
            val bom = Dep("org.jetbrains.kotlin-wrappers", "kotlin-wrappers-bom", Ver("1.0.0-pre.651", 0))
            val kotlin_actions_toolkit = Dep("org.jetbrains.kotlin-wrappers", "kotlin-actions-toolkit", Ver("0.0.1-pre.651", 0))
            val kotlin_browser = Dep("org.jetbrains.kotlin-wrappers", "kotlin-browser", Ver("1.0.0-pre.651", 0))
            val kotlin_cesium = Dep("org.jetbrains.kotlin-wrappers", "kotlin-cesium", Ver("1.111.0-pre.651", 0))
            val kotlin_css = Dep("org.jetbrains.kotlin-wrappers", "kotlin-css", Ver("1.0.0-pre.651", 0))
            val kotlin_csstype = Dep("org.jetbrains.kotlin-wrappers", "kotlin-csstype", Ver("3.1.2-pre.651", 0))
            val kotlin_emotion = Dep("org.jetbrains.kotlin-wrappers", "kotlin-emotion", Ver("11.11.1-pre.651", 0))
            val kotlin_history = Dep("org.jetbrains.kotlin-wrappers", "kotlin-history", Ver("5.3.0-pre.506-compat", 0))
            val kotlin_js = Dep("org.jetbrains.kotlin-wrappers", "kotlin-js", Ver("1.0.0-pre.651", 0))
            val kotlin_mui = Dep("org.jetbrains.kotlin-wrappers", "kotlin-mui", Ver("5.14.12-pre.638", 0))
            val kotlin_mui_icons = Dep("org.jetbrains.kotlin-wrappers", "kotlin-mui-icons", Ver("5.14.12-pre.638", 0))
            val kotlin_node = Dep("org.jetbrains.kotlin-wrappers", "kotlin-node", Ver("18.16.12-pre.651", 0))
            val kotlin_popper = Dep("org.jetbrains.kotlin-wrappers", "kotlin-popper", Ver("2.11.8-pre.651", 0))
            val kotlin_react = Dep("org.jetbrains.kotlin-wrappers", "kotlin-react", Ver("18.2.0-pre.651", 0))
            val kotlin_react_beautiful_dnd = Dep("org.jetbrains.kotlin-wrappers", "kotlin-react-beautiful-dnd", Ver("13.1.1-pre.651", 0))
            val kotlin_react_core = Dep("org.jetbrains.kotlin-wrappers", "kotlin-react-core", Ver("18.2.0-pre.651", 0))
            val kotlin_react_dom = Dep("org.jetbrains.kotlin-wrappers", "kotlin-react-dom", Ver("18.2.0-pre.651", 0))
            val kotlin_react_dom_legacy = Dep("org.jetbrains.kotlin-wrappers", "kotlin-react-dom-legacy", Ver("18.2.0-pre.651", 0))
            val kotlin_react_dom_test_utils = Dep("org.jetbrains.kotlin-wrappers", "kotlin-react-dom-test-utils", Ver("18.2.0-pre.651", 0))
            val kotlin_react_legacy = Dep("org.jetbrains.kotlin-wrappers", "kotlin-react-legacy", Ver("18.2.0-pre.651", 0))
            val kotlin_react_popper = Dep("org.jetbrains.kotlin-wrappers", "kotlin-react-popper", Ver("2.3.0-pre.651", 0))
            val kotlin_react_redux = Dep("org.jetbrains.kotlin-wrappers", "kotlin-react-redux", Ver("7.2.6-pre.651", 0))
            val kotlin_react_router = Dep("org.jetbrains.kotlin-wrappers", "kotlin-react-router", Ver("6.20.0-pre.651", 0))
            val kotlin_react_router_dom = Dep("org.jetbrains.kotlin-wrappers", "kotlin-react-router-dom", Ver("6.20.0-pre.651", 0))
            val kotlin_react_select = Dep("org.jetbrains.kotlin-wrappers", "kotlin-react-select", Ver("5.8.0-pre.651", 0))
            val kotlin_react_use = Dep("org.jetbrains.kotlin-wrappers", "kotlin-react-use", Ver("17.4.0-pre.651", 0))
            val kotlin_redux = Dep("org.jetbrains.kotlin-wrappers", "kotlin-redux", Ver("4.1.2-pre.651", 0))
            val kotlin_remix_run_router = Dep("org.jetbrains.kotlin-wrappers", "kotlin-remix-run-router", Ver("1.13.0-pre.651", 0))
            val kotlin_ring_ui = Dep("org.jetbrains.kotlin-wrappers", "kotlin-ring-ui", Ver("4.1.5-pre.651", 0))
            val kotlin_styled = Dep("org.jetbrains.kotlin-wrappers", "kotlin-styled", Ver("5.3.11-pre.651", 0))
            val kotlin_styled_next = Dep("org.jetbrains.kotlin-wrappers", "kotlin-styled-next", Ver("1.2.3-pre.651", 0))
            val kotlin_tanstack_query_core = Dep("org.jetbrains.kotlin-wrappers", "kotlin-tanstack-query-core", Ver("5.8.7-pre.651", 0))
            val kotlin_tanstack_react_query = Dep("org.jetbrains.kotlin-wrappers", "kotlin-tanstack-react-query", Ver("5.8.9-pre.651", 0))
            val kotlin_tanstack_react_query_devtools = Dep("org.jetbrains.kotlin-wrappers", "kotlin-tanstack-react-query-devtools", Ver("5.8.9-pre.651", 0))
            val kotlin_tanstack_react_table = Dep("org.jetbrains.kotlin-wrappers", "kotlin-tanstack-react-table", Ver("8.10.7-pre.651", 0))
            val kotlin_tanstack_react_virtual = Dep("org.jetbrains.kotlin-wrappers", "kotlin-tanstack-react-virtual", Ver("3.0.0-beta.68-pre.651", 200))
            val kotlin_tanstack_table_core = Dep("org.jetbrains.kotlin-wrappers", "kotlin-tanstack-table-core", Ver("8.10.7-pre.651", 0))
            val kotlin_tanstack_virtual_core = Dep("org.jetbrains.kotlin-wrappers", "kotlin-tanstack-virtual-core", Ver("3.0.0-beta.68-pre.651", 200))
            val kotlin_typescript = Dep("org.jetbrains.kotlin-wrappers", "kotlin-typescript", Ver("4.9.5-pre.651", 0))
            val kotlin_web = Dep("org.jetbrains.kotlin-wrappers", "kotlin-web", Ver("1.0.0-pre.651", 0))
        }
    }
    object Kodein {
        object Di {
            val kodein_di_conf_js = Dep("org.kodein.di", "kodein-di-conf-js", Ver("7.21.0", 0))
            val kodein_di_conf_jvm = Dep("org.kodein.di", "kodein-di-conf-jvm", Ver("7.21.0", 0))
            val kodein_di_framework_android_core = Dep("org.kodein.di", "kodein-di-framework-android-core", Ver("7.21.0", 0))
            val kodein_di_framework_android_support = Dep("org.kodein.di", "kodein-di-framework-android-support", Ver("7.21.0", 0))
            val kodein_di_framework_android_x = Dep("org.kodein.di", "kodein-di-framework-android-x", Ver("7.21.0", 0))
            val kodein_di_framework_ktor_server_jvm = Dep("org.kodein.di", "kodein-di-framework-ktor-server-jvm", Ver("7.21.0", 0))
            val kodein_di_framework_tornadofx_jvm = Dep("org.kodein.di", "kodein-di-framework-tornadofx-jvm", Ver("7.21.0", 0))
            val kodein_di_js = Dep("org.kodein.di", "kodein-di-js", Ver("7.21.0", 0))
            val kodein_di_jxinject_jvm = Dep("org.kodein.di", "kodein-di-jxinject-jvm", Ver("7.21.0", 0))
        }
    }
    object Mockito {
        val android = Dep("org.mockito", "mockito-android", Ver("5.7.0", 0))
        val core = Dep("org.mockito", "mockito-core", Ver("5.7.0", 0))
        val errorprone = Dep("org.mockito", "mockito-errorprone", Ver("5.7.0", 0))
        val inline = Dep("org.mockito", "mockito-inline", Ver("5.2.0", 0))
        val junit_jupiter = Dep("org.mockito", "mockito-junit-jupiter", Ver("5.7.0", 0))
        object Kotlin {
            val mockito_kotlin = Dep("org.mockito.kotlin", "mockito-kotlin", Ver("5.1.0", 0))
        }
    }
    object Robolectric {
        val robolectric = Dep("org.robolectric", "robolectric", Ver("4.11.1", 0))
    }
    object SpekFramework {
        object Spek2 {
            val spek_dsl_js = Dep("org.spekframework.spek2", "spek-dsl-js", Ver("2.0.19", 0))
            val spek_dsl_jvm = Dep("org.spekframework.spek2", "spek-dsl-jvm", Ver("2.0.19", 0))
            val spek_dsl_metadata = Dep("org.spekframework.spek2", "spek-dsl-metadata", Ver("2.0.16", 0))
            val spek_dsl_native_linux = Dep("org.spekframework.spek2", "spek-dsl-native-linux", Ver("2.0.19", 0))
            val spek_dsl_native_macos = Dep("org.spekframework.spek2", "spek-dsl-native-macos", Ver("2.0.19", 0))
            val spek_dsl_native_windows = Dep("org.spekframework.spek2", "spek-dsl-native-windows", Ver("2.0.19", 0))
            val spek_runner_junit5 = Dep("org.spekframework.spek2", "spek-runner-junit5", Ver("2.0.19", 0))
            val spek_runtime_jvm = Dep("org.spekframework.spek2", "spek-runtime-jvm", Ver("2.0.19", 0))
            val spek_runtime_metadata = Dep("org.spekframework.spek2", "spek-runtime-metadata", Ver("2.0.16", 0))
        }
    }
    object SpringFramework {
        object Amqp {
            val spring_rabbit_test = Dep("org.springframework.amqp", "spring-rabbit-test", Ver("3.1.0", 0))
        }
        object Batch {
            val spring_batch_test = Dep("org.springframework.batch", "spring-batch-test", Ver("5.1.0", 0))
        }
        object Boot {
            val spring_boot_configuration_processor = Dep("org.springframework.boot", "spring-boot-configuration-processor", Ver("3.2.0", 0))
            val spring_boot_dependencies = Dep("org.springframework.boot", "spring-boot-dependencies", Ver("3.2.0", 0))
            val spring_boot_devtools = Dep("org.springframework.boot", "spring-boot-devtools", Ver("3.2.0", 0))
            val spring_boot_starter_activemq = Dep("org.springframework.boot", "spring-boot-starter-activemq", Ver("3.2.0", 0))
            val spring_boot_starter_actuator = Dep("org.springframework.boot", "spring-boot-starter-actuator", Ver("3.2.0", 0))
            val spring_boot_starter_amqp = Dep("org.springframework.boot", "spring-boot-starter-amqp", Ver("3.2.0", 0))
            val spring_boot_starter_artemis = Dep("org.springframework.boot", "spring-boot-starter-artemis", Ver("3.2.0", 0))
            val spring_boot_starter_batch = Dep("org.springframework.boot", "spring-boot-starter-batch", Ver("3.2.0", 0))
            val spring_boot_starter_cache = Dep("org.springframework.boot", "spring-boot-starter-cache", Ver("3.2.0", 0))
            val spring_boot_starter_data_cassandra = Dep("org.springframework.boot", "spring-boot-starter-data-cassandra", Ver("3.2.0", 0))
            val spring_boot_starter_data_cassandra_reactive = Dep("org.springframework.boot", "spring-boot-starter-data-cassandra-reactive", Ver("3.2.0", 0))
            val spring_boot_starter_data_couchbase = Dep("org.springframework.boot", "spring-boot-starter-data-couchbase", Ver("3.2.0", 0))
            val spring_boot_starter_data_couchbase_reactive = Dep("org.springframework.boot", "spring-boot-starter-data-couchbase-reactive", Ver("3.2.0", 0))
            val spring_boot_starter_data_elasticsearch = Dep("org.springframework.boot", "spring-boot-starter-data-elasticsearch", Ver("3.2.0", 0))
            val spring_boot_starter_data_jdbc = Dep("org.springframework.boot", "spring-boot-starter-data-jdbc", Ver("3.2.0", 0))
            val spring_boot_starter_data_jpa = Dep("org.springframework.boot", "spring-boot-starter-data-jpa", Ver("3.2.0", 0))
            val spring_boot_starter_data_ldap = Dep("org.springframework.boot", "spring-boot-starter-data-ldap", Ver("3.2.0", 0))
            val spring_boot_starter_data_mongodb = Dep("org.springframework.boot", "spring-boot-starter-data-mongodb", Ver("3.2.0", 0))
            val spring_boot_starter_data_mongodb_reactive = Dep("org.springframework.boot", "spring-boot-starter-data-mongodb-reactive", Ver("3.2.0", 0))
            val spring_boot_starter_data_neo4j = Dep("org.springframework.boot", "spring-boot-starter-data-neo4j", Ver("3.2.0", 0))
            val spring_boot_starter_data_r2dbc = Dep("org.springframework.boot", "spring-boot-starter-data-r2dbc", Ver("3.2.0", 0))
            val spring_boot_starter_data_redis = Dep("org.springframework.boot", "spring-boot-starter-data-redis", Ver("3.2.0", 0))
            val spring_boot_starter_data_redis_reactive = Dep("org.springframework.boot", "spring-boot-starter-data-redis-reactive", Ver("3.2.0", 0))
            val spring_boot_starter_data_rest = Dep("org.springframework.boot", "spring-boot-starter-data-rest", Ver("3.2.0", 0))
            val spring_boot_starter_data_solr = Dep("org.springframework.boot", "spring-boot-starter-data-solr", Ver("2.4.13", 0))
            val spring_boot_starter_freemarker = Dep("org.springframework.boot", "spring-boot-starter-freemarker", Ver("3.2.0", 0))
            val spring_boot_starter_groovy_templates = Dep("org.springframework.boot", "spring-boot-starter-groovy-templates", Ver("3.2.0", 0))
            val spring_boot_starter_hateoas = Dep("org.springframework.boot", "spring-boot-starter-hateoas", Ver("3.2.0", 0))
            val spring_boot_starter_integration = Dep("org.springframework.boot", "spring-boot-starter-integration", Ver("3.2.0", 0))
            val spring_boot_starter_jdbc = Dep("org.springframework.boot", "spring-boot-starter-jdbc", Ver("3.2.0", 0))
            val spring_boot_starter_jersey = Dep("org.springframework.boot", "spring-boot-starter-jersey", Ver("3.2.0", 0))
            val spring_boot_starter_jooq = Dep("org.springframework.boot", "spring-boot-starter-jooq", Ver("3.2.0", 0))
            val spring_boot_starter_mail = Dep("org.springframework.boot", "spring-boot-starter-mail", Ver("3.2.0", 0))
            val spring_boot_starter_mustache = Dep("org.springframework.boot", "spring-boot-starter-mustache", Ver("3.2.0", 0))
            val spring_boot_starter_oauth2_client = Dep("org.springframework.boot", "spring-boot-starter-oauth2-client", Ver("3.2.0", 0))
            val spring_boot_starter_oauth2_resource_server = Dep("org.springframework.boot", "spring-boot-starter-oauth2-resource-server", Ver("3.2.0", 0))
            val spring_boot_starter_quartz = Dep("org.springframework.boot", "spring-boot-starter-quartz", Ver("3.2.0", 0))
            val spring_boot_starter_rsocket = Dep("org.springframework.boot", "spring-boot-starter-rsocket", Ver("3.2.0", 0))
            val spring_boot_starter_security = Dep("org.springframework.boot", "spring-boot-starter-security", Ver("3.2.0", 0))
            val spring_boot_starter_test = Dep("org.springframework.boot", "spring-boot-starter-test", Ver("3.2.0", 0))
            val spring_boot_starter_thymeleaf = Dep("org.springframework.boot", "spring-boot-starter-thymeleaf", Ver("3.2.0", 0))
            val spring_boot_starter_validation = Dep("org.springframework.boot", "spring-boot-starter-validation", Ver("3.2.0", 0))
            val spring_boot_starter_web = Dep("org.springframework.boot", "spring-boot-starter-web", Ver("3.2.0", 0))
            val spring_boot_starter_web_services = Dep("org.springframework.boot", "spring-boot-starter-web-services", Ver("3.2.0", 0))
            val spring_boot_starter_webflux = Dep("org.springframework.boot", "spring-boot-starter-webflux", Ver("3.2.0", 0))
            val spring_boot_starter_websocket = Dep("org.springframework.boot", "spring-boot-starter-websocket", Ver("3.2.0", 0))
        }
        object Cloud {
            val spring_cloud_bus = Dep("org.springframework.cloud", "spring-cloud-bus", Ver("4.0.1", 0))
            val spring_cloud_cloudfoundry_discovery = Dep("org.springframework.cloud", "spring-cloud-cloudfoundry-discovery", Ver("3.1.3", 0))
            val spring_cloud_config_server = Dep("org.springframework.cloud", "spring-cloud-config-server", Ver("4.0.4", 0))
            val spring_cloud_dependencies = Dep("org.springframework.cloud", "spring-cloud-dependencies", Ver("2022.0.4", 0))
            val spring_cloud_function_web = Dep("org.springframework.cloud", "spring-cloud-function-web", Ver("4.0.5", 0))
            val spring_cloud_gcp_starter = Dep("org.springframework.cloud", "spring-cloud-gcp-starter", Ver("1.2.8.RELEASE", 0))
            val spring_cloud_gcp_starter_pubsub = Dep("org.springframework.cloud", "spring-cloud-gcp-starter-pubsub", Ver("1.2.8.RELEASE", 0))
            val spring_cloud_gcp_starter_storage = Dep("org.springframework.cloud", "spring-cloud-gcp-starter-storage", Ver("1.2.8.RELEASE", 0))
            val spring_cloud_starter = Dep("org.springframework.cloud", "spring-cloud-starter", Ver("4.0.4", 0))
            val spring_cloud_starter_aws = Dep("org.springframework.cloud", "spring-cloud-starter-aws", Ver("2.2.6.RELEASE", 0))
            val spring_cloud_starter_aws_jdbc = Dep("org.springframework.cloud", "spring-cloud-starter-aws-jdbc", Ver("2.2.6.RELEASE", 0))
            val spring_cloud_starter_aws_messaging = Dep("org.springframework.cloud", "spring-cloud-starter-aws-messaging", Ver("2.2.6.RELEASE", 0))
            val spring_cloud_starter_circuitbreaker_reactor_resilience4j = Dep("org.springframework.cloud", "spring-cloud-starter-circuitbreaker-reactor-resilience4j", Ver("3.0.3", 0))
            val spring_cloud_starter_config = Dep("org.springframework.cloud", "spring-cloud-starter-config", Ver("4.0.4", 0))
            val spring_cloud_starter_consul_config = Dep("org.springframework.cloud", "spring-cloud-starter-consul-config", Ver("4.0.3", 0))
            val spring_cloud_starter_consul_discovery = Dep("org.springframework.cloud", "spring-cloud-starter-consul-discovery", Ver("4.0.3", 0))
            val spring_cloud_starter_contract_stub_runner = Dep("org.springframework.cloud", "spring-cloud-starter-contract-stub-runner", Ver("4.0.4", 0))
            val spring_cloud_starter_contract_verifier = Dep("org.springframework.cloud", "spring-cloud-starter-contract-verifier", Ver("4.0.4", 0))
            val spring_cloud_starter_gateway = Dep("org.springframework.cloud", "spring-cloud-starter-gateway", Ver("4.0.8", 0))
            val spring_cloud_starter_loadbalancer = Dep("org.springframework.cloud", "spring-cloud-starter-loadbalancer", Ver("4.0.4", 0))
            val spring_cloud_starter_netflix_eureka_client = Dep("org.springframework.cloud", "spring-cloud-starter-netflix-eureka-client", Ver("4.0.3", 0))
            val spring_cloud_starter_netflix_eureka_server = Dep("org.springframework.cloud", "spring-cloud-starter-netflix-eureka-server", Ver("4.0.3", 0))
            val spring_cloud_starter_netflix_hystrix = Dep("org.springframework.cloud", "spring-cloud-starter-netflix-hystrix", Ver("2.2.10.RELEASE", 0))
            val spring_cloud_starter_netflix_hystrix_dashboard = Dep("org.springframework.cloud", "spring-cloud-starter-netflix-hystrix-dashboard", Ver("2.2.10.RELEASE", 0))
            val spring_cloud_starter_netflix_ribbon = Dep("org.springframework.cloud", "spring-cloud-starter-netflix-ribbon", Ver("2.2.10.RELEASE", 0))
            val spring_cloud_starter_netflix_turbine = Dep("org.springframework.cloud", "spring-cloud-starter-netflix-turbine", Ver("2.2.10.RELEASE", 0))
            val spring_cloud_starter_netflix_turbine_stream = Dep("org.springframework.cloud", "spring-cloud-starter-netflix-turbine-stream", Ver("2.2.10.RELEASE", 0))
            val spring_cloud_starter_netflix_zuul = Dep("org.springframework.cloud", "spring-cloud-starter-netflix-zuul", Ver("2.2.10.RELEASE", 0))
            val spring_cloud_starter_oauth2 = Dep("org.springframework.cloud", "spring-cloud-starter-oauth2", Ver("2.2.5.RELEASE", 0))
            val spring_cloud_starter_open_service_broker = Dep("org.springframework.cloud", "spring-cloud-starter-open-service-broker", Ver("4.1.0", 0))
            val spring_cloud_starter_openfeign = Dep("org.springframework.cloud", "spring-cloud-starter-openfeign", Ver("4.0.4", 0))
            val spring_cloud_starter_security = Dep("org.springframework.cloud", "spring-cloud-starter-security", Ver("2.2.5.RELEASE", 0))
            val spring_cloud_starter_sleuth = Dep("org.springframework.cloud", "spring-cloud-starter-sleuth", Ver("3.1.9", 0))
            val spring_cloud_starter_task = Dep("org.springframework.cloud", "spring-cloud-starter-task", Ver("3.0.3", 0))
            val spring_cloud_starter_vault_config = Dep("org.springframework.cloud", "spring-cloud-starter-vault-config", Ver("4.0.1", 0))
            val spring_cloud_starter_zipkin = Dep("org.springframework.cloud", "spring-cloud-starter-zipkin", Ver("2.2.8.RELEASE", 0))
            val spring_cloud_starter_zookeeper_config = Dep("org.springframework.cloud", "spring-cloud-starter-zookeeper-config", Ver("4.0.1", 0))
            val spring_cloud_starter_zookeeper_discovery = Dep("org.springframework.cloud", "spring-cloud-starter-zookeeper-discovery", Ver("4.0.1", 0))
            val spring_cloud_stream = Dep("org.springframework.cloud", "spring-cloud-stream", Ver("4.0.4", 0))
            val spring_cloud_stream_binder_kafka = Dep("org.springframework.cloud", "spring-cloud-stream-binder-kafka", Ver("4.0.4", 0))
            val spring_cloud_stream_binder_kafka_streams = Dep("org.springframework.cloud", "spring-cloud-stream-binder-kafka-streams", Ver("4.0.4", 0))
            val spring_cloud_stream_binder_rabbit = Dep("org.springframework.cloud", "spring-cloud-stream-binder-rabbit", Ver("4.0.4", 0))
        }
        object Data {
            val spring_data_rest_hal_explorer = Dep("org.springframework.data", "spring-data-rest-hal-explorer", Ver("4.2.0", 0))
        }
        object Geode {
            val spring_geode_bom = Dep("org.springframework.geode", "spring-geode-bom", Ver("1.7.5", 0))
            val spring_geode_starter = Dep("org.springframework.geode", "spring-geode-starter", Ver("1.7.5", 0))
        }
        object Integration {
            val spring_integration_amqp = Dep("org.springframework.integration", "spring-integration-amqp", Ver("6.2.0", 0))
            val spring_integration_gemfire = Dep("org.springframework.integration", "spring-integration-gemfire", Ver("5.5.20", 0))
            val spring_integration_jdbc = Dep("org.springframework.integration", "spring-integration-jdbc", Ver("6.2.0", 0))
            val spring_integration_jms = Dep("org.springframework.integration", "spring-integration-jms", Ver("6.2.0", 0))
            val spring_integration_jpa = Dep("org.springframework.integration", "spring-integration-jpa", Ver("6.2.0", 0))
            val spring_integration_kafka = Dep("org.springframework.integration", "spring-integration-kafka", Ver("6.2.0", 0))
            val spring_integration_mail = Dep("org.springframework.integration", "spring-integration-mail", Ver("6.2.0", 0))
            val spring_integration_mongodb = Dep("org.springframework.integration", "spring-integration-mongodb", Ver("6.2.0", 0))
            val spring_integration_r2dbc = Dep("org.springframework.integration", "spring-integration-r2dbc", Ver("6.2.0", 0))
            val spring_integration_redis = Dep("org.springframework.integration", "spring-integration-redis", Ver("6.2.0", 0))
            val spring_integration_rsocket = Dep("org.springframework.integration", "spring-integration-rsocket", Ver("6.2.0", 0))
            val spring_integration_security = Dep("org.springframework.integration", "spring-integration-security", Ver("6.2.0", 0))
            val spring_integration_stomp = Dep("org.springframework.integration", "spring-integration-stomp", Ver("6.2.0", 0))
            val spring_integration_test = Dep("org.springframework.integration", "spring-integration-test", Ver("6.2.0", 0))
            val spring_integration_webflux = Dep("org.springframework.integration", "spring-integration-webflux", Ver("6.2.0", 0))
            val spring_integration_websocket = Dep("org.springframework.integration", "spring-integration-websocket", Ver("6.2.0", 0))
            val spring_integration_ws = Dep("org.springframework.integration", "spring-integration-ws", Ver("6.2.0", 0))
        }
        object Kafka {
            val spring_kafka = Dep("org.springframework.kafka", "spring-kafka", Ver("3.1.0", 0))
            val spring_kafka_test = Dep("org.springframework.kafka", "spring-kafka-test", Ver("3.1.0", 0))
        }
        object Restdocs {
            val spring_restdocs_webtestclient = Dep("org.springframework.restdocs", "spring-restdocs-webtestclient", Ver("3.0.1", 0))
        }
        object Security {
            val spring_security_messaging = Dep("org.springframework.security", "spring-security-messaging", Ver("6.2.0", 0))
            val spring_security_rsocket = Dep("org.springframework.security", "spring-security-rsocket", Ver("6.2.0", 0))
            val spring_security_test = Dep("org.springframework.security", "spring-security-test", Ver("6.2.0", 0))
        }
        object Session {
            val spring_session_data_redis = Dep("org.springframework.session", "spring-session-data-redis", Ver("3.2.0", 0))
            val spring_session_jdbc = Dep("org.springframework.session", "spring-session-jdbc", Ver("3.2.0", 0))
        }
    }
}
object Pl {
    object MarekLangiewicz {
        val abcdk = Dep("pl.mareklangiewicz", "abcdk", Ver("0.0.17", 0))
        val abcdk_js = Dep("pl.mareklangiewicz", "abcdk-js", Ver("0.0.17", 0))
        val abcdk_jvm = Dep("pl.mareklangiewicz", "abcdk-jvm", Ver("0.0.17", 0))
        val abcdk_linuxx64 = Dep("pl.mareklangiewicz", "abcdk-linuxx64", Ver("0.0.17", 0))
        val kground = Dep("pl.mareklangiewicz", "kground", Ver("0.0.21", 0))
        val kground_io = Dep("pl.mareklangiewicz", "kground-io", Ver("0.0.21", 0))
        val kground_io_js = Dep("pl.mareklangiewicz", "kground-io-js", Ver("0.0.21", 0))
        val kground_io_jvm = Dep("pl.mareklangiewicz", "kground-io-jvm", Ver("0.0.21", 0))
        val kground_js = Dep("pl.mareklangiewicz", "kground-js", Ver("0.0.21", 0))
        val kground_jvm = Dep("pl.mareklangiewicz", "kground-jvm", Ver("0.0.21", 0))
        val kgroundx = Dep("pl.mareklangiewicz", "kgroundx", Ver("0.0.21", 0))
        val kgroundx_io = Dep("pl.mareklangiewicz", "kgroundx-io", Ver("0.0.21", 0))
        val kgroundx_io_js = Dep("pl.mareklangiewicz", "kgroundx-io-js", Ver("0.0.21", 0))
        val kgroundx_io_jvm = Dep("pl.mareklangiewicz", "kgroundx-io-jvm", Ver("0.0.21", 0))
        val kgroundx_js = Dep("pl.mareklangiewicz", "kgroundx-js", Ver("0.0.21", 0))
        val kgroundx_jvm = Dep("pl.mareklangiewicz", "kgroundx-jvm", Ver("0.0.21", 0))
        val kgroundx_maintenance = Dep("pl.mareklangiewicz", "kgroundx-maintenance", Ver("0.0.21", 0))
        val kgroundx_maintenance_jvm = Dep("pl.mareklangiewicz", "kgroundx-maintenance-jvm", Ver("0.0.21", 0))
        val kommandjupyter = Dep("pl.mareklangiewicz", "kommandjupyter", Ver("0.0.31", 0))
        val kommandjupyter_jvm = Dep("pl.mareklangiewicz", "kommandjupyter-jvm", Ver("0.0.31", 0))
        val kommandline = Dep("pl.mareklangiewicz", "kommandline", Ver("0.0.31", 0))
        val kommandline_js = Dep("pl.mareklangiewicz", "kommandline-js", Ver("0.0.31", 0))
        val kommandline_jvm = Dep("pl.mareklangiewicz", "kommandline-jvm", Ver("0.0.31", 0))
        val kommandsamples = Dep("pl.mareklangiewicz", "kommandsamples", Ver("0.0.31", 0))
        val kommandsamples_js = Dep("pl.mareklangiewicz", "kommandsamples-js", Ver("0.0.31", 0))
        val kommandsamples_jvm = Dep("pl.mareklangiewicz", "kommandsamples-jvm", Ver("0.0.31", 0))
        val rxmock = Dep("pl.mareklangiewicz", "rxmock", Ver("0.0.03", 0))
        val rxmock_jvm = Dep("pl.mareklangiewicz", "rxmock-jvm", Ver("0.0.03", 0))
        val smokk = Dep("pl.mareklangiewicz", "smokk", Ver("0.0.08", 0))
        val smokk_jvm = Dep("pl.mareklangiewicz", "smokk-jvm", Ver("0.0.08", 0))
        val smokkx = Dep("pl.mareklangiewicz", "smokkx", Ver("0.0.08", 0))
        val smokkx_jvm = Dep("pl.mareklangiewicz", "smokkx-jvm", Ver("0.0.08", 0))
        val template_andro_app = Dep("pl.mareklangiewicz", "template-andro-app", Ver("0.0.03", 0))
        val template_andro_lib = Dep("pl.mareklangiewicz", "template-andro-lib", Ver("0.0.03", 0))
        val template_mpp_lib = Dep("pl.mareklangiewicz", "template-mpp-lib", Ver("0.0.02", 0))
        val template_mpp_lib_js = Dep("pl.mareklangiewicz", "template-mpp-lib-js", Ver("0.0.02", 0))
        val template_mpp_lib_jvm = Dep("pl.mareklangiewicz", "template-mpp-lib-jvm", Ver("0.0.02", 0))
        val tuplek = Dep("pl.mareklangiewicz", "tuplek", Ver("0.0.12", 0))
        val tuplek_js = Dep("pl.mareklangiewicz", "tuplek-js", Ver("0.0.12", 0))
        val tuplek_jvm = Dep("pl.mareklangiewicz", "tuplek-jvm", Ver("0.0.12", 0))
        val tuplek_linuxx64 = Dep("pl.mareklangiewicz", "tuplek-linuxx64", Ver("0.0.12", 0))
        val upue = Dep("pl.mareklangiewicz", "upue", Ver("0.0.14", 0))
        val upue_js = Dep("pl.mareklangiewicz", "upue-js", Ver("0.0.14", 0))
        val upue_jvm = Dep("pl.mareklangiewicz", "upue-jvm", Ver("0.0.14", 0))
        val upue_linuxx64 = Dep("pl.mareklangiewicz", "upue-linuxx64", Ver("0.0.14", 0))
        val upue_test = Dep("pl.mareklangiewicz", "upue-test", Ver("0.0.14", 0))
        val upue_test_js = Dep("pl.mareklangiewicz", "upue-test-js", Ver("0.0.14", 0))
        val upue_test_jvm = Dep("pl.mareklangiewicz", "upue-test-jvm", Ver("0.0.14", 0))
        val uspek = Dep("pl.mareklangiewicz", "uspek", Ver("0.0.28", 0))
        val uspek_js = Dep("pl.mareklangiewicz", "uspek-js", Ver("0.0.28", 0))
        val uspek_jvm = Dep("pl.mareklangiewicz", "uspek-jvm", Ver("0.0.28", 0))
        val uspek_linuxx64 = Dep("pl.mareklangiewicz", "uspek-linuxx64", Ver("0.0.28", 0))
        val uspekx = Dep("pl.mareklangiewicz", "uspekx", Ver("0.0.28", 0))
        val uspekx_js = Dep("pl.mareklangiewicz", "uspekx-js", Ver("0.0.28", 0))
        val uspekx_junit4 = Dep("pl.mareklangiewicz", "uspekx-junit4", Ver("0.0.28", 0))
        val uspekx_junit4_jvm = Dep("pl.mareklangiewicz", "uspekx-junit4-jvm", Ver("0.0.28", 0))
        val uspekx_junit5 = Dep("pl.mareklangiewicz", "uspekx-junit5", Ver("0.0.28", 0))
        val uspekx_junit5_jvm = Dep("pl.mareklangiewicz", "uspekx-junit5-jvm", Ver("0.0.28", 0))
        val uspekx_jvm = Dep("pl.mareklangiewicz", "uspekx-jvm", Ver("0.0.28", 0))
        val uspekx_linuxx64 = Dep("pl.mareklangiewicz", "uspekx-linuxx64", Ver("0.0.28", 0))
        val uwidgets = Dep("pl.mareklangiewicz", "uwidgets", Ver("0.0.06", 0))
        val uwidgets_js = Dep("pl.mareklangiewicz", "uwidgets-js", Ver("0.0.06", 0))
        val uwidgets_jvm = Dep("pl.mareklangiewicz", "uwidgets-jvm", Ver("0.0.06", 0))
        val uwidgets_udemo = Dep("pl.mareklangiewicz", "uwidgets-udemo", Ver("0.0.06", 0))
        val uwidgets_udemo_js = Dep("pl.mareklangiewicz", "uwidgets-udemo-js", Ver("0.0.06", 0))
        val uwidgets_udemo_jvm = Dep("pl.mareklangiewicz", "uwidgets-udemo-jvm", Ver("0.0.06", 0))
    }
}

// endregion [Deps Generated]