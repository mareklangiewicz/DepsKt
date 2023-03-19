@file:Suppress("PackageDirectoryMismatch", "unused", "MemberVisibilityCanBePrivate")

package pl.mareklangiewicz.deps

import libs
import pl.mareklangiewicz.defaults.*

/**
 * Common dependencies versions for java/kotlin/android projects
 *
 * @see <a href="https://github.com/langara/deps.kt">https://github.com/langara/deps.kt</a>
 */
object Vers {
    val kotlin14 = v(1, 4, 32)
    val kotlin15 = v(1, 5, 31)
    val kotlin16 = v(1, 6, 21)
    val kotlin1720 = v(1, 7, 20)
    val kotlin17 = v(1, 7, 21)
    val kotlin1800 = v(1, 8, 0, patchLength = 1)
    val kotlin18 = v(1, 8, 10)
    val kotlin = kotlin18
    // https://kotlinlang.org/docs/releases.html#release-details
    // https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md
    // https://github.com/JetBrains/kotlin/releases
    // compatibility with compose:
    //   https://developer.android.com/jetpack/androidx/releases/compose-kotlin
    //   https://androidx.dev/storage/compose-compiler/repository
    //   https://github.com/JetBrains/compose-jb/blob/63846c63c0b7399340638de0645369dd3bb6ef1c/gradle-plugins/compose/src/main/kotlin/org/jetbrains/compose/ComposeCompilerCompatability.kt

    const val defaultJvm = "17" // I had terrible issues with "16" (andro compose project)

    const val kotlinxDateTime = "0.4.0"
    // https://github.com/Kotlin/kotlinx-datetime

    const val kotlinxCoroutines = "1.6.4"
    // https://github.com/Kotlin/kotlinx.coroutines/releases

    const val kotlinxSerialization = "1.5.0"
    // https://github.com/Kotlin/kotlinx.serialization/releases

    const val kotlinxAtomicFu = "0.20.0"
    // https://github.com/Kotlin/kotlinx.atomicfu/releases

    const val kotlinxHtml = "0.8.0"
    // https://github.com/Kotlin/kotlinx.html/releases

    const val kotlinxNodeJs = "0.0.7"
    // https://github.com/Kotlin/kotlinx-nodejs

    // just a reference - not useful in typical cases
    const val gradle5 = "5.6.4"
    const val gradle6 = "6.8.3"
    const val gradle7 = "7.6.1"
    const val gradle8 = "8.0.2"
    const val gradle = gradle8
    // https://gradle.org/releases/
    // https://services.gradle.org/versions
    // https://services.gradle.org/versions/current
    // https://services.gradle.org/versions/release-candidate

    const val composeJbMain = "1.3.1"
    const val composeJbEdge = "1.4.0-alpha01-dev977"
    const val composeJb = composeJbEdge

    // https://github.com/JetBrains/compose-jb
    // https://github.com/JetBrains/compose-jb/releases
    // https://github.com/JetBrains/compose-jb/blob/master/CHANGELOG.md
    // https://maven.pkg.jetbrains.space/public/p/compose/dev/org/jetbrains/compose/
    // https://maven.pkg.jetbrains.space/public/p/compose/dev/org/jetbrains/compose/compose-gradle-plugin/


    const val composeAndroidStableBoM = "2022.12.00" // I don't use it for now (stable usually too old for me)
    val composeAndroidMain = v(1, 3, 3, patchLength = 1)
    val composeAndroidEdge = v(1, 4, 0, patchLength = 1, suffix = "-beta02")
    // https://developer.android.com/jetpack/androidx/releases/compose
    val composeAndroid = composeAndroidEdge
    val composeCompilerStable = v(1, 4, 3, patchLength = 1)
    // val composeCompilerAlpha = v(1, 4, 0, patchLength = 1, suffix = "-alpha02")
    val composeCompilerDev1720 = "1.4.0-dev-k1.7.20-e49b3b6028b"
    val composeCompilerDev1721 = "1.4.0-dev-k1.7.21-d324f46b7bd"
    val composeCompilerDev180Beta = "1.4.0-dev-k1.8.0-Beta-73ea385313b"
    val composeCompilerDev180RC = "1.4.0-dev-k1.8.0-RC-4c1865595ed"
    val composeCompilerDev1800 = "1.4.0-dev-k1.8.0-33c0ad36f83"
    val composeCompilerDev1810 = "1.4.1-dev-k1.8.10-c312d77f4cb"
    val composeCompilerDev1820Beta = "1.4.3-dev-k1.8.20-Beta-c5841510cbf"
    val composeCompiler = composeCompilerStable
    // https://developer.android.com/jetpack/androidx/releases/compose-kotlin#pre-release_kotlin_compatibility
    // https://androidx.dev/storage/compose-compiler/repository
    // https://mvnrepository.com/artifact/org.jetbrains.compose.compiler/compiler

    val composeAndroidMaterial3Stable = v(1, 0, 1, patchLength = 1)
    val composeAndroidMaterial3Alpha = v(1, 1, 0, patchLength = 1, suffix = "-alpha07")
    val composeAndroidMaterial3 = composeAndroidMaterial3Alpha
    // https://developer.android.com/jetpack/androidx/releases/compose

    const val googleAccompanistForCompose13 = "0.28.0"
    const val googleAccompanistForCompose14 = "0.29.1-alpha"
    const val googleAccompanist = googleAccompanistForCompose14
        // https://google.github.io/accompanist/
        // https://central.sonatype.com/search?q=com.google.accompanist
    const val googleAccompanistImage = "0.15.0"
        // https://central.sonatype.com/search?q=accompanist-imageloading-core
        // https://central.sonatype.com/search?q=accompanist-coil
        // https://central.sonatype.com/search?q=accompanist-glide
    const val googleAccompanistPicasso = "0.6.2"
        // https://central.sonatype.com/search?q=accompanist-picasso

    const val androidGradlePlugin = "8.1.0-alpha07"
    // https://maven.google.com/web/index.html#com.android.tools.build:gradle
    // https://developer.android.com/studio/releases/gradle-plugin
    // https://google.github.io/android-gradle-dsl/

    @Deprecated("Use https://developer.android.com/studio/build/maven-publish-plugin")
    const val androidMavenGradlePlugin = "2.1" // https://github.com/dcendents/android-maven-gradle-plugin/releases
    const val nexusPublishGradlePlugin = "1.2.0" // https://github.com/gradle-nexus/publish-plugin/

    val dokkaGradlePlugin = kotlin // will it be synced with kotlin version in the future?

    val osackyDoctorPlugin = v(0, 8, 1, patchLength = 1)
    // https://plugins.gradle.org/plugin/com.osacky.doctor

    const val androidSdkCompile = 33
    const val androidSdkTarget = androidSdkCompile
    const val androidSdkMin = 26
    // https://developer.android.com/about/dashboards/index.html
    // https://source.android.com/setup/start/build-numbers

    @Deprecated("Deprecated with android gradle plugin 3.0.0 or higher")
    const val androidBuildTools = "33.0.1"
    // https://developer.android.com/studio/releases/build-tools.html

    @Deprecated("Use androidx")
    const val androidSupport = "28.0.0"
    // https://developer.android.com/topic/libraries/support-library/revisions.html

    // https://developer.android.com/jetpack/androidx/versions
    // https://dl.google.com/dl/android/maven2/index.html

    const val androidxCore = "1.7.0-rc01"

    const val androidxActivity = "1.4.0-rc01"

    const val androidxAppcompat = "1.4.0-beta01"

    const val androidxConstraint1 = "1.1.3"

    // https://developer.android.com/training/constraint-layout
    const val androidxConstraint2 = "2.2.0-alpha07"
    const val androidxConstraint = androidxConstraint2

    const val androidxNavigation = "2.6.0-alpha06"
    // https://developer.android.com/jetpack/androidx/releases/navigation

    const val androidxRecyclerview = "1.3.0-alpha01"
    const val androidxCardview = "1.0.0"
    const val androidMaterial = "1.5.0-alpha04"
    const val androidxAnnotation = "1.3.0-beta01"
    const val androidxPreference = "1.1.1"
    const val androidxBrowser = "1.4.0-rc01"
    const val androidxBrowserHelper = "2.4.0"
    // https://github.com/GoogleChrome/android-browser-helper

    const val androidxPercentLayout = "1.0.0"
    const val androidxFlexboxLayout = "3.0.0"
    // https://github.com/google/flexbox-layout/releases

    const val androidxLifecycle = "2.6.0-rc01"
    // https://developer.android.com/jetpack/androidx/releases/lifecycle

    const val androidxCamera = "1.3.0-alpha04"
    // https://developer.android.com/jetpack/androidx/releases/camera

    const val androidxRoom = "2.5.0"
    // https://developer.android.com/jetpack/androidx/releases/room

    const val androidxAutofill = "1.2.0-beta01"
    // https://developer.android.com/jetpack/androidx/releases/autofill

    const val androidxTestCore = "1.5.0"
    const val androidxTestAnnotation = "1.0.1"
    const val androidxTestRunner = "1.5.2"
    const val androidxTestRules = androidxTestCore
    const val androidxTestExtTruth = androidxTestCore
    const val androidxTestExtJUnit = "1.1.5"
    const val androidxEspresso = "3.5.1"
    // https://developer.android.com/jetpack/androidx/releases/test


    const val androidCommons = "0.0.24"
    // https://github.com/elpassion/android-commons/releases

    const val rxjava2 = "2.2.16"
    const val rxjava3 = "3.1.6"
    // https://github.com/ReactiveX/RxJava/releases

    const val rxkotlin = "3.0.1"
    // https://github.com/ReactiveX/RxKotlin/releases

    const val rxbinding = "4.0.0"
    // https://github.com/JakeWharton/RxBinding
    // https://github.com/JakeWharton/RxBinding/releases
    // https://github.com/JakeWharton/RxBinding/blob/master/CHANGELOG.md

    const val rxrelay = "3.0.1"
    // https://github.com/JakeWharton/RxRelay
    // https://github.com/JakeWharton/RxRelay/tags

    const val rxandroid = "3.0.2"
    // https://github.com/ReactiveX/RxAndroid/releases

    const val rxlifecycle = "4.0.2"
    // https://github.com/trello/RxLifecycle/releases

    const val retrofit = "2.9.0"
    // https://github.com/square/retrofit
    // https://github.com/square/retrofit/tags

    const val okhttp = "5.0.0-alpha.11"
    // https://github.com/square/okhttp
    // https://github.com/square/okhttp/tags

    const val okio = "3.3.0"
    // https://square.github.io/okio/changelog/
    // https://square.github.io/okio/#releases

    const val dbusJava = "4.2.1"
    // https://github.com/hypfvieh/dbus-java

    const val dbusKotlin = "0.0.08"
    // https://github.com/langara/dbus-kotlin

    const val javaWebsocket = "1.5.3"
    // https://mvnrepository.com/artifact/org.java-websocket/Java-WebSocket

    const val slf4jSimple = "2.0.6"
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple

    const val log4j2 = "2.19.0"
    // http://logging.apache.org/log4j/2.x/maven-artifacts.html

    const val googleServicesPlugin = "4.3.10"
    // https://developers.google.com/android/guides/google-services-plugin

    const val googlePlayServicesBase = "18.0.0"
    // https://developers.google.com/android/guides/setup
    // https://developers.google.com/android/guides/releases

    const val firebaseGitlive = "1.4.3"
    // https://github.com/GitLiveApp/firebase-kotlin-sdk

    const val firebaseCrashlyticsPlugin = "2.8.1"
    // https://firebase.google.com/docs/crashlytics/get-started?platform=android

    const val firebaseAdmin = "8.1.0"
    // https://firebase.google.com/docs/admin/setup

    const val firebaseAndroidBoM = "29.0.2"
    // https://firebase.google.com/support/release-notes/android
    // https://firebase.google.com/docs/android/setup#add-sdks

    const val firebaseUiAuth = "8.0.0"
    // https://github.com/firebase/FirebaseUI-Android
    // https://github.com/firebase/FirebaseUI-Android/releases
    // https://firebase.google.com/docs/auth/android/firebaseui

    const val googleCloudBoM = "24.0.0"
    // https://github.com/GoogleCloudPlatform/cloud-opensource-java/wiki/The-Google-Cloud-Platform-Libraries-BOM

    const val googleAuth = "1.3.0"
    // https://github.com/googleapis/google-auth-library-java

    const val googleGuavaJre = "31.0.1-jre"
    const val googleGuavaAndroid = "31.0.1-android"
    // https://github.com/google/guava

    const val googleGuavaMissingMetadataPlugin = "0.5"
    // https://github.com/jjohannes/missing-metadata-guava

    const val picasso = "2.8"
    // https://github.com/square/picasso
    // https://github.com/square/picasso/releases

    const val materialDialogs = "3.3.0"
    // https://github.com/afollestad/material-dialogs
    // https://github.com/afollestad/material-dialogs/releases

    const val leakcanary = "2.7"
    // https://github.com/square/leakcanary/releases

    const val paperwork = "1.2.7"
    // https://github.com/zsoltk/paperwork/releases

    const val mockitoCore2 = "2.28.2"
    const val mockitoCore3 = "3.12.4"
    const val mockitoCore4 = "4.1.0"
    const val mockitoCore = mockitoCore4
    // https://github.com/mockito/mockito/releases
    // https://search.maven.org/artifact/org.mockito/mockito-core

    const val mockitoKotlin2nhaarman = "2.2.0" // last version with old package name: com.nhaarman.mockitokotlin2
    const val mockitoKotlin2 = "2.2.11"
    const val mockitoKotlin3 = "3.2.0"
    const val mockitoKotlin4 = "4.0.0"
    const val mockitoKotlin = mockitoKotlin4
    // https://github.com/nhaarman/mockito-kotlin/releases

    const val mockitoAndroid2 = "2.28.2"
    const val mockitoAndroid3 = "3.12.4"
    const val mockitoAndroid4 = "4.1.0"
    const val mockitoAndroid = mockitoAndroid4
    // https://search.maven.org/artifact/org.mockito/mockito-android


    const val robolectric = "4.8"
    // https://robolectric.org/getting-started/

    const val junit4 = "4.13.2"
    // https://github.com/junit-team/junit4/releases

    const val junit5 = "5.9.1"
    // https://github.com/junit-team/junit5/releases

    const val googleTruth = "1.1.3"
    // https://github.com/google/truth/releases

    const val androidTestRunnerClass = "androidx.test.runner.AndroidJUnitRunner"
    // https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner.html

    const val realm = "10.11.1"
    // https://docs.mongodb.com/realm/sdk/android/install/

    const val ktor = "2.2.2"
    // https://maven.pkg.jetbrains.space/public/p/ktor/eap/io/ktor/ktor/
    // https://github.com/ktorio/ktor
    // https://github.com/ktorio/ktor/releases

    const val rsocket = "0.15.4"
    // https://github.com/rsocket/rsocket-kotlin
    // https://github.com/rsocket/rsocket-kotlin/releases

    const val splitties = "3.0.0"
    // https://github.com/LouisCAD/Splitties/releases
    // https://github.com/LouisCAD/Splitties

    const val docoptJava = "0.6.0.20150202"
    // https://mvnrepository.com/artifact/com.offbytwo/docopt
    // https://github.com/docopt/docopt.java

    const val npmReact = "18.2.0"
    // https://reactjs.org/versions

    const val npmStyled = "5.3.3"

    private const val kotlinJsWrappersPre = "pre.498"

    // https://github.com/JetBrains/kotlin-wrappers
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-wrappers-bom
    // NOTE: syntax for mpp build files: implementation(project.dependencies.enforcedPlatform(deps.kotlinJsWrappersBoM))
    val kotlinJsWrappersBoM = "1.0.0-$kotlinJsWrappersPre"

    // https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-react/README.md
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-react
    @Deprecated("Use BoM")
    val kotlinJsWrappersReact = "$npmReact-$kotlinJsWrappersPre"

    // https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-react-dom/README.md
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-react-dom
    @Suppress("DEPRECATION")
    @Deprecated("Use BoM")
    val kotlinJsWrappersReactDom = kotlinJsWrappersReact

    // https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-styled/README.md
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-styled
    @Deprecated("Use BoM")
    val kotlinJsWrappersStyled = "$npmStyled-$kotlinJsWrappersPre"


    // My libs - see details in LibsDetails.kt:
    // https://github.com/langara/deps.kt/blob/master/src/main/kotlin/deps/LibsDetails.kt
    // https://repo1.maven.org/maven2/pl/mareklangiewicz/


    val smokk = libs.SMokK
    val rxmock = libs.RxMock
    val abcdk = libs.AbcdK.version
    val tuplek = libs.TupleK.version
    val uspek = libs.USpek.version
    val upue = libs.UPue.version
    val kommandLine = libs.KommandLine.version
    val templateMPP = libs.TemplateMPP.version

    const val sandboxui = "0.0.5"
    // https://github.com/langara/sandboxui/releases

    const val recyclerui = "0.0.2"
    // https://github.com/langara/recyclerui/releases
}
