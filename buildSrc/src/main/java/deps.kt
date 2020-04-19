@file:Suppress("unused", "SpellCheckingInspection", "MemberVisibilityCanBePrivate")

/**
 * Common dependencies versions for java/kotlin/android projects
 *
 * @see <a href="https://github.com/langara/deps.kt">https://github.com/langara/deps.kt</a>
 */
object Vers {
    const val kotlinMajor = 1
    const val kotlinMinor = 3
    const val kotlinPatch = 72

    const val kotlin = "$kotlinMajor.$kotlinMinor.$kotlinPatch"
        // https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md
        // https://github.com/JetBrains/kotlin/releases

    @Deprecated("Use current Kotlin", ReplaceWith("kotlin"))
    const val kotlin12 = "1.2.70"

    const val kotlinxCoroutines = "1.3.5"
        // https://github.com/Kotlin/kotlinx.coroutines/releases

     // just a reference - not useful in typical cases
    const val gradle4 = "4.10.3"
    const val gradle5 = "5.6.4"
    const val gradle6 = "6.3"
    const val gradle = gradle6
        // https://gradle.org/releases/
        // https://services.gradle.org/versions
        // https://services.gradle.org/versions/current

    const val androidGradlePlugin = "4.1.0-alpha06"
    // https://developer.android.com/studio/releases/gradle-plugin
    // https://google.github.io/android-gradle-dsl/

    const val androidMavenGradlePlugin = "2.1" // https://github.com/dcendents/android-maven-gradle-plugin/releases

    const val androidCompileSdk = 29
    const val androidMinSdk = 23
    const val androidTargetSdk = 29
        // https://developer.android.com/about/dashboards/index.html
        // https://source.android.com/setup/start/build-numbers

    @Deprecated("Deprecated with android gradle plugin 3.0.0 or higher")
    const val androidBuildTools = "29.0.0"
        // https://developer.android.com/studio/releases/build-tools.html

    @Deprecated("Use androidx")
    const val androidSupport = "28.0.0"
        // https://developer.android.com/topic/libraries/support-library/revisions.html

    // https://developer.android.com/jetpack/androidx/versions
    // https://dl.google.com/dl/android/maven2/index.html

    const val androidxCore = "1.2.0-beta02"

    const val androidxAppcompat = "1.1.0"

    const val androidxConstraint1 = "1.1.3"
        // https://developer.android.com/training/constraint-layout
    const val androidxConstraint2 = "2.0.0-beta4"
    const val androidxConstraint = androidxConstraint2

    const val androidxRecyclerview = "1.1.0-rc01"
    const val androidxCardview = "1.0.0"
    const val androidMaterial = "1.2.0-alpha01"
    const val androidxAnnotation = "1.1.0"
    const val androidxPreference = "1.1.0"
    const val androidxBrowser = "1.2.0-alpha09"
    const val androidxPercentLayout = "1.0.0"
    const val androidxFlexboxLayout = "2.0.0"
        // https://github.com/google/flexbox-layout/releases

    const val androidxLifecycle = "2.2.0-rc03"
        // https://developer.android.com/jetpack/androidx/releases/lifecycle

    const val androidxRoom = "2.2.3"
        // https://developer.android.com/jetpack/androidx/releases/room

    const val androidxTest = "1.2.1-alpha02"
    const val androidxTestRunner = "1.3.0-alpha02"
    const val androidxTestRules = "1.3.0-alpha02"
        // https://developer.android.com/jetpack/androidx/releases/test

    const val androidxEspresso = "3.3.0-alpha02"

    const val androidCommons = "0.0.24"
        // https://github.com/elpassion/android-commons/releases

    const val rxjava2 = "2.2.16"
    const val rxjava3 = "3.0.2"
        // https://github.com/ReactiveX/RxJava/releases

    const val rxkotlin = "2.4.0"
        // https://github.com/ReactiveX/RxKotlin/releases

    const val rxbinding = "3.1.0"
        // https://github.com/JakeWharton/RxBinding
        // https://github.com/JakeWharton/RxBinding/releases
        // https://github.com/JakeWharton/RxBinding/blob/master/CHANGELOG.md

    const val rxrelay = "2.1.1"
        // https://github.com/JakeWharton/RxRelay/releases

    const val rxandroid = "2.1.1"
        // https://github.com/ReactiveX/RxAndroid/releases

    const val rxlifecycle = "3.1.0"
        // https://github.com/trello/RxLifecycle/releases

    const val retrofit = "2.7.1"
        // https://github.com/square/retrofit
        // https://github.com/square/retrofit/releases

    const val okhttp = "4.3.0"
        // https://github.com/square/okhttp
        // https://github.com/square/okhttp/releases

    const val javaWebsocket = "1.4.0"
        // https://mvnrepository.com/artifact/org.java-websocket/Java-WebSocket

    const val playServicesBase = "17.1.0"
        // https://developers.google.com/android/guides/setup
        // https://developers.google.com/android/guides/releases

    const val firebaseAppIndexing = "19.0.0"

    const val picasso = "2.71828"
    // https://github.com/square/picasso
    // https://github.com/square/picasso/releases

    const val materialDialogs = "3.1.1"
    // https://github.com/afollestad/material-dialogs
    // https://github.com/afollestad/material-dialogs/releases

    const val leakcanary = "2.1"
        // https://github.com/square/leakcanary/releases

    const val paperwork = "1.2.7"
        // https://github.com/zsoltk/paperwork/releases

    const val mockitoKotlin = "2.2.0"
        // https://github.com/nhaarman/mockito-kotlin/releases

    const val junit4 = "4.13"
        // https://github.com/junit-team/junit4/releases

    const val junit5 = "5.5.2"
        // https://github.com/junit-team/junit5/releases

    const val googleTruth = "1.0"
        // https://github.com/google/truth/releases

    const val androidTestRunnerClass = "androidx.test.runner.AndroidJUnitRunner"
        // https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner.html

    const val realm = "6.0.2"
        // https://realm.io/docs/java/latest/

    const val tuplek = "0.0.2"
        // https://github.com/langara/tuplek/releases

    const val abcdk = "0.0.2"
        // https://github.com/langara/abcdk/releases

    const val rxmock = "0.0.2"
        // https://github.com/langara/rxmock/releases

    const val smokk = "0.0.4"
        // https://github.com/langara/smokk/releases

    const val uspek = "0.0.6"
        // https://github.com/langara/uspek/releases

    const val sandboxui = "0.0.5"
        // https://github.com/langara/sandboxui/releases

    const val recyclerui = "0.0.2"
        // https://github.com/langara/recyclerui/releases

    const val ktor = "1.3.2"
        // https://github.com/ktorio/ktor
        // https://github.com/ktorio/ktor/releases
        // https://bintray.com/kotlin/ktor/ktor

    const val splitties = "3.0.0-alpha06"
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
    @Deprecated("Use specific jdk version", ReplaceWith("kotlinStdlib7"))
    val kotlinStdlib = kotlinStdlib7
    val kotlinReflect = dep("org.jetbrains.kotlin", "kotlin-reflect", Vers.kotlin)

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
    val javaWebsocket = dep("org.java-websocket", "Java-WebSocket", Vers.javaWebsocket)
    val playServicesBase = dep("com.google.android.gms", "play-services-base", Vers.playServicesBase)
    val firebaseAppIndexing = dep("com.google.firebase", "firebase-appindexing", Vers.firebaseAppIndexing)
    val picasso = dep("com.squareup.picasso", "picasso", Vers.picasso)
    val materialDialogs = dep("com.afollestad.material-dialogs", "core", Vers.materialDialogs)
    val leakcanary = dep("com.squareup.leakcanary", "leakcanary-android", Vers.leakcanary)
    val paperwork = dep("hu.supercluster", "paperwork", Vers.paperwork)
    val paperworkPlugin = dep("hu.supercluster", "paperwork-plugin", Vers.paperwork)
    val junit4 = dep("junit", "junit", Vers.junit4)
    val junit5 = dep("org.junit.jupiter", "junit-jupiter-api", Vers.junit5)
    val junit5engine = dep("org.junit.jupiter", "junit-jupiter-engine", Vers.junit5)
    val tuplek = dep("com.github.langara", "TupleK", Vers.tuplek)
    val abcdk = dep("com.github.langara", "AbcdK", Vers.abcdk)
    val rxmock = dep("com.github.langara", "RxMock", Vers.rxmock)
    val smokk = dep("com.github.langara", "SmokK", Vers.smokk)
    val uspek = dep("com.github.langara", "USpek", Vers.uspek)
    val sandboxui = dep("com.github.langara", "SandboxUi", Vers.sandboxui)
    val recyclerui = dep("com.github.langara", "RecyclerUi", Vers.recyclerui)
    val googleTruth = dep("com.google.truth", "truth", Vers.googleTruth)
    val mockitoKotlin = dep("com.nhaarman.mockitokotlin2", "mockito-kotlin", Vers.mockitoKotlin)
    val androidTestRunner = dep("androidx.test", "runner", Vers.androidxTestRunner)
    val androidTestRules = dep("androidx.test", "rules", Vers.androidxTestRules)
    val realmGradlePlugin = dep("io.realm", "realm-gradle-plugin", Vers.realm)
    val ktorServerNetty = dep("io.ktor", "ktor-server-netty", Vers.ktor)
    val ktorAuth = dep("io.ktor", "ktor-auth", Vers.ktor)
    val ktorClientCore = dep("io.ktor", "ktor-client-core", Vers.ktor)
    val ktorClientApache = dep("io.ktor", "ktor-client-apache", Vers.ktor)
    val splitties = dep("com.louiscad.splitties", "splitties-fun-pack-android-material-components-with-views-dsl", Vers.splitties)
}

private fun dep(group: String, name: String, version: String) = mapOf(
        "group" to group,
        "name" to name,
        "version" to version
)

