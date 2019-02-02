@file:Suppress("unused", "SpellCheckingInspection", "MemberVisibilityCanBePrivate")

/**
 * Common dependencies versions for java/kotlin/android projects
 *
 * @see <a href="https://github.com/langara/deps.kt">https://github.com/langara/deps.kt</a>
 */
object Vers {
    const val kotlinMajor = 1
    const val kotlinMinor = 3
    const val kotlinPatch = 20

    const val kotlin = "$kotlinMajor.$kotlinMinor.$kotlinPatch"
        // https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md
        // https://github.com/JetBrains/kotlin/releases

    @Deprecated("Use current Kotlin", ReplaceWith("kotlin"))
    const val kotlin12 = "1.2.70"

    const val kotlinxCoroutines = "1.1.1"
        // https://github.com/Kotlin/kotlinx.coroutines/releases

     // just a reference - not useful in typical cases
    const val gradle4 = "4.10.3"
    const val gradle5 = "5.1.1"
    const val gradle = gradle5
        // https://gradle.org/releases/
        // https://services.gradle.org/versions
        // https://services.gradle.org/versions/current

    const val androidGradlePlugin = "3.3.0"
        // https://google.github.io/android-gradle-dsl/
        // https://developer.android.com/studio/releases/gradle-plugin

    const val androidMavenGradlePlugin = "2.1" // https://github.com/dcendents/android-maven-gradle-plugin/releases

    const val androidCompileSdk = 28
    const val androidMinSdk = 23
    const val androidTargetSdk = 28
        // https://developer.android.com/about/dashboards/index.html
        // https://source.android.com/setup/start/build-numbers

    @Deprecated("Deprecated with android gradle plugin 3.0.0 or higher")
    const val androidBuildTools = "28.0.3"
        // https://developer.android.com/studio/releases/build-tools.html

    @Deprecated("Use androidx")
    const val androidSupport = "28.0.0"
        // https://developer.android.com/topic/libraries/support-library/revisions.html

    const val androidxAppcompat = "1.0.1"

    const val androidxConstraint = "1.1.2"
        // https://developer.android.com/training/constraint-layout

    const val androidxRecyclerview = "1.0.0"
    const val androidxCardview = "1.0.0"
    const val androidMaterial = "1.0.0"
    const val androidxAnnotation = "1.0.0"
    const val androidxPreference = "1.0.0"
    const val androidxBrowser = "1.0.0"
    const val androidxPercent = "1.0.0"

    const val androidxLifecycle = "2.0.0"
        // https://developer.android.com/topic/libraries/architecture/adding-components.html

    const val androidArchPersistenceRoom = "2.1.0-alpha03"
        // https://developer.android.com/topic/libraries/architecture/adding-components#room

    const val androidxTest = "1.1.1"
        // https://developer.android.com/topic/libraries/testing-support-library/release-notes.html

    const val androidxEspresso = "3.1.1"

    const val androidCommons = "0.0.24"
        // https://github.com/elpassion/android-commons/releases

    const val rxjava = "2.2.6"
        // https://github.com/ReactiveX/RxJava/releases

    const val rxkotlin = "2.3.0"
        // https://github.com/ReactiveX/RxKotlin/releases

    const val rxbinding = "3.0.0-alpha2"
        // https://github.com/JakeWharton/RxBinding
        // https://github.com/JakeWharton/RxBinding/releases

    const val rxrelay = "2.1.0"
        // https://github.com/JakeWharton/RxRelay/releases

    const val rxandroid = "2.1.0"
        // https://github.com/ReactiveX/RxAndroid/releases

    const val rxlifecycle = "3.0.0"
        // https://github.com/trello/RxLifecycle/releases

    const val retrofit = "2.5.0"
        // https://github.com/square/retrofit
        // https://github.com/square/retrofit/releases

    const val okhttp = "3.12.0"
        // https://github.com/square/okhttp
        // https://github.com/square/okhttp/releases

    const val javaWebsocket = "1.3.9"
        // https://mvnrepository.com/artifact/org.java-websocket/Java-WebSocket

    const val playServices = "15.0.0"
        // https://developers.google.com/android/guides/releases

    const val picasso = "2.71828"
    // https://github.com/square/picasso
    // https://github.com/square/picasso/releases

    const val materialDialogs = "2.0.0-rc05"
    // https://github.com/afollestad/material-dialogs
    // https://github.com/afollestad/material-dialogs/releases

    const val leakcanary = "1.6.2"
        // https://github.com/square/leakcanary/releases

    const val paperwork = "1.2.7"
        // https://github.com/zsoltk/paperwork/releases

    const val mockitoKotlin = "2.0.0"
        // https://github.com/nhaarman/mockito-kotlin/releases

    const val junit = "4.12"
        // https://github.com/junit-team/junit4/releases

    const val googleTruth = "0.42"
        // https://github.com/google/truth/releases

    const val androidTestRunnerClass = "androidx.test.runner.AndroidJUnitRunner"
        // https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner.html

    const val realm = "5.8.0"
        // https://realm.io/docs/java/latest/

    const val tuplek = "0.0.1"
        // https://github.com/langara/tuplek/releases

    const val rxmock = "0.0.1"
        // https://github.com/langara/rxmock/releases

    const val smokk = "0.0.1"
    // https://github.com/langara/smokk/releases

    const val uspek = "0.0.2"
        // https://github.com/langara/uspek/releases

    const val ktor = "1.1.1"
        // https://github.com/ktorio/ktor
        // https://github.com/ktorio/ktor/releases
        // https://bintray.com/kotlin/ktor/ktor

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
    val kotlinxCoroutinesCore = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-core", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesAndroid = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-android", Vers.kotlinxCoroutines)

    // just a reference - not useful in typical cases
    const val gradleBaseUrl = "https://services.gradle.org/distributions"
    const val gradleUrlBin = "$gradleBaseUrl/gradle-${Vers.gradle}-bin.zip"
    const val gradleUrlAll = "$gradleBaseUrl/gradle-${Vers.gradle}-all.zip"

    val androidxAppcompat = dep("androidx.appcompat", "appcompat", Vers.androidxAppcompat)
    val androidxRecyclerview = dep("androidx.recyclerview", "recyclerview", Vers.androidxRecyclerview)
    val androidxCardview = dep("androidx.cardview", "cardview", Vers.androidxCardview)
    val androidMaterial = dep("com.google.android.material", "material", Vers.androidMaterial)
    val androidxAnnotation = dep("androidx.annotation", "annotation", Vers.androidxAnnotation)
    val androidxPreference = dep("androidx.preference", "preference", Vers.androidxPreference)
    val androidxBrowser = dep("androidx.browser", "browser", Vers.androidxBrowser)
    val androidxPercent = dep("androidx.percentlayout", "percentlayout", Vers.androidxPercent)
    val androidxConstraint = dep("androidx.constraintlayout", "constraintlayout", Vers.androidxConstraint)

    val androidxLifecycleCommon = dep("androidx.lifecycle", "lifecycle-common", Vers.androidxLifecycle)
    val androidxLifecycleExtensions = dep("androidx.lifecycle", "lifecycle-extensions", Vers.androidxLifecycle)
    val androidxLifecycleViewModel = dep("androidx.lifecycle", "lifecycle-viewmodel", Vers.androidxLifecycle)
    val androidxLifecycleLiveData = dep("androidx.lifecycle", "lifecycle-livedata", Vers.androidxLifecycle)
    val androidxLifecycleCompiler = dep("androidx.lifecycle", "lifecycle-compiler", Vers.androidxLifecycle)

    val androidxRoomRuntime = dep("androidx.room", "room-runtime", Vers.androidArchPersistenceRoom)
    val androidxRoomCompiler = dep("androidx.room", "room-compiler", Vers.androidArchPersistenceRoom)
    val androidxEspresso = dep("androidx.test.espresso", "espresso-core", Vers.androidxEspresso)

    val androidCommonsEspresso = dep("com.github.elpassion.android-commons", "espresso", Vers.androidCommons)
    val androidCommonsRxJavaTest = dep("com.github.elpassion.android-commons", "rxjava-test", Vers.androidCommons)
    val androidCommonsSharedPrefs = dep("com.github.elpassion.android-commons", "shared-preferences", Vers.androidCommons)
    val androidCommonsSharedPrefsMoshi = dep("com.github.elpassion.android-commons", "shared-preferences-moshi-converter-adapter", Vers.androidCommons)
    val androidCommonsSharedPrefsGson = dep("com.github.elpassion.android-commons", "shared-preferences-gson-converter-adapter", Vers.androidCommons)
    val androidCommonsView = dep("com.github.elpassion.android-commons", "view", Vers.androidCommons)
    val androidCommonsPager = dep("com.github.elpassion.android-commons", "pager", Vers.androidCommons)
    val androidCommonsRecycler = dep("com.github.elpassion.android-commons", "recycler", Vers.androidCommons)

    val rxjava = dep("io.reactivex.rxjava2", "rxjava", Vers.rxjava)
    val rxkotlin = dep("io.reactivex.rxjava2", "rxkotlin", Vers.rxkotlin)
    val rxandroid = dep("io.reactivex.rxjava2", "rxandroid", Vers.rxandroid)
    val rxrelay = dep("com.jakewharton.rxrelay2", "rxrelay", Vers.rxrelay)
    val rxbindingKotlin = dep("com.jakewharton.rxbinding2", "rxbinding-kotlin", Vers.rxbinding)
    val rxlifecycleComponents = dep("com.trello.rxlifecycle2", "rxlifecycle-components", Vers.rxlifecycle)
    val rxlifecycleKotlin = dep("com.trello.rxlifecycle2", "rxlifecycle-kotlin", Vers.rxlifecycle)
    val retrofit = dep("com.squareup.retrofit2", "retrofit", Vers.retrofit)
    val retrofitMoshi = dep("com.squareup.retrofit2", "converter-moshi", Vers.retrofit)
    val retrofitRxjava = dep("com.squareup.retrofit2", "adapter-rxjava2", Vers.retrofit)
    val okhttp = dep("com.squareup.okhttp3", "okhttp", Vers.okhttp)
    val okhttpLogging = dep("com.squareup.okhttp3", "logging-interceptor", Vers.okhttp)
    val javaWebsocket = dep("org.java-websocket", "Java-WebSocket", Vers.javaWebsocket)
    val playServicesBase = dep("com.google.android.gms", "play-services-base", Vers.playServices)
    val firebaseAppIndexing = dep("com.google.firebase", "firebase-appindexing", Vers.playServices)
    val picasso = dep("com.squareup.picasso", "picasso", Vers.picasso)
    val materialDialogs = dep("com.afollestad.material-dialogs", "core", Vers.materialDialogs)
    val leakcanary = dep("com.squareup.leakcanary", "leakcanary-android", Vers.leakcanary)
    val leakcanaryNoOp = dep("com.squareup.leakcanary", "leakcanary-android-no-op", Vers.leakcanary)
    val paperwork = dep("hu.supercluster", "paperwork", Vers.paperwork)
    val paperworkPlugin = dep("hu.supercluster", "paperwork-plugin", Vers.paperwork)
    val junit = dep("junit", "junit", Vers.junit)
    val tuplek = dep("com.github.langara", "TupleK", Vers.tuplek)
    val rxmock = dep("com.github.langara", "RxMock", Vers.rxmock)
    val smokk = dep("com.github.langara", "SmokK", Vers.smokk)
    val uspek = dep("com.github.langara", "USpek", Vers.uspek)
    val googleTruth = dep("com.google.truth", "truth", Vers.googleTruth)
    val mockitoKotlin = dep("com.nhaarman.mockitokotlin2", "mockito-kotlin", Vers.mockitoKotlin)
    val androidTestRunner = dep("androidx.test", "runner", Vers.androidxTest)
    val androidTestRules = dep("androidx.test", "rules", Vers.androidxTest)
    val realmGradlePlugin = dep("io.realm", "realm-gradle-plugin", Vers.realm)
    val ktorServerNetty = dep("io.ktor", "ktor-server-netty", Vers.ktor)
    val ktorAuth = dep("io.ktor", "ktor-auth", Vers.ktor)
    val ktorClientCore = dep("io.ktor", "ktor-client-core", Vers.ktor)
    val ktorClientApache = dep("io.ktor", "ktor-client-apache", Vers.ktor)
}

private fun dep(group: String, name: String, version: String) = mapOf(
        "group" to group,
        "name" to name,
        "version" to version
)

