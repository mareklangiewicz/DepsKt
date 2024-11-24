@file:Suppress(
  "unused",
  "SpellCheckingInspection",
  "GrazieInspection",
  "ClassName",
  "PackageDirectoryMismatch",
  "MemberVisibilityCanBePrivate", "FunctionName",
)

package pl.mareklangiewicz.deps

import pl.mareklangiewicz.utils.*

// region [[Deps Data Structures]]

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
 * Unknown -> 900
 *
 * Other positive numbers are allowed if really necessary.
 */
data class Instability(val instability: Int)

private val reStableStr = """\d{1,3}\.\d{1,8}\.\d{1,8}"""
private val instabilities = linkedMapOf<Regex, Int>(
  // order of keys/regexes matters (potentially more stable only if not matched as less stable)
  Regex("(?i:snapshot)") to 500,
  Regex("(?i:preview)") to 400,
  Regex("(?i:dev)") to 320,
  Regex("(?i:alpha)") to 300,
  Regex("(?i:beta)") to 200,
  Regex("(?i:eap)") to 140,
  Regex("$reStableStr.*?M\\d") to 120,
  Regex("$reStableStr.*?(?i:rc)") to 100,
  Regex(reStableStr) to 0,
)

fun detectInstability(verStr: String): Instability {
  for (re in instabilities.keys)
    if (re.find(verStr) != null)
      return Instability(instabilities[re]!!)
  return Instability(900) // Unknown (so assume very unstable)
}

infix fun Instability?.moreStableThan(other: Instability?): Boolean {
  val left = this?.instability ?: Int.MAX_VALUE
  val right = other?.instability ?: Int.MAX_VALUE
  return left < right
}

data class Ver(val str: String, val instability: Instability = detectInstability(str)) {
  constructor(ver: String, instability: Int) : this(ver, Instability(instability))
  constructor(major: Int, minor: Int, patch: Int, patchLength: Int = 2, suffix: String = "") :
    this("$major.$minor.${patch.toString().padStart(patchLength, '0')}$suffix")

  val code get() = str.toVerIntCode()
}

class NoSuchVerException(val dep: Dep, override val message: String = "No such ver.") : NoSuchElementException()

/** versions should always be sorted from oldest to newest */
data class Dep(val group: String, val name: String, val vers: List<Ver>) : CharSequence {
  constructor(group: String, name: String, vararg vers: Ver) : this(group, name, vers.toList())

  val verLast: Ver get() = verLastOrNull ?: throw NoSuchVerException(this, "No vers at all.")
  val verLastOrNull: Ver? get() = vers.lastOrNull()
  val mvn: String get() = verLastOrNull?.str?.let { "$group:$name:$it" } ?: "$group:$name"
  override fun toString() = mvn
  override val length get() = mvn.length
  override fun get(index: Int) = mvn[index]
  override fun subSequence(startIndex: Int, endIndex: Int) = mvn.subSequence(startIndex, endIndex)
}

fun DepP(pluginId: String, vararg vers: Ver) = Dep(pluginId, "$pluginId.gradle.plugin", *vers)

fun Dep.withNoVer() = copy(vers = emptyList())
fun Dep.withVer(ver: Ver) = copy(vers = listOf(ver))
fun Dep.withVer(verStr: String, verInstability: Instability = detectInstability(verStr)) =
  withVer(Ver(verStr, verInstability))

fun Dep.withVers(vararg vers: Ver) = copy(vers = vers.toList())
fun Dep.withVers(maxInstability: Instability) =
  copy(vers = vers.filter { !(maxInstability moreStableThan it.instability) })

val Dep.verLastStable get() = vers.lastOrNull { it.instability.instability == 0 } ?: throw NoSuchVerException(this, "No stable vers.")

// endregion [[Deps Data Structures]]


// region [[Deps Selected]]

/**
 * - [releases](https://github.com/JetBrains/kotlin/releases)
 * - [release details](https://kotlinlang.org/docs/releases.html#release-details)
 * - [changelog](https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md)
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
 * - [github](https://github.com/mareklangiewicz)
 * - [github repositories](https://github.com/mareklangiewicz?tab=repositories)
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

// endregion [[Deps Selected]]



// Micro syntax/dsl (private syntax sugar) so diffs can be understood at glance.
private infix fun String.d(name: String) = Dep(this, name)
private infix fun Dep.w(verStr: String) = copy(vers = vers + Ver(verStr))

// @formatter:off
// region [[Deps Generated]]

object AndroidX {
  object Activity {
    val activity = "androidx.activity" d "activity" w "1.3.0-beta01" w "1.9.3" w "1.10.0-beta01"
    val compose = "androidx.activity" d "activity-compose" w "1.3.0-beta01" w "1.9.3" w "1.10.0-beta01"
    val ktx = "androidx.activity" d "activity-ktx" w "1.3.0-beta01" w "1.9.3" w "1.10.0-beta01"
  }
  object Annotation {
    val annotation = "androidx.annotation" d "annotation" w "1.3.0-alpha01" w "1.9.1"
    val experimental = "androidx.annotation" d "annotation-experimental" w "1.2.0-alpha01" w "1.4.1" w "1.5.0-alpha01"
  }
  object AppCompat {
    val appcompat = "androidx.appcompat" d "appcompat" w "1.7.0"
    val resources = "androidx.appcompat" d "appcompat-resources" w "1.7.0"
  }
  object AppSearch {
    val appsearch = "androidx.appsearch" d "appsearch" w "1.1.0-alpha06"
    val builtin_types = "androidx.appsearch" d "appsearch-builtin-types" w "1.1.0-alpha06"
    val compiler = "androidx.appsearch" d "appsearch-compiler" w "1.1.0-alpha06"
    val local_storage = "androidx.appsearch" d "appsearch-local-storage" w "1.1.0-alpha06"
    val platform_storage = "androidx.appsearch" d "appsearch-platform-storage" w "1.1.0-alpha06"
  }
  object Arch {
    object Core {
      val common = "androidx.arch.core" d "core-common" w "2.2.0"
      val runtime = "androidx.arch.core" d "core-runtime" w "2.2.0"
      val testing = "androidx.arch.core" d "core-testing" w "2.2.0"
    }
  }
  object AsyncLayoutInflater {
    val appcompat = "androidx.asynclayoutinflater" d "asynclayoutinflater-appcompat" w "1.1.0-alpha01"
    val asynclayoutinflater = "androidx.asynclayoutinflater" d "asynclayoutinflater" w "1.0.0" w "1.1.0-alpha01"
  }
  object AutoFill {
    val autofill = "androidx.autofill" d "autofill" w "1.1.0" w "1.3.0-beta01"
  }
  object Benchmark {
    val common = "androidx.benchmark" d "benchmark-common" w "1.1.0-alpha03" w "1.3.3" w "1.4.0-alpha05"
    val gradle_plugin = "androidx.benchmark" d "benchmark-gradle-plugin" w "1.3.3" w "1.4.0-alpha05"
    val junit4 = "androidx.benchmark" d "benchmark-junit4" w "1.1.0-alpha03" w "1.3.3" w "1.4.0-alpha05"
    val macro = "androidx.benchmark" d "benchmark-macro" w "1.1.0-alpha03" w "1.3.3" w "1.4.0-alpha05"
    val macro_junit4 = "androidx.benchmark" d "benchmark-macro-junit4" w "1.1.0-alpha03" w "1.3.3" w "1.4.0-alpha05"
  }
  object Biometric {
    val biometric = "androidx.biometric" d "biometric" w "1.1.0" w "1.4.0-alpha02"
    val ktx = "androidx.biometric" d "biometric-ktx" w "1.4.0-alpha02"
  }
  object Browser {
    val browser = "androidx.browser" d "browser" w "1.8.0"
  }
  object Camera {
    val camera2 = "androidx.camera" d "camera-camera2" w "1.4.0" w "1.5.0-alpha03"
    val core = "androidx.camera" d "camera-core" w "1.4.0" w "1.5.0-alpha03"
    val extensions = "androidx.camera" d "camera-extensions" w "1.4.0" w "1.5.0-alpha03"
    val lifecycle = "androidx.camera" d "camera-lifecycle" w "1.4.0" w "1.5.0-alpha03"
    val mlkit_vision = "androidx.camera" d "camera-mlkit-vision" w "1.4.0" w "1.5.0-alpha03"
    val video = "androidx.camera" d "camera-video" w "1.4.0" w "1.5.0-alpha03"
    val view = "androidx.camera" d "camera-view" w "1.4.0" w "1.5.0-alpha03"
  }
  object Car {
    object App {
      val app = "androidx.car.app" d "app" w "1.4.0" w "1.7.0-beta03"
      val automotive = "androidx.car.app" d "app-automotive" w "1.4.0" w "1.7.0-beta03"
      val projected = "androidx.car.app" d "app-projected" w "1.4.0" w "1.7.0-beta03"
      val testing = "androidx.car.app" d "app-testing" w "1.4.0" w "1.7.0-beta03"
    }
  }
  object CardView {
    val cardview = "androidx.cardview" d "cardview" w "1.0.0"
  }
  object Collection {
    val collection = "androidx.collection" d "collection" w "1.4.5" w "1.5.0-alpha06"
    val ktx = "androidx.collection" d "collection-ktx" w "1.4.5" w "1.5.0-alpha06"
  }
  object Compose {
    val bom = "androidx.compose" d "compose-bom" w "2024.11.00"
    object Animation {
      val animation = "androidx.compose.animation" d "animation" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val core = "androidx.compose.animation" d "animation-core" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val graphics = "androidx.compose.animation" d "animation-graphics" w "1.7.5" w "1.8.0-alpha06"
    }
    object Compiler {
      val compiler = "androidx.compose.compiler" d "compiler" w "1.0.0-beta06" w "1.5.15"
    }
    object Foundation {
      val foundation = "androidx.compose.foundation" d "foundation" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val layout = "androidx.compose.foundation" d "foundation-layout" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
    }
    object Material {
      val icons_core = "androidx.compose.material" d "material-icons-core" w "1.0.0-beta06" w "1.7.5"
      val icons_extended = "androidx.compose.material" d "material-icons-extended" w "1.0.0-beta06" w "1.7.5"
      val material = "androidx.compose.material" d "material" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val ripple = "androidx.compose.material" d "material-ripple" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
    }
    object Material3 {
      val material3 = "androidx.compose.material3" d "material3" w "1.3.1" w "1.4.0-alpha04"
      val window_size_class = "androidx.compose.material3" d "material3-window-size-class" w "1.3.1" w "1.4.0-alpha04"
    }
    object Runtime {
      val dispatch = "androidx.compose.runtime" d "runtime-dispatch" w "1.0.0-alpha12"
      val livedata = "androidx.compose.runtime" d "runtime-livedata" w "1.7.5" w "1.8.0-alpha06"
      val runtime = "androidx.compose.runtime" d "runtime" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val rxjava2 = "androidx.compose.runtime" d "runtime-rxjava2" w "1.7.5" w "1.8.0-alpha06"
      val rxjava3 = "androidx.compose.runtime" d "runtime-rxjava3" w "1.7.5" w "1.8.0-alpha06"
      val saveable = "androidx.compose.runtime" d "runtime-saveable" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val tracing = "androidx.compose.runtime" d "runtime-tracing" w "1.7.5" w "1.8.0-alpha06"
    }
    object Ui {
      val geometry = "androidx.compose.ui" d "ui-geometry" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val graphics = "androidx.compose.ui" d "ui-graphics" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val test = "androidx.compose.ui" d "ui-test" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val test_junit4 = "androidx.compose.ui" d "ui-test-junit4" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val test_manifest = "androidx.compose.ui" d "ui-test-manifest" w "1.7.5" w "1.8.0-alpha06"
      val text = "androidx.compose.ui" d "ui-text" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val text_google_fonts = "androidx.compose.ui" d "ui-text-google-fonts" w "1.7.5" w "1.8.0-alpha06"
      val tooling = "androidx.compose.ui" d "ui-tooling" w "1.7.5" w "1.8.0-alpha06"
      val tooling_data = "androidx.compose.ui" d "ui-tooling-data" w "1.7.5" w "1.8.0-alpha06"
      val tooling_preview = "androidx.compose.ui" d "ui-tooling-preview" w "1.7.5" w "1.8.0-alpha06"
      val ui = "androidx.compose.ui" d "ui" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val unit = "androidx.compose.ui" d "ui-unit" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val util = "androidx.compose.ui" d "ui-util" w "1.0.0-beta06" w "1.7.5" w "1.8.0-alpha06"
      val viewbinding = "androidx.compose.ui" d "ui-viewbinding" w "1.7.5" w "1.8.0-alpha06"
    }
  }
  object Concurrent {
    val futures = "androidx.concurrent" d "concurrent-futures" w "1.2.0"
    val futures_ktx = "androidx.concurrent" d "concurrent-futures-ktx" w "1.2.0"
  }
  object ConstraintLayout {
    val compose = "androidx.constraintlayout" d "constraintlayout-compose" w "1.1.0"
    val constraintlayout = "androidx.constraintlayout" d "constraintlayout" w "2.2.0"
  }
  object ContentPager {
    val contentpager = "androidx.contentpager" d "contentpager" w "1.0.0"
  }
  object CoordinatorLayout {
    val coordinatorlayout = "androidx.coordinatorlayout" d "coordinatorlayout" w "1.2.0" w "1.3.0-alpha02"
  }
  object Core {
    val animation = "androidx.core" d "core-animation" w "1.0.0"
    val animation_testing = "androidx.core" d "core-animation-testing" w "1.0.0"
    val core = "androidx.core" d "core" w "1.15.0"
    val google_shortcuts = "androidx.core" d "core-google-shortcuts" w "1.1.0" w "1.2.0-alpha01"
    val ktx = "androidx.core" d "core-ktx" w "1.15.0"
    val performance = "androidx.core" d "core-performance" w "1.0.0"
    val remoteviews = "androidx.core" d "core-remoteviews" w "1.1.0"
    val role = "androidx.core" d "core-role" w "1.0.0" w "1.1.0-rc01"
    val splashscreen = "androidx.core" d "core-splashscreen" w "1.0.1" w "1.1.0-rc01" w "1.2.0-alpha02"
    object Uwb {
      val rxjava3 = "androidx.core.uwb" d "uwb-rxjava3" w "1.0.0-alpha09"
      val uwb = "androidx.core.uwb" d "uwb" w "1.0.0-alpha09"
    }
  }
  object CursorAdapter {
    val cursoradapter = "androidx.cursoradapter" d "cursoradapter" w "1.0.0"
  }
  object CustomView {
    val customview = "androidx.customview" d "customview" w "1.1.0" w "1.2.0-alpha02"
    val poolingcontainer = "androidx.customview" d "customview-poolingcontainer" w "1.0.0"
  }
  object DataStore {
    val core = "androidx.datastore" d "datastore-core" w "1.1.1"
    val core_okio = "androidx.datastore" d "datastore-core-okio" w "1.1.1"
    val datastore = "androidx.datastore" d "datastore" w "1.1.1"
    val preferences = "androidx.datastore" d "datastore-preferences" w "1.1.1"
    val preferences_core = "androidx.datastore" d "datastore-preferences-core" w "1.1.1"
    val preferences_rxjava2 = "androidx.datastore" d "datastore-preferences-rxJava2" w "1.1.1"
    val preferences_rxjava3 = "androidx.datastore" d "datastore-preferences-rxJava3" w "1.1.1"
    val rxjava2 = "androidx.datastore" d "datastore-rxJava2" w "1.1.1"
    val rxjava3 = "androidx.datastore" d "datastore-rxJava3" w "1.1.1"
  }
  object DocumentFile {
    val documentfile = "androidx.documentfile" d "documentfile" w "1.0.1" w "1.1.0-alpha01"
  }
  object Draganddrop {
    val draganddrop = "androidx.draganddrop" d "draganddrop" w "1.0.0"
  }
  object DrawerLayout {
    val drawerlayout = "androidx.drawerlayout" d "drawerlayout" w "1.2.0"
  }
  object DynamicAnimation {
    val dynamicanimation = "androidx.dynamicanimation" d "dynamicanimation" w "1.0.0" w "1.1.0-alpha03"
    val ktx = "androidx.dynamicanimation" d "dynamicanimation-ktx" w "1.0.0-alpha03"
  }
  object Emoji {
    val appcompat = "androidx.emoji" d "emoji-appcompat" w "1.1.0" w "1.2.0-alpha03"
    val bundled = "androidx.emoji" d "emoji-bundled" w "1.1.0" w "1.2.0-alpha03"
    val emoji = "androidx.emoji" d "emoji" w "1.1.0" w "1.2.0-alpha03"
  }
  object Emoji2 {
    val bundled = "androidx.emoji2" d "emoji2-bundled" w "1.5.0"
    val emoji2 = "androidx.emoji2" d "emoji2" w "1.5.0"
    val views = "androidx.emoji2" d "emoji2-views" w "1.5.0"
    val views_helper = "androidx.emoji2" d "emoji2-views-helper" w "1.5.0"
  }
  object Enterprise {
    val feedback = "androidx.enterprise" d "enterprise-feedback" w "1.1.0"
    val feedback_testing = "androidx.enterprise" d "enterprise-feedback-testing" w "1.1.0"
  }
  object Exifinterface {
    val exifinterface = "androidx.exifinterface" d "exifinterface" w "1.3.7" w "1.4.0-alpha01"
  }
  object Fragment {
    val fragment = "androidx.fragment" d "fragment" w "1.8.5"
    val ktx = "androidx.fragment" d "fragment-ktx" w "1.8.5"
    val testing = "androidx.fragment" d "fragment-testing" w "1.8.5"
  }
  object Games {
    val activity = "androidx.games" d "games-activity" w "3.0.5" w "4.0.0-alpha01"
    val controller = "androidx.games" d "games-controller" w "2.0.2"
    val frame_pacing = "androidx.games" d "games-frame-pacing" w "2.1.2"
    val performance_tuner = "androidx.games" d "games-performance-tuner" w "2.0.0"
    val text_input = "androidx.games" d "games-text-input" w "3.0.4" w "4.0.0-alpha01"
  }
  object Glance {
    val appwidget = "androidx.glance" d "glance-appwidget" w "1.1.1"
    val glance = "androidx.glance" d "glance" w "1.1.1"
    val wear_tiles = "androidx.glance" d "glance-wear-tiles" w "1.0.0-alpha05"
  }
  object GraphIcs {
    val core = "androidx.graphics" d "graphics-core" w "1.0.2"
  }
  object GridLayout {
    val gridlayout = "androidx.gridlayout" d "gridlayout" w "1.0.0" w "1.1.0-beta01"
  }
  object Health {
    val services_client = "androidx.health" d "health-services-client" w "1.0.0-rc02" w "1.1.0-alpha04"
    object Connect {
      val client = "androidx.health.connect" d "connect-client" w "1.1.0-alpha10"
    }
  }
  object Heifwriter {
    val heifwriter = "androidx.heifwriter" d "heifwriter" w "1.0.0" w "1.1.0-alpha03"
  }
  object Hilt {
    val compiler = "androidx.hilt" d "hilt-compiler" w "1.2.0"
    val navigation_compose = "androidx.hilt" d "hilt-navigation-compose" w "1.0.0-alpha03" w "1.2.0"
    val navigation_fragment = "androidx.hilt" d "hilt-navigation-fragment" w "1.2.0"
    val work = "androidx.hilt" d "hilt-work" w "1.2.0"
  }
  object Input {
    val motionprediction = "androidx.input" d "input-motionprediction" w "1.0.0-beta05"
  }
  object Interpolator {
    val interpolator = "androidx.interpolator" d "interpolator" w "1.0.0"
  }
  object JavaScriptengine {
    val javascriptengine = "androidx.javascriptengine" d "javascriptengine" w "1.0.0-beta01"
  }
  object Leanback {
    val grid = "androidx.leanback" d "leanback-grid" w "1.0.0-alpha03"
    val leanback = "androidx.leanback" d "leanback" w "1.0.0" w "1.1.0-rc02" w "1.2.0-alpha04"
    val paging = "androidx.leanback" d "leanback-paging" w "1.1.0-alpha11"
    val preference = "androidx.leanback" d "leanback-preference" w "1.0.0" w "1.1.0-rc01" w "1.2.0-alpha04"
    val tab = "androidx.leanback" d "leanback-tab" w "1.1.0-beta01"
  }
  object Lifecycle {
    val common = "androidx.lifecycle" d "lifecycle-common" w "2.8.7" w "2.9.0-alpha07"
    val common_java8 = "androidx.lifecycle" d "lifecycle-common-java8" w "2.8.7" w "2.9.0-alpha07"
    val compiler = "androidx.lifecycle" d "lifecycle-compiler" w "2.8.7" w "2.9.0-alpha07"
    val extensions = "androidx.lifecycle" d "lifecycle-extensions" w "2.2.0"
    val livedata = "androidx.lifecycle" d "lifecycle-livedata" w "2.8.7" w "2.9.0-alpha07"
    val livedata_ktx = "androidx.lifecycle" d "lifecycle-livedata-ktx" w "2.8.7" w "2.9.0-alpha07"
    val process = "androidx.lifecycle" d "lifecycle-process" w "2.8.7" w "2.9.0-alpha07"
    val reactivestreams = "androidx.lifecycle" d "lifecycle-reactivestreams" w "2.8.7" w "2.9.0-alpha07"
    val reactivestreams_ktx = "androidx.lifecycle" d "lifecycle-reactivestreams-ktx" w "2.8.7" w "2.9.0-alpha07"
    val runtime = "androidx.lifecycle" d "lifecycle-runtime" w "2.8.7" w "2.9.0-alpha07"
    val runtime_compose = "androidx.lifecycle" d "lifecycle-runtime-compose" w "2.8.7" w "2.9.0-alpha07"
    val runtime_ktx = "androidx.lifecycle" d "lifecycle-runtime-ktx" w "2.8.7" w "2.9.0-alpha07"
    val runtime_testing = "androidx.lifecycle" d "lifecycle-runtime-testing" w "2.8.7" w "2.9.0-alpha07"
    val service = "androidx.lifecycle" d "lifecycle-service" w "2.8.7" w "2.9.0-alpha07"
    val viewmodel = "androidx.lifecycle" d "lifecycle-viewmodel" w "2.8.7" w "2.9.0-alpha07"
    val viewmodel_compose = "androidx.lifecycle" d "lifecycle-viewmodel-compose" w "1.0.0-alpha06" w "2.8.7" w "2.9.0-alpha07"
    val viewmodel_ktx = "androidx.lifecycle" d "lifecycle-viewmodel-ktx" w "2.8.7" w "2.9.0-alpha07"
    val viewmodel_savedstate = "androidx.lifecycle" d "lifecycle-viewmodel-savedstate" w "2.8.7" w "2.9.0-alpha07"
  }
  object Loader {
    val loader = "androidx.loader" d "loader" w "1.1.0"
  }
  object LocalBroadcastManager {
    val localbroadcastmanager = "androidx.localbroadcastmanager" d "localbroadcastmanager" w "1.1.0"
  }
  object Media {
    val media = "androidx.media" d "media" w "1.7.0"
  }
  object Media2 {
    val common = "androidx.media2" d "media2-common" w "1.3.0"
    val exoplayer = "androidx.media2" d "media2-exoplayer" w "1.3.0"
    val player = "androidx.media2" d "media2-player" w "1.3.0"
    val session = "androidx.media2" d "media2-session" w "1.3.0"
    val widget = "androidx.media2" d "media2-widget" w "1.3.0"
  }
  object Media3 {
    val cast = "androidx.media3" d "media3-cast" w "1.4.1" w "1.5.0-rc02"
    val common = "androidx.media3" d "media3-common" w "1.4.1" w "1.5.0-rc02"
    val database = "androidx.media3" d "media3-database" w "1.4.1" w "1.5.0-rc02"
    val datasource = "androidx.media3" d "media3-datasource" w "1.4.1" w "1.5.0-rc02"
    val datasource_cronet = "androidx.media3" d "media3-datasource-cronet" w "1.4.1" w "1.5.0-rc02"
    val datasource_okhttp = "androidx.media3" d "media3-datasource-okhttp" w "1.4.1" w "1.5.0-rc02"
    val datasource_rtmp = "androidx.media3" d "media3-datasource-rtmp" w "1.4.1" w "1.5.0-rc02"
    val decoder = "androidx.media3" d "media3-decoder" w "1.4.1" w "1.5.0-rc02"
    val exoplayer = "androidx.media3" d "media3-exoplayer" w "1.4.1" w "1.5.0-rc02"
    val exoplayer_dash = "androidx.media3" d "media3-exoplayer-dash" w "1.4.1" w "1.5.0-rc02"
    val exoplayer_hls = "androidx.media3" d "media3-exoplayer-hls" w "1.4.1" w "1.5.0-rc02"
    val exoplayer_ima = "androidx.media3" d "media3-exoplayer-ima" w "1.4.1" w "1.5.0-rc02"
    val exoplayer_rtsp = "androidx.media3" d "media3-exoplayer-rtsp" w "1.4.1" w "1.5.0-rc02"
    val exoplayer_workmanager = "androidx.media3" d "media3-exoplayer-workmanager" w "1.4.1" w "1.5.0-rc02"
    val extractor = "androidx.media3" d "media3-extractor" w "1.4.1" w "1.5.0-rc02"
    val session = "androidx.media3" d "media3-session" w "1.4.1" w "1.5.0-rc02"
    val test_utils = "androidx.media3" d "media3-test-utils" w "1.4.1" w "1.5.0-rc02"
    val test_utils_robolectric = "androidx.media3" d "media3-test-utils-robolectric" w "1.4.1" w "1.5.0-rc02"
    val transformer = "androidx.media3" d "media3-transformer" w "1.4.1" w "1.5.0-rc02"
    val ui = "androidx.media3" d "media3-ui" w "1.4.1" w "1.5.0-rc02"
    val ui_leanback = "androidx.media3" d "media3-ui-leanback" w "1.4.1" w "1.5.0-rc02"
  }
  object MediaRouter {
    val mediarouter = "androidx.mediarouter" d "mediarouter" w "1.7.0"
  }
  object Metrics {
    val performance = "androidx.metrics" d "metrics-performance" w "1.0.0-beta01"
  }
  object Multidex {
    val instrumentation = "androidx.multidex" d "multidex-instrumentation" w "2.0.0"
    val multidex = "androidx.multidex" d "multidex" w "2.0.1"
  }
  object Navigation {
    val common = "androidx.navigation" d "navigation-common" w "2.4.0-alpha02" w "2.8.4" w "2.9.0-alpha03"
    val common_ktx = "androidx.navigation" d "navigation-common-ktx" w "2.4.0-alpha02" w "2.8.4" w "2.9.0-alpha03"
    val compose = "androidx.navigation" d "navigation-compose" w "2.4.0-alpha02" w "2.8.4" w "2.9.0-alpha03"
    val dynamic_features_fragment = "androidx.navigation" d "navigation-dynamic-features-fragment" w "2.8.4" w "2.9.0-alpha03"
    val fragment = "androidx.navigation" d "navigation-fragment" w "2.8.4" w "2.9.0-alpha03"
    val fragment_ktx = "androidx.navigation" d "navigation-fragment-ktx" w "2.8.4" w "2.9.0-alpha03"
    val runtime = "androidx.navigation" d "navigation-runtime" w "2.4.0-alpha02" w "2.8.4" w "2.9.0-alpha03"
    val runtime_ktx = "androidx.navigation" d "navigation-runtime-ktx" w "2.4.0-alpha02" w "2.8.4" w "2.9.0-alpha03"
    val safe_args_generator = "androidx.navigation" d "navigation-safe-args-generator" w "2.8.4" w "2.9.0-alpha03"
    val safe_args_gradle_plugin = "androidx.navigation" d "navigation-safe-args-gradle-plugin" w "2.8.4" w "2.9.0-alpha03"
    val testing = "androidx.navigation" d "navigation-testing" w "2.4.0-alpha02" w "2.8.4" w "2.9.0-alpha03"
    val ui = "androidx.navigation" d "navigation-ui" w "2.8.4" w "2.9.0-alpha03"
    val ui_ktx = "androidx.navigation" d "navigation-ui-ktx" w "2.8.4" w "2.9.0-alpha03"
  }
  object Paging {
    val common = "androidx.paging" d "paging-common" w "3.1.0-alpha01" w "3.3.4"
    val common_ktx = "androidx.paging" d "paging-common-ktx" w "3.3.4"
    val compose = "androidx.paging" d "paging-compose" w "1.0.0-alpha10" w "3.3.4"
    val guava = "androidx.paging" d "paging-guava" w "3.3.4"
    val runtime = "androidx.paging" d "paging-runtime" w "3.3.4"
    val runtime_ktx = "androidx.paging" d "paging-runtime-ktx" w "3.3.4"
    val rxjava2 = "androidx.paging" d "paging-rxjava2" w "3.3.4"
    val rxjava2_ktx = "androidx.paging" d "paging-rxjava2-ktx" w "3.3.4"
    val rxjava3 = "androidx.paging" d "paging-rxjava3" w "3.3.4"
    val testing = "androidx.paging" d "paging-testing" w "3.3.4"
  }
  object Palette {
    val ktx = "androidx.palette" d "palette-ktx" w "1.0.0"
    val palette = "androidx.palette" d "palette" w "1.0.0"
  }
  object PercentLayout {
    val percentlayout = "androidx.percentlayout" d "percentlayout" w "1.0.0"
  }
  object Preference {
    val ktx = "androidx.preference" d "preference-ktx" w "1.2.1"
    val preference = "androidx.preference" d "preference" w "1.2.1"
  }
  object Print {
    val print = "androidx.print" d "print" w "1.0.0" w "1.1.0-beta01"
  }
  object Recommendation {
    val recommendation = "androidx.recommendation" d "recommendation" w "1.0.0"
  }
  object RecyclerView {
    val recyclerview = "androidx.recyclerview" d "recyclerview" w "1.3.2" w "1.4.0-rc01"
    val selection = "androidx.recyclerview" d "recyclerview-selection" w "1.1.0" w "1.2.0-alpha01"
  }
  object Remotecallback {
    val processor = "androidx.remotecallback" d "remotecallback-processor" w "1.0.0-alpha02"
    val remotecallback = "androidx.remotecallback" d "remotecallback" w "1.0.0-alpha02"
  }
  object Room {
    val common = "androidx.room" d "room-common" w "2.6.1" w "2.7.0-alpha11"
    val compiler = "androidx.room" d "room-compiler" w "2.6.1" w "2.7.0-alpha11"
    val guava = "androidx.room" d "room-guava" w "2.6.1" w "2.7.0-alpha11"
    val ktx = "androidx.room" d "room-ktx" w "2.6.1" w "2.7.0-alpha11"
    val paging = "androidx.room" d "room-paging" w "2.6.1" w "2.7.0-alpha11"
    val paging_guava = "androidx.room" d "room-paging-guava" w "2.6.1" w "2.7.0-alpha11"
    val paging_rxjava2 = "androidx.room" d "room-paging-rxjava2" w "2.6.1" w "2.7.0-alpha11"
    val paging_rxjava3 = "androidx.room" d "room-paging-rxjava3" w "2.6.1" w "2.7.0-alpha11"
    val runtime = "androidx.room" d "room-runtime" w "2.6.1" w "2.7.0-alpha11"
    val rxjava2 = "androidx.room" d "room-rxjava2" w "2.6.1" w "2.7.0-alpha11"
    val rxjava3 = "androidx.room" d "room-rxjava3" w "2.6.1" w "2.7.0-alpha11"
    val testing = "androidx.room" d "room-testing" w "2.6.1" w "2.7.0-alpha11"
  }
  object Savedstate {
    val ktx = "androidx.savedstate" d "savedstate-ktx" w "1.2.1" w "1.3.0-alpha05"
    val savedstate = "androidx.savedstate" d "savedstate" w "1.2.1" w "1.3.0-alpha05"
  }
  object Security {
    val app_authenticator = "androidx.security" d "security-app-authenticator" w "1.0.0-beta01"
    val app_authenticator_testing = "androidx.security" d "security-app-authenticator-testing" w "1.0.0-beta01"
    val crypto = "androidx.security" d "security-crypto" w "1.0.0" w "1.1.0-alpha06"
    val crypto_ktx = "androidx.security" d "security-crypto-ktx" w "1.1.0-alpha06"
    val identity_credential = "androidx.security" d "security-identity-credential" w "1.0.0-alpha03"
  }
  object ShareTarget {
    val sharetarget = "androidx.sharetarget" d "sharetarget" w "1.2.0"
  }
  object Slice {
    val builders = "androidx.slice" d "slice-builders" w "1.0.0" w "1.1.0-alpha02"
    val builders_ktx = "androidx.slice" d "slice-builders-ktx" w "1.0.0-alpha08"
    val core = "androidx.slice" d "slice-core" w "1.0.0" w "1.1.0-alpha02"
    val view = "androidx.slice" d "slice-view" w "1.0.0" w "1.1.0-alpha02"
  }
  object SlidingpaneLayout {
    val slidingpanelayout = "androidx.slidingpanelayout" d "slidingpanelayout" w "1.2.0"
  }
  object Sqlite {
    val framework = "androidx.sqlite" d "sqlite-framework" w "2.4.0" w "2.5.0-alpha11"
    val ktx = "androidx.sqlite" d "sqlite-ktx" w "2.4.0" w "2.5.0-alpha11"
    val sqlite = "androidx.sqlite" d "sqlite" w "2.4.0" w "2.5.0-alpha11"
  }
  object Startup {
    val runtime = "androidx.startup" d "startup-runtime" w "1.2.0"
  }
  object SwiperefreshLayout {
    val swiperefreshlayout = "androidx.swiperefreshlayout" d "swiperefreshlayout" w "1.1.0" w "1.2.0-alpha01"
  }
  object Test {
    val core = "androidx.test" d "core" w "1.6.1"
    val core_ktx = "androidx.test" d "core-ktx" w "1.6.1"
    val monitor = "androidx.test" d "monitor" w "1.7.2"
    val orchestrator = "androidx.test" d "orchestrator" w "1.5.1" w "1.6.0-alpha01"
    val rules = "androidx.test" d "rules" w "1.6.1"
    val runner = "androidx.test" d "runner" w "1.6.2"
    object Espresso {
      val accessibility = "androidx.test.espresso" d "espresso-accessibility" w "3.6.1"
      val contrib = "androidx.test.espresso" d "espresso-contrib" w "3.6.1"
      val core = "androidx.test.espresso" d "espresso-core" w "3.6.1"
      val device = "androidx.test.espresso" d "espresso-device" w "1.0.1"
      val idling_resource = "androidx.test.espresso" d "espresso-idling-resource" w "3.6.1"
      val intents = "androidx.test.espresso" d "espresso-intents" w "3.6.1"
      val remote = "androidx.test.espresso" d "espresso-remote" w "3.6.1"
      val web = "androidx.test.espresso" d "espresso-web" w "3.6.1"
      object Idling {
        val concurrent = "androidx.test.espresso.idling" d "idling-concurrent" w "3.6.1"
        val net = "androidx.test.espresso.idling" d "idling-net" w "3.6.1"
      }
    }
    object Ext {
      val junit = "androidx.test.ext" d "junit" w "1.2.1"
      val junit_gtest = "androidx.test.ext" d "junit-gtest" w "1.0.0-alpha02"
      val junit_ktx = "androidx.test.ext" d "junit-ktx" w "1.2.1"
      val truth = "androidx.test.ext" d "truth" w "1.6.0"
    }
    object Services {
      val test_services = "androidx.test.services" d "test-services" w "1.5.0" w "1.6.0-alpha01"
    }
    object UiAutoMator {
      val uiautomator = "androidx.test.uiautomator" d "uiautomator" w "2.3.0" w "2.4.0-alpha01"
    }
  }
  object TextClassifier {
    val textclassifier = "androidx.textclassifier" d "textclassifier" w "1.0.0-alpha04"
  }
  object Tracing {
    val ktx = "androidx.tracing" d "tracing-ktx" w "1.1.0-alpha01" w "1.2.0" w "1.3.0-alpha02"
    val perfetto = "androidx.tracing" d "tracing-perfetto" w "1.0.0"
    val tracing = "androidx.tracing" d "tracing" w "1.1.0-alpha01" w "1.2.0" w "1.3.0-alpha02"
  }
  object Transition {
    val ktx = "androidx.transition" d "transition-ktx" w "1.5.1"
    val transition = "androidx.transition" d "transition" w "1.5.1"
  }
  object Tv {
    val foundation = "androidx.tv" d "tv-foundation" w "1.0.0-alpha11"
    val material = "androidx.tv" d "tv-material" w "1.0.0"
  }
  object TvProvider {
    val tvprovider = "androidx.tvprovider" d "tvprovider" w "1.0.0" w "1.1.0-alpha01"
  }
  object Vectordrawable {
    val animated = "androidx.vectordrawable" d "vectordrawable-animated" w "1.2.0"
    val seekable = "androidx.vectordrawable" d "vectordrawable-seekable" w "1.0.0"
    val vectordrawable = "androidx.vectordrawable" d "vectordrawable" w "1.2.0"
  }
  object Versionedparcelable {
    val versionedparcelable = "androidx.versionedparcelable" d "versionedparcelable" w "1.2.0"
  }
  object ViewPager {
    val viewpager = "androidx.viewpager" d "viewpager" w "1.0.0" w "1.1.0-rc01"
  }
  object ViewPager2 {
    val viewpager2 = "androidx.viewpager2" d "viewpager2" w "1.1.0"
  }
  object Wear {
    val input = "androidx.wear" d "wear-input" w "1.1.0" w "1.2.0-alpha02"
    val input_testing = "androidx.wear" d "wear-input-testing" w "1.1.0" w "1.2.0-alpha02"
    val ongoing = "androidx.wear" d "wear-ongoing" w "1.0.0" w "1.1.0-alpha01"
    val phone_interactions = "androidx.wear" d "wear-phone-interactions" w "1.0.1" w "1.1.0-alpha04"
    val remote_interactions = "androidx.wear" d "wear-remote-interactions" w "1.0.0" w "1.1.0-rc01"
    val wear = "androidx.wear" d "wear" w "1.3.0" w "1.4.0-alpha01"
    object Compose {
      val foundation = "androidx.wear.compose" d "compose-foundation" w "1.0.0-alpha01" w "1.4.0" w "1.5.0-alpha06"
      val material = "androidx.wear.compose" d "compose-material" w "1.0.0-alpha01" w "1.4.0" w "1.5.0-alpha06"
      val navigation = "androidx.wear.compose" d "compose-navigation" w "1.4.0" w "1.5.0-alpha06"
    }
    object Tiles {
      val material = "androidx.wear.tiles" d "tiles-material" w "1.4.1" w "1.5.0-alpha04"
      val renderer = "androidx.wear.tiles" d "tiles-renderer" w "1.4.1" w "1.5.0-alpha04"
      val testing = "androidx.wear.tiles" d "tiles-testing" w "1.4.1" w "1.5.0-alpha04"
      val tiles = "androidx.wear.tiles" d "tiles" w "1.4.1" w "1.5.0-alpha04"
    }
    object Watchface {
      val complications_data_source = "androidx.wear.watchface" d "watchface-complications-data-source" w "1.2.1" w "1.3.0-alpha04"
      val complications_data_source_ktx = "androidx.wear.watchface" d "watchface-complications-data-source-ktx" w "1.2.1" w "1.3.0-alpha04"
      val complications_rendering = "androidx.wear.watchface" d "watchface-complications-rendering" w "1.2.1" w "1.3.0-alpha04"
      val editor = "androidx.wear.watchface" d "watchface-editor" w "1.2.1" w "1.3.0-alpha04"
      val watchface = "androidx.wear.watchface" d "watchface" w "1.2.1" w "1.3.0-alpha04"
    }
  }
  object WebKit {
    val webkit = "androidx.webkit" d "webkit" w "1.12.1" w "1.13.0-alpha01"
  }
  object Window {
    val java = "androidx.window" d "window-java" w "1.3.0" w "1.4.0-alpha05"
    val rxjava2 = "androidx.window" d "window-rxjava2" w "1.3.0" w "1.4.0-alpha05"
    val rxjava3 = "androidx.window" d "window-rxjava3" w "1.3.0" w "1.4.0-alpha05"
    val testing = "androidx.window" d "window-testing" w "1.3.0" w "1.4.0-alpha05"
    val window = "androidx.window" d "window" w "1.3.0" w "1.4.0-alpha05"
  }
  object Work {
    val gcm = "androidx.work" d "work-gcm" w "2.10.0"
    val multiprocess = "androidx.work" d "work-multiprocess" w "2.10.0"
    val runtime = "androidx.work" d "work-runtime" w "2.10.0"
    val runtime_ktx = "androidx.work" d "work-runtime-ktx" w "2.10.0"
    val rxjava2 = "androidx.work" d "work-rxjava2" w "2.10.0"
    val rxjava3 = "androidx.work" d "work-rxjava3" w "2.10.0"
    val testing = "androidx.work" d "work-testing" w "2.10.0"
  }
}
object App {
  object Cash {
    object Copper {
      val flow = "app.cash.copper" d "copper-flow" w "1.0.0"
      val rx2 = "app.cash.copper" d "copper-rx2" w "1.0.0"
      val rx3 = "app.cash.copper" d "copper-rx3" w "1.0.0"
    }
    object Licensee {
      val gradle_plugin = "app.cash.licensee" d "licensee-gradle-plugin" w "1.12.0"
    }
    object Molecule {
      val gradle_plugin = "app.cash.molecule" d "molecule-gradle-plugin" w "1.4.3"
      val runtime = "app.cash.molecule" d "molecule-runtime" w "2.0.0"
    }
    object Turbine {
      val turbine = "app.cash.turbine" d "turbine" w "1.2.0"
    }
  }
}
object Co {
  object TouchLab {
    val kermit = "co.touchlab" d "kermit" w "2.0.5"
    val kermit_bugsnag = "co.touchlab" d "kermit-bugsnag" w "2.0.5"
    val kermit_bugsnag_test = "co.touchlab" d "kermit-bugsnag-test" w "1.1.3" w "1.2.0-M2"
    val kermit_crashlytics = "co.touchlab" d "kermit-crashlytics" w "2.0.5"
    val kermit_crashlytics_test = "co.touchlab" d "kermit-crashlytics-test" w "1.1.3" w "1.2.0-M2"
    val kermit_gradle_plugin = "co.touchlab" d "kermit-gradle-plugin" w "1.2.3"
    val kermit_test = "co.touchlab" d "kermit-test" w "2.0.5"
    val stately_common = "co.touchlab" d "stately-common" w "2.1.0"
    val stately_concurrency = "co.touchlab" d "stately-concurrency" w "2.1.0"
    val stately_iso_collections = "co.touchlab" d "stately-iso-collections" w "2.1.0"
    val stately_isolate = "co.touchlab" d "stately-isolate" w "2.1.0"
  }
}
object Com {
  object Android {
    object Billingclient {
      val billing = "com.android.billingclient" d "billing" w "7.1.1"
      val billing_ktx = "com.android.billingclient" d "billing-ktx" w "7.1.1"
    }
    object Installreferrer {
      val installreferrer = "com.android.installreferrer" d "installreferrer" w "2.2"
    }
    object Tools {
      val desugar_jdk_libs = "com.android.tools" d "desugar_jdk_libs" w "2.1.3"
      val r8 = "com.android.tools" d "r8" w "8.5.35"
      object Build {
        val gradle = "com.android.tools.build" d "gradle" w "2.3.0" w "8.7.2" w "8.8.0-beta01" w "8.9.0-alpha03"
      }
    }
  }
  object ApolloGraphQl {
    object Apollo3 {
      val apollo_adapters = "com.apollographql.apollo3" d "apollo-adapters" w "3.8.5" w "4.0.0-beta.7"
      val apollo_api = "com.apollographql.apollo3" d "apollo-api" w "3.8.5" w "4.0.0-beta.7"
      val apollo_ast = "com.apollographql.apollo3" d "apollo-ast" w "3.8.5" w "4.0.0-beta.7"
      val apollo_http_cache = "com.apollographql.apollo3" d "apollo-http-cache" w "3.8.5" w "4.0.0-beta.7"
      val apollo_idling_resource = "com.apollographql.apollo3" d "apollo-idling-resource" w "3.8.5" w "4.0.0-beta.7"
      val apollo_mockserver = "com.apollographql.apollo3" d "apollo-mockserver" w "3.8.5" w "4.0.0-beta.7"
      val apollo_normalized_cache = "com.apollographql.apollo3" d "apollo-normalized-cache" w "3.8.5" w "4.0.0-beta.7"
      val apollo_normalized_cache_sqlite = "com.apollographql.apollo3" d "apollo-normalized-cache-sqlite" w "3.8.5" w "4.0.0-beta.7"
      val apollo_runtime = "com.apollographql.apollo3" d "apollo-runtime" w "3.8.5" w "4.0.0-beta.7"
      val apollo_testing_support = "com.apollographql.apollo3" d "apollo-testing-support" w "3.8.5" w "4.0.0-beta.7"
    }
  }
  object GitHub {
    object Ajalt {
      object Clikt {
        val clikt = "com.github.ajalt.clikt" d "clikt" w "5.0.1"
        val core = "com.github.ajalt.clikt" d "clikt-core" w "5.0.1"
        val markdown = "com.github.ajalt.clikt" d "clikt-markdown" w "5.0.1"
      }
      object Mordant {
        val coroutines = "com.github.ajalt.mordant" d "mordant-coroutines" w "3.0.1"
        val graal_ffi = "com.github.ajalt.mordant" d "mordant-graal-ffi"
        val jvm_ffm = "com.github.ajalt.mordant" d "mordant-jvm-ffm" w "3.0.1"
        val jvm_jna = "com.github.ajalt.mordant" d "mordant-jvm-jna" w "3.0.1"
        val markdown = "com.github.ajalt.mordant" d "mordant-markdown" w "3.0.1"
        val mordant = "com.github.ajalt.mordant" d "mordant" w "3.0.1"
        val omnibus = "com.github.ajalt.mordant" d "mordant-omnibus"
      }
    }
    object ChuckerTeam {
      object Chucker {
        val library = "com.github.chuckerteam.chucker" d "library" w "4.0.0"
        val library_no_op = "com.github.chuckerteam.chucker" d "library-no-op" w "4.0.0"
      }
    }
  }
  object Google {
    object Accompanist {
      val appcompat_theme = "com.google.accompanist" d "accompanist-appcompat-theme" w "0.36.0"
      val coil = "com.google.accompanist" d "accompanist-coil" w "0.15.0"
      val drawablepainter = "com.google.accompanist" d "accompanist-drawablepainter" w "0.36.0"
      val flowlayout = "com.google.accompanist" d "accompanist-flowlayout" w "0.36.0"
      val glide = "com.google.accompanist" d "accompanist-glide" w "0.15.0"
      val imageloading_core = "com.google.accompanist" d "accompanist-imageloading-core" w "0.15.0"
      val insets = "com.google.accompanist" d "accompanist-insets" w "0.30.1" w "0.31.5-beta"
      val insets_ui = "com.google.accompanist" d "accompanist-insets-ui" w "0.36.0"
      val navigation_animation = "com.google.accompanist" d "accompanist-navigation-animation" w "0.36.0"
      val navigation_material = "com.google.accompanist" d "accompanist-navigation-material" w "0.36.0"
      val pager = "com.google.accompanist" d "accompanist-pager" w "0.36.0"
      val pager_indicators = "com.google.accompanist" d "accompanist-pager-indicators" w "0.36.0"
      val permissions = "com.google.accompanist" d "accompanist-permissions" w "0.36.0"
      val picasso = "com.google.accompanist" d "accompanist-picasso" w "0.6.2"
      val placeholder = "com.google.accompanist" d "accompanist-placeholder" w "0.36.0"
      val placeholder_material = "com.google.accompanist" d "accompanist-placeholder-material" w "0.36.0"
      val swiperefresh = "com.google.accompanist" d "accompanist-swiperefresh" w "0.36.0"
      val systemuicontroller = "com.google.accompanist" d "accompanist-systemuicontroller" w "0.36.0"
      val webview = "com.google.accompanist" d "accompanist-webview" w "0.36.0"
    }
    object Ambient {
      object Crossdevice {
        val crossdevice = "com.google.ambient.crossdevice" d "crossdevice" w "0.1.0-preview01"
      }
    }
    object Android {
      object Fhir {
        val data_capture = "com.google.android.fhir" d "data-capture" w "1.2.0"
        val engine = "com.google.android.fhir" d "engine" w "1.0.0"
        val workflow = "com.google.android.fhir" d "workflow" w "0.1.0-beta01"
      }
      object Flexbox {
        val flexbox = "com.google.android.flexbox" d "flexbox" w "3.0.0"
      }
      object Gms {
        val oss_licenses_plugin = "com.google.android.gms" d "oss-licenses-plugin" w "0.10.6"
        val play_services_analytics = "com.google.android.gms" d "play-services-analytics" w "18.1.1"
        val play_services_appset = "com.google.android.gms" d "play-services-appset" w "16.1.0"
        val play_services_auth = "com.google.android.gms" d "play-services-auth" w "21.2.0"
        val play_services_auth_api_phone = "com.google.android.gms" d "play-services-auth-api-phone" w "18.1.0"
        val play_services_auth_blockstore = "com.google.android.gms" d "play-services-auth-blockstore" w "16.4.0"
        val play_services_awareness = "com.google.android.gms" d "play-services-awareness" w "19.1.0"
        val play_services_base = "com.google.android.gms" d "play-services-base" w "18.5.0"
        val play_services_basement = "com.google.android.gms" d "play-services-basement" w "18.5.0"
        val play_services_cast = "com.google.android.gms" d "play-services-cast" w "22.0.0"
        val play_services_cast_framework = "com.google.android.gms" d "play-services-cast-framework" w "22.0.0"
        val play_services_cast_tv = "com.google.android.gms" d "play-services-cast-tv" w "21.1.1"
        val play_services_cronet = "com.google.android.gms" d "play-services-cronet" w "18.1.0"
        val play_services_drive = "com.google.android.gms" d "play-services-drive" w "17.0.0"
        val play_services_fido = "com.google.android.gms" d "play-services-fido" w "21.1.0"
        val play_services_fitness = "com.google.android.gms" d "play-services-fitness" w "21.2.0"
        val play_services_games = "com.google.android.gms" d "play-services-games" w "23.2.0"
        val play_services_gcm = "com.google.android.gms" d "play-services-gcm" w "17.0.0"
        val play_services_identity = "com.google.android.gms" d "play-services-identity" w "18.1.0"
        val play_services_instantapps = "com.google.android.gms" d "play-services-instantapps" w "18.1.0"
        val play_services_location = "com.google.android.gms" d "play-services-location" w "21.3.0"
        val play_services_maps = "com.google.android.gms" d "play-services-maps" w "19.0.0"
        val play_services_mlkit_barcode_scanning = "com.google.android.gms" d "play-services-mlkit-barcode-scanning" w "18.3.1"
        val play_services_mlkit_face_detection = "com.google.android.gms" d "play-services-mlkit-face-detection" w "17.1.0"
        val play_services_mlkit_image_labeling = "com.google.android.gms" d "play-services-mlkit-image-labeling" w "16.0.8"
        val play_services_mlkit_image_labeling_custom = "com.google.android.gms" d "play-services-mlkit-image-labeling-custom" w "16.0.0-beta5"
        val play_services_mlkit_language_id = "com.google.android.gms" d "play-services-mlkit-language-id" w "17.0.0"
        val play_services_mlkit_text_recognition = "com.google.android.gms" d "play-services-mlkit-text-recognition" w "19.0.1"
        val play_services_nearby = "com.google.android.gms" d "play-services-nearby" w "19.3.0"
        val play_services_oss_licenses = "com.google.android.gms" d "play-services-oss-licenses" w "17.1.0"
        val play_services_panorama = "com.google.android.gms" d "play-services-panorama" w "17.1.0"
        val play_services_password_complexity = "com.google.android.gms" d "play-services-password-complexity" w "18.1.0"
        val play_services_pay = "com.google.android.gms" d "play-services-pay" w "16.5.0"
        val play_services_recaptcha = "com.google.android.gms" d "play-services-recaptcha" w "17.1.0"
        val play_services_safetynet = "com.google.android.gms" d "play-services-safetynet" w "18.1.0"
        val play_services_tagmanager = "com.google.android.gms" d "play-services-tagmanager" w "18.1.1"
        val play_services_tasks = "com.google.android.gms" d "play-services-tasks" w "18.2.0"
        val play_services_vision = "com.google.android.gms" d "play-services-vision" w "20.1.3"
        val play_services_wallet = "com.google.android.gms" d "play-services-wallet" w "19.4.0"
        val play_services_wearable = "com.google.android.gms" d "play-services-wearable" w "19.0.0"
        val strict_version_matcher_plugin = "com.google.android.gms" d "strict-version-matcher-plugin" w "1.2.4"
      }
      object Horologist {
        val audio = "com.google.android.horologist" d "horologist-audio" w "0.6.20" w "0.7.5-alpha"
        val audio_ui = "com.google.android.horologist" d "horologist-audio-ui" w "0.6.20" w "0.7.5-alpha"
        val composables = "com.google.android.horologist" d "horologist-composables" w "0.6.20" w "0.7.5-alpha"
        val compose_layout = "com.google.android.horologist" d "horologist-compose-layout" w "0.6.20" w "0.7.5-alpha"
        val compose_tools = "com.google.android.horologist" d "horologist-compose-tools" w "0.6.20" w "0.7.5-alpha"
        val datalayer = "com.google.android.horologist" d "horologist-datalayer" w "0.6.20" w "0.7.5-alpha"
        val media = "com.google.android.horologist" d "horologist-media" w "0.6.20" w "0.7.5-alpha"
        val media3_backend = "com.google.android.horologist" d "horologist-media3-backend" w "0.6.20" w "0.7.5-alpha"
        val media_data = "com.google.android.horologist" d "horologist-media-data" w "0.6.20" w "0.7.5-alpha"
        val media_ui = "com.google.android.horologist" d "horologist-media-ui" w "0.6.20" w "0.7.5-alpha"
        val network_awareness = "com.google.android.horologist" d "horologist-network-awareness" w "0.6.20" w "0.7.5-alpha"
        val tiles = "com.google.android.horologist" d "horologist-tiles" w "0.6.20" w "0.7.5-alpha"
      }
      object Libraries {
        object Places {
          val places = "com.google.android.libraries.places" d "places" w "4.1.0"
        }
      }
      object Material {
        val compose_theme_adapter = "com.google.android.material" d "compose-theme-adapter" w "1.2.1"
        val compose_theme_adapter_3 = "com.google.android.material" d "compose-theme-adapter-3" w "1.1.1"
        val material = "com.google.android.material" d "material" w "1.12.0" w "1.13.0-alpha08"
      }
      object Play {
        val core = "com.google.android.play" d "core" w "1.10.3"
        val core_ktx = "com.google.android.play" d "core-ktx" w "1.8.1"
      }
      object Support {
        val wearable = "com.google.android.support" d "wearable" w "2.9.0"
      }
      object Wearable {
        val wearable = "com.google.android.wearable" d "wearable" w "2.9.0"
      }
    }
    object AndroidBrowserHelper {
      val androidbrowserhelper = "com.google.androidbrowserhelper" d "androidbrowserhelper" w "2.5.0"
    }
    object Ar {
      val core = "com.google.ar" d "core" w "1.46.0"
      object Sceneform {
        val animation = "com.google.ar.sceneform" d "animation" w "1.17.1"
        val assets = "com.google.ar.sceneform" d "assets" w "1.17.1"
        val base = "com.google.ar.sceneform" d "sceneform-base" w "1.17.1"
        val core = "com.google.ar.sceneform" d "core" w "1.17.1"
        val filament_android = "com.google.ar.sceneform" d "filament-android" w "1.17.1"
        val plugin = "com.google.ar.sceneform" d "plugin" w "1.17.1"
        val rendering = "com.google.ar.sceneform" d "rendering" w "1.17.1"
        object Ux {
          val sceneform_ux = "com.google.ar.sceneform.ux" d "sceneform-ux" w "1.17.1"
        }
      }
    }
    object Dagger {
      val android = "com.google.dagger" d "dagger-android" w "2.52"
      val android_processor = "com.google.dagger" d "dagger-android-processor" w "2.52"
      val android_support = "com.google.dagger" d "dagger-android-support" w "2.52"
      val compiler = "com.google.dagger" d "dagger-compiler" w "2.52"
      val dagger = "com.google.dagger" d "dagger" w "2.52"
      val grpc_server = "com.google.dagger" d "dagger-grpc-server" w "2.52"
      val grpc_server_annotations = "com.google.dagger" d "dagger-grpc-server-annotations" w "2.52"
      val grpc_server_processor = "com.google.dagger" d "dagger-grpc-server-processor" w "2.52"
      val gwt = "com.google.dagger" d "dagger-gwt" w "2.52"
      val hilt_android = "com.google.dagger" d "hilt-android" w "2.52"
      val hilt_android_compiler = "com.google.dagger" d "hilt-android-compiler" w "2.52"
      val hilt_android_gradle_plugin = "com.google.dagger" d "hilt-android-gradle-plugin" w "2.52"
      val hilt_android_testing = "com.google.dagger" d "hilt-android-testing" w "2.52"
      val hilt_compiler = "com.google.dagger" d "hilt-compiler" w "2.52"
      val producers = "com.google.dagger" d "dagger-producers" w "2.52"
      val spi = "com.google.dagger" d "dagger-spi" w "2.52"
    }
    object Firebase {
      val analytics = "com.google.firebase" d "firebase-analytics" w "22.1.2"
      val analytics_ktx = "com.google.firebase" d "firebase-analytics-ktx" w "22.1.2"
      val appdistribution_gradle = "com.google.firebase" d "firebase-appdistribution-gradle" w "5.0.0"
      val appindexing = "com.google.firebase" d "firebase-appindexing" w "20.0.0"
      val auth = "com.google.firebase" d "firebase-auth" w "23.1.0"
      val auth_ktx = "com.google.firebase" d "firebase-auth-ktx" w "23.1.0"
      val bom = "com.google.firebase" d "firebase-bom" w "33.6.0"
      val config = "com.google.firebase" d "firebase-config" w "22.0.1"
      val config_ktx = "com.google.firebase" d "firebase-config-ktx" w "22.0.1"
      val crashlytics = "com.google.firebase" d "firebase-crashlytics" w "19.2.1"
      val crashlytics_gradle = "com.google.firebase" d "firebase-crashlytics-gradle" w "3.0.2"
      val crashlytics_ktx = "com.google.firebase" d "firebase-crashlytics-ktx" w "19.2.1"
      val crashlytics_ndk = "com.google.firebase" d "firebase-crashlytics-ndk" w "19.2.1"
      val database = "com.google.firebase" d "firebase-database" w "21.0.0"
      val database_ktx = "com.google.firebase" d "firebase-database-ktx" w "21.0.0"
      val dynamic_links = "com.google.firebase" d "firebase-dynamic-links" w "22.1.0"
      val dynamic_links_ktx = "com.google.firebase" d "firebase-dynamic-links-ktx" w "22.1.0"
      val dynamic_module_support = "com.google.firebase" d "firebase-dynamic-module-support" w "16.0.0-beta03"
      val firestore = "com.google.firebase" d "firebase-firestore" w "25.1.1"
      val firestore_ktx = "com.google.firebase" d "firebase-firestore-ktx" w "25.1.1"
      val functions = "com.google.firebase" d "firebase-functions" w "21.1.0"
      val functions_ktx = "com.google.firebase" d "firebase-functions-ktx" w "21.1.0"
      val inappmessaging = "com.google.firebase" d "firebase-inappmessaging" w "21.0.1"
      val inappmessaging_display = "com.google.firebase" d "firebase-inappmessaging-display" w "21.0.1"
      val inappmessaging_display_ktx = "com.google.firebase" d "firebase-inappmessaging-display-ktx" w "21.0.1"
      val inappmessaging_ktx = "com.google.firebase" d "firebase-inappmessaging-ktx" w "21.0.1"
      val messaging = "com.google.firebase" d "firebase-messaging" w "24.1.0"
      val messaging_directboot = "com.google.firebase" d "firebase-messaging-directboot" w "24.1.0"
      val messaging_ktx = "com.google.firebase" d "firebase-messaging-ktx" w "24.1.0"
      val ml_modeldownloader = "com.google.firebase" d "firebase-ml-modeldownloader" w "25.0.1"
      val ml_modeldownloader_ktx = "com.google.firebase" d "firebase-ml-modeldownloader-ktx" w "25.0.1"
      val perf = "com.google.firebase" d "firebase-perf" w "21.0.2"
      val perf_ktx = "com.google.firebase" d "firebase-perf-ktx" w "21.0.2"
      val perf_plugin = "com.google.firebase" d "perf-plugin" w "1.4.2"
      val storage = "com.google.firebase" d "firebase-storage" w "21.0.1"
      val storage_ktx = "com.google.firebase" d "firebase-storage-ktx" w "21.0.1"
    }
    object Gms {
      val google_services = "com.google.gms" d "google-services" w "3.1.1" w "4.4.2"
    }
    object Maps {
      object Android {
        val maps_compose = "com.google.maps.android" d "maps-compose" w "6.2.1"
        val maps_ktx = "com.google.maps.android" d "maps-ktx" w "5.1.1"
        val maps_rx = "com.google.maps.android" d "maps-rx" w "1.0.0"
        val maps_utils = "com.google.maps.android" d "android-maps-utils" w "3.9.0"
        val maps_utils_ktx = "com.google.maps.android" d "maps-utils-ktx" w "5.1.1"
      }
    }
    object MlKit {
      val barcode_scanning = "com.google.mlkit" d "barcode-scanning" w "17.3.0"
      val digital_ink_recognition = "com.google.mlkit" d "digital-ink-recognition" w "18.1.0"
      val entity_extraction = "com.google.mlkit" d "entity-extraction" w "16.0.0-beta5"
      val face_detection = "com.google.mlkit" d "face-detection" w "16.1.7"
      val image_labeling = "com.google.mlkit" d "image-labeling" w "17.0.9"
      val image_labeling_custom = "com.google.mlkit" d "image-labeling-custom" w "17.0.3"
      val language_id = "com.google.mlkit" d "language-id" w "17.0.6"
      val linkfirebase = "com.google.mlkit" d "linkfirebase" w "17.0.0"
      val object_detection = "com.google.mlkit" d "object-detection" w "17.0.2"
      val object_detection_custom = "com.google.mlkit" d "object-detection-custom" w "17.0.2"
      val playstore_dynamic_feature_support = "com.google.mlkit" d "playstore-dynamic-feature-support" w "16.0.0-beta2"
      val pose_detection = "com.google.mlkit" d "pose-detection" w "17.0.0" w "18.0.0-beta5"
      val pose_detection_accurate = "com.google.mlkit" d "pose-detection-accurate" w "17.0.0" w "18.0.0-beta5"
      val segmentation_selfie = "com.google.mlkit" d "segmentation-selfie" w "16.0.0-beta6"
      val smart_reply = "com.google.mlkit" d "smart-reply" w "17.0.4"
      val text_recognition = "com.google.mlkit" d "text-recognition" w "16.0.1"
      val text_recognition_chinese = "com.google.mlkit" d "text-recognition-chinese" w "16.0.1"
      val text_recognition_devanagari = "com.google.mlkit" d "text-recognition-devanagari" w "16.0.1"
      val text_recognition_japanese = "com.google.mlkit" d "text-recognition-japanese" w "16.0.1"
      val text_recognition_korean = "com.google.mlkit" d "text-recognition-korean" w "16.0.1"
      val translate = "com.google.mlkit" d "translate" w "17.0.3"
    }
    object Modernstorage {
      val bom = "com.google.modernstorage" d "modernstorage-bom" w "1.0.0-alpha06"
      val permissions = "com.google.modernstorage" d "modernstorage-permissions" w "1.0.0-alpha06"
      val photopicker = "com.google.modernstorage" d "modernstorage-photopicker" w "1.0.0-alpha06"
      val storage = "com.google.modernstorage" d "modernstorage-storage" w "1.0.0-alpha06"
    }
    object Oboe {
      val oboe = "com.google.oboe" d "oboe" w "1.9.0"
    }
    object Truth {
      val parent = "com.google.truth" d "truth-parent" w "1.4.4"
      val truth = "com.google.truth" d "truth" w "1.4.4"
    }
  }
  object JakeWharton {
    object Confundus {
      val gradle = "com.jakewharton.confundus" d "confundus-gradle" w "1.0.0"
    }
    object Moshi {
      val shimo = "com.jakewharton.moshi" d "shimo" w "0.1.1"
    }
    object Picnic {
      val picnic = "com.jakewharton.picnic" d "picnic" w "0.7.0"
    }
    object Retrofit {
      val retrofit2_kotlinx_serialization_converter = "com.jakewharton.retrofit" d "retrofit2-kotlinx-serialization-converter" w "1.0.0"
    }
    object RxBinding3 {
      val rxbinding = "com.jakewharton.rxbinding3" d "rxbinding" w "3.1.0"
      val rxbinding_appcompat = "com.jakewharton.rxbinding3" d "rxbinding-appcompat" w "3.1.0"
      val rxbinding_core = "com.jakewharton.rxbinding3" d "rxbinding-core" w "3.1.0"
      val rxbinding_drawerlayout = "com.jakewharton.rxbinding3" d "rxbinding-drawerlayout" w "3.1.0"
      val rxbinding_leanback = "com.jakewharton.rxbinding3" d "rxbinding-leanback" w "3.1.0"
      val rxbinding_material = "com.jakewharton.rxbinding3" d "rxbinding-material" w "3.1.0"
      val rxbinding_recyclerview = "com.jakewharton.rxbinding3" d "rxbinding-recyclerview" w "3.1.0"
      val rxbinding_slidingpanelayout = "com.jakewharton.rxbinding3" d "rxbinding-slidingpanelayout" w "3.1.0"
      val rxbinding_swiperefreshlayout = "com.jakewharton.rxbinding3" d "rxbinding-swiperefreshlayout" w "3.1.0"
      val rxbinding_viewpager = "com.jakewharton.rxbinding3" d "rxbinding-viewpager" w "3.1.0"
      val rxbinding_viewpager2 = "com.jakewharton.rxbinding3" d "rxbinding-viewpager2" w "3.1.0"
    }
    object RxBinding4 {
      val rxbinding = "com.jakewharton.rxbinding4" d "rxbinding" w "4.0.0"
      val rxbinding_appcompat = "com.jakewharton.rxbinding4" d "rxbinding-appcompat" w "4.0.0"
      val rxbinding_core = "com.jakewharton.rxbinding4" d "rxbinding-core" w "4.0.0"
      val rxbinding_drawerlayout = "com.jakewharton.rxbinding4" d "rxbinding-drawerlayout" w "4.0.0"
      val rxbinding_leanback = "com.jakewharton.rxbinding4" d "rxbinding-leanback" w "4.0.0"
      val rxbinding_material = "com.jakewharton.rxbinding4" d "rxbinding-material" w "4.0.0"
      val rxbinding_recyclerview = "com.jakewharton.rxbinding4" d "rxbinding-recyclerview" w "4.0.0"
      val rxbinding_slidingpanelayout = "com.jakewharton.rxbinding4" d "rxbinding-slidingpanelayout" w "4.0.0"
      val rxbinding_swiperefreshlayout = "com.jakewharton.rxbinding4" d "rxbinding-swiperefreshlayout" w "4.0.0"
      val rxbinding_viewpager = "com.jakewharton.rxbinding4" d "rxbinding-viewpager" w "4.0.0"
      val rxbinding_viewpager2 = "com.jakewharton.rxbinding4" d "rxbinding-viewpager2" w "4.0.0"
    }
    object RxRelay2 {
      val rxrelay = "com.jakewharton.rxrelay2" d "rxrelay" w "2.1.1"
    }
    object RxRelay3 {
      val rxrelay = "com.jakewharton.rxrelay3" d "rxrelay" w "3.0.1"
    }
    object Timber {
      val timber = "com.jakewharton.timber" d "timber" w "5.0.1"
    }
    object Wormhole {
      val gradle = "com.jakewharton.wormhole" d "wormhole-gradle" w "0.3.1"
    }
  }
  object Louiscad {
    object Splitties {
      val activities = "com.louiscad.splitties" d "splitties-activities" w "3.0.0"
      val alertdialog = "com.louiscad.splitties" d "splitties-alertdialog" w "3.0.0"
      val alertdialog_appcompat = "com.louiscad.splitties" d "splitties-alertdialog-appcompat" w "3.0.0"
      val alertdialog_appcompat_coroutines = "com.louiscad.splitties" d "splitties-alertdialog-appcompat-coroutines" w "3.0.0"
      val alertdialog_material = "com.louiscad.splitties" d "splitties-alertdialog-material" w "3.0.0"
      val appctx = "com.louiscad.splitties" d "splitties-appctx" w "3.0.0"
      val arch_lifecycle = "com.louiscad.splitties" d "splitties-arch-lifecycle" w "3.0.0"
      val arch_room = "com.louiscad.splitties" d "splitties-arch-room" w "3.0.0"
      val bitflags = "com.louiscad.splitties" d "splitties-bitflags" w "3.0.0"
      val bundle = "com.louiscad.splitties" d "splitties-bundle" w "3.0.0"
      val checkedlazy = "com.louiscad.splitties" d "splitties-checkedlazy" w "3.0.0"
      val collections = "com.louiscad.splitties" d "splitties-collections" w "3.0.0"
      val coroutines = "com.louiscad.splitties" d "splitties-coroutines" w "3.0.0"
      val dimensions = "com.louiscad.splitties" d "splitties-dimensions" w "3.0.0"
      val exceptions = "com.louiscad.splitties" d "splitties-exceptions" w "3.0.0"
      val fragmentargs = "com.louiscad.splitties" d "splitties-fragmentargs" w "3.0.0"
      val fragments = "com.louiscad.splitties" d "splitties-fragments" w "3.0.0"
      val fun_pack_android_appcompat = "com.louiscad.splitties" d "splitties-fun-pack-android-appcompat" w "3.0.0"
      val fun_pack_android_appcompat_with_views_dsl = "com.louiscad.splitties" d "splitties-fun-pack-android-appcompat-with-views-dsl" w "3.0.0"
      val fun_pack_android_base = "com.louiscad.splitties" d "splitties-fun-pack-android-base" w "3.0.0"
      val fun_pack_android_base_with_views_dsl = "com.louiscad.splitties" d "splitties-fun-pack-android-base-with-views-dsl" w "3.0.0"
      val fun_pack_android_material_components = "com.louiscad.splitties" d "splitties-fun-pack-android-material-components" w "3.0.0"
      val fun_pack_android_material_components_with_views_dsl = "com.louiscad.splitties" d "splitties-fun-pack-android-material-components-with-views-dsl" w "3.0.0"
      val initprovider = "com.louiscad.splitties" d "splitties-initprovider" w "3.0.0-beta06"
      val intents = "com.louiscad.splitties" d "splitties-intents" w "3.0.0"
      val lifecycle_coroutines = "com.louiscad.splitties" d "splitties-lifecycle-coroutines" w "3.0.0"
      val mainhandler = "com.louiscad.splitties" d "splitties-mainhandler" w "3.0.0"
      val mainthread = "com.louiscad.splitties" d "splitties-mainthread" w "3.0.0"
      val material_colors = "com.louiscad.splitties" d "splitties-material-colors" w "3.0.0"
      val material_lists = "com.louiscad.splitties" d "splitties-material-lists" w "3.0.0"
      val permissions = "com.louiscad.splitties" d "splitties-permissions" w "3.0.0"
      val preferences = "com.louiscad.splitties" d "splitties-preferences" w "3.0.0"
      val resources = "com.louiscad.splitties" d "splitties-resources" w "3.0.0"
      val snackbar = "com.louiscad.splitties" d "splitties-snackbar" w "3.0.0"
      val stetho_init = "com.louiscad.splitties" d "splitties-stetho-init" w "3.0.0"
      val systemservices = "com.louiscad.splitties" d "splitties-systemservices" w "3.0.0"
      val toast = "com.louiscad.splitties" d "splitties-toast" w "3.0.0"
      val typesaferecyclerview = "com.louiscad.splitties" d "splitties-typesaferecyclerview" w "3.0.0"
      val views = "com.louiscad.splitties" d "splitties-views" w "3.0.0"
      val views_appcompat = "com.louiscad.splitties" d "splitties-views-appcompat" w "3.0.0"
      val views_cardview = "com.louiscad.splitties" d "splitties-views-cardview" w "3.0.0"
      val views_coroutines = "com.louiscad.splitties" d "splitties-views-coroutines" w "3.0.0"
      val views_coroutines_material = "com.louiscad.splitties" d "splitties-views-coroutines-material" w "3.0.0"
      val views_dsl = "com.louiscad.splitties" d "splitties-views-dsl" w "3.0.0"
      val views_dsl_appcompat = "com.louiscad.splitties" d "splitties-views-dsl-appcompat" w "3.0.0"
      val views_dsl_constraintlayout = "com.louiscad.splitties" d "splitties-views-dsl-constraintlayout" w "3.0.0"
      val views_dsl_coordinatorlayout = "com.louiscad.splitties" d "splitties-views-dsl-coordinatorlayout" w "3.0.0"
      val views_dsl_material = "com.louiscad.splitties" d "splitties-views-dsl-material" w "3.0.0"
      val views_dsl_recyclerview = "com.louiscad.splitties" d "splitties-views-dsl-recyclerview" w "3.0.0"
      val views_material = "com.louiscad.splitties" d "splitties-views-material" w "3.0.0"
      val views_recyclerview = "com.louiscad.splitties" d "splitties-views-recyclerview" w "3.0.0"
      val views_selectable = "com.louiscad.splitties" d "splitties-views-selectable" w "3.0.0"
      val views_selectable_appcompat = "com.louiscad.splitties" d "splitties-views-selectable-appcompat" w "3.0.0"
      val views_selectable_constraintlayout = "com.louiscad.splitties" d "splitties-views-selectable-constraintlayout" w "3.0.0"
    }
  }
  object Nhaarman {
    object MockitoKotlin2 {
      val mockito_kotlin = "com.nhaarman.mockitokotlin2" d "mockito-kotlin" w "2.2.0"
    }
  }
  object Rickclephas {
    object Kmp {
      val nativecoroutines_annotations = "com.rickclephas.kmp" d "kmp-nativecoroutines-annotations" w "0.13.3" w "1.0.0-ALPHA-37-kotlin-2.1.0-RC"
      val nativecoroutines_compiler = "com.rickclephas.kmp" d "kmp-nativecoroutines-compiler" w "0.13.3" w "1.0.0-ALPHA-37-kotlin-2.1.0-RC"
      val nativecoroutines_compiler_embeddable = "com.rickclephas.kmp" d "kmp-nativecoroutines-compiler-embeddable" w "0.13.3" w "1.0.0-ALPHA-37-kotlin-2.1.0-RC"
      val nativecoroutines_core = "com.rickclephas.kmp" d "kmp-nativecoroutines-core" w "0.13.3" w "1.0.0-ALPHA-37-kotlin-2.1.0-RC"
      val nativecoroutines_gradle_plugin = "com.rickclephas.kmp" d "kmp-nativecoroutines-gradle-plugin" w "0.13.3" w "1.0.0-ALPHA-37-kotlin-2.1.0-RC"
    }
  }
  object Russhwolf {
    val multiplatform_settings = "com.russhwolf" d "multiplatform-settings" w "1.2.0"
    val multiplatform_settings_coroutines = "com.russhwolf" d "multiplatform-settings-coroutines" w "1.2.0"
    val multiplatform_settings_coroutines_native_mt = "com.russhwolf" d "multiplatform-settings-coroutines-native-mt" w "0.9"
    val multiplatform_settings_datastore = "com.russhwolf" d "multiplatform-settings-datastore" w "1.2.0"
    val multiplatform_settings_no_arg = "com.russhwolf" d "multiplatform-settings-no-arg" w "1.2.0"
    val multiplatform_settings_serialization = "com.russhwolf" d "multiplatform-settings-serialization" w "1.2.0"
    val multiplatform_settings_test = "com.russhwolf" d "multiplatform-settings-test" w "1.2.0"
  }
  object SquareUp {
    val kotlinpoet = "com.squareup" d "kotlinpoet" w "2.0.0"
    val kotlinpoet_metadata = "com.squareup" d "kotlinpoet-metadata" w "2.0.0"
    val kotlinpoet_metadata_specs = "com.squareup" d "kotlinpoet-metadata-specs" w "1.9.0"
    object LeakCanary {
      val android = "com.squareup.leakcanary" d "leakcanary-android" w "2.14" w "3.0-alpha-8"
      val android_instrumentation = "com.squareup.leakcanary" d "leakcanary-android-instrumentation" w "2.14" w "3.0-alpha-8"
      val android_process = "com.squareup.leakcanary" d "leakcanary-android-process" w "2.14" w "3.0-alpha-8"
      val deobfuscation_gradle_plugin = "com.squareup.leakcanary" d "leakcanary-deobfuscation-gradle-plugin" w "2.14" w "3.0-alpha-8"
      val object_watcher = "com.squareup.leakcanary" d "leakcanary-object-watcher" w "2.14" w "3.0-alpha-8"
      val object_watcher_android = "com.squareup.leakcanary" d "leakcanary-object-watcher-android" w "2.14" w "3.0-alpha-8"
      val plumber_android = "com.squareup.leakcanary" d "plumber-android" w "2.14" w "3.0-alpha-8"
      val shark = "com.squareup.leakcanary" d "shark" w "2.14" w "3.0-alpha-8"
      val shark_android = "com.squareup.leakcanary" d "shark-android" w "2.14" w "3.0-alpha-8"
      val shark_cli = "com.squareup.leakcanary" d "shark-cli" w "2.14" w "3.0-alpha-8"
      val shark_graph = "com.squareup.leakcanary" d "shark-graph" w "2.14" w "3.0-alpha-8"
      val shark_hprof = "com.squareup.leakcanary" d "shark-hprof" w "2.14" w "3.0-alpha-8"
    }
    object Logcat {
      val logcat = "com.squareup.logcat" d "logcat" w "0.1"
    }
    object Moshi {
      val adapters = "com.squareup.moshi" d "moshi-adapters" w "1.15.1"
      val kotlin = "com.squareup.moshi" d "moshi-kotlin" w "1.15.1"
      val kotlin_codegen = "com.squareup.moshi" d "moshi-kotlin-codegen" w "1.15.1"
      val moshi = "com.squareup.moshi" d "moshi" w "1.15.1"
    }
    object Okhttp3 {
      val logging_interceptor = "com.squareup.okhttp3" d "logging-interceptor" w "4.12.0" w "5.0.0-alpha.14"
      val mockwebserver = "com.squareup.okhttp3" d "mockwebserver" w "4.12.0" w "5.0.0-alpha.14"
      val mockwebserver3 = "com.squareup.okhttp3" d "mockwebserver3" w "5.0.0-alpha.14"
      val mockwebserver3_junit4 = "com.squareup.okhttp3" d "mockwebserver3-junit4" w "5.0.0-alpha.14"
      val mockwebserver3_junit5 = "com.squareup.okhttp3" d "mockwebserver3-junit5" w "5.0.0-alpha.14"
      val okhttp = "com.squareup.okhttp3" d "okhttp" w "4.12.0" w "5.0.0-alpha.14"
      val okhttp_android = "com.squareup.okhttp3" d "okhttp-android" w "5.0.0-alpha.14"
      val okhttp_bom = "com.squareup.okhttp3" d "okhttp-bom" w "4.12.0" w "5.0.0-alpha.14"
      val okhttp_brotli = "com.squareup.okhttp3" d "okhttp-brotli" w "4.12.0" w "5.0.0-alpha.14"
      val okhttp_coroutines = "com.squareup.okhttp3" d "okhttp-coroutines" w "5.0.0-alpha.14"
      val okhttp_dnsoverhttps = "com.squareup.okhttp3" d "okhttp-dnsoverhttps" w "4.12.0" w "5.0.0-alpha.14"
      val okhttp_sse = "com.squareup.okhttp3" d "okhttp-sse" w "4.12.0" w "5.0.0-alpha.14"
      val okhttp_tls = "com.squareup.okhttp3" d "okhttp-tls" w "4.12.0" w "5.0.0-alpha.14"
      val okhttp_urlconnection = "com.squareup.okhttp3" d "okhttp-urlconnection" w "4.12.0" w "5.0.0-alpha.14"
    }
    object Okio {
      val okio = "com.squareup.okio" d "okio" w "3.9.1"
    }
    object Picasso {
      val picasso = "com.squareup.picasso" d "picasso" w "2.71828"
      val pollexor = "com.squareup.picasso" d "picasso-pollexor" w "2.71828"
    }
    object Retrofit2 {
      val adapter_java8 = "com.squareup.retrofit2" d "adapter-java8" w "2.11.0"
      val adapter_rxjava = "com.squareup.retrofit2" d "adapter-rxjava" w "2.11.0"
      val adapter_rxjava2 = "com.squareup.retrofit2" d "adapter-rxjava2" w "2.11.0"
      val adapter_rxjava3 = "com.squareup.retrofit2" d "adapter-rxjava3" w "2.11.0"
      val converter_gson = "com.squareup.retrofit2" d "converter-gson" w "2.11.0"
      val converter_jackson = "com.squareup.retrofit2" d "converter-jackson" w "2.11.0"
      val converter_moshi = "com.squareup.retrofit2" d "converter-moshi" w "2.11.0"
      val converter_scalars = "com.squareup.retrofit2" d "converter-scalars" w "2.11.0"
      val converter_simplexml = "com.squareup.retrofit2" d "converter-simplexml" w "2.11.0"
      val converter_wire = "com.squareup.retrofit2" d "converter-wire" w "2.11.0"
      val retrofit = "com.squareup.retrofit2" d "retrofit" w "2.11.0"
      val retrofit_mock = "com.squareup.retrofit2" d "retrofit-mock" w "2.11.0"
    }
    object SqlDelight {
      val android_driver = "com.squareup.sqldelight" d "android-driver" w "1.5.5"
      val android_paging3_extensions = "com.squareup.sqldelight" d "android-paging3-extensions" w "1.5.5"
      val android_paging_extensions = "com.squareup.sqldelight" d "android-paging-extensions" w "1.5.5"
      val coroutines_extensions = "com.squareup.sqldelight" d "coroutines-extensions" w "1.5.5"
      val gradle_plugin = "com.squareup.sqldelight" d "gradle-plugin" w "1.5.5"
      val jdbc_driver = "com.squareup.sqldelight" d "jdbc-driver" w "1.5.5"
      val native_driver = "com.squareup.sqldelight" d "native-driver" w "1.5.5"
      val rxjava2_extensions = "com.squareup.sqldelight" d "rxjava2-extensions" w "1.5.5"
      val rxjava3_extensions = "com.squareup.sqldelight" d "rxjava3-extensions" w "1.5.5"
      val sqlite_driver = "com.squareup.sqldelight" d "sqlite-driver" w "1.5.5"
      val sqljs_driver = "com.squareup.sqldelight" d "sqljs-driver" w "1.5.5"
    }
    object Wire {
      val gradle_plugin = "com.squareup.wire" d "wire-gradle-plugin" w "5.1.0"
      val grpc_client = "com.squareup.wire" d "wire-grpc-client" w "5.1.0"
      val runtime = "com.squareup.wire" d "wire-runtime" w "5.1.0"
    }
  }
}
object Io {
  object Arrow_kt {
    val arrow_core = "io.arrow-kt" d "arrow-core" w "1.2.4" w "2.0.0-beta.3"
    val arrow_fx_coroutines = "io.arrow-kt" d "arrow-fx-coroutines" w "1.2.4" w "2.0.0-beta.3"
    val arrow_fx_stm = "io.arrow-kt" d "arrow-fx-stm" w "1.2.4" w "2.0.0-beta.3"
    val arrow_optics = "io.arrow-kt" d "arrow-optics" w "1.2.4" w "2.0.0-beta.3"
    val arrow_optics_ksp_plugin = "io.arrow-kt" d "arrow-optics-ksp-plugin" w "1.2.4" w "2.0.0-beta.3"
    val arrow_optics_reflect = "io.arrow-kt" d "arrow-optics-reflect" w "1.2.4" w "2.0.0-beta.3"
    val arrow_stack = "io.arrow-kt" d "arrow-stack" w "1.2.4" w "2.0.0-beta.3"
    object Analysis {
      object Kotlin {
        val io_arrow_kt_analysis_kotlin_gradle_plugin = "io.arrow-kt.analysis.kotlin" d "io.arrow-kt.analysis.kotlin.gradle.plugin" w "2.0.2" w "2.0.3-alpha.2"
      }
    }
  }
  object Coil_kt {
    val coil = "io.coil-kt" d "coil" w "2.7.0"
    val coil_base = "io.coil-kt" d "coil-base" w "2.7.0"
    val coil_compose = "io.coil-kt" d "coil-compose" w "2.7.0"
    val coil_compose_base = "io.coil-kt" d "coil-compose-base" w "2.7.0"
    val coil_gif = "io.coil-kt" d "coil-gif" w "2.7.0"
    val coil_svg = "io.coil-kt" d "coil-svg" w "2.7.0"
    val coil_video = "io.coil-kt" d "coil-video" w "2.7.0"
  }
  object GitHub {
    object JavaEden {
      object Orchid {
        val orchidall = "io.github.javaeden.orchid" d "OrchidAll" w "v0.5.3"
        val orchidasciidoc = "io.github.javaeden.orchid" d "OrchidAsciidoc"
        val orchidazure = "io.github.javaeden.orchid" d "OrchidAzure"
        val orchidbible = "io.github.javaeden.orchid" d "OrchidBible"
        val orchidbitbucket = "io.github.javaeden.orchid" d "OrchidBitbucket"
        val orchidblog = "io.github.javaeden.orchid" d "OrchidBlog" w "v0.3.12"
        val orchidbsdoc = "io.github.javaeden.orchid" d "OrchidBsDoc" w "v0.3.12"
        val orchidchangelog = "io.github.javaeden.orchid" d "OrchidChangelog" w "v0.3.12"
        val orchidcopper = "io.github.javaeden.orchid" d "OrchidCopper"
        val orchidcore = "io.github.javaeden.orchid" d "OrchidCore" w "v0.3.12"
        val orchiddiagrams = "io.github.javaeden.orchid" d "OrchidDiagrams"
        val orchiddocs = "io.github.javaeden.orchid" d "OrchidDocs"
        val orchideditorial = "io.github.javaeden.orchid" d "OrchidEditorial" w "v0.5.3"
        val orchidforms = "io.github.javaeden.orchid" d "OrchidForms"
        val orchidfutureimperfect = "io.github.javaeden.orchid" d "OrchidFutureImperfect" w "v0.3.11"
        val orchidgithub = "io.github.javaeden.orchid" d "OrchidGithub"
        val orchidgitlab = "io.github.javaeden.orchid" d "OrchidGitlab"
        val orchidgroovydoc = "io.github.javaeden.orchid" d "OrchidGroovydoc"
        val orchidjavadoc = "io.github.javaeden.orchid" d "OrchidJavadoc" w "v0.3.12"
        val orchidkotlindoc = "io.github.javaeden.orchid" d "OrchidKotlindoc"
        val orchidkss = "io.github.javaeden.orchid" d "OrchidKSS" w "v0.3.12"
        val orchidlanguagepack = "io.github.javaeden.orchid" d "OrchidLanguagePack" w "v0.5.3"
        val orchidnetlify = "io.github.javaeden.orchid" d "OrchidNetlify"
        val orchidnetlifycms = "io.github.javaeden.orchid" d "OrchidNetlifyCMS"
        val orchidpages = "io.github.javaeden.orchid" d "OrchidPages" w "v0.5.3"
        val orchidplugindocs = "io.github.javaeden.orchid" d "OrchidPluginDocs"
        val orchidposts = "io.github.javaeden.orchid" d "OrchidPosts" w "v0.3.11"
        val orchidpresentations = "io.github.javaeden.orchid" d "OrchidPresentations" w "v0.5.3"
        val orchidsearch = "io.github.javaeden.orchid" d "OrchidSearch"
        val orchidsourcedoc = "io.github.javaeden.orchid" d "OrchidSourceDoc"
        val orchidswagger = "io.github.javaeden.orchid" d "OrchidSwagger"
        val orchidswiftdoc = "io.github.javaeden.orchid" d "OrchidSwiftdoc"
        val orchidsyntaxhighlighter = "io.github.javaeden.orchid" d "OrchidSyntaxHighlighter"
        val orchidtaxonomies = "io.github.javaeden.orchid" d "OrchidTaxonomies"
        val orchidtest = "io.github.javaeden.orchid" d "OrchidTest"
        val orchidwiki = "io.github.javaeden.orchid" d "OrchidWiki" w "v0.5.3"
        val orchidwritersblocks = "io.github.javaeden.orchid" d "OrchidWritersBlocks"
      }
    }
    object TypeSafeGitHub {
      val action_binding_generator = "io.github.typesafegithub" d "action-binding-generator" w "3.0.1"
      val action_updates_checker = "io.github.typesafegithub" d "action-updates-checker" w "3.0.1"
      val github_workflows_kt = "io.github.typesafegithub" d "github-workflows-kt" w "3.0.1"
      val shared_internal = "io.github.typesafegithub" d "shared-internal" w "3.0.1"
    }
  }
  object Insert_koin {
    val koin_android = "io.insert-koin" d "koin-android" w "4.0.0" w "4.1.0-Beta1"
    val koin_android_compat = "io.insert-koin" d "koin-android-compat" w "4.0.0" w "4.1.0-Beta1"
    val koin_androidx_compose = "io.insert-koin" d "koin-androidx-compose" w "4.0.0" w "4.1.0-Beta1"
    val koin_androidx_navigation = "io.insert-koin" d "koin-androidx-navigation" w "4.0.0" w "4.1.0-Beta1"
    val koin_androidx_workmanager = "io.insert-koin" d "koin-androidx-workmanager" w "4.0.0" w "4.1.0-Beta1"
    val koin_core = "io.insert-koin" d "koin-core" w "4.0.0" w "4.1.0-Beta1"
    val koin_ktor = "io.insert-koin" d "koin-ktor" w "4.0.0" w "4.1.0-Beta1"
    val koin_logger_slf4j = "io.insert-koin" d "koin-logger-slf4j" w "4.0.0" w "4.1.0-Beta1"
    val koin_test = "io.insert-koin" d "koin-test" w "4.0.0" w "4.1.0-Beta1"
    val koin_test_junit4 = "io.insert-koin" d "koin-test-junit4" w "4.0.0" w "4.1.0-Beta1"
    val koin_test_junit5 = "io.insert-koin" d "koin-test-junit5" w "4.0.0" w "4.1.0-Beta1"
  }
  object Kotest {
    val assertions_arrow = "io.kotest" d "kotest-assertions-arrow" w "4.4.3"
    val assertions_compiler = "io.kotest" d "kotest-assertions-compiler" w "4.6.4" w "5.0.0.M3"
    val assertions_core = "io.kotest" d "kotest-assertions-core" w "5.9.1" w "6.0.0.M1"
    val assertions_json = "io.kotest" d "kotest-assertions-json" w "5.9.1" w "6.0.0.M1"
    val assertions_jsoup = "io.kotest" d "kotest-assertions-jsoup" w "4.4.3"
    val assertions_klock = "io.kotest" d "kotest-assertions-klock" w "4.4.3"
    val assertions_konform = "io.kotest" d "kotest-assertions-konform" w "4.4.3"
    val assertions_kotlinx_time = "io.kotest" d "kotest-assertions-kotlinx-time" w "4.4.3"
    val assertions_ktor = "io.kotest" d "kotest-assertions-ktor" w "4.4.3"
    val assertions_sql = "io.kotest" d "kotest-assertions-sql" w "5.9.1"
    val core = "io.kotest" d "kotest-core" w "4.1.3" w "4.2.0.RC2"
    val framework_api = "io.kotest" d "kotest-framework-api" w "5.9.1" w "6.0.0.M1"
    val framework_datatest = "io.kotest" d "kotest-framework-datatest" w "5.9.1"
    val plugins_pitest = "io.kotest" d "kotest-plugins-pitest" w "4.4.3"
    val property = "io.kotest" d "kotest-property" w "5.9.1" w "6.0.0.M1"
    val property_arrow = "io.kotest" d "kotest-property-arrow" w "4.4.3"
    val runner_junit4 = "io.kotest" d "kotest-runner-junit4" w "5.9.1" w "6.0.0.M1"
    val runner_junit5 = "io.kotest" d "kotest-runner-junit5" w "5.9.1" w "6.0.0.M1"
    object Extensions {
      val kotest_extensions_allure = "io.kotest.extensions" d "kotest-extensions-allure" w "1.4.0"
      val kotest_extensions_embedded_kafka = "io.kotest.extensions" d "kotest-extensions-embedded-kafka" w "2.0.0"
      val kotest_extensions_gherkin = "io.kotest.extensions" d "kotest-extensions-gherkin" w "0.1.0"
      val kotest_extensions_koin = "io.kotest.extensions" d "kotest-extensions-koin" w "1.3.0"
      val kotest_extensions_mockserver = "io.kotest.extensions" d "kotest-extensions-mockserver" w "1.3.0"
      val kotest_extensions_pitest = "io.kotest.extensions" d "kotest-extensions-pitest" w "1.2.0"
      val kotest_extensions_robolectric = "io.kotest.extensions" d "kotest-extensions-robolectric" w "0.5.0"
      val kotest_extensions_spring = "io.kotest.extensions" d "kotest-extensions-spring" w "1.3.0"
      val kotest_extensions_testcontainers = "io.kotest.extensions" d "kotest-extensions-testcontainers" w "2.0.2"
      val kotest_extensions_wiremock = "io.kotest.extensions" d "kotest-extensions-wiremock" w "3.1.0"
      val kotest_property_arbs = "io.kotest.extensions" d "kotest-property-arbs" w "2.1.2"
      val kotest_property_datetime = "io.kotest.extensions" d "kotest-property-datetime" w "2.0.0"
    }
  }
  object Ktor {
    val client_android = "io.ktor" d "ktor-client-android" w "3.0.1"
    val client_apache = "io.ktor" d "ktor-client-apache" w "3.0.1"
    val client_auth = "io.ktor" d "ktor-client-auth" w "3.0.1"
    val client_cio = "io.ktor" d "ktor-client-cio" w "3.0.1"
    val client_content_negotiation = "io.ktor" d "ktor-client-content-negotiation" w "3.0.1"
    val client_content_negotiation_tests = "io.ktor" d "ktor-client-content-negotiation-tests" w "3.0.1"
    val client_core = "io.ktor" d "ktor-client-core" w "3.0.1"
    val client_curl = "io.ktor" d "ktor-client-curl" w "3.0.1"
    val client_darwin = "io.ktor" d "ktor-client-darwin" w "3.0.1"
    val client_encoding = "io.ktor" d "ktor-client-encoding" w "3.0.1"
    val client_gson = "io.ktor" d "ktor-client-gson" w "3.0.1"
    val client_jackson = "io.ktor" d "ktor-client-jackson" w "3.0.1"
    val client_java = "io.ktor" d "ktor-client-java" w "3.0.1"
    val client_jetty = "io.ktor" d "ktor-client-jetty" w "3.0.1"
    val client_json = "io.ktor" d "ktor-client-json" w "3.0.1"
    val client_json_tests = "io.ktor" d "ktor-client-json-tests" w "2.3.13"
    val client_logging = "io.ktor" d "ktor-client-logging" w "3.0.1"
    val client_mock = "io.ktor" d "ktor-client-mock" w "3.0.1"
    val client_okhttp = "io.ktor" d "ktor-client-okhttp" w "3.0.1"
    val client_resources = "io.ktor" d "ktor-client-resources" w "3.0.1"
    val client_serialization = "io.ktor" d "ktor-client-serialization" w "3.0.1"
    val client_tests = "io.ktor" d "ktor-client-tests" w "3.0.1"
    val events = "io.ktor" d "ktor-events" w "3.0.1"
    val http = "io.ktor" d "ktor-http" w "3.0.1"
    val http_cio = "io.ktor" d "ktor-http-cio" w "3.0.1"
    val io = "io.ktor" d "ktor-io" w "3.0.1"
    val network = "io.ktor" d "ktor-network" w "3.0.1"
    val network_tls = "io.ktor" d "ktor-network-tls" w "3.0.1"
    val network_tls_certificates = "io.ktor" d "ktor-network-tls-certificates" w "3.0.1"
    val resources = "io.ktor" d "ktor-resources" w "3.0.1"
    val serialization = "io.ktor" d "ktor-serialization" w "3.0.1"
    val serialization_gson = "io.ktor" d "ktor-serialization-gson" w "3.0.1"
    val serialization_jackson = "io.ktor" d "ktor-serialization-jackson" w "3.0.1"
    val serialization_kotlinx = "io.ktor" d "ktor-serialization-kotlinx" w "3.0.1"
    val serialization_kotlinx_cbor = "io.ktor" d "ktor-serialization-kotlinx-cbor" w "3.0.1"
    val serialization_kotlinx_json = "io.ktor" d "ktor-serialization-kotlinx-json" w "3.0.1"
    val serialization_kotlinx_tests = "io.ktor" d "ktor-serialization-kotlinx-tests" w "3.0.1"
    val serialization_kotlinx_xml = "io.ktor" d "ktor-serialization-kotlinx-xml" w "3.0.1"
    val server = "io.ktor" d "ktor-server" w "3.0.1"
    val server_auth = "io.ktor" d "ktor-server-auth" w "3.0.1"
    val server_auth_jwt = "io.ktor" d "ktor-server-auth-jwt" w "3.0.1"
    val server_auth_ldap = "io.ktor" d "ktor-server-auth-ldap" w "3.0.1"
    val server_auto_head_response = "io.ktor" d "ktor-server-auto-head-response" w "3.0.1"
    val server_caching_headers = "io.ktor" d "ktor-server-caching-headers" w "3.0.1"
    val server_call_id = "io.ktor" d "ktor-server-call-id" w "3.0.1"
    val server_call_logging = "io.ktor" d "ktor-server-call-logging" w "3.0.1"
    val server_cio = "io.ktor" d "ktor-server-cio" w "3.0.1"
    val server_compression = "io.ktor" d "ktor-server-compression" w "3.0.1"
    val server_conditional_headers = "io.ktor" d "ktor-server-conditional-headers" w "3.0.1"
    val server_content_negotiation = "io.ktor" d "ktor-server-content-negotiation" w "3.0.1"
    val server_core = "io.ktor" d "ktor-server-core" w "3.0.1"
    val server_cors = "io.ktor" d "ktor-server-cors" w "3.0.1"
    val server_data_conversion = "io.ktor" d "ktor-server-data-conversion" w "3.0.1"
    val server_default_headers = "io.ktor" d "ktor-server-default-headers" w "3.0.1"
    val server_double_receive = "io.ktor" d "ktor-server-double-receive" w "3.0.1"
    val server_forwarded_header = "io.ktor" d "ktor-server-forwarded-header" w "3.0.1"
    val server_freemarker = "io.ktor" d "ktor-server-freemarker" w "3.0.1"
    val server_host_common = "io.ktor" d "ktor-server-host-common" w "3.0.1"
    val server_hsts = "io.ktor" d "ktor-server-hsts" w "3.0.1"
    val server_html_builder = "io.ktor" d "ktor-server-html-builder" w "3.0.1"
    val server_http_redirect = "io.ktor" d "ktor-server-http-redirect" w "3.0.1"
    val server_jetty = "io.ktor" d "ktor-server-jetty" w "3.0.1"
    val server_jte = "io.ktor" d "ktor-server-jte" w "3.0.1"
    val server_locations = "io.ktor" d "ktor-server-locations" w "2.3.13"
    val server_method_override = "io.ktor" d "ktor-server-method-override" w "3.0.1"
    val server_metrics = "io.ktor" d "ktor-server-metrics" w "3.0.1"
    val server_metrics_micrometer = "io.ktor" d "ktor-server-metrics-micrometer" w "3.0.1"
    val server_mustache = "io.ktor" d "ktor-server-mustache" w "3.0.1"
    val server_netty = "io.ktor" d "ktor-server-netty" w "3.0.1"
    val server_partial_content = "io.ktor" d "ktor-server-partial-content" w "3.0.1"
    val server_pebble = "io.ktor" d "ktor-server-pebble" w "3.0.1"
    val server_resources = "io.ktor" d "ktor-server-resources" w "3.0.1"
    val server_servlet = "io.ktor" d "ktor-server-servlet" w "3.0.1"
    val server_sessions = "io.ktor" d "ktor-server-sessions" w "3.0.1"
    val server_status_pages = "io.ktor" d "ktor-server-status-pages" w "3.0.1"
    val server_test_host = "io.ktor" d "ktor-server-test-host" w "3.0.1"
    val server_test_suites = "io.ktor" d "ktor-server-test-suites" w "2.3.13" w "3.0.0-beta-2"
    val server_thymeleaf = "io.ktor" d "ktor-server-thymeleaf" w "3.0.1"
    val server_tomcat = "io.ktor" d "ktor-server-tomcat" w "3.0.1"
    val server_velocity = "io.ktor" d "ktor-server-velocity" w "3.0.1"
    val server_webjars = "io.ktor" d "ktor-server-webjars" w "3.0.1"
    val server_websockets = "io.ktor" d "ktor-server-websockets" w "3.0.1"
    val test_dispatcher = "io.ktor" d "ktor-test-dispatcher" w "3.0.1"
    val utils = "io.ktor" d "ktor-utils" w "3.0.1"
    val websocket_serialization = "io.ktor" d "ktor-websocket-serialization" w "3.0.1"
    val websockets = "io.ktor" d "ktor-websockets" w "3.0.1"
  }
  object MockK {
    val android = "io.mockk" d "mockk-android" w "1.13.13"
    val common = "io.mockk" d "mockk-common" w "1.12.5"
    val mockk = "io.mockk" d "mockk" w "1.13.13"
  }
  object Pivotal {
    object Spring {
      object Cloud {
        val spring_cloud_services_starter_circuit_breaker = "io.pivotal.spring.cloud" d "spring-cloud-services-starter-circuit-breaker" w "2.4.1"
        val spring_cloud_services_starter_config_client = "io.pivotal.spring.cloud" d "spring-cloud-services-starter-config-client" w "4.1.6"
        val spring_cloud_services_starter_service_registry = "io.pivotal.spring.cloud" d "spring-cloud-services-starter-service-registry" w "4.1.6"
      }
    }
  }
  object Projectreactor {
    val reactor_test = "io.projectreactor" d "reactor-test" w "3.7.0"
    object Kotlin {
      val reactor_kotlin_extensions = "io.projectreactor.kotlin" d "reactor-kotlin-extensions" w "1.2.3"
    }
  }
  object RSocket {
    object Kotlin {
      val rsocket_core = "io.rsocket.kotlin" d "rsocket-core" w "0.16.0"
      val rsocket_ktor_client = "io.rsocket.kotlin" d "rsocket-ktor-client" w "0.16.0"
      val rsocket_ktor_server = "io.rsocket.kotlin" d "rsocket-ktor-server" w "0.16.0"
      val rsocket_transport_ktor = "io.rsocket.kotlin" d "rsocket-transport-ktor" w "0.15.4"
      val rsocket_transport_ktor_tcp = "io.rsocket.kotlin" d "rsocket-transport-ktor-tcp" w "0.16.0"
      val rsocket_transport_ktor_websocket = "io.rsocket.kotlin" d "rsocket-transport-ktor-websocket" w "0.15.4"
      val rsocket_transport_ktor_websocket_client = "io.rsocket.kotlin" d "rsocket-transport-ktor-websocket-client" w "0.16.0"
      val rsocket_transport_ktor_websocket_server = "io.rsocket.kotlin" d "rsocket-transport-ktor-websocket-server" w "0.16.0"
      val rsocket_transport_nodejs_tcp = "io.rsocket.kotlin" d "rsocket-transport-nodejs-tcp" w "0.16.0"
    }
  }
  object ReactiveX {
    object RxJava2 {
      val rxandroid = "io.reactivex.rxjava2" d "rxandroid" w "2.1.1"
      val rxjava = "io.reactivex.rxjava2" d "rxjava" w "2.2.21"
      val rxkotlin = "io.reactivex.rxjava2" d "rxkotlin" w "2.4.0"
    }
    object RxJava3 {
      val rxandroid = "io.reactivex.rxjava3" d "rxandroid" w "3.0.2"
      val rxjava = "io.reactivex.rxjava3" d "rxjava" w "3.1.9"
      val rxkotlin = "io.reactivex.rxjava3" d "rxkotlin" w "3.0.1"
    }
  }
  object Realm {
    val gradle_plugin = "io.realm" d "realm-gradle-plugin" w "10.19.0"
  }
  object Strikt {
    val arrow = "io.strikt" d "strikt-arrow" w "0.35.1"
    val bom = "io.strikt" d "strikt-bom" w "0.35.1"
    val core = "io.strikt" d "strikt-core" w "0.35.1"
    val gradle = "io.strikt" d "strikt-gradle" w "0.31.0"
    val jackson = "io.strikt" d "strikt-jackson" w "0.35.1"
    val java_time = "io.strikt" d "strikt-java-time" w "0.28.2"
    val mockk = "io.strikt" d "strikt-mockk" w "0.35.1"
    val protobuf = "io.strikt" d "strikt-protobuf" w "0.35.1"
    val spring = "io.strikt" d "strikt-spring" w "0.35.1"
  }
}
object JUnit {
  val junit = "junit" d "junit" w "4.13.2"
}
object Org {
  object AssertJ {
    val core = "org.assertj" d "assertj-core" w "3.26.3"
    val db = "org.assertj" d "assertj-db" w "3.0.0"
    val guava = "org.assertj" d "assertj-guava" w "3.26.3"
    val joda_time = "org.assertj" d "assertj-joda-time" w "2.2.0"
    val swing = "org.assertj" d "assertj-swing" w "3.17.1"
  }
  object Hamcrest {
    val core = "org.hamcrest" d "hamcrest-core" w "3.0"
    val hamcrest = "org.hamcrest" d "hamcrest" w "3.0"
    val library = "org.hamcrest" d "hamcrest-library" w "3.0"
  }
  object Hildan {
    object Chrome {
      val devtools_kotlin = "org.hildan.chrome" d "chrome-devtools-kotlin" w "6.5.0-1383960"
    }
  }
  object Http4k {
    val aws = "org.http4k" d "http4k-aws" w "5.35.3.0"
    val bom = "org.http4k" d "http4k-bom" w "5.35.3.0"
    val client_apache = "org.http4k" d "http4k-client-apache" w "5.35.3.0"
    val client_apache4 = "org.http4k" d "http4k-client-apache4" w "5.35.3.0"
    val client_apache4_async = "org.http4k" d "http4k-client-apache4-async" w "5.35.3.0"
    val client_apache_async = "org.http4k" d "http4k-client-apache-async" w "5.35.3.0"
    val client_jetty = "org.http4k" d "http4k-client-jetty" w "5.35.3.0"
    val client_okhttp = "org.http4k" d "http4k-client-okhttp" w "5.35.3.0"
    val client_websocket = "org.http4k" d "http4k-client-websocket" w "5.35.3.0"
    val cloudnative = "org.http4k" d "http4k-cloudnative" w "5.35.3.0"
    val contract = "org.http4k" d "http4k-contract" w "5.35.3.0"
    val core = "org.http4k" d "http4k-core" w "5.35.3.0"
    val format_argo = "org.http4k" d "http4k-format-argo" w "5.35.3.0"
    val format_core = "org.http4k" d "http4k-format-core" w "5.35.3.0"
    val format_gson = "org.http4k" d "http4k-format-gson" w "5.35.3.0"
    val format_jackson = "org.http4k" d "http4k-format-jackson" w "5.35.3.0"
    val format_jackson_xml = "org.http4k" d "http4k-format-jackson-xml" w "5.35.3.0"
    val format_jackson_yaml = "org.http4k" d "http4k-format-jackson-yaml" w "5.35.3.0"
    val format_klaxon = "org.http4k" d "http4k-format-klaxon" w "5.35.3.0"
    val format_kotlinx_serialization = "org.http4k" d "http4k-format-kotlinx-serialization" w "5.35.3.0"
    val format_moshi = "org.http4k" d "http4k-format-moshi" w "5.35.3.0"
    val format_xml = "org.http4k" d "http4k-format-xml" w "5.35.3.0"
    val graphql = "org.http4k" d "http4k-graphql" w "5.35.3.0"
    val incubator = "org.http4k" d "http4k-incubator" w "5.35.3.0"
    val jsonrpc = "org.http4k" d "http4k-jsonrpc" w "5.35.3.0"
    val metrics_micrometer = "org.http4k" d "http4k-metrics-micrometer" w "5.35.3.0"
    val multipart = "org.http4k" d "http4k-multipart" w "5.35.3.0"
    val opentelemetry = "org.http4k" d "http4k-opentelemetry" w "5.35.3.0"
    val realtime_core = "org.http4k" d "http4k-realtime-core" w "5.35.3.0"
    val resilience4j = "org.http4k" d "http4k-resilience4j" w "5.35.3.0"
    val security_oauth = "org.http4k" d "http4k-security-oauth" w "5.35.3.0"
    val server_apache = "org.http4k" d "http4k-server-apache" w "5.35.3.0"
    val server_apache4 = "org.http4k" d "http4k-server-apache4" w "5.35.3.0"
    val server_jetty = "org.http4k" d "http4k-server-jetty" w "5.35.3.0"
    val server_ktorcio = "org.http4k" d "http4k-server-ktorcio" w "5.35.3.0"
    val server_ktornetty = "org.http4k" d "http4k-server-ktornetty" w "5.35.3.0"
    val server_netty = "org.http4k" d "http4k-server-netty" w "5.35.3.0"
    val server_ratpack = "org.http4k" d "http4k-server-ratpack" w "5.35.3.0"
    val server_undertow = "org.http4k" d "http4k-server-undertow" w "5.35.3.0"
    val serverless_alibaba = "org.http4k" d "http4k-serverless-alibaba" w "5.35.3.0"
    val serverless_azure = "org.http4k" d "http4k-serverless-azure" w "5.35.3.0"
    val serverless_gcf = "org.http4k" d "http4k-serverless-gcf" w "5.35.3.0"
    val serverless_lambda = "org.http4k" d "http4k-serverless-lambda" w "5.35.3.0"
    val serverless_lambda_runtime = "org.http4k" d "http4k-serverless-lambda-runtime" w "5.35.3.0"
    val serverless_openwhisk = "org.http4k" d "http4k-serverless-openwhisk" w "5.35.3.0"
    val serverless_tencent = "org.http4k" d "http4k-serverless-tencent" w "5.35.3.0"
    val template_core = "org.http4k" d "http4k-template-core" w "5.35.3.0"
    val template_dust = "org.http4k" d "http4k-template-dust" w "4.48.0.0"
    val template_freemarker = "org.http4k" d "http4k-template-freemarker" w "5.35.3.0"
    val template_handlebars = "org.http4k" d "http4k-template-handlebars" w "5.35.3.0"
    val template_jade4j = "org.http4k" d "http4k-template-jade4j" w "5.12.2.1"
    val template_pebble = "org.http4k" d "http4k-template-pebble" w "5.35.3.0"
    val template_thymeleaf = "org.http4k" d "http4k-template-thymeleaf" w "5.35.3.0"
    val testing_approval = "org.http4k" d "http4k-testing-approval" w "5.35.3.0"
    val testing_chaos = "org.http4k" d "http4k-testing-chaos" w "5.35.3.0"
    val testing_hamkrest = "org.http4k" d "http4k-testing-hamkrest" w "5.35.3.0"
    val testing_kotest = "org.http4k" d "http4k-testing-kotest" w "5.35.3.0"
    val testing_servirtium = "org.http4k" d "http4k-testing-servirtium" w "5.35.3.0"
    val testing_strikt = "org.http4k" d "http4k-testing-strikt" w "5.35.3.0"
    val testing_webdriver = "org.http4k" d "http4k-testing-webdriver" w "5.35.3.0"
  }
  object JUnit {
    val bom = "org.junit" d "junit-bom" w "5.11.3"
    object Jupiter {
      val junit_jupiter = "org.junit.jupiter" d "junit-jupiter" w "5.11.3"
      val junit_jupiter_api = "org.junit.jupiter" d "junit-jupiter-api" w "5.11.3"
      val junit_jupiter_engine = "org.junit.jupiter" d "junit-jupiter-engine" w "5.11.3"
      val junit_jupiter_migrationsupport = "org.junit.jupiter" d "junit-jupiter-migrationsupport" w "5.11.3"
      val junit_jupiter_params = "org.junit.jupiter" d "junit-jupiter-params" w "5.11.3"
    }
  }
  object JetBrains {
    object Compose {
      val gradle_plugin = "org.jetbrains.compose" d "compose-gradle-plugin" w "1.7.1" w "1.8.0+check" w "1.8.0-dev1905" w "1.8.0-dev1920"
      object Compiler {
        val compiler = "org.jetbrains.compose.compiler" d "compiler" w "1.5.8.1" w "1.5.9-kt-2.0.0-Beta4" w "1.5.14"
      }
    }
    object Exposed {
      val core = "org.jetbrains.exposed" d "exposed-core" w "0.56.0"
      val dao = "org.jetbrains.exposed" d "exposed-dao" w "0.56.0"
      val jdbc = "org.jetbrains.exposed" d "exposed-jdbc" w "0.56.0"
    }
    object Kotlin {
      val reflect = "org.jetbrains.kotlin" d "kotlin-reflect" w "2.0.21" w "2.1.0-RC2"
      val script_runtime = "org.jetbrains.kotlin" d "kotlin-script-runtime" w "2.0.21" w "2.1.0-RC2"
      val stdlib = "org.jetbrains.kotlin" d "kotlin-stdlib" w "2.0.21" w "2.1.0-RC2"
      val stdlib_common = "org.jetbrains.kotlin" d "kotlin-stdlib-common" w "2.0.21" w "2.1.0-RC2"
      val stdlib_jdk7 = "org.jetbrains.kotlin" d "kotlin-stdlib-jdk7" w "2.0.21" w "2.1.0-RC2"
      val stdlib_jdk8 = "org.jetbrains.kotlin" d "kotlin-stdlib-jdk8" w "2.0.21" w "2.1.0-RC2"
      val stdlib_js = "org.jetbrains.kotlin" d "kotlin-stdlib-js" w "2.0.21" w "2.1.0-RC2"
      val test = "org.jetbrains.kotlin" d "kotlin-test" w "2.0.21" w "2.1.0-RC2"
      val test_annotations_common = "org.jetbrains.kotlin" d "kotlin-test-annotations-common" w "2.0.21" w "2.1.0-RC2"
      val test_common = "org.jetbrains.kotlin" d "kotlin-test-common" w "2.0.21" w "2.1.0-RC2"
      val test_js = "org.jetbrains.kotlin" d "kotlin-test-js" w "2.0.21" w "2.1.0-RC2"
      val test_js_runner = "org.jetbrains.kotlin" d "kotlin-test-js-runner" w "2.0.21"
      val test_junit = "org.jetbrains.kotlin" d "kotlin-test-junit" w "2.0.21" w "2.1.0-RC2"
      val test_junit5 = "org.jetbrains.kotlin" d "kotlin-test-junit5" w "2.0.21" w "2.1.0-RC2"
      val test_testng = "org.jetbrains.kotlin" d "kotlin-test-testng" w "2.0.21" w "2.1.0-RC2"
    }
    object KotlinX {
      val atomicfu_gradle_plugin = "org.jetbrains.kotlinx" d "atomicfu-gradle-plugin" w "0.26.0"
      val cli = "org.jetbrains.kotlinx" d "kotlinx-cli" w "0.3.6"
      val collections_immutable = "org.jetbrains.kotlinx" d "kotlinx-collections-immutable" w "0.3.8"
      val collections_immutable_jvm = "org.jetbrains.kotlinx" d "kotlinx-collections-immutable-jvm" w "0.3.8"
      val coroutines_android = "org.jetbrains.kotlinx" d "kotlinx-coroutines-android" w "1.9.0"
      val coroutines_bom = "org.jetbrains.kotlinx" d "kotlinx-coroutines-bom" w "1.9.0"
      val coroutines_core = "org.jetbrains.kotlinx" d "kotlinx-coroutines-core" w "1.9.0"
      val coroutines_debug = "org.jetbrains.kotlinx" d "kotlinx-coroutines-debug" w "1.9.0"
      val coroutines_guava = "org.jetbrains.kotlinx" d "kotlinx-coroutines-guava" w "1.9.0"
      val coroutines_javafx = "org.jetbrains.kotlinx" d "kotlinx-coroutines-javafx" w "1.9.0"
      val coroutines_jdk8 = "org.jetbrains.kotlinx" d "kotlinx-coroutines-jdk8" w "1.9.0"
      val coroutines_jdk9 = "org.jetbrains.kotlinx" d "kotlinx-coroutines-jdk9" w "1.9.0"
      val coroutines_play_services = "org.jetbrains.kotlinx" d "kotlinx-coroutines-play-services" w "1.9.0"
      val coroutines_reactive = "org.jetbrains.kotlinx" d "kotlinx-coroutines-reactive" w "1.9.0"
      val coroutines_reactor = "org.jetbrains.kotlinx" d "kotlinx-coroutines-reactor" w "1.9.0"
      val coroutines_rx2 = "org.jetbrains.kotlinx" d "kotlinx-coroutines-rx2" w "1.9.0"
      val coroutines_rx3 = "org.jetbrains.kotlinx" d "kotlinx-coroutines-rx3" w "1.9.0"
      val coroutines_slf4j = "org.jetbrains.kotlinx" d "kotlinx-coroutines-slf4j" w "1.9.0"
      val coroutines_swing = "org.jetbrains.kotlinx" d "kotlinx-coroutines-swing" w "1.9.0"
      val coroutines_test = "org.jetbrains.kotlinx" d "kotlinx-coroutines-test" w "1.9.0"
      val dataframe = "org.jetbrains.kotlinx" d "dataframe" w "0.14.2" w "0.15.0-dev-5103"
      val dataframe_arrow = "org.jetbrains.kotlinx" d "dataframe-arrow" w "0.14.2" w "0.15.0-dev-5103"
      val dataframe_core = "org.jetbrains.kotlinx" d "dataframe-core" w "0.14.2" w "0.15.0-dev-5103"
      val dataframe_excel = "org.jetbrains.kotlinx" d "dataframe-excel" w "0.14.2" w "0.15.0-dev-5103"
      val datetime = "org.jetbrains.kotlinx" d "kotlinx-datetime" w "0.6.1"
      val html = "org.jetbrains.kotlinx" d "kotlinx-html" w "0.11.0"
      val io_jvm = "org.jetbrains.kotlinx" d "kotlinx-io-jvm" w "0.1.16"
      val kotlin_deeplearning_api = "org.jetbrains.kotlinx" d "kotlin-deeplearning-api" w "0.5.2" w "0.6.0-alpha-1"
      val kotlin_deeplearning_onnx = "org.jetbrains.kotlinx" d "kotlin-deeplearning-onnx" w "0.5.2" w "0.6.0-alpha-1"
      val kotlin_deeplearning_visualization = "org.jetbrains.kotlinx" d "kotlin-deeplearning-visualization" w "0.5.2" w "0.6.0-alpha-1"
      val lincheck = "org.jetbrains.kotlinx" d "lincheck" w "2.34"
      val lincheck_jvm = "org.jetbrains.kotlinx" d "lincheck-jvm" w "2.34"
      val multik_api = "org.jetbrains.kotlinx" d "multik-api" w "0.1.1"
      val multik_default = "org.jetbrains.kotlinx" d "multik-default" w "0.2.3"
      val multik_jvm = "org.jetbrains.kotlinx" d "multik-jvm" w "0.1.1"
      val multik_native = "org.jetbrains.kotlinx" d "multik-native" w "0.1.1"
      val nodejs = "org.jetbrains.kotlinx" d "kotlinx-nodejs"
      val reflect_lite = "org.jetbrains.kotlinx" d "kotlinx.reflect.lite" w "1.1.0" w "1.2.0-RC"
      val serialization_bom = "org.jetbrains.kotlinx" d "kotlinx-serialization-bom" w "1.7.3"
      val serialization_cbor = "org.jetbrains.kotlinx" d "kotlinx-serialization-cbor" w "1.7.3"
      val serialization_core = "org.jetbrains.kotlinx" d "kotlinx-serialization-core" w "1.7.3"
      val serialization_hocon = "org.jetbrains.kotlinx" d "kotlinx-serialization-hocon" w "1.7.3"
      val serialization_json = "org.jetbrains.kotlinx" d "kotlinx-serialization-json" w "1.7.3"
      val serialization_json_okio = "org.jetbrains.kotlinx" d "kotlinx-serialization-json-okio" w "1.7.3"
      val serialization_properties = "org.jetbrains.kotlinx" d "kotlinx-serialization-properties" w "1.7.3"
      val serialization_protobuf = "org.jetbrains.kotlinx" d "kotlinx-serialization-protobuf" w "1.7.3"
    }
    object Kotlin_Wrappers {
      val bom = "org.jetbrains.kotlin-wrappers" d "kotlin-wrappers-bom" w "1.0.0-pre.837"
      val kotlin_actions_toolkit = "org.jetbrains.kotlin-wrappers" d "kotlin-actions-toolkit" w "1.0.0-pre.837"
      val kotlin_browser = "org.jetbrains.kotlin-wrappers" d "kotlin-browser" w "1.0.0-pre.837"
      val kotlin_cesium = "org.jetbrains.kotlin-wrappers" d "kotlin-cesium" w "1.115.0-pre.711"
      val kotlin_css = "org.jetbrains.kotlin-wrappers" d "kotlin-css" w "1.0.0-pre.837"
      val kotlin_csstype = "org.jetbrains.kotlin-wrappers" d "kotlin-csstype" w "3.1.3-pre.837"
      val kotlin_emotion = "org.jetbrains.kotlin-wrappers" d "kotlin-emotion" w "11.13.5-pre.837"
      val kotlin_history = "org.jetbrains.kotlin-wrappers" d "kotlin-history" w "5.3.0-pre.506-compat"
      val kotlin_js = "org.jetbrains.kotlin-wrappers" d "kotlin-js" w "1.0.0-pre.837"
      val kotlin_mui = "org.jetbrains.kotlin-wrappers" d "kotlin-mui" w "5.14.12-pre.638"
      val kotlin_mui_icons = "org.jetbrains.kotlin-wrappers" d "kotlin-mui-icons" w "5.14.12-pre.638"
      val kotlin_node = "org.jetbrains.kotlin-wrappers" d "kotlin-node" w "22.5.5-pre.837"
      val kotlin_popper = "org.jetbrains.kotlin-wrappers" d "kotlin-popper" w "2.11.8-pre.792"
      val kotlin_react = "org.jetbrains.kotlin-wrappers" d "kotlin-react" w "18.3.1-pre.837"
      val kotlin_react_beautiful_dnd = "org.jetbrains.kotlin-wrappers" d "kotlin-react-beautiful-dnd" w "13.1.1-pre.837"
      val kotlin_react_core = "org.jetbrains.kotlin-wrappers" d "kotlin-react-core" w "18.3.1-pre.837"
      val kotlin_react_dom = "org.jetbrains.kotlin-wrappers" d "kotlin-react-dom" w "18.3.1-pre.837"
      val kotlin_react_dom_legacy = "org.jetbrains.kotlin-wrappers" d "kotlin-react-dom-legacy" w "18.3.1-pre.837"
      val kotlin_react_dom_test_utils = "org.jetbrains.kotlin-wrappers" d "kotlin-react-dom-test-utils" w "18.3.1-pre.837"
      val kotlin_react_legacy = "org.jetbrains.kotlin-wrappers" d "kotlin-react-legacy" w "18.3.1-pre.837"
      val kotlin_react_popper = "org.jetbrains.kotlin-wrappers" d "kotlin-react-popper" w "2.3.0-pre.837"
      val kotlin_react_redux = "org.jetbrains.kotlin-wrappers" d "kotlin-react-redux" w "7.2.6-pre.785"
      val kotlin_react_router = "org.jetbrains.kotlin-wrappers" d "kotlin-react-router" w "6.28.0-pre.837"
      val kotlin_react_router_dom = "org.jetbrains.kotlin-wrappers" d "kotlin-react-router-dom" w "6.28.0-pre.837"
      val kotlin_react_select = "org.jetbrains.kotlin-wrappers" d "kotlin-react-select" w "5.8.3-pre.837"
      val kotlin_react_use = "org.jetbrains.kotlin-wrappers" d "kotlin-react-use" w "17.4.0-pre.837"
      val kotlin_redux = "org.jetbrains.kotlin-wrappers" d "kotlin-redux" w "4.1.2-pre.785"
      val kotlin_remix_run_router = "org.jetbrains.kotlin-wrappers" d "kotlin-remix-run-router" w "1.21.0-pre.837"
      val kotlin_ring_ui = "org.jetbrains.kotlin-wrappers" d "kotlin-ring-ui" w "4.1.5-pre.784"
      val kotlin_styled = "org.jetbrains.kotlin-wrappers" d "kotlin-styled" w "5.3.11-pre.717"
      val kotlin_styled_next = "org.jetbrains.kotlin-wrappers" d "kotlin-styled-next" w "1.2.4-pre.837"
      val kotlin_tanstack_query_core = "org.jetbrains.kotlin-wrappers" d "kotlin-tanstack-query-core" w "5.60.6-pre.837"
      val kotlin_tanstack_react_query = "org.jetbrains.kotlin-wrappers" d "kotlin-tanstack-react-query" w "5.61.0-pre.837"
      val kotlin_tanstack_react_query_devtools = "org.jetbrains.kotlin-wrappers" d "kotlin-tanstack-react-query-devtools" w "5.61.0-pre.837"
      val kotlin_tanstack_react_table = "org.jetbrains.kotlin-wrappers" d "kotlin-tanstack-react-table" w "8.20.5-pre.837"
      val kotlin_tanstack_react_virtual = "org.jetbrains.kotlin-wrappers" d "kotlin-tanstack-react-virtual" w "3.10.9-pre.837"
      val kotlin_tanstack_table_core = "org.jetbrains.kotlin-wrappers" d "kotlin-tanstack-table-core" w "8.20.5-pre.837"
      val kotlin_tanstack_virtual_core = "org.jetbrains.kotlin-wrappers" d "kotlin-tanstack-virtual-core" w "3.10.9-pre.837"
      val kotlin_typescript = "org.jetbrains.kotlin-wrappers" d "kotlin-typescript" w "5.4.5-pre.837"
      val kotlin_web = "org.jetbrains.kotlin-wrappers" d "kotlin-web" w "1.0.0-pre.837"
    }
  }
  object Kodein {
    object Di {
      val kodein_di_conf_js = "org.kodein.di" d "kodein-di-conf-js" w "7.22.0"
      val kodein_di_conf_jvm = "org.kodein.di" d "kodein-di-conf-jvm" w "7.22.0"
      val kodein_di_framework_android_core = "org.kodein.di" d "kodein-di-framework-android-core" w "7.22.0"
      val kodein_di_framework_android_support = "org.kodein.di" d "kodein-di-framework-android-support" w "7.22.0"
      val kodein_di_framework_android_x = "org.kodein.di" d "kodein-di-framework-android-x" w "7.22.0"
      val kodein_di_framework_ktor_server_jvm = "org.kodein.di" d "kodein-di-framework-ktor-server-jvm" w "7.22.0"
      val kodein_di_framework_tornadofx_jvm = "org.kodein.di" d "kodein-di-framework-tornadofx-jvm" w "7.22.0"
      val kodein_di_js = "org.kodein.di" d "kodein-di-js" w "7.22.0"
      val kodein_di_jxinject_jvm = "org.kodein.di" d "kodein-di-jxinject-jvm" w "7.22.0"
    }
  }
  object Mockito {
    val android = "org.mockito" d "mockito-android" w "5.14.2"
    val core = "org.mockito" d "mockito-core" w "5.14.2"
    val errorprone = "org.mockito" d "mockito-errorprone" w "5.14.2"
    val inline = "org.mockito" d "mockito-inline" w "5.2.0"
    val junit_jupiter = "org.mockito" d "mockito-junit-jupiter" w "5.14.2"
    object Kotlin {
      val mockito_kotlin = "org.mockito.kotlin" d "mockito-kotlin" w "5.4.0"
    }
  }
  object Robolectric {
    val robolectric = "org.robolectric" d "robolectric" w "4.14.1"
  }
  object Slf4j {
    val api = "org.slf4j" d "slf4j-api" w "2.0.16" w "2.1.0-alpha1"
    val simple = "org.slf4j" d "slf4j-simple" w "2.0.16" w "2.1.0-alpha1"
  }
  object SpekFramework {
    object Spek2 {
      val spek_dsl_js = "org.spekframework.spek2" d "spek-dsl-js" w "2.0.19"
      val spek_dsl_jvm = "org.spekframework.spek2" d "spek-dsl-jvm" w "2.0.19"
      val spek_dsl_metadata = "org.spekframework.spek2" d "spek-dsl-metadata" w "2.0.16"
      val spek_dsl_native_linux = "org.spekframework.spek2" d "spek-dsl-native-linux" w "2.0.19"
      val spek_dsl_native_macos = "org.spekframework.spek2" d "spek-dsl-native-macos" w "2.0.19"
      val spek_dsl_native_windows = "org.spekframework.spek2" d "spek-dsl-native-windows" w "2.0.19"
      val spek_runner_junit5 = "org.spekframework.spek2" d "spek-runner-junit5" w "2.0.19"
      val spek_runtime_jvm = "org.spekframework.spek2" d "spek-runtime-jvm" w "2.0.19"
      val spek_runtime_metadata = "org.spekframework.spek2" d "spek-runtime-metadata" w "2.0.16"
    }
  }
  object SpringFramework {
    object Amqp {
      val spring_rabbit_test = "org.springframework.amqp" d "spring-rabbit-test" w "3.2.0"
    }
    object Batch {
      val spring_batch_test = "org.springframework.batch" d "spring-batch-test" w "5.2.0"
    }
    object Boot {
      val spring_boot_configuration_processor = "org.springframework.boot" d "spring-boot-configuration-processor" w "3.4.0"
      val spring_boot_dependencies = "org.springframework.boot" d "spring-boot-dependencies" w "3.4.0"
      val spring_boot_devtools = "org.springframework.boot" d "spring-boot-devtools" w "3.4.0"
      val spring_boot_starter_activemq = "org.springframework.boot" d "spring-boot-starter-activemq" w "3.4.0"
      val spring_boot_starter_actuator = "org.springframework.boot" d "spring-boot-starter-actuator" w "3.4.0"
      val spring_boot_starter_amqp = "org.springframework.boot" d "spring-boot-starter-amqp" w "3.4.0"
      val spring_boot_starter_artemis = "org.springframework.boot" d "spring-boot-starter-artemis" w "3.4.0"
      val spring_boot_starter_batch = "org.springframework.boot" d "spring-boot-starter-batch" w "3.4.0"
      val spring_boot_starter_cache = "org.springframework.boot" d "spring-boot-starter-cache" w "3.4.0"
      val spring_boot_starter_data_cassandra = "org.springframework.boot" d "spring-boot-starter-data-cassandra" w "3.4.0"
      val spring_boot_starter_data_cassandra_reactive = "org.springframework.boot" d "spring-boot-starter-data-cassandra-reactive" w "3.4.0"
      val spring_boot_starter_data_couchbase = "org.springframework.boot" d "spring-boot-starter-data-couchbase" w "3.4.0"
      val spring_boot_starter_data_couchbase_reactive = "org.springframework.boot" d "spring-boot-starter-data-couchbase-reactive" w "3.4.0"
      val spring_boot_starter_data_elasticsearch = "org.springframework.boot" d "spring-boot-starter-data-elasticsearch" w "3.4.0"
      val spring_boot_starter_data_jdbc = "org.springframework.boot" d "spring-boot-starter-data-jdbc" w "3.4.0"
      val spring_boot_starter_data_jpa = "org.springframework.boot" d "spring-boot-starter-data-jpa" w "3.4.0"
      val spring_boot_starter_data_ldap = "org.springframework.boot" d "spring-boot-starter-data-ldap" w "3.4.0"
      val spring_boot_starter_data_mongodb = "org.springframework.boot" d "spring-boot-starter-data-mongodb" w "3.4.0"
      val spring_boot_starter_data_mongodb_reactive = "org.springframework.boot" d "spring-boot-starter-data-mongodb-reactive" w "3.4.0"
      val spring_boot_starter_data_neo4j = "org.springframework.boot" d "spring-boot-starter-data-neo4j" w "3.4.0"
      val spring_boot_starter_data_r2dbc = "org.springframework.boot" d "spring-boot-starter-data-r2dbc" w "3.4.0"
      val spring_boot_starter_data_redis = "org.springframework.boot" d "spring-boot-starter-data-redis" w "3.4.0"
      val spring_boot_starter_data_redis_reactive = "org.springframework.boot" d "spring-boot-starter-data-redis-reactive" w "3.4.0"
      val spring_boot_starter_data_rest = "org.springframework.boot" d "spring-boot-starter-data-rest" w "3.4.0"
      val spring_boot_starter_data_solr = "org.springframework.boot" d "spring-boot-starter-data-solr" w "2.4.13"
      val spring_boot_starter_freemarker = "org.springframework.boot" d "spring-boot-starter-freemarker" w "3.4.0"
      val spring_boot_starter_groovy_templates = "org.springframework.boot" d "spring-boot-starter-groovy-templates" w "3.4.0"
      val spring_boot_starter_hateoas = "org.springframework.boot" d "spring-boot-starter-hateoas" w "3.4.0"
      val spring_boot_starter_integration = "org.springframework.boot" d "spring-boot-starter-integration" w "3.4.0"
      val spring_boot_starter_jdbc = "org.springframework.boot" d "spring-boot-starter-jdbc" w "3.4.0"
      val spring_boot_starter_jersey = "org.springframework.boot" d "spring-boot-starter-jersey" w "3.4.0"
      val spring_boot_starter_jooq = "org.springframework.boot" d "spring-boot-starter-jooq" w "3.4.0"
      val spring_boot_starter_mail = "org.springframework.boot" d "spring-boot-starter-mail" w "3.4.0"
      val spring_boot_starter_mustache = "org.springframework.boot" d "spring-boot-starter-mustache" w "3.4.0"
      val spring_boot_starter_oauth2_client = "org.springframework.boot" d "spring-boot-starter-oauth2-client" w "3.4.0"
      val spring_boot_starter_oauth2_resource_server = "org.springframework.boot" d "spring-boot-starter-oauth2-resource-server" w "3.4.0"
      val spring_boot_starter_quartz = "org.springframework.boot" d "spring-boot-starter-quartz" w "3.4.0"
      val spring_boot_starter_rsocket = "org.springframework.boot" d "spring-boot-starter-rsocket" w "3.4.0"
      val spring_boot_starter_security = "org.springframework.boot" d "spring-boot-starter-security" w "3.4.0"
      val spring_boot_starter_test = "org.springframework.boot" d "spring-boot-starter-test" w "3.4.0"
      val spring_boot_starter_thymeleaf = "org.springframework.boot" d "spring-boot-starter-thymeleaf" w "3.4.0"
      val spring_boot_starter_validation = "org.springframework.boot" d "spring-boot-starter-validation" w "3.4.0"
      val spring_boot_starter_web = "org.springframework.boot" d "spring-boot-starter-web" w "3.4.0"
      val spring_boot_starter_web_services = "org.springframework.boot" d "spring-boot-starter-web-services" w "3.4.0"
      val spring_boot_starter_webflux = "org.springframework.boot" d "spring-boot-starter-webflux" w "3.4.0"
      val spring_boot_starter_websocket = "org.springframework.boot" d "spring-boot-starter-websocket" w "3.4.0"
    }
    object Cloud {
      val spring_cloud_bus = "org.springframework.cloud" d "spring-cloud-bus" w "4.1.2"
      val spring_cloud_cloudfoundry_discovery = "org.springframework.cloud" d "spring-cloud-cloudfoundry-discovery" w "3.1.4"
      val spring_cloud_config_server = "org.springframework.cloud" d "spring-cloud-config-server" w "4.1.3"
      val spring_cloud_dependencies = "org.springframework.cloud" d "spring-cloud-dependencies" w "2023.0.3"
      val spring_cloud_function_web = "org.springframework.cloud" d "spring-cloud-function-web" w "4.1.3"
      val spring_cloud_gcp_starter = "org.springframework.cloud" d "spring-cloud-gcp-starter" w "1.2.8.RELEASE"
      val spring_cloud_gcp_starter_pubsub = "org.springframework.cloud" d "spring-cloud-gcp-starter-pubsub" w "1.2.8.RELEASE"
      val spring_cloud_gcp_starter_storage = "org.springframework.cloud" d "spring-cloud-gcp-starter-storage" w "1.2.8.RELEASE"
      val spring_cloud_starter = "org.springframework.cloud" d "spring-cloud-starter" w "4.1.4"
      val spring_cloud_starter_aws = "org.springframework.cloud" d "spring-cloud-starter-aws" w "2.2.6.RELEASE"
      val spring_cloud_starter_aws_jdbc = "org.springframework.cloud" d "spring-cloud-starter-aws-jdbc" w "2.2.6.RELEASE"
      val spring_cloud_starter_aws_messaging = "org.springframework.cloud" d "spring-cloud-starter-aws-messaging" w "2.2.6.RELEASE"
      val spring_cloud_starter_circuitbreaker_reactor_resilience4j = "org.springframework.cloud" d "spring-cloud-starter-circuitbreaker-reactor-resilience4j" w "3.1.2"
      val spring_cloud_starter_config = "org.springframework.cloud" d "spring-cloud-starter-config" w "4.1.3"
      val spring_cloud_starter_consul_config = "org.springframework.cloud" d "spring-cloud-starter-consul-config" w "4.1.2"
      val spring_cloud_starter_consul_discovery = "org.springframework.cloud" d "spring-cloud-starter-consul-discovery" w "4.1.2"
      val spring_cloud_starter_contract_stub_runner = "org.springframework.cloud" d "spring-cloud-starter-contract-stub-runner" w "4.1.4"
      val spring_cloud_starter_contract_verifier = "org.springframework.cloud" d "spring-cloud-starter-contract-verifier" w "4.1.4"
      val spring_cloud_starter_gateway = "org.springframework.cloud" d "spring-cloud-starter-gateway" w "4.1.5"
      val spring_cloud_starter_loadbalancer = "org.springframework.cloud" d "spring-cloud-starter-loadbalancer" w "4.1.4"
      val spring_cloud_starter_netflix_eureka_client = "org.springframework.cloud" d "spring-cloud-starter-netflix-eureka-client" w "4.1.3"
      val spring_cloud_starter_netflix_eureka_server = "org.springframework.cloud" d "spring-cloud-starter-netflix-eureka-server" w "4.1.3"
      val spring_cloud_starter_netflix_hystrix = "org.springframework.cloud" d "spring-cloud-starter-netflix-hystrix" w "2.2.10.RELEASE"
      val spring_cloud_starter_netflix_hystrix_dashboard = "org.springframework.cloud" d "spring-cloud-starter-netflix-hystrix-dashboard" w "2.2.10.RELEASE"
      val spring_cloud_starter_netflix_ribbon = "org.springframework.cloud" d "spring-cloud-starter-netflix-ribbon" w "2.2.10.RELEASE"
      val spring_cloud_starter_netflix_turbine = "org.springframework.cloud" d "spring-cloud-starter-netflix-turbine" w "2.2.10.RELEASE"
      val spring_cloud_starter_netflix_turbine_stream = "org.springframework.cloud" d "spring-cloud-starter-netflix-turbine-stream" w "2.2.10.RELEASE"
      val spring_cloud_starter_netflix_zuul = "org.springframework.cloud" d "spring-cloud-starter-netflix-zuul" w "2.2.10.RELEASE"
      val spring_cloud_starter_oauth2 = "org.springframework.cloud" d "spring-cloud-starter-oauth2" w "2.2.5.RELEASE"
      val spring_cloud_starter_open_service_broker = "org.springframework.cloud" d "spring-cloud-starter-open-service-broker" w "4.3.1"
      val spring_cloud_starter_openfeign = "org.springframework.cloud" d "spring-cloud-starter-openfeign" w "4.1.3"
      val spring_cloud_starter_security = "org.springframework.cloud" d "spring-cloud-starter-security" w "2.2.5.RELEASE"
      val spring_cloud_starter_sleuth = "org.springframework.cloud" d "spring-cloud-starter-sleuth" w "3.1.11"
      val spring_cloud_starter_task = "org.springframework.cloud" d "spring-cloud-starter-task" w "3.1.2"
      val spring_cloud_starter_vault_config = "org.springframework.cloud" d "spring-cloud-starter-vault-config" w "4.1.3"
      val spring_cloud_starter_zipkin = "org.springframework.cloud" d "spring-cloud-starter-zipkin" w "2.2.8.RELEASE"
      val spring_cloud_starter_zookeeper_config = "org.springframework.cloud" d "spring-cloud-starter-zookeeper-config" w "4.1.2"
      val spring_cloud_starter_zookeeper_discovery = "org.springframework.cloud" d "spring-cloud-starter-zookeeper-discovery" w "4.1.2"
      val spring_cloud_stream = "org.springframework.cloud" d "spring-cloud-stream" w "4.1.3"
      val spring_cloud_stream_binder_kafka = "org.springframework.cloud" d "spring-cloud-stream-binder-kafka" w "4.1.3"
      val spring_cloud_stream_binder_kafka_streams = "org.springframework.cloud" d "spring-cloud-stream-binder-kafka-streams" w "4.1.3"
      val spring_cloud_stream_binder_rabbit = "org.springframework.cloud" d "spring-cloud-stream-binder-rabbit" w "4.1.3"
    }
    object Data {
      val spring_data_rest_hal_explorer = "org.springframework.data" d "spring-data-rest-hal-explorer" w "4.4.0"
    }
    object Geode {
      val spring_geode_bom = "org.springframework.geode" d "spring-geode-bom" w "1.7.5"
      val spring_geode_starter = "org.springframework.geode" d "spring-geode-starter" w "1.7.5"
    }
    object Integration {
      val spring_integration_amqp = "org.springframework.integration" d "spring-integration-amqp" w "6.4.0"
      val spring_integration_gemfire = "org.springframework.integration" d "spring-integration-gemfire" w "5.5.20"
      val spring_integration_jdbc = "org.springframework.integration" d "spring-integration-jdbc" w "6.4.0"
      val spring_integration_jms = "org.springframework.integration" d "spring-integration-jms" w "6.4.0"
      val spring_integration_jpa = "org.springframework.integration" d "spring-integration-jpa" w "6.4.0"
      val spring_integration_kafka = "org.springframework.integration" d "spring-integration-kafka" w "6.4.0"
      val spring_integration_mail = "org.springframework.integration" d "spring-integration-mail" w "6.4.0"
      val spring_integration_mongodb = "org.springframework.integration" d "spring-integration-mongodb" w "6.4.0"
      val spring_integration_r2dbc = "org.springframework.integration" d "spring-integration-r2dbc" w "6.4.0"
      val spring_integration_redis = "org.springframework.integration" d "spring-integration-redis" w "6.4.0"
      val spring_integration_rsocket = "org.springframework.integration" d "spring-integration-rsocket" w "6.4.0"
      val spring_integration_security = "org.springframework.integration" d "spring-integration-security" w "6.2.11"
      val spring_integration_stomp = "org.springframework.integration" d "spring-integration-stomp" w "6.4.0"
      val spring_integration_test = "org.springframework.integration" d "spring-integration-test" w "6.4.0"
      val spring_integration_webflux = "org.springframework.integration" d "spring-integration-webflux" w "6.4.0"
      val spring_integration_websocket = "org.springframework.integration" d "spring-integration-websocket" w "6.4.0"
      val spring_integration_ws = "org.springframework.integration" d "spring-integration-ws" w "6.4.0"
    }
    object Kafka {
      val spring_kafka = "org.springframework.kafka" d "spring-kafka" w "3.3.0"
      val spring_kafka_test = "org.springframework.kafka" d "spring-kafka-test" w "3.3.0"
    }
    object Restdocs {
      val spring_restdocs_webtestclient = "org.springframework.restdocs" d "spring-restdocs-webtestclient" w "3.0.3"
    }
    object Security {
      val spring_security_messaging = "org.springframework.security" d "spring-security-messaging" w "6.4.1"
      val spring_security_rsocket = "org.springframework.security" d "spring-security-rsocket" w "6.4.1"
      val spring_security_test = "org.springframework.security" d "spring-security-test" w "6.4.1"
    }
    object Session {
      val spring_session_data_redis = "org.springframework.session" d "spring-session-data-redis" w "3.4.0"
      val spring_session_jdbc = "org.springframework.session" d "spring-session-jdbc" w "3.4.0"
    }
  }
}
object Pl {
  object MarekLangiewicz {
    val abcdk = "pl.mareklangiewicz" d "abcdk" w "0.0.27"
    val abcdk_js = "pl.mareklangiewicz" d "abcdk-js" w "0.0.27"
    val abcdk_jvm = "pl.mareklangiewicz" d "abcdk-jvm" w "0.0.27"
    val abcdk_linuxx64 = "pl.mareklangiewicz" d "abcdk-linuxx64" w "0.0.27"
    val kground = "pl.mareklangiewicz" d "kground" w "0.1.03"
    val kground_io = "pl.mareklangiewicz" d "kground-io" w "0.1.03"
    val kground_io_js = "pl.mareklangiewicz" d "kground-io-js" w "0.1.03"
    val kground_io_jvm = "pl.mareklangiewicz" d "kground-io-jvm" w "0.1.03"
    val kground_io_linuxx64 = "pl.mareklangiewicz" d "kground-io-linuxx64" w "0.1.03"
    val kground_js = "pl.mareklangiewicz" d "kground-js" w "0.1.03"
    val kground_jvm = "pl.mareklangiewicz" d "kground-jvm" w "0.1.03"
    val kground_linuxx64 = "pl.mareklangiewicz" d "kground-linuxx64" w "0.1.03"
    val kgroundx = "pl.mareklangiewicz" d "kgroundx" w "0.1.03"
    val kgroundx_experiments = "pl.mareklangiewicz" d "kgroundx-experiments" w "0.1.03"
    val kgroundx_experiments_jvm = "pl.mareklangiewicz" d "kgroundx-experiments-jvm" w "0.1.03"
    val kgroundx_io = "pl.mareklangiewicz" d "kgroundx-io" w "0.1.03"
    val kgroundx_io_js = "pl.mareklangiewicz" d "kgroundx-io-js" w "0.1.03"
    val kgroundx_io_jvm = "pl.mareklangiewicz" d "kgroundx-io-jvm" w "0.1.03"
    val kgroundx_io_linuxx64 = "pl.mareklangiewicz" d "kgroundx-io-linuxx64" w "0.1.03"
    val kgroundx_js = "pl.mareklangiewicz" d "kgroundx-js" w "0.1.03"
    val kgroundx_jupyter = "pl.mareklangiewicz" d "kgroundx-jupyter" w "0.1.03"
    val kgroundx_jupyter_jvm = "pl.mareklangiewicz" d "kgroundx-jupyter-jvm" w "0.1.03"
    val kgroundx_jvm = "pl.mareklangiewicz" d "kgroundx-jvm" w "0.1.03"
    val kgroundx_linuxx64 = "pl.mareklangiewicz" d "kgroundx-linuxx64" w "0.1.03"
    val kgroundx_maintenance = "pl.mareklangiewicz" d "kgroundx-maintenance" w "0.1.03"
    val kgroundx_maintenance_jvm = "pl.mareklangiewicz" d "kgroundx-maintenance-jvm" w "0.1.03"
    val kgroundx_workflows = "pl.mareklangiewicz" d "kgroundx-workflows" w "0.1.03"
    val kgroundx_workflows_jvm = "pl.mareklangiewicz" d "kgroundx-workflows-jvm" w "0.1.03"
    val kommand_line = "pl.mareklangiewicz" d "kommand-line" w "0.1.03"
    val kommand_line_js = "pl.mareklangiewicz" d "kommand-line-js" w "0.1.03"
    val kommand_line_jvm = "pl.mareklangiewicz" d "kommand-line-jvm" w "0.1.03"
    val kommand_line_linuxx64 = "pl.mareklangiewicz" d "kommand-line-linuxx64" w "0.1.03"
    val kommand_samples = "pl.mareklangiewicz" d "kommand-samples" w "0.1.03"
    val kommand_samples_js = "pl.mareklangiewicz" d "kommand-samples-js" w "0.1.03"
    val kommand_samples_jvm = "pl.mareklangiewicz" d "kommand-samples-jvm" w "0.1.03"
    val kommand_samples_linuxx64 = "pl.mareklangiewicz" d "kommand-samples-linuxx64" w "0.1.03"
    val rxmock = "pl.mareklangiewicz" d "rxmock" w "0.0.24"
    val rxmock_jvm = "pl.mareklangiewicz" d "rxmock-jvm" w "0.0.24"
    val smokk = "pl.mareklangiewicz" d "smokk" w "0.0.09"
    val smokk_js = "pl.mareklangiewicz" d "smokk-js" w "0.0.09"
    val smokk_jvm = "pl.mareklangiewicz" d "smokk-jvm" w "0.0.09"
    val smokk_linuxx64 = "pl.mareklangiewicz" d "smokk-linuxx64" w "0.0.09"
    val smokkx = "pl.mareklangiewicz" d "smokkx" w "0.0.09"
    val smokkx_js = "pl.mareklangiewicz" d "smokkx-js" w "0.0.09"
    val smokkx_jvm = "pl.mareklangiewicz" d "smokkx-jvm" w "0.0.09"
    val smokkx_linuxx64 = "pl.mareklangiewicz" d "smokkx-linuxx64" w "0.0.09"
    val tuplek = "pl.mareklangiewicz" d "tuplek" w "0.0.19"
    val tuplek_js = "pl.mareklangiewicz" d "tuplek-js" w "0.0.19"
    val tuplek_jvm = "pl.mareklangiewicz" d "tuplek-jvm" w "0.0.19"
    val tuplek_linuxx64 = "pl.mareklangiewicz" d "tuplek-linuxx64" w "0.0.19"
    val upue = "pl.mareklangiewicz" d "upue" w "0.0.20"
    val upue_js = "pl.mareklangiewicz" d "upue-js" w "0.0.20"
    val upue_jvm = "pl.mareklangiewicz" d "upue-jvm" w "0.0.20"
    val upue_linuxx64 = "pl.mareklangiewicz" d "upue-linuxx64" w "0.0.20"
    val uspek = "pl.mareklangiewicz" d "uspek" w "0.0.38"
    val uspek_js = "pl.mareklangiewicz" d "uspek-js" w "0.0.38"
    val uspek_jvm = "pl.mareklangiewicz" d "uspek-jvm" w "0.0.38"
    val uspek_linuxx64 = "pl.mareklangiewicz" d "uspek-linuxx64" w "0.0.38"
    val uspekx = "pl.mareklangiewicz" d "uspekx" w "0.0.38"
    val uspekx_js = "pl.mareklangiewicz" d "uspekx-js" w "0.0.38"
    val uspekx_junit4 = "pl.mareklangiewicz" d "uspekx-junit4" w "0.0.38"
    val uspekx_junit4_jvm = "pl.mareklangiewicz" d "uspekx-junit4-jvm" w "0.0.38"
    val uspekx_junit5 = "pl.mareklangiewicz" d "uspekx-junit5" w "0.0.38"
    val uspekx_junit5_jvm = "pl.mareklangiewicz" d "uspekx-junit5-jvm" w "0.0.38"
    val uspekx_jvm = "pl.mareklangiewicz" d "uspekx-jvm" w "0.0.38"
    val uspekx_linuxx64 = "pl.mareklangiewicz" d "uspekx-linuxx64" w "0.0.38"
    val uwidgets = "pl.mareklangiewicz" d "uwidgets" w "0.0.38"
    val uwidgets_demo = "pl.mareklangiewicz" d "uwidgets-demo" w "0.0.38"
    val uwidgets_demo_js = "pl.mareklangiewicz" d "uwidgets-demo-js" w "0.0.38"
    val uwidgets_demo_jvm = "pl.mareklangiewicz" d "uwidgets-demo-jvm" w "0.0.38"
    val uwidgets_js = "pl.mareklangiewicz" d "uwidgets-js" w "0.0.38"
    val uwidgets_jvm = "pl.mareklangiewicz" d "uwidgets-jvm" w "0.0.38"
  }
}

// endregion [[Deps Generated]]
// @formatter:on
