@file:Suppress("unused", "SpellCheckingInspection", "MemberVisibilityCanBePrivate")

/**
 * Common dependencies versions for java/kotlin/android projects
 *
 * @see <a href="https://github.com/langara/deps.kt">https://github.com/langara/deps.kt</a>
 */
object Vers {
    const val kotlinMajor = 1
    const val kotlinMinor = 4
    const val kotlinPatch = 31
//    const val kotlinPatch = 32 // compose compiler (1.0.0-alpha13) complains it wants kotlin .31..
    const val kotlinSuffix = "" // with hyphen (like "-2")

    const val kotlin = "$kotlinMajor.$kotlinMinor.$kotlinPatch$kotlinSuffix"
        // https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md
        // https://github.com/JetBrains/kotlin/releases

    const val kotlinxCoroutines = "1.4.3"
        // https://github.com/Kotlin/kotlinx.coroutines/releases

     // just a reference - not useful in typical cases
    const val gradle5 = "5.6.4"
    const val gradle6 = "6.8.3"
    const val gradle = gradle6
        // https://gradle.org/releases/
        // https://services.gradle.org/versions
        // https://services.gradle.org/versions/current

//    const val composeDesktop = "0.3.2"
    const val composeDesktop = "0.4.0-build178"
        // https://github.com/JetBrains/compose-jb

    const val composeAndroid = "1.0.0-beta03"
        // https://developer.android.com/jetpack/androidx/releases/compose

    const val androidGradlePlugin = "7.0.0-alpha13"
    // https://developer.android.com/studio/releases/gradle-plugin
    // https://google.github.io/android-gradle-dsl/

    const val androidMavenGradlePlugin = "2.1" // https://github.com/dcendents/android-maven-gradle-plugin/releases

    const val androidCompileSdk = 30
    const val androidMinSdk = 26
    const val androidTargetSdk = 30
        // https://developer.android.com/about/dashboards/index.html
        // https://source.android.com/setup/start/build-numbers

    @Deprecated("Deprecated with android gradle plugin 3.0.0 or higher")
    const val androidBuildTools = "29.0.2"
        // https://developer.android.com/studio/releases/build-tools.html

    @Deprecated("Use androidx")
    const val androidSupport = "28.0.0"
        // https://developer.android.com/topic/libraries/support-library/revisions.html

    // https://developer.android.com/jetpack/androidx/versions
    // https://dl.google.com/dl/android/maven2/index.html

    const val androidxCore = "1.3.2"

    const val androidxAppcompat = "1.3.0-beta01"

    const val androidxConstraint1 = "1.1.3"
        // https://developer.android.com/training/constraint-layout
    const val androidxConstraint2 = "2.0.4"
    const val androidxConstraint = androidxConstraint2

    const val androidxRecyclerview = "1.1.0-rc01"
    const val androidxCardview = "1.0.0"
    const val androidMaterial = "1.2.0-alpha01"
    const val androidxAnnotation = "1.1.0"
    const val androidxPreference = "1.1.0"
    const val androidxBrowser = "1.3.0-alpha01"
    const val androidxPercentLayout = "1.0.0"
    const val androidxFlexboxLayout = "2.0.1"
        // https://github.com/google/flexbox-layout/releases

    const val androidxLifecycle = "2.3.0-rc01"
        // https://developer.android.com/jetpack/androidx/releases/lifecycle

    const val androidxRoom = "2.3.0-beta01"
        // https://developer.android.com/jetpack/androidx/releases/room

    const val androidxTest = "1.3.1-alpha03"
    const val androidxTestRunner = "1.3.1-alpha03"
    const val androidxTestRules = "1.3.1-alpha03"
        // https://developer.android.com/jetpack/androidx/releases/test

    const val androidxEspresso = "3.3.0-alpha02"

    const val androidCommons = "0.0.24"
        // https://github.com/elpassion/android-commons/releases

    const val rxjava2 = "2.2.16"
    const val rxjava3 = "3.0.10"
        // https://github.com/ReactiveX/RxJava/releases

    const val rxkotlin = "3.0.1"
        // https://github.com/ReactiveX/RxKotlin/releases

    const val rxbinding = "4.0.0"
        // https://github.com/JakeWharton/RxBinding
        // https://github.com/JakeWharton/RxBinding/releases
        // https://github.com/JakeWharton/RxBinding/blob/master/CHANGELOG.md

    const val rxrelay = "3.0.0"
        // https://github.com/JakeWharton/RxRelay/releases

    const val rxandroid = "3.0.0"
        // https://github.com/ReactiveX/RxAndroid/releases

    const val rxlifecycle = "4.0.2"
        // https://github.com/trello/RxLifecycle/releases

    const val retrofit = "2.9.0"
        // https://github.com/square/retrofit
        // https://github.com/square/retrofit/releases

    const val okhttp = "4.9.0"
        // https://github.com/square/okhttp
        // https://github.com/square/okhttp/releases

    const val dbusJava = "3.3.0"
        // https://github.com/hypfvieh/dbus-java

    const val dbusKotlin = "0.0.04"
    // https://github.com/langara/dbus-kotlin

    const val javaWebsocket = "1.5.1"
        // https://mvnrepository.com/artifact/org.java-websocket/Java-WebSocket

//    const val slf4jSimple = "1.7.30"
    const val slf4jSimple = "2.0.0-alpha1"
        // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple

    const val log4j2 = "2.14.1"
        // http://logging.apache.org/log4j/2.x/maven-artifacts.html

    const val googleServicesPlugin = "4.3.5"
        // https://developers.google.com/android/guides/google-services-plugin

    const val googlePlayServicesBase = "17.6.0"
        // https://developers.google.com/android/guides/setup
        // https://developers.google.com/android/guides/releases

    const val firebaseGitlive = "1.2.0"
        // https://github.com/GitLiveApp/firebase-kotlin-sdk

    const val firebaseCrashlyticsPlugin = "2.4.1"
        // https://firebase.google.com/docs/crashlytics/get-started?platform=android

    const val firebaseAnalytics = "17.4.3"
    const val firebaseCrashlytics = "17.0.1"
    const val firebaseAppIndexing = "19.1.0"

    const val firebaseUiAuth = "7.1.1"
        // https://github.com/firebase/FirebaseUI-Android
        // https://github.com/firebase/FirebaseUI-Android/releases
        // https://firebase.google.com/docs/auth/android/firebaseui

    const val picasso = "2.8"
    // https://github.com/square/picasso
    // https://github.com/square/picasso/releases

    const val materialDialogs = "3.3.0"
    // https://github.com/afollestad/material-dialogs
    // https://github.com/afollestad/material-dialogs/releases

    const val leakcanary = "2.6"
        // https://github.com/square/leakcanary/releases

    const val paperwork = "1.2.7"
        // https://github.com/zsoltk/paperwork/releases

    const val mockitoKotlin = "2.2.0"
        // https://github.com/nhaarman/mockito-kotlin/releases

    const val junit4 = "4.13.1"
        // https://github.com/junit-team/junit4/releases

    const val junit5 = "5.6.3"
        // https://github.com/junit-team/junit5/releases

    const val googleTruth = "1.1.2"
        // https://github.com/google/truth/releases

    const val androidTestRunnerClass = "androidx.test.runner.AndroidJUnitRunner"
        // https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner.html

    const val realm = "10.0.1"
        // https://realm.io/docs/java/latest/

    const val tuplek = "0.0.2"
        // https://github.com/langara/tuplek/releases

    const val abcdk = "0.0.2"
        // https://github.com/langara/abcdk/releases

    const val rxmock = "0.0.2"
        // https://github.com/langara/rxmock/releases

    const val smokk = "0.0.4"
        // https://github.com/langara/smokk/releases

    const val uspek = "0.0.13"
        // https://github.com/langara/uspek/releases

    const val upue = "0.0.03"
    // https://github.com/langara/upue/releases

    const val sandboxui = "0.0.5"
        // https://github.com/langara/sandboxui/releases

    const val recyclerui = "0.0.2"
        // https://github.com/langara/recyclerui/releases

    const val ktorEap = "1.6.0-eap-3"
    const val ktorStable = "1.5.1"
    const val ktorForRsocket = "1.4.3" // TODO: remove when rsocket start working with new ktor
    const val ktor = ktorForRsocket
        // https://maven.pkg.jetbrains.space/public/p/ktor/eap/io/ktor/ktor/
        // https://github.com/ktorio/ktor
        // https://github.com/ktorio/ktor/releases
        // https://bintray.com/kotlin/ktor/ktor

    const val rsocket = "0.12.0"
        // https://github.com/rsocket/rsocket-kotlin
        // https://github.com/rsocket/rsocket-kotlin/releases

    const val splitties = "3.0.0-beta01"
        // https://github.com/LouisCAD/Splitties/releases
        // https://github.com/LouisCAD/Splitties
}

/**
 * Common dependencies for java/kotlin/android projects
 *
 * @see <a href="https://github.com/langara/deps.kt">https://github.com/langara/deps.kt</a>
 */
object Deps {
    val kotlinGradlePlugin = dep("org.jetbrains.kotlin", "kotlin-gradle-plugin", Vers.kotlin)
    val androidGradlePlugin = dep("com.android.tools.build", "gradle", Vers.androidGradlePlugin)
    val androidMavenGradlePlugin = dep("com.github.dcendents", "android-maven-gradle-plugin", Vers.androidMavenGradlePlugin)
    val kotlinStdlib7 = dep("org.jetbrains.kotlin", "kotlin-stdlib-jdk7", Vers.kotlin)
    val kotlinStdlib8 = dep("org.jetbrains.kotlin", "kotlin-stdlib-jdk8", Vers.kotlin)
    val kotlinReflect = dep("org.jetbrains.kotlin", "kotlin-reflect", Vers.kotlin)
    val kotlinTestCommon = dep("org.jetbrains.kotlin", "kotlin-test-common", Vers.kotlin)
    val kotlinTestAnnotationsCommon = dep("org.jetbrains.kotlin", "kotlin-test-annotations-common", Vers.kotlin)
    val kotlinTestJUnit = dep("org.jetbrains.kotlin", "kotlin-test-junit", Vers.kotlin)
    val kotlinTestJs = dep("org.jetbrains.kotlin", "kotlin-test-js", Vers.kotlin)

    val composeDesktopGradlePlugin = dep("org.jetbrains.compose", "compose-gradle-plugin", Vers.composeDesktop)
    val composeAndroidAnimation = dep("androidx.compose.animation", "animation", Vers.composeAndroid)
    val composeAndroidAnimationCore = dep("androidx.compose.animation", "animation-core", Vers.composeAndroid)
    val composeAndroidCompiler = dep("androidx.compose.compiler", "compiler", Vers.composeAndroid)
    val composeAndroidFoundation = dep("androidx.compose.foundation", "foundation", Vers.composeAndroid)
    val composeAndroidFoundationLayout = dep("androidx.compose.foundation", "foundation-layout", Vers.composeAndroid)
    val composeAndroidFoundationShape = dep("androidx.compose.foundation", "foundation-shape", Vers.composeAndroid)
    val composeAndroidFoundationText = dep("androidx.compose.foundation", "foundation-text", Vers.composeAndroid)
    val composeAndroidMaterial = dep("androidx.compose.material", "material", Vers.composeAndroid)
    val composeAndroidMaterialIcons = dep("androidx.compose.material", "material-icons", Vers.composeAndroid)
    val composeAndroidRuntime = dep("androidx.compose.runtime", "runtime", Vers.composeAndroid)
    val composeAndroidRuntimeDispatch = dep("androidx.compose.runtime", "runtime-dispatch", Vers.composeAndroid)
    val composeAndroidRuntimeFrames = dep("androidx.compose.runtime", "runtime-frames", Vers.composeAndroid)
    val composeAndroidUi = dep("androidx.compose.ui", "ui", Vers.composeAndroid)
    val composeAndroidUiGeometry = dep("androidx.compose.ui", "ui-geometry", Vers.composeAndroid)
    val composeAndroidUiGraphics = dep("androidx.compose.ui", "ui-graphics", Vers.composeAndroid)
    val composeAndroidUiPlatform = dep("androidx.compose.ui", "ui-platform", Vers.composeAndroid)
    val composeAndroidUiTest = dep("androidx.compose.ui", "ui-test", Vers.composeAndroid)
    val composeAndroidUiTooling = dep("androidx.compose.ui", "ui-tooling", Vers.composeAndroid)

    val kotlinxCoroutinesCommon = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-core-common", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesCore = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-core", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesDebug = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-debug", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesTest = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-test", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesReactive = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-reactive", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesReactor = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-reactor", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesRx2 = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-rx2", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesAndroid = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-android", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesJavaFx = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-javafx", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesSwing = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-swing", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesJdk8 = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesGuava = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-quava", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesSlf4j = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-slf4j", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesPlayServices = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-play-services", Vers.kotlinxCoroutines)

    // just a reference - not useful in typical cases
    const val gradleBaseUrl = "https://services.gradle.org/distributions"
    const val gradleUrlBin = "$gradleBaseUrl/gradle-${Vers.gradle}-bin.zip"
    const val gradleUrlAll = "$gradleBaseUrl/gradle-${Vers.gradle}-all.zip"

    val androidxCore = dep("androidx.core", "core", Vers.androidxCore)
    val androidxCoreKtx = dep("androidx.core", "core-ktx", Vers.androidxCore)
    val androidxAppcompat = dep("androidx.appcompat", "appcompat", Vers.androidxAppcompat)
    val androidxRecyclerview = dep("androidx.recyclerview", "recyclerview", Vers.androidxRecyclerview)
    val androidxCardview = dep("androidx.cardview", "cardview", Vers.androidxCardview)
    val androidMaterial = dep("com.google.android.material", "material", Vers.androidMaterial)
    val androidxAnnotation = dep("androidx.annotation", "annotation", Vers.androidxAnnotation)
    val androidxPreference = dep("androidx.preference", "preference", Vers.androidxPreference)
    val androidxPreferenceKtx = androidxPreference + ("name" to "preference-ktx")
    val androidxBrowser = dep("androidx.browser", "browser", Vers.androidxBrowser)
    val androidxPercentLayout = dep("androidx.percentlayout", "percentlayout", Vers.androidxPercentLayout)
    val androidxFlexboxLayout = dep("com.google.android", "flexbox", Vers.androidxFlexboxLayout)
    val androidxConstraint1 = dep("androidx.constraintlayout", "constraintlayout", Vers.androidxConstraint1)
    val androidxConstraint2 = androidxConstraint1 + ("version" to Vers.androidxConstraint2)
    val androidxConstraint = androidxConstraint1
    val androidxConstraint1Solver = androidxConstraint1 + ("name" to "constraintlayout-solver")
    val androidxConstraint2Solver = androidxConstraint1Solver + ("version" to Vers.androidxConstraint2)
    val androidxConstraintSolver = androidxConstraint1Solver

    val androidxLifecycleCommon = dep("androidx.lifecycle", "lifecycle-common", Vers.androidxLifecycle)
    val androidxLifecycleCompiler = androidxLifecycleCommon + ("name" to "lifecycle-compiler")
    val androidxLifecycleExtensions = androidxLifecycleCommon + ("name" to "lifecycle-extensions")
    val androidxLifecycleLiveData = androidxLifecycleCommon + ("name" to "lifecycle-livedata")
    val androidxLifecycleLiveDataCore = androidxLifecycleCommon + ("name" to "lifecycle-livedata-core")
    val androidxLifecycleLiveDataCoreKtx = androidxLifecycleCommon + ("name" to "lifecycle-livedata-core-ktx")
    val androidxLifecycleViewModel = androidxLifecycleCommon + ("name" to "lifecycle-viewmodel")
    val androidxLifecycleViewModelKtx = androidxLifecycleCommon + ("name" to "lifecycle-viewmodel-ktx")

    val androidxRoomRuntime = dep("androidx.room", "room-runtime", Vers.androidxRoom)
    val androidxRoomCompiler = dep("androidx.room", "room-compiler", Vers.androidxRoom)
    val androidxRoomKtx = dep("androidx.room", "room-ktx", Vers.androidxRoom)
    val androidxRoomRxJava2 = dep("androidx.room", "room-rxjava2", Vers.androidxRoom)
    val androidxRoomTesting = dep("androidx.room", "room-testing", Vers.androidxRoom)

    val androidxEspressoAccessibility = dep("androidx.test.espresso", "espresso-accessibility", Vers.androidxEspresso)
    val androidxEspressoContrib = dep("androidx.test.espresso", "espresso-contrib", Vers.androidxEspresso)
    val androidxEspressoCore = dep("androidx.test.espresso", "espresso-core", Vers.androidxEspresso)
    val androidxEspressoIdlingResource = dep("androidx.test.espresso", "espresso-idling-resource", Vers.androidxEspresso)
    val androidxEspressoIntents = dep("androidx.test.espresso", "espresso-intents", Vers.androidxEspresso)
    val androidxEspressoRemote = dep("androidx.test.espresso", "espresso-remote", Vers.androidxEspresso)
    val androidxEspressoWeb = dep("androidx.test.espresso", "espresso-web", Vers.androidxEspresso)

    val androidCommonsEspresso = dep("com.github.elpassion.android-commons", "espresso", Vers.androidCommons)
    val androidCommonsRxJavaTest = dep("com.github.elpassion.android-commons", "rxjava-test", Vers.androidCommons)
    val androidCommonsSharedPrefs = dep("com.github.elpassion.android-commons", "shared-preferences", Vers.androidCommons)
    val androidCommonsSharedPrefsMoshi = dep("com.github.elpassion.android-commons", "shared-preferences-moshi-converter-adapter", Vers.androidCommons)
    val androidCommonsSharedPrefsGson = dep("com.github.elpassion.android-commons", "shared-preferences-gson-converter-adapter", Vers.androidCommons)
    val androidCommonsView = dep("com.github.elpassion.android-commons", "view", Vers.androidCommons)
    val androidCommonsPager = dep("com.github.elpassion.android-commons", "pager", Vers.androidCommons)
    val androidCommonsRecycler = dep("com.github.elpassion.android-commons", "recycler", Vers.androidCommons)

    val rxjava2 = dep("io.reactivex.rxjava2", "rxjava", Vers.rxjava2)
    val rxjava3 = dep("io.reactivex.rxjava3", "rxjava", Vers.rxjava3)
    val rxkotlin = dep("io.reactivex.rxjava2", "rxkotlin", Vers.rxkotlin)
    val rxandroid = dep("io.reactivex.rxjava2", "rxandroid", Vers.rxandroid)
    val rxrelay = dep("com.jakewharton.rxrelay2", "rxrelay", Vers.rxrelay)
    val rxbinding = dep("com.jakewharton.rxbinding3", "rxbinding", Vers.rxbinding)
    val rxbindingCore = rxbinding + ("name" to "rxbinding-core")
    val rxbindingAppCompat = rxbinding + ("name" to "rxbinding-appcompat")
    val rxbindingDrawerLayout = rxbinding + ("name" to "rxbinding-drawerlayout")
    val rxbindingLeanback = rxbinding + ("name" to "rxbinding-leanback")
    val rxbindingRecyclerView = rxbinding + ("name" to "rxbinding-recyclerview")
    val rxbindingSlidingPaneLayout = rxbinding + ("name" to "rxbinding-slidingpanelayout")
    val rxbindingSwipeRefreshLayout = rxbinding + ("name" to "rxbinding-swiperefreshlayout")
    val rxbindingViewPager = rxbinding + ("name" to "rxbinding-viewpager")
    val rxlifecycleComponents = dep("com.trello.rxlifecycle2", "rxlifecycle-components", Vers.rxlifecycle)
    val rxlifecycleKotlin = dep("com.trello.rxlifecycle2", "rxlifecycle-kotlin", Vers.rxlifecycle)
    val retrofit = dep("com.squareup.retrofit2", "retrofit", Vers.retrofit)
    val retrofitMoshi = dep("com.squareup.retrofit2", "converter-moshi", Vers.retrofit)
    val retrofitRxjava = dep("com.squareup.retrofit2", "adapter-rxjava2", Vers.retrofit)
    val okhttp = dep("com.squareup.okhttp3", "okhttp", Vers.okhttp)
    val okhttpLogging = dep("com.squareup.okhttp3", "logging-interceptor", Vers.okhttp)
    val dbusJava = dep("com.github.hypfvieh", "dbus-java", Vers.dbusJava)
    val dbusJavaOsgi = dep("com.github.hypfvieh", "dbus-java-osgi", Vers.dbusJava)
    val dbusJavaUtils = dep("com.github.hypfvieh", "dbus-java-utils", Vers.dbusJava)
    val javaWebsocket = dep("org.java-websocket", "java-websocket", Vers.javaWebsocket)
    val slf4jSimple = dep("org.slf4j", "slf4j-simple", Vers.slf4jSimple)
    val log4j2api = dep("org.apache.logging.log4j", "log4j-api", Vers.log4j2)
    val log4j2core = dep("org.apache.logging.log4j", "log4j-core", Vers.log4j2)
    val googleServicesPlugin = dep("com.google.gms", "google-services", Vers.googleServicesPlugin)
    val googlePlayServicesBase = dep("com.google.android.gms", "play-services-base", Vers.googlePlayServicesBase)
    val firebaseGitliveAuth = dep("dev.gitlive", "firebase-auth", Vers.firebaseGitlive)
    val firebaseGitliveDB = dep("dev.gitlive", "firebase-database", Vers.firebaseGitlive)
    val firebaseGitliveFirestore = dep("dev.gitlive", "firebase-firestore", Vers.firebaseGitlive)
    val firebaseGitliveFunctions = dep("dev.gitlive", "firebase-functions", Vers.firebaseGitlive)
    val firebaseGitliveMessaging = dep("dev.gitlive", "firebase-messaging", Vers.firebaseGitlive)
    val firebaseGitliveStorage = dep("dev.gitlive", "firebase-storage", Vers.firebaseGitlive)
    val firebaseCrashlyticsPlugin = dep("com.google.firebase", "firebase-crashlytics-gradle", Vers.firebaseCrashlyticsPlugin)
    val firebaseAnalytics = dep("com.google.firebase", "firebase-analytics", Vers.firebaseAnalytics)
    val firebaseCrashlytics = dep("com.google.firebase", "firebase-crashlytics", Vers.firebaseCrashlytics)
    val firebaseAppIndexing = dep("com.google.firebase", "firebase-appindexing", Vers.firebaseAppIndexing)
    val firebaseUiAuth = dep("com.firebaseui", "firebase-ui-auth", Vers.firebaseUiAuth)
    val picasso = dep("com.squareup.picasso", "picasso", Vers.picasso)
    val materialDialogs = dep("com.afollestad.material-dialogs", "core", Vers.materialDialogs)
    val leakcanary = dep("com.squareup.leakcanary", "leakcanary-android", Vers.leakcanary)
    val paperwork = dep("hu.supercluster", "paperwork", Vers.paperwork)
    val paperworkPlugin = dep("hu.supercluster", "paperwork-plugin", Vers.paperwork)
    val junit4 = dep("junit", "junit", Vers.junit4)
    val junit5 = dep("org.junit.jupiter", "junit-jupiter-api", Vers.junit5)
    val junit5engine = dep("org.junit.jupiter", "junit-jupiter-engine", Vers.junit5)
    val tuplek = dep("com.github.langara", "tuplek", Vers.tuplek)
    val abcdk = dep("com.github.langara", "abcdk", Vers.abcdk)
    val rxmock = dep("com.github.langara", "rxmock", Vers.rxmock)
    val smokk = dep("com.github.langara", "smokk", Vers.smokk)
    val uspek = dep("com.github.langara.uspek", "uspek", Vers.uspek)
    val upue = dep("com.github.langara.upue", "upue", Vers.upue)
    val dbusKotlin = dep("com.github.langara", "dbus-kotlin", Vers.dbusKotlin)
    val sandboxui = dep("com.github.langara", "sandboxui", Vers.sandboxui)
    val recyclerui = dep("com.github.langara", "recyclerui", Vers.recyclerui)
    val googleTruth = dep("com.google.truth", "truth", Vers.googleTruth)
    val mockitoKotlin = dep("com.nhaarman.mockitokotlin2", "mockito-kotlin", Vers.mockitoKotlin)
    val androidTestRunner = dep("androidx.test", "runner", Vers.androidxTestRunner)
    val androidTestRules = dep("androidx.test", "rules", Vers.androidxTestRules)
    val realmGradlePlugin = dep("io.realm", "realm-gradle-plugin", Vers.realm)
    val ktorServerCore = dep("io.ktor", "ktor-server-core", Vers.ktor)
    val ktorServerCio = dep("io.ktor", "ktor-server-cio", Vers.ktor)
    val ktorServerNetty = dep("io.ktor", "ktor-server-netty", Vers.ktor)
    val ktorAuth = dep("io.ktor", "ktor-auth", Vers.ktor)
    val ktorClientCore = dep("io.ktor", "ktor-client-core", Vers.ktor)
    val ktorClientCio = dep("io.ktor", "ktor-client-cio", Vers.ktor)
    val ktorClientApache = dep("io.ktor", "ktor-client-apache", Vers.ktor)
    val rsocketCore = dep("io.rsocket.kotlin", "rsocket-core", Vers.rsocket)
    val rsocketKtor = dep("io.rsocket.kotlin", "rsocket-transport-ktor", Vers.rsocket)
    val rsocketKtorClient = dep("io.rsocket.kotlin", "rsocket-transport-ktor-client", Vers.rsocket)
    val rsocketKtorServer = dep("io.rsocket.kotlin", "rsocket-transport-ktor-server", Vers.rsocket)
    val splitties = dep("com.louiscad.splitties", "splitties-fun-pack-android-material-components-with-views-dsl", Vers.splitties)
}

private fun dep(group: String, name: String, version: String) = mapOf(
        "group" to group,
        "name" to name,
        "version" to version
)

object Repos {
    const val ktorEap = "https://maven.pkg.jetbrains.space/public/p/ktor/eap"
    const val composeDesktopDev = "https://maven.pkg.jetbrains.space/public/p/compose/dev"
    const val jitpack = "https://jitpack.io"
}