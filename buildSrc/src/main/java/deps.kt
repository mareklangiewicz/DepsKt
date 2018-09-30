@file:Suppress("unused")

/**
 * Common dependencies versions for java/kotlin/android projects
 *
 * @see <a href="https://github.com/langara/deps.kt">https://github.com/langara/deps.kt</a>
 */
object Vers {
    val kotlinMajor = 1
    val kotlinMinor = 2
    val kotlinPatch = 70
    val kotlin = "$kotlinMajor.$kotlinMinor.$kotlinPatch" // https://github.com/JetBrains/kotlin/releases
    val kotlinxCoroutines = "0.26.1" // https://github.com/Kotlin/kotlinx.coroutines/releases
    val androidGradlePlugin = "3.2.0-rc03" // https://google.github.io/android-gradle-dsl/
    val androidMavenGradlePlugin = "2.0" // https://github.com/dcendents/android-maven-gradle-plugin/releases
    val androidCompileSdk = 28 // https://developer.android.com/about/dashboards/index.html
    val androidMinSdk = 23
    val androidTargetSdk = 28
    @Deprecated("Deprecated with android gradle plugin 3.0.0 or higher")
    val androidBuildTools = "28.0.2" // https://developer.android.com/studio/releases/build-tools.html
    val androidSupport = "28.0.0-rc02" // https://developer.android.com/topic/libraries/support-library/revisions.html
    val androidSupportConstraint = "1.0.2" // https://developer.android.com/training/constraint-layout
    val androidArchLifecycle = "1.1.1" // https://developer.android.com/topic/libraries/architecture/adding-components.html
    val androidArchPersistenceRoom = "1.0.0"
    val androidSupportTest = "1.0.1" // https://developer.android.com/topic/libraries/testing-support-library/release-notes.html
    val androidEspresso = "3.0.1"
    val androidCommons = "0.0.23" // https://github.com/elpassion/android-commons/releases
    val rxjava = "2.2.1" // https://github.com/ReactiveX/RxJava/releases
    val rxkotlin = "2.3.0" // https://github.com/ReactiveX/RxKotlin/releases
    val rxbinding = "2.1.1" // https://github.com/JakeWharton/RxBinding/releases
    val rxrelay = "2.0.0" // https://github.com/JakeWharton/RxRelay/releases
    val rxandroid = "2.1.0" // https://github.com/ReactiveX/RxAndroid/releases
    val rxlifecycle = "2.2.2" // https://github.com/trello/RxLifecycle/releases
    val retrofit = "2.3.0" // https://github.com/square/retrofit/releases
    val okhttp = "3.9.1" // https://github.com/square/okhttp/releases
    val javaWebsocket = "1.3.8" // https://mvnrepository.com/artifact/org.java-websocket/Java-WebSocket
    val playServices = "15.0.0" // https://developers.google.com/android/guides/releases
    val picasso = "2.5.2" // https://github.com/square/picasso/releases
    val materialDialogs = "0.9.6.0" // https://github.com/afollestad/material-dialogs/releases
    val leakcanary = "1.5.4" // https://github.com/square/leakcanary/releases
    val paperwork = "1.2.7" // https://github.com/zsoltk/paperwork/releases
    val mockitoKotlin = "2.0.0-alpha03" // https://github.com/nhaarman/mockito-kotlin/releases
    val junit = "4.12" // https://github.com/junit-team/junit4/releases
    val googleTruth = "0.39" // https://github.com/google/truth/releases
    val androidTestRunnerClass = "android.support.test.runner.AndroidJUnitRunner"
    // https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner.html
    val realm = "5.3.1" // https://realm.io/docs/java/latest/
    val uspek = "25459c8b62"
    val ktor = "0.9.5"
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
    val androidSupportV4 = dep("com.android.support", "support-v4", Vers.androidSupport)
    val androidSupportV13 = dep("com.android.support", "support-v13", Vers.androidSupport)
    val androidSupportAppcompat = dep("com.android.support", "appcompat-v7", Vers.androidSupport)
    val androidSupportRecyclerview = dep("com.android.support", "recyclerview-v7", Vers.androidSupport)
    val androidSupportCardview = dep("com.android.support", "cardview-v7", Vers.androidSupport)
    val androidSupportDesign = dep("com.android.support", "design", Vers.androidSupport)
    val androidSupportAnnotations = dep("com.android.support", "support-annotations", Vers.androidSupport)
    val androidSupportPreference = dep("com.android.support", "preference-v14", Vers.androidSupport)
    val androidSupportCustomtabs = dep("com.android.support", "customtabs", Vers.androidSupport)
    val androidSupportPercent = dep("com.android.support", "percent", Vers.androidSupport)
    val androidSupportConstraint = dep("com.android.support.constraint", "constraint-layout", Vers.androidSupportConstraint)
    val androidArchLifecycleExtensions = dep("android.arch.lifecycle", "extensions", Vers.androidArchLifecycle)
    val androidArchLifecycleViewModel = dep("android.arch.lifecycle", "viewmodel", Vers.androidArchLifecycle)
    val androidArchLifecycleLiveData = dep("android.arch.lifecycle", "livedata", Vers.androidArchLifecycle)
    val androidArchLifecycleCompiler = dep("android.arch.lifecycle", "compiler", Vers.androidArchLifecycle)
    val androidArchPersistenceRoomRuntime = dep("android.arch.persistence.room", "runtime", Vers.androidArchPersistenceRoom)
    val androidArchPersistenceRoomCompiler = dep("android.arch.persistence.room", "compiler", Vers.androidArchPersistenceRoom)
    val androidEspresso = dep("com.android.support.test.espresso", "espresso-core", Vers.androidEspresso)
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
    val materialDialogsCore = dep("com.afollestad.material-dialogs", "core", Vers.materialDialogs)
    val materialDialogsCommons = dep("com.afollestad.material-dialogs", "commons", Vers.materialDialogs)
    val leakcanary = dep("com.squareup.leakcanary", "leakcanary-android", Vers.leakcanary)
    val leakcanaryNoOp = dep("com.squareup.leakcanary", "leakcanary-android-no-op", Vers.leakcanary)
    val paperwork = dep("hu.supercluster", "paperwork", Vers.paperwork)
    val paperworkPlugin = dep("hu.supercluster", "paperwork-plugin", Vers.paperwork)
    val junit = dep("junit", "junit", Vers.junit)
    val uspek = dep("com.github.langara", "USpek", Vers.uspek)
    val googleTruth = dep("com.google.truth", "truth", Vers.googleTruth)
    val mockitoKotlin = dep("com.nhaarman.mockitokotlin2", "mockito-kotlin", Vers.mockitoKotlin)
    val androidTestRunner = dep("com.android.support.test", "runner", Vers.androidSupportTest)
    val androidTestRules = dep("com.android.support.test", "rules", Vers.androidSupportTest)
    val realmGradlePlugin = dep("io.realm", "realm-gradle-plugin", Vers.realm)
    val ktorServerNetty = dep("io.ktor", "ktor-server-netty", Vers.ktor)
}

private fun dep(group: String, name: String, version: String) = mapOf(
        "group" to group,
        "name" to name,
        "version" to version
)

