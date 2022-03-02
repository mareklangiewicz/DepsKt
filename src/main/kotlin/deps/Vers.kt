package pl.mareklangiewicz.deps

/**
 * Common dependencies versions for java/kotlin/android projects
 *
 * @see <a href="https://github.com/langara/deps.kt">https://github.com/langara/deps.kt</a>
 */
object Vers {
    const val kotlinMajor = 1
    const val kotlinMinor = 6
    const val kotlinPatch = 10
    const val kotlinSuffix = "" // with hyphen (like "-2")

    const val kotlin14 = "1.4.32"
    const val kotlin15 = "1.5.31"
    const val kotlin16 = "$kotlinMajor.$kotlinMinor.$kotlinPatch$kotlinSuffix"
    const val kotlin = kotlin16
    // https://kotlinlang.org/docs/releases.html#release-details
    // https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md
    // https://github.com/JetBrains/kotlin/releases

    const val defaultJvm = "11" // I had terrible issues with "16" (andro compose project)

    const val kotlinxDateTime = "0.3.2"
    // https://github.com/Kotlin/kotlinx-datetime

    const val kotlinxCoroutines = "1.6.0"
    // https://github.com/Kotlin/kotlinx.coroutines/releases

    const val kotlinxSerialization = "1.3.2"
    // https://github.com/Kotlin/kotlinx.serialization/releases

    const val kotlinxAtomicFu = "0.17.0"
    // https://github.com/Kotlin/kotlinx.atomicfu/releases

    const val kotlinxHtml = "0.7.2"
    // https://github.com/Kotlin/kotlinx.html/releases

    const val kotlinxNodeJs = "0.0.7"
    // https://github.com/Kotlin/kotlinx-nodejs

    // just a reference - not useful in typical cases
    const val gradle5 = "5.6.4"
    const val gradle6 = "6.8.3"
    const val gradle7 = "7.4"
    const val gradle = gradle7
    // https://gradle.org/releases/
    // https://services.gradle.org/versions
    // https://services.gradle.org/versions/current
    // https://services.gradle.org/versions/release-candidate

    const val composeDesktopVerMain = "1.0.1"
    const val composeDesktopVerEdge = "1.2.0-alpha01-dev602"
//    const val composeDesktopVerExperiment = "0.0.0-master-dev570"
    const val composeDesktop = composeDesktopVerEdge

    // https://github.com/JetBrains/compose-jb
    // https://github.com/JetBrains/compose-jb/releases
    // https://github.com/JetBrains/compose-jb/blob/master/CHANGELOG.md


    const val composeAndroid = "1.2.0-alpha03"
    const val composeAndroidCompiler = composeAndroid
    const val composeAndroidMaterial3 = "1.0.0-alpha05"
    // https://developer.android.com/jetpack/androidx/releases/compose

    const val googleAccompanist = "0.24.2-alpha"
    const val googleAccompanistImage = "0.15.0"
    const val googleAccompanistPicasso = "0.6.2"
    // https://search.maven.org/search?q=g:com.google.accompanist
    // https://google.github.io/accompanist/

    const val androidGradlePlugin = "7.3.0-alpha04"
    // https://maven.google.com/web/index.html#com.android.tools.build:gradle
    // https://developer.android.com/studio/releases/gradle-plugin
    // https://google.github.io/android-gradle-dsl/

    @Deprecated("Use https://developer.android.com/studio/build/maven-publish-plugin")
    const val androidMavenGradlePlugin = "2.1" // https://github.com/dcendents/android-maven-gradle-plugin/releases
    const val nexusPublishGradlePlugin = "1.1.0" // https://github.com/gradle-nexus/publish-plugin/

    const val dokkaGradlePlugin = kotlin // will it be synced with kotlin version in the future?

    const val androidCompileSdk = 31
    const val androidMinSdk = 26
    const val androidTargetSdk = androidCompileSdk
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
    const val androidxConstraint2 = "2.1.1"
    const val androidxConstraint = androidxConstraint2

    const val androidxRecyclerview = "1.3.0-alpha01"
    const val androidxCardview = "1.0.0"
    const val androidMaterial = "1.5.0-alpha04"
    const val androidxAnnotation = "1.3.0-beta01"
    const val androidxPreference = "1.1.1"
    const val androidxBrowser = "1.4.0-rc01"
    const val androidxBrowserHelper = "2.2.2"
    // https://github.com/GoogleChrome/android-browser-helper

    const val androidxPercentLayout = "1.0.0"
    const val androidxFlexboxLayout = "3.0.0"
    // https://github.com/google/flexbox-layout/releases

    const val androidxLifecycle = "2.4.0"
    // https://developer.android.com/jetpack/androidx/releases/lifecycle

    const val androidxCamera = "1.1.0-alpha12"
    const val androidxCameraExtensions = "1.0.0-alpha32"
    const val androidxCameraView = "1.0.0-alpha32"
    // https://developer.android.com/jetpack/androidx/releases/camera

    const val androidxRoom = "2.4.0-rc01"
    // https://developer.android.com/jetpack/androidx/releases/room

    const val androidxTest = "1.4.1-alpha03"
    const val androidxTestRunner = androidxTest
    const val androidxTestRules = androidxTest
    const val androidxTestExtTruth = "1.5.0-alpha03"
    const val androidxTestExtJUnit = "1.1.4-alpha03"
    // https://developer.android.com/jetpack/androidx/releases/test

    const val androidxEspresso = "3.4.0"

    const val androidCommons = "0.0.24"
    // https://github.com/elpassion/android-commons/releases

    const val rxjava2 = "2.2.16"
    const val rxjava3 = "3.1.3"
    // https://github.com/ReactiveX/RxJava/releases

    const val rxkotlin = "3.0.1"
    // https://github.com/ReactiveX/RxKotlin/releases

    const val rxbinding = "4.0.0"
    // https://github.com/JakeWharton/RxBinding
    // https://github.com/JakeWharton/RxBinding/releases
    // https://github.com/JakeWharton/RxBinding/blob/master/CHANGELOG.md

    const val rxrelay = "3.0.1"
    // https://github.com/JakeWharton/RxRelay/releases

    const val rxandroid = "3.0.0"
    // https://github.com/ReactiveX/RxAndroid/releases

    const val rxlifecycle = "4.0.2"
    // https://github.com/trello/RxLifecycle/releases

    const val retrofit = "2.9.0"
    // https://github.com/square/retrofit
    // https://github.com/square/retrofit/releases

    const val okhttp = "5.0.0-alpha.2"
    // https://github.com/square/okhttp
    // https://github.com/square/okhttp/releases

    const val okio = "3.0.0"
    // https://square.github.io/okio/changelog/
    // https://square.github.io/okio/#releases

    const val dbusJava = "3.3.1"
    // https://github.com/hypfvieh/dbus-java

    const val dbusKotlin = "0.0.08"
    // https://github.com/langara/dbus-kotlin

    const val javaWebsocket = "1.5.2"
    // https://mvnrepository.com/artifact/org.java-websocket/Java-WebSocket

    //    const val slf4jSimple = "1.7.30"
    const val slf4jSimple = "2.0.0-alpha5"
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple

    const val log4j2 = "2.16.0"
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

    const val npmReact = "17.0.2"
    // https://reactjs.org/versions

    const val npmStyled = "5.3.3"

    // https://github.com/JetBrains/kotlin-wrappers
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-wrappers-bom
    const val kotlinJsWrappersBoM = "0.0.1-pre.293-kotlin-$kotlin"

    // https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-react/README.md
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-react
    @Deprecated("Use BoM")
    const val kotlinJsWrappersReact = "$npmReact-pre.293-kotlin-$kotlin"

    // https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-react-dom/README.md
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-react-dom
    @Suppress("DEPRECATION")
    @Deprecated("Use BoM")
    const val kotlinJsWrappersReactDom = kotlinJsWrappersReact

    // https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-styled/README.md
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-styled
    @Deprecated("Use BoM")
    const val kotlinJsWrappersStyled = "$npmStyled-pre.293-kotlin-$kotlin"



    const val tuplek = "0.0.04"
    // https://github.com/langara/tuplek/releases

    const val abcdk = "0.0.05"
    // https://github.com/langara/abcdk/releases

    const val rxmock = "0.0.2"
    // https://github.com/langara/rxmock/releases

    const val smokk = "0.0.4"
    // https://github.com/langara/smokk/releases

    const val uspek = "0.0.21"
    // https://github.com/langara/uspek/releases

    const val upue = "0.0.09"
    // https://github.com/langara/upue/releases

    const val kommandLine = "0.0.05"
    // https://github.com/langara/kommandline/releases

    const val sandboxui = "0.0.5"
    // https://github.com/langara/sandboxui/releases

    const val recyclerui = "0.0.2"
    // https://github.com/langara/recyclerui/releases
}
