@file:Suppress("PackageDirectoryMismatch", "unused", "MemberVisibilityCanBePrivate")

package pl.mareklangiewicz.deps

@Deprecated("Use DepsNew")
object VersOld {

    // temporary hack until I remove a whole old Vers object
    private val Dep.v get() = ver?.ver ?: error("Can not find last version in $this")
    private val Dep.vStable get() = verStable?.ver ?: error("Can not find last stable version in $this")
    private val Ver?.v get() = this?.ver ?: error("Can not find version in $this")

    val kotlin = KotlinVer.v

    val defaultJvm = JvmDefaultVer

    val kotlinxDateTime = KotlinX.datetime.v

    val kotlinxCoroutines = KotlinX.coroutines_core.v

    val kotlinxSerialization = KotlinX.serialization_core.v

    val kotlinxAtomicFu = KotlinX.atomicfu_gradle_plugin.v

    val kotlinxHtml = KotlinX.html.v

    val kotlinxNodeJs = KotlinX.nodejs.v

    val gradle = GradleVer.ver

    val composeJbMain = Compose.gradle_plugin.v
    val composeJbEdge = ComposeEdgeGradlePlugin.v
    val composeJb = composeJbEdge

    val composeAndroidStableBoM = ComposeAndro.bom.v
    val composeAndroidMain = AndroidX.Compose.Runtime.runtime.vStable
    val composeAndroidEdge = AndroidX.Compose.Runtime.runtime.v

    val composeAndroid = composeAndroidEdge
    val composeCompilerLatest = AndroidX.Compose.Compiler.compiler.v
    val composeCompilerStable = AndroidX.Compose.Compiler.compiler.vStable
    val composeCompiler = composeCompilerLatest

    val composeCompilerDev1720 = "1.4.0-dev-k1.7.20-e49b3b6028b"
    val composeCompilerDev1721 = "1.4.0-dev-k1.7.21-d324f46b7bd"
    val composeCompilerDev180Beta = "1.4.0-dev-k1.8.0-Beta-73ea385313b"
    val composeCompilerDev180RC = "1.4.0-dev-k1.8.0-RC-4c1865595ed"
    val composeCompilerDev1800 = "1.4.0-dev-k1.8.0-33c0ad36f83"
    val composeCompilerDev1810 = "1.4.1-dev-k1.8.10-c312d77f4cb"
    val composeCompilerDev1820Beta = "1.4.3-dev-k1.8.20-Beta-c5841510cbf"
    val composeCompilerDev1820RC = "1.4.4-dev-k1.8.20-RC-88d9f3a8232"
    val composeCompilerDev1820 = "1.4.4-dev-k1.8.20-f6ae19e64ff"

    val composeAndroidMaterial3Stable = AndroidX.Compose.Material3.material3.vStable
    val composeAndroidMaterial3Latest = AndroidX.Compose.Material3.material3.v
    val composeAndroidMaterial3 = composeAndroidMaterial3Latest

    val googleAccompanist = GoogleAccompanist.placeholder.v
        // kinda incorrect (taking ver from "placeholder" artifact) because there is no one common version
        // see: https://google.github.io/accompanist/

    val googleAccompanistImage = GoogleAccompanist.imageloading_core.v

    val googleAccompanistPicasso = GoogleAccompanist.picasso.v

    val androidGradlePlugin = GradleAndroPluginVer.ver

    @Deprecated("Use GradleAndroPluginVer https://developer.android.com/build/publish-library")
    val androidMavenGradlePlugin = GradleAndroMavenPluginVer.ver
    val nexusPublishGradlePlugin = GradleNexusPublishPluginVer.ver

    val dokkaGradlePlugin = GradleDokkaPluginVer.ver

    val osackyDoctorPlugin = GradleOsackyDoctorPluginVer.ver

    val androidSdkCompile = AndroSdkCompileVer
    val androidSdkTarget = AndroSdkTargetVer
    val androidSdkMin = AndroSdkMinVer

    @Deprecated("Deprecated with android gradle plugin 3.0.0 or higher")
    val androidBuildTools = AndroBuildToolsVer.ver

    @Deprecated("Use androidx")
    val androidSupport = AndroSupportLibraryVer.ver

    val androidxCore = AndroidX.Core.core.v

    val androidxActivity = AndroidX.Activity.activity.v

    val androidxAppcompat = AndroidX.AppCompat.appcompat.v

    val androidxConstraint1 = "1.1.3"

    val androidxConstraint2 = AndroidX.ConstraintLayout.constraintlayout.v
    val androidxConstraint = androidxConstraint2

    val androidxNavigation = AndroidX.Navigation.common.v

    val androidxRecyclerview = AndroidX.RecyclerView.recyclerview.v
    val androidxCardview = AndroidX.CardView.cardview.v

    val androidMaterial = Com.Google.Android.Material.material.v

    val androidxAnnotation = AndroidX.Annotation.annotation.v
    val androidxPreference = AndroidX.Preference.preference.v
    val androidxBrowser = AndroidX.Browser.browser.v
    val androidxBrowserHelper = Com.Google.AndroidBrowserHelper.androidbrowserhelper.v

    val androidxPercentLayout = AndroidX.PercentLayout.percentlayout.v
    val androidxFlexboxLayout = Com.Google.Android.Flexbox.flexbox.v

    val androidxLifecycle = AndroidX.Lifecycle.common.v

    val androidxCamera = AndroidX.Camera.core.v

    val androidxRoom = AndroidX.Room.common.v

    val androidxAutofill = AndroidX.AutoFill.autofill.v

    val androidxTestCore = AndroidX.Test.core.v
    val androidxTestAnnotation = "1.1.0-alpha01"
    val androidxTestRunner = AndroidX.Test.runner.v
    val androidxTestRules = AndroidX.Test.rules.v
    val androidxTestExtTruth = AndroidX.Test.Ext.truth.v
    val androidxTestExtJUnit = AndroidX.Test.Ext.junit.v
    val androidxEspresso = AndroidX.Test.Espresso.core.v

    val androidCommons = "0.0.24" // https://github.com/elpassion/android-commons/releases

    val rxjava2 = Io.ReactiveX.RxJava2.rxjava.v
    val rxjava3 = Io.ReactiveX.RxJava3.rxjava.v

    val rxkotlin = Io.ReactiveX.RxJava3.rxkotlin.v

    val rxbinding = Com.JakeWharton.RxBinding4.rxbinding.v

    val rxrelay = Com.JakeWharton.RxRelay3.rxrelay.v

    val rxandroid = Io.ReactiveX.RxJava3.rxandroid.v

    val rxlifecycle = "4.0.2" // https://github.com/trello/RxLifecycle/releases

    val retrofit = Com.SquareUp.Retrofit2.retrofit.v

    val okhttp = Com.SquareUp.Okhttp3.okhttp.v

    val okio = Com.SquareUp.Okio.okio.v


    // TODO: add to DepsNew:

    val dbusJava = "4.3.0" // https://github.com/hypfvieh/dbus-java
    val dbusKotlin = "0.0.08" // https://github.com/langara/dbus-kotlin
    val javaWebsocket = "1.5.3" // https://mvnrepository.com/artifact/org.java-websocket/Java-WebSocket
    val slf4jSimple = "2.0.7" // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    val log4j2 = "2.20.0" // http://logging.apache.org/log4j/2.x/maven-artifacts.html
    val firebaseGitlive = "1.7.2" // https://github.com/GitLiveApp/firebase-kotlin-sdk
    val firebaseAdmin = "9.1.1" // https://firebase.google.com/docs/admin/setup
    val firebaseUiAuth = "8.0.2"
    // https://github.com/firebase/FirebaseUI-Android
    // https://github.com/firebase/FirebaseUI-Android/releases
    // https://firebase.google.com/docs/auth/android/firebaseui
    val googleCloudBoM = "26.12.0" // https://cloud.google.com/java/docs/bom
    val googleAuth = "1.16.1" // https://github.com/googleapis/google-auth-library-java
    val googleGuavaJre = "31.1-jre" // https://github.com/google/guava
    val googleGuavaAndroid = "31.1-android"
    @Deprecated("The functionality of this plugin has been integrated into org.gradlex.java-ecosystem-capabilities")
    val googleGuavaMissingMetadataPlugin = "31.1.1" // https://github.com/jjohannes/missing-metadata-guava
    val materialDialogs = "3.3.0" // https://github.com/afollestad/material-dialogs
    val docoptJava = "0.6.0.20150202"
    // https://mvnrepository.com/artifact/com.offbytwo/docopt
    // https://github.com/docopt/docopt.java



    val googleServicesPlugin = Com.Google.Gms.google_services.v

    val googlePlayServicesBase = Com.Google.Android.Gms.play_services_base.v

    val firebaseCrashlyticsPlugin = Com.Google.Firebase.crashlytics_gradle.v

    val firebaseAndroidBoM = Com.Google.Firebase.bom.v

    val picasso = Com.SquareUp.Picasso.picasso.v

    val leakcanary = Com.SquareUp.LeakCanary.android.v

    @Deprecated("This is very old")
    val paperwork = "1.2.7" // https://github.com/zsoltk/paperwork/releases

    val mockitoCore = Org.Mockito.core.v

    val mockitoKotlin2nhaarman = Com.Nhaarman.MockitoKotlin2.mockito_kotlin.v
        // last version with old package name: com.nhaarman.mockitokotlin2

    val mockitoKotlin2 = "2.2.11"
    val mockitoKotlin3 = "3.2.0"
    val mockitoKotlin4 = Org.Mockito.Kotlin.mockito_kotlin.v
    val mockitoKotlin = mockitoKotlin4

    val mockitoAndroid2 = "2.28.2"
    val mockitoAndroid3 = "3.12.4"
    val mockitoAndroid4 = "4.1.0"
    val mockitoAndroid5 = Org.Mockito.android.v
    val mockitoAndroid = mockitoAndroid5

    val robolectric = Org.Robolectric.robolectric.v

    val junit4 = JUnit.junit.v

    val junit5 = Org.JUnit.Jupiter.junit_jupiter.v

    val googleTruth = Com.Google.Truth.truth.v

    @Deprecated("Use androidx")
    val androidTestRunnerClass = "androidx.test.runner.AndroidJUnitRunner"
    // https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner.html

    val realm = Io.Realm.gradle_plugin.v

    val ktor = Io.Ktor.io.v

    val rsocket = Io.RSocket.Kotlin.rsocket_core.v

    val splitties = Com.Louiscad.Splitties.bundle.v

    @Deprecated("")
    val npmReact = "18.2.0"
    // https://github.com/facebook/react/blob/main/CHANGELOG.md

    @Deprecated("")
    val npmStyled = "5.3.9"
    // https://github.com/styled-components/styled-components

    // https://github.com/JetBrains/kotlin-wrappers
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-wrappers-bom
    // NOTE: syntax for mpp build files: implementation(project.dependencies.enforcedPlatform(deps.kotlinJsWrappersBoM))
    val kotlinJsWrappersBoM = Org.JetBrains.Kotlin_Wrappers.bom.v

    // https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-react/README.md
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-react
    @Deprecated("Use BoM")
    val kotlinJsWrappersReact = Org.JetBrains.Kotlin_Wrappers.kotlin_react.v

    // https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-react-dom/README.md
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-react-dom
    @Deprecated("Use BoM")
    val kotlinJsWrappersReactDom = Org.JetBrains.Kotlin_Wrappers.kotlin_react_dom.v

    // https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-styled/README.md
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-styled
    @Deprecated("Use BoM")
    val kotlinJsWrappersStyled = Org.JetBrains.Kotlin_Wrappers.kotlin_styled.v

    val smokk = Langiewicz.smokk.v
    val rxmock = Langiewicz.rxmock.v
    val abcdk = Langiewicz.abcdk.v
    val tuplek = Langiewicz.tuplek.v
    val uspek = Langiewicz.uspek.v
    val upue = Langiewicz.upue.v
    val kommandLine = Langiewicz.kommandline.v
    val templateMPP = Langiewicz.template_mpp_lib.v

    // FIXME_later: move this library to sonatype, then add to generated DepsNew
    val sandboxui = "0.0.5"
    // https://github.com/langara/sandboxui/releases

    // FIXME_later: move this library to sonatype, then add to generated DepsNew
    val recyclerui = "0.0.2"
    // https://github.com/langara/recyclerui/releases
}
