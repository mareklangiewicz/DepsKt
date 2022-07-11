@file:Suppress("PackageDirectoryMismatch", "unused", "MemberVisibilityCanBePrivate")

package pl.mareklangiewicz.deps

import libs
import pl.mareklangiewicz.defaults.v

/**
 * Common dependencies versions for java/kotlin/android projects
 *
 * @see <a href="https://github.com/langara/deps.kt">https://github.com/langara/deps.kt</a>
 */
object Vers {
    val kotlin14 = v(1, 4, 32)
    val kotlin15 = v(1, 5, 31)
    val kotlin16 = v(1, 6, 21)
    val kotlin17 = v(1,7,0, patchLength = 1)
    val kotlin = kotlin17
    // https://kotlinlang.org/docs/releases.html#release-details
    // https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md
    // https://github.com/JetBrains/kotlin/releases
    // compatibility with compose:
    //   https://developer.android.com/jetpack/androidx/releases/compose-kotlin
    //   https://androidx.dev/storage/compose-compiler/repository

    const val defaultJvm = "11" // I had terrible issues with "16" (andro compose project)

    const val kotlinxDateTime = "0.3.2"
    // https://github.com/Kotlin/kotlinx-datetime

    const val kotlinxCoroutines = "1.6.0"
    // https://github.com/Kotlin/kotlinx.coroutines/releases

    const val kotlinxSerialization = "1.3.2"
    // https://github.com/Kotlin/kotlinx.serialization/releases

    const val kotlinxAtomicFu = "0.17.1"
    // https://github.com/Kotlin/kotlinx.atomicfu/releases

    const val kotlinxHtml = "0.7.5"
    // https://github.com/Kotlin/kotlinx.html/releases

    const val kotlinxNodeJs = "0.0.7"
    // https://github.com/Kotlin/kotlinx-nodejs

    // just a reference - not useful in typical cases
    const val gradle5 = "5.6.4"
    const val gradle6 = "6.8.3"
    const val gradle7 = "7.5-rc-4"
    const val gradle = gradle7
    // https://gradle.org/releases/
    // https://services.gradle.org/versions
    // https://services.gradle.org/versions/current
    // https://services.gradle.org/versions/release-candidate

    const val composeJbMain = "1.1.1"
    const val composeJbEdge = "1.2.0-alpha01-dev741"
    const val composeJb = composeJbEdge

    // https://github.com/JetBrains/compose-jb
    // https://github.com/JetBrains/compose-jb/releases
    // https://github.com/JetBrains/compose-jb/blob/master/CHANGELOG.md


    const val composeAndroidMain = "1.2.0-rc03"
    const val composeAndroidEdge = "1.3.0-alpha01"
    const val composeAndroid = composeAndroidEdge
    val composeAndroidCompiler = v(1, 2, 0, patchLength = 1)
        // https://android-developers.googleblog.com/2022/06/independent-versioning-of-Jetpack-Compose-libraries.html?utm_source=dlvr.it&utm_medium=twitter
    const val composeAndroidMaterial3 = "1.0.0-alpha14"
    // https://developer.android.com/jetpack/androidx/releases/compose

    const val googleAccompanist = "0.24.11-rc"
    const val googleAccompanistImage = "0.15.0"
    const val googleAccompanistPicasso = "0.6.2"
    // https://search.maven.org/search?q=g:com.google.accompanist
    // https://google.github.io/accompanist/

    const val androidGradlePlugin = "7.4.0-alpha08"
    // https://maven.google.com/web/index.html#com.android.tools.build:gradle
    // https://developer.android.com/studio/releases/gradle-plugin
    // https://google.github.io/android-gradle-dsl/

    @Deprecated("Use https://developer.android.com/studio/build/maven-publish-plugin")
    const val androidMavenGradlePlugin = "2.1" // https://github.com/dcendents/android-maven-gradle-plugin/releases
    const val nexusPublishGradlePlugin = "1.1.0" // https://github.com/gradle-nexus/publish-plugin/

    val dokkaGradlePlugin = kotlin // will it be synced with kotlin version in the future?

    const val androidSdkCompile = 32
    const val androidSdkTarget = androidSdkCompile
    const val androidSdkMin = 26
    // https://developer.android.com/about/dashboards/index.html
    // https://source.android.com/setup/start/build-numbers

    @Deprecated("Deprecated with android gradle plugin 3.0.0 or higher")
    const val androidBuildTools = "30.0.2"
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
    const val androidxConstraint2 = "2.1.3"
    const val androidxConstraint = androidxConstraint2

    const val androidxNavigation = "2.5.0-rc01"
    // https://developer.android.com/jetpack/androidx/releases/navigation

    const val androidxRecyclerview = "1.3.0-alpha01"
    const val androidxCardview = "1.0.0"
    const val androidMaterial = "1.5.0-alpha04"
    const val androidxAnnotation = "1.3.0-beta01"
    const val androidxPreference = "1.1.1"
    const val androidxBrowser = "1.4.0-rc01"
    const val androidxBrowserHelper = "2.3.0"
    // https://github.com/GoogleChrome/android-browser-helper

    const val androidxPercentLayout = "1.0.0"
    const val androidxFlexboxLayout = "3.0.0"
    // https://github.com/google/flexbox-layout/releases

    const val androidxLifecycle = "2.5.0-rc01"
    // https://developer.android.com/jetpack/androidx/releases/lifecycle

    const val androidxCamera = "1.1.0-rc01"
    const val androidxCameraExtensions = "1.0.0-alpha32"
    const val androidxCameraView = "1.0.0-alpha32"
    // https://developer.android.com/jetpack/androidx/releases/camera

    const val androidxRoom = "2.5.0-alpha01"
    // https://developer.android.com/jetpack/androidx/releases/room

    const val androidxTest = "1.4.1-alpha06"
    const val androidxTestRunner = "1.5.0-alpha03"
    const val androidxTestRules = androidxTest
    const val androidxTestExtTruth = "1.5.0-alpha06"
    const val androidxTestExtJUnit = "1.1.4-alpha06"
    // https://developer.android.com/jetpack/androidx/releases/test

    const val androidxEspresso = "3.5.0-alpha06"

    const val androidCommons = "0.0.24"
    // https://github.com/elpassion/android-commons/releases

    const val rxjava2 = "2.2.16"
    const val rxjava3 = "3.1.4"
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

    const val rxandroid = "3.0.0"
    // https://github.com/ReactiveX/RxAndroid/releases

    const val rxlifecycle = "4.0.2"
    // https://github.com/trello/RxLifecycle/releases

    const val retrofit = "2.9.0"
    // https://github.com/square/retrofit
    // https://github.com/square/retrofit/tags

    const val okhttp = "5.0.0-alpha.7"
    // https://github.com/square/okhttp
    // https://github.com/square/okhttp/tags

    const val okio = "3.1.0"
    // https://square.github.io/okio/changelog/
    // https://square.github.io/okio/#releases

    const val dbusJava = "3.3.1"
    // https://github.com/hypfvieh/dbus-java

    const val dbusKotlin = "0.0.08"
    // https://github.com/langara/dbus-kotlin

    const val javaWebsocket = "1.5.2"
    // https://mvnrepository.com/artifact/org.java-websocket/Java-WebSocket

    //    const val slf4jSimple = "1.7.30"
    const val slf4jSimple = "2.0.0-alpha7"
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple

    const val log4j2 = "2.17.2"
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

    const val junit4 = "4.13.2"
    // https://github.com/junit-team/junit4/releases

    const val junit5 = "5.8.2"
    // https://github.com/junit-team/junit5/releases

    const val googleTruth = "1.1.3"
    // https://github.com/google/truth/releases

    const val androidTestRunnerClass = "androidx.test.runner.AndroidJUnitRunner"
    // https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner.html

    const val realm = "10.9.0"
    // https://docs.mongodb.com/realm/sdk/android/install/

    const val ktor = "1.6.7"
    // https://maven.pkg.jetbrains.space/public/p/ktor/eap/io/ktor/ktor/
    // https://github.com/ktorio/ktor
    // https://github.com/ktorio/ktor/releases

    const val rsocket = "0.14.3"
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

    private const val kotlinJsWrappersPre = "pre.347"

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
