/**
 * Common dependencies for typical java/kotlin/android projects
 *
 * @see <a href="https://github.com/langara/deps.kt">https://github.com/langara/deps.kt</a>
 */

object vers {
    val kotlin = "1.2.21"
    val kotlinxCoroutines = "0.22"
    val androidGradlePlugin = "3.2.0-alpha02"
    val androidMavenGradlePlugin = "2.0"
    val androidCompileSdk = "27"
    val androidMinSdk = "23"
    val androidTargetSdk = "27"
    val androidBuildTools = "27.0.3"
    val androidSupport = "27.0.2"
    val androidSupportConstraint = "1.0.2"
    val androidSupportTest = "1.0.1"
    val androidEspresso = "3.0.1"
    val androidCommons = "0.0.21"
    val rxjava = "2.1.1"
    val rxkotlin = "2.1.0"
    val rxbinding = "2.0.0"
    val rxrelay = "2.0.0"
    val rxandroid = "2.0.1"
    val rxlifecycle = "2.2.1"
    val retrofit = "2.3.0"
    val okhttp = "3.9.1"
    val javaWebsocket = "1.3.4"
    val playServices = "11.8.0"
    val picasso = "2.5.2"
    val materialDialogs = "0.9.6.0"
    val leakcanary = "1.5.4"
    val paperwork = "1.2.7"
    val mockitoKotlin = "2.0.0-alpha02"
    val mockitoAndroid = "2.10.0"
    val junit = "4.12"
    val googleTruth = "0.28"
    val androidTestRunnerClass = "android.support.test.runner.AndroidJUnitRunner"
}

private fun dep(group: String, name: String, version: String) = mapOf(
        "group" to group,
        "name" to name,
        "version" to version
)

object deps {
    val kotlinGradlePlugin = dep("org.jetbrains.kotlin", "kotlin-gradle-plugin", vers.kotlin)
    val androidGradlePlugin = dep("com.android.tools.build", "gradle", vers.androidGradlePlugin)
    val androidMavenGradlePlugin = dep("com.github.dcendents", "android-maven-gradle-plugin", vers.androidMavenGradlePlugin)
    val kotlinStdlib = dep("org.jetbrains.kotlin", "kotlin-stdlib-jre7", vers.kotlin)
    val kotlinxCoroutinesCore = dep("org.jetbrains.kotlinx", "kotlinx-coroutines-core", vers.kotlinxCoroutines)
    val androidSupportV4 = dep("com.android.support", "support-v4", vers.androidSupport)
    val androidSupportV13 = dep("com.android.support", "support-v13", vers.androidSupport)
    val androidSupportAppcompat = dep("com.android.support", "appcompat-v7", vers.androidSupport)
    val androidSupportRecyclerview = dep("com.android.support", "recyclerview-v7", vers.androidSupport)
    val androidSupportCardview = dep("com.android.support", "cardview-v7", vers.androidSupport)
    val androidSupportDesign = dep("com.android.support", "design", vers.androidSupport)
    val androidSupportAnnotations = dep("com.android.support", "support-annotations", vers.androidSupport)
    val androidSupportPreference = dep("com.android.support", "preference-v14", vers.androidSupport)
    val androidSupportCustomtabs = dep("com.android.support", "customtabs", vers.androidSupport)
    val androidSupportPercent = dep("com.android.support", "percent", vers.androidSupport)
    val androidSupportConstraint = dep("com.android.support.constraint", "constraint-layout", vers.androidSupportConstraint)
    val androidEspresso = dep("com.android.support.test.espresso", "espresso-core", vers.androidEspresso)
    val rxjava = dep("io.reactivex.rxjava2", "rxjava", vers.rxjava)
    val rxkotlin = dep("io.reactivex.rxjava2", "rxkotlin", vers.rxkotlin)
    val rxrelay = dep("com.jakewharton.rxrelay2", "rxrelay", vers.rxrelay)
    val rxbindingKotlin = dep("com.jakewharton.rxbinding2", "rxbinding-kotlin", vers.rxbinding)
    val rxlifecycleComponents = dep("com.trello.rxlifecycle2", "rxlifecycle-components", vers.rxlifecycle)
    val rxlifecycleKotlin = dep("com.trello.rxlifecycle2", "rxlifecycle-kotlin", vers.rxlifecycle)
    val retrofit = dep("com.squareup.retrofit2", "retrofit", vers.retrofit)
    val retrofitMoshi = dep("com.squareup.retrofit2", "converter-moshi", vers.retrofit)
    val retrofitRxjava = dep("com.squareup.retrofit2", "adapter-rxjava2", vers.retrofit)
    val okhttp = dep("com.squareup.okhttp3", "okhttp", vers.okhttp)
    val okhttpLogging = dep("com.squareup.okhttp3", "logging-interceptor", vers.okhttp)
    val javaWebsocket = dep("org.java-websocket", "Java-WebSocket", vers.javaWebsocket)
    val playServicesBase = dep("com.google.android.gms", "play-services-base", vers.playServices)
    val firebaseAppIndexing = dep("com.google.firebase", "firebase-appindexing", vers.playServices)
    val picasso = dep("com.squareup.picasso", "picasso", vers.picasso)
    val materialDialogsCore = dep("com.afollestad.material-dialogs", "core", vers.materialDialogs)
    val materialDialogsCommons = dep("com.afollestad.material-dialogs", "commons", vers.materialDialogs)
    val leakcanary = dep("com.squareup.leakcanary", "leakcanary-android", vers.leakcanary)
    val leakcanaryNoOp = dep("com.squareup.leakcanary", "leakcanary-android-no-op", vers.leakcanary)
    val paperwork = dep("hu.supercluster", "paperwork", vers.paperwork)
    val paperworkPlugin = dep("hu.supercluster", "paperwork-plugin", vers.paperwork)
    val junit = dep("junit", "junit", vers.junit)
    val googleTruth = dep("com.google.truth", "truth", vers.googleTruth)
    val mockitoKotlin = dep("com.nhaarman.mockitokotlin2", "mockito-kotlin", vers.mockitoKotlin)
    val androidTestRunner = dep("com.android.support.test", "runner", vers.androidSupportTest)
    val androidTestRules = dep("com.android.support.test", "rules", vers.androidSupportTest)
}


