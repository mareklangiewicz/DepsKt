@file:Suppress("unused", "DEPRECATION", "SpellCheckingInspection", "MemberVisibilityCanBePrivate")

package pl.mareklangiewicz.deps

import org.gradle.api.artifacts.dsl.DependencyHandler

/**
 * Common dependencies for java/kotlin/android projects
 *
 * @see <a href="https://github.com/langara/deps.kt">https://github.com/langara/deps.kt</a>
 */
object Deps {
    private val kotlin = dep("org.jetbrains.kotlin", "", Vers.kotlin)
    val kotlinGradlePlugin = kotlin withName "kotlin-gradle-plugin"
    val kotlin14GradlePlugin = kotlinGradlePlugin ver Vers.kotlin14
    val kotlin15GradlePlugin = kotlinGradlePlugin ver Vers.kotlin15
    val androidGradlePlugin = dep("com.android.tools.build", "gradle", Vers.androidGradlePlugin)

    @Deprecated("Use https://developer.android.com/studio/build/maven-publish-plugin")
    val androidMavenGradlePlugin = dep("com.github.dcendents", "android-maven-gradle-plugin", Vers.androidMavenGradlePlugin)
    val nexusPublishGradlePlugin = dep("io.github.gradle-nexus", "publish-plugin", Vers.nexusPublishGradlePlugin)

    val kotlinStdlib7 = kotlin withName "kotlin-stdlib-jdk7"
    val kotlinStdlib8 = kotlin withName "kotlin-stdlib-jdk8"
    val kotlinScriptRuntime = kotlin withName "kotlin-script-runtime"
    val kotlinReflect = kotlin withName "kotlin-reflect"
    val kotlinTestCommon = kotlin withName "kotlin-test-common"
    val kotlinTestAnnotationsCommon = kotlin withName "kotlin-test-annotations-common"
    val kotlinTestJUnit = kotlin withName "kotlin-test-junit"
    val kotlinTestJs = kotlin withName "kotlin-test-js"

    val composeDesktopGradlePlugin = dep("org.jetbrains.compose", "compose-gradle-plugin", Vers.composeDesktop)
    val composeAndroidAnimation = dep("androidx.compose.animation", "animation", Vers.composeAndroid)
    val composeAndroidAnimationCore = composeAndroidAnimation withName "animation-core"
    val composeAndroidCompiler = dep("androidx.compose.compiler", "compiler", Vers.composeAndroidCompiler)
    val composeAndroidFoundation = dep("androidx.compose.foundation", "foundation", Vers.composeAndroid)
    val composeAndroidFoundationLayout = composeAndroidFoundation withName "foundation-layout"
    val composeAndroidFoundationShape = composeAndroidFoundation withName "foundation-shape"
    val composeAndroidFoundationText = composeAndroidFoundation withName "foundation-text"
    val composeAndroidMaterial3 = dep("androidx.compose.material3", "material3", Vers.composeAndroidMaterial3)
    val composeAndroidMaterial = dep("androidx.compose.material", "material", Vers.composeAndroid)
    val composeAndroidMaterialIcons = composeAndroidMaterial withName "material-icons"
    val composeAndroidRuntime = dep("androidx.compose.runtime", "runtime", Vers.composeAndroid)
    val composeAndroidRuntimeDispatch = composeAndroidRuntime withName "runtime-dispatch"
    val composeAndroidRuntimeFrames = composeAndroidRuntime withName "runtime-frames"
    val composeAndroidUi = dep("androidx.compose.ui", "ui", Vers.composeAndroid)
    val composeAndroidUiGeometry = composeAndroidUi withName "ui-geometry"
    val composeAndroidUiGraphics = composeAndroidUi withName "ui-graphics"
    val composeAndroidUiPlatform = composeAndroidUi withName "ui-platform"
    val composeAndroidUiTest = composeAndroidUi withName "ui-test"
    val composeAndroidUiTestJUnit4 = composeAndroidUi withName "ui-test-junit4"
    val composeAndroidUiTestManifest = composeAndroidUi withName "ui-test-manifest"
    val composeAndroidUiTooling = composeAndroidUi withName "ui-tooling"

    private val googleAccompanist = dep("com.google.accompanist", "", Vers.googleAccompanist)
    val googleAccompanistSytemUiController = googleAccompanist withName "accompanist-systemuicontroller"
    val googleAccompanistSwipeRefresh = googleAccompanist withName "accompanist-swiperefresh"
    val googleAccompanistPlaceholder = googleAccompanist withName "accompanist-placeholder"
    val googleAccompanistPlaceholderMaterial = googleAccompanist withName "accompanist-placeholder-material"
    val googleAccompanistPermissions = googleAccompanist withName "accompanist-permissions"
    val googleAccompanistPageIndicators = googleAccompanist withName "accompanist-page-indicators"
    val googleAccompanistPager = googleAccompanist withName "accompanist-pager"
    val googleAccompanistNavigationMaterial = googleAccompanist withName "accompanist-navigation-material"
    val googleAccompanistNavigationAnimation = googleAccompanist withName "accompanist-navigation-animation"
    val googleAccompanistInsets = googleAccompanist withName "accompanist-insets"
    val googleAccompanistInsetsUi = googleAccompanist withName "accompanist-insets-ui"
    val googleAccompanistFlowLayout = googleAccompanist withName "accompanist-flowlayout"
    val googleAccompanistDrawablePainter = googleAccompanist withName "accompanist-drawablepainter"
    val googleAccompanistAppCompatTheme = googleAccompanist withName "accompanist-appcompat-theme"
    val googleAccompanistImageLoadingCore = googleAccompanist withName "accompanist-imageloading-core" ver Vers.googleAccompanistImage
    val googleAccompanistGlide = googleAccompanist withName "accompanist-glide" ver Vers.googleAccompanistImage
    val googleAccompanistCoil = googleAccompanist withName "accompanist-coil" ver Vers.googleAccompanistImage
    val googleAccompanistPicasso = googleAccompanist withName "accompanist-picasso" ver Vers.googleAccompanistPicasso

    val kotlinxDateTime = dep("org.jetbrains.kotlinx", "kotlinx-datetime", Vers.kotlinxDateTime)

    val kotlinxSerializationCore = dep("org.jetbrains.kotlinx", "kotlinx-serialization-core", Vers.kotlinxSerialization)
    val kotlinxSerializationJson = kotlinxSerializationCore withName "kotlinx-serialization-json"

    val kotlinxAtomicFuCommon = dep("org.jetbrains.kotlinx", "atomicfu-common", Vers.kotlinxAtomicFu)
    val kotlinxAtomicFuGradlePlugin = kotlinxAtomicFuCommon withName "atomicfu-gradle-plugin"

    val kotlinxHtml = dep("org.jetbrains.kotlinx", "kotlinx-html", Vers.kotlinxHtml)
    val kotlinxHtmlJvm = kotlinxHtml withName "kotlinx-html-jvm"
    val kotlinxHtmlJs = kotlinxHtml withName "kotlinx-html-js"

    val kotlinxNodeJs = dep("org.jetbrains.kotlinx", "kotlinx-nodejs", Vers.kotlinxNodeJs)

    private val kotlinxCoroutines = dep("org.jetbrains.kotlinx", "", Vers.kotlinxCoroutines)
    val kotlinxCoroutinesCommon = kotlinxCoroutines withName "kotlinx-coroutines-core-common"
    val kotlinxCoroutinesCore = kotlinxCoroutines withName "kotlinx-coroutines-core"
    val kotlinxCoroutinesDebug = kotlinxCoroutines withName "kotlinx-coroutines-debug"
    val kotlinxCoroutinesTest = kotlinxCoroutines withName "kotlinx-coroutines-test"
    val kotlinxCoroutinesReactive = kotlinxCoroutines withName "kotlinx-coroutines-reactive"
    val kotlinxCoroutinesReactor = kotlinxCoroutines withName "kotlinx-coroutines-reactor"
    val kotlinxCoroutinesRx2 = kotlinxCoroutines withName "kotlinx-coroutines-rx2"
    val kotlinxCoroutinesRx3 = kotlinxCoroutines withName "kotlinx-coroutines-rx3"
    val kotlinxCoroutinesAndroid = kotlinxCoroutines withName "kotlinx-coroutines-android"
    val kotlinxCoroutinesJavaFx = kotlinxCoroutines withName "kotlinx-coroutines-javafx"
    val kotlinxCoroutinesSwing = kotlinxCoroutines withName "kotlinx-coroutines-swing"
    val kotlinxCoroutinesJdk8 = kotlinxCoroutines withName "kotlinx-coroutines-jdk8"
    val kotlinxCoroutinesGuava = kotlinxCoroutines withName "kotlinx-coroutines-quava"
    val kotlinxCoroutinesSlf4j = kotlinxCoroutines withName "kotlinx-coroutines-slf4j"
    val kotlinxCoroutinesPlayServices = kotlinxCoroutines withName "kotlinx-coroutines-play-services"

    // just a reference - not useful in typical cases
    const val gradleBaseUrl = "https://services.gradle.org/distributions"
    const val gradleUrlBin = "$gradleBaseUrl/gradle-${Vers.gradle}-bin.zip"
    const val gradleUrlAll = "$gradleBaseUrl/gradle-${Vers.gradle}-all.zip"

    val androidxCore = dep("androidx.core", "core", Vers.androidxCore)
    val androidxCoreKtx = dep("androidx.core", "core-ktx", Vers.androidxCore)
    val androidxActivityKtx = dep("androidx.activity", "activity-ktx", Vers.androidxActivity)
    val androidxActivityCompose = dep("androidx.activity", "activity-compose", Vers.androidxActivity)
    val androidxAppcompat = dep("androidx.appcompat", "appcompat", Vers.androidxAppcompat)
    val androidxRecyclerview = dep("androidx.recyclerview", "recyclerview", Vers.androidxRecyclerview)
    val androidxCardview = dep("androidx.cardview", "cardview", Vers.androidxCardview)
    val androidMaterial = dep("com.google.android.material", "material", Vers.androidMaterial)
    val androidxAnnotation = dep("androidx.annotation", "annotation", Vers.androidxAnnotation)
    val androidxPreference = dep("androidx.preference", "preference", Vers.androidxPreference)
    val androidxPreferenceKtx = androidxPreference withName "preference-ktx"
    val androidxBrowser = dep("androidx.browser", "browser", Vers.androidxBrowser)
    val androidxBrowserHelper = dep("com.google.androidbrowserhelper", "androidbrowserhelper", Vers.androidxBrowserHelper)
    val androidxPercentLayout = dep("androidx.percentlayout", "percentlayout", Vers.androidxPercentLayout)
    val androidxFlexboxLayout = dep("com.google.android", "flexbox", Vers.androidxFlexboxLayout)
    val androidxConstraint1 = dep("androidx.constraintlayout", "constraintlayout", Vers.androidxConstraint1)
    val androidxConstraint2 = androidxConstraint1 ver Vers.androidxConstraint2
    val androidxConstraint = androidxConstraint1
    val androidxConstraint1Solver = androidxConstraint1  withName "constraintlayout-solver"
    val androidxConstraint2Solver = androidxConstraint1Solver ver Vers.androidxConstraint2
    val androidxConstraintSolver = androidxConstraint1Solver

    val androidxLifecycleCommon = dep("androidx.lifecycle", "lifecycle-common", Vers.androidxLifecycle)
    val androidxLifecycleCompiler = androidxLifecycleCommon  withName "lifecycle-compiler"
    val androidxLifecycleExtensions = androidxLifecycleCommon  withName "lifecycle-extensions"
    val androidxLifecycleRuntime = androidxLifecycleCommon  withName "lifecycle-runtime"
    val androidxLifecycleRuntimeKtx = androidxLifecycleCommon  withName "lifecycle-runtime-ktx"
    val androidxLifecycleLiveData = androidxLifecycleCommon  withName "lifecycle-livedata"
    val androidxLifecycleLiveDataCore = androidxLifecycleCommon  withName "lifecycle-livedata-core"
    val androidxLifecycleLiveDataCoreKtx = androidxLifecycleCommon  withName "lifecycle-livedata-core-ktx"
    val androidxLifecycleViewModel = androidxLifecycleCommon  withName "lifecycle-viewmodel"
    val androidxLifecycleViewModelKtx = androidxLifecycleCommon  withName "lifecycle-viewmodel-ktx"

    private val androidxCamera = dep("androidx.camera", "", Vers.androidxCamera)
    val androidxCameraCore = androidxCamera withName "camera-core"
    val androidxCameraCamera2 = androidxCamera withName "camera-camera2"
    val androidxCameraLifecycle = androidxCamera withName "camera-lifecycle"
    val androidxCameraVideo = androidxCamera withName "camera-video"
    val androidxCameraExtensions = androidxCamera withName "camera-extensions" ver Vers.androidxCameraExtensions
    val androidxCameraView = androidxCamera withName "camera-view" ver Vers.androidxCameraView

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
    val rxkotlin = dep("io.reactivex.rxjava3", "rxkotlin", Vers.rxkotlin)
    val rxandroid = dep("io.reactivex.rxjava3", "rxandroid", Vers.rxandroid)
    val rxrelay = dep("com.jakewharton.rxrelay3", "rxrelay", Vers.rxrelay)
    val rxbinding = dep("com.jakewharton.rxbinding4", "rxbinding", Vers.rxbinding)
    val rxbindingCore = rxbinding  withName "rxbinding-core"
    val rxbindingAppCompat = rxbinding  withName "rxbinding-appcompat"
    val rxbindingDrawerLayout = rxbinding  withName "rxbinding-drawerlayout"
    val rxbindingLeanback = rxbinding  withName "rxbinding-leanback"
    val rxbindingRecyclerView = rxbinding  withName "rxbinding-recyclerview"
    val rxbindingSlidingPaneLayout = rxbinding  withName "rxbinding-slidingpanelayout"
    val rxbindingSwipeRefreshLayout = rxbinding  withName "rxbinding-swiperefreshlayout"
    val rxbindingViewPager = rxbinding  withName "rxbinding-viewpager"
    val rxlifecycleComponents = dep("com.trello.rxlifecycle2", "rxlifecycle-components", Vers.rxlifecycle)
    val rxlifecycleKotlin = dep("com.trello.rxlifecycle2", "rxlifecycle-kotlin", Vers.rxlifecycle)
    val retrofit = dep("com.squareup.retrofit2", "retrofit", Vers.retrofit)
    val retrofitMoshi = dep("com.squareup.retrofit2", "converter-moshi", Vers.retrofit)
    val retrofitRxjava = dep("com.squareup.retrofit2", "adapter-rxjava2", Vers.retrofit)
    val okhttp = dep("com.squareup.okhttp3", "okhttp", Vers.okhttp)
    val okhttpLogging = dep("com.squareup.okhttp3", "logging-interceptor", Vers.okhttp)
    val okioBoM = dep("com.squareup.okio", "okio-bom", Vers.okio)
    val okio = dep("com.squareup.okio", "okio", Vers.okio)
    val okioNodeFileSystem = dep("com.squareup.okio", "okio-nodefakefilesystem-js", Vers.okio)
    val okioFakeFileSystem = dep("com.squareup.okio", "okio-fakefilesystem", Vers.okio)
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

    val firebaseAdmin = dep("com.google.firebase", "firebase-admin", Vers.firebaseAdmin)

    val firebaseAndroidBoM = dep("com.google.firebase", "firebase-bom", Vers.firebaseAndroidBoM)
    val firebaseAnalyticsKtx = dep("com.google.firebase", "firebase-analytics-ktx")
    val firebaseCrashlyticsKtx = dep("com.google.firebase", "firebase-crashlytics-ktx")
    val firebaseAppIndexingKtx = dep("com.google.firebase", "firebase-appindexing-ktx")
    val firebaseFirestoreKtx = dep("com.google.firebase", "firebase-firestore-ktx")
    val firebaseFunctionsKtx = dep("com.google.firebase", "firebase-functions-ktx")
    val firebaseAuthKtx = dep("com.google.firebase", "firebase-auth-ktx")
    val firebaseMessagingKtx = dep("com.google.firebase", "firebase-messaging-ktx")
    val firebaseInAppMessagingKtx = dep("com.google.firebase", "firebase-inappmessaging-ktx")
    val firebasePerformanceKtx = dep("com.google.firebase", "firebase-perf-ktx")
    val firebaseStorageKtx = dep("com.google.firebase", "firebase-storage-ktx")
    val firebaseRealtimeDbKtx = dep("com.google.firebase", "firebase-database-ktx")
    val firebaseRemoteConfigKtx = dep("com.google.firebase", "firebase-config-ktx")
    val firebaseDynamicLinksKtx = dep("com.google.firebase", "firebase-dynamic-links-ktx")

    val firebaseUiAuth = dep("com.firebaseui", "firebase-ui-auth", Vers.firebaseUiAuth)

    val googleCloudBoM = dep("com.google.cloud", "libraries-bom", Vers.googleCloudBoM)
        // FIXME: some extension functions for BoM deps, so its easier to add it to multiplatform projects than:
        // implementation(project.dependencies.platform(Deps.googleCloudBoM))
        // and to make it impossible to mistakenly add it as normal dependency like:
        // implementation(Deps.googleCloudBoM)

    val googleCloudStorage = dep("com.google.cloud", "google-cloud-storage")
    val googleCloudFirestore = dep("com.google.cloud", "google-cloud-firestore")

    val googleAuthCredentials = dep("com.google.auth", "google-auth-library-credentials", Vers.googleAuth)
    val googleAuthOAuth2Http = dep("com.google.auth", "google-auth-library-oauth2-http", Vers.googleAuth)
    val googleAuthAppEngine = dep("com.google.auth", "google-auth-library-appengine", Vers.googleAuth)

    val googleGuava = dep("com.google.guava", "guava") // ver from googleCloudBoM
    val googleGuavaJre = googleGuava ver Vers.googleGuavaJre
    val googleGuavaAndroid = googleGuava ver Vers.googleGuavaAndroid

    val googleGuavaMissingMetadataPlugin = dep("de.jjohannes.gradle", "missing-metadata-guava", Vers.googleGuavaMissingMetadataPlugin)

    val picasso = dep("com.squareup.picasso", "picasso", Vers.picasso)
    val materialDialogs = dep("com.afollestad.material-dialogs", "core", Vers.materialDialogs)
    val leakcanary = dep("com.squareup.leakcanary", "leakcanary-android", Vers.leakcanary)
    val paperwork = dep("hu.supercluster", "paperwork", Vers.paperwork)
    val paperworkPlugin = dep("hu.supercluster", "paperwork-plugin", Vers.paperwork)
    val junit4 = dep("junit", "junit", Vers.junit4)
    val junit5 = dep("org.junit.jupiter", "junit-jupiter-api", Vers.junit5)
    val junit5engine = dep("org.junit.jupiter", "junit-jupiter-engine", Vers.junit5)

    val googleTruth = dep("com.google.truth", "truth", Vers.googleTruth)

    val mockitoCore2 = dep("org.mockito", "mockito-core", Vers.mockitoCore2)
    val mockitoCore3 = dep("org.mockito", "mockito-core", Vers.mockitoCore3)
    val mockitoCore4 = dep("org.mockito", "mockito-core", Vers.mockitoCore4)
    val mockitoCore = mockitoCore4

    val mockitoKotlin2 = dep("org.mockito.kotlin", "mockito-kotlin", Vers.mockitoKotlin2)
    val mockitoKotlin3 = dep("org.mockito.kotlin", "mockito-kotlin", Vers.mockitoKotlin3)
    val mockitoKotlin4 = dep("org.mockito.kotlin", "mockito-kotlin", Vers.mockitoKotlin4)
    val mockitoKotlin = mockitoKotlin4

    val mockitoAndroid2 = dep("org.mockito", "mockito-android", Vers.mockitoAndroid2)
    val mockitoAndroid3 = dep("org.mockito", "mockito-android", Vers.mockitoAndroid3)
    val mockitoAndroid4 = dep("org.mockito", "mockito-android", Vers.mockitoAndroid4)
    val mockitoAndroid = mockitoAndroid4

    val androidxTestRunner = dep("androidx.test", "runner", Vers.androidxTestRunner)
    val androidxTestRules = dep("androidx.test", "rules", Vers.androidxTestRules)
    val androidxTestExtTruth = dep("androidx.test.ext", "truth", Vers.androidxTestExtTruth)
    val androidxTestExtJUnit = dep("androidx.test.ext", "junit", Vers.androidxTestExtJUnit)
    val androidxTestExtJUnitKtx = dep("androidx.test.ext", "junit-ktx", Vers.androidxTestExtJUnit)
    val realmGradlePlugin = dep("io.realm", "realm-gradle-plugin", Vers.realm)
    private val ktor = dep("io.ktor", "", Vers.ktor)
    val ktorServerCore = ktor withName "ktor-server-core"
    val ktorServerCio = ktor withName "ktor-server-cio"
    val ktorServerNetty = ktor withName "ktor-server-netty"
    val ktorTlsCertificates = ktor withName "ktor-network-tls-certificates"
    val ktorAuth = ktor withName "ktor-auth"
    val ktorClientCore = ktor withName "ktor-client-core"
    val ktorClientJs = ktor withName "ktor-client-js"
    val ktorClientCio = ktor withName "ktor-client-cio"
    val ktorClientApache = ktor withName "ktor-client-apache"
    private val rsocket = dep("io.rsocket.kotlin", "", Vers.rsocket)
    val rsocketCore = rsocket withName "rsocket-core"
    val rsocketKtor = rsocket withName "rsocket-transport-ktor"
    val rsocketKtorClient = rsocket withName "rsocket-transport-ktor-client"
    val rsocketKtorServer = rsocket withName "rsocket-transport-ktor-server"
    val splitties = dep("com.louiscad.splitties", "splitties-fun-pack-android-material-components-with-views-dsl", Vers.splitties)
    val docoptJava = dep("com.offbytwo", "docopt", Vers.docoptJava)

    private const val kotlinJsWrappersGroup = "org.jetbrains.kotlin-wrappers"
    val kotlinJsWrappersBoM = dep(kotlinJsWrappersGroup, "kotlin-wrappers-bom", Vers.kotlinJsWrappersBoM)
    val kotlinJsWrappersReact = dep(kotlinJsWrappersGroup, "kotlin-react")
    val kotlinJsWrappersReactDom = dep(kotlinJsWrappersGroup, "kotlin-react-dom")
    val kotlinJsWrappersStyled = dep(kotlinJsWrappersGroup, "kotlin-styled")



    const val marekGroup = "pl.mareklangiewicz"

    val tuplek = dep(marekGroup, "tuplek", Vers.tuplek)
    val abcdk = dep(marekGroup, "abcdk", Vers.abcdk)
    val rxmock = dep(marekGroup, "rxmock", Vers.rxmock)
    val smokk = dep(marekGroup, "smokk", Vers.smokk)

    val uspek = dep(marekGroup, "uspek", Vers.uspek)
    val uspekx = dep(marekGroup, "uspekx", Vers.uspek)

    val upue = dep(marekGroup, "upue", Vers.upue)
    val upueTest = dep(marekGroup, "upue-test", Vers.upue)

    val kommandLine = dep(marekGroup, "kommandline", Vers.kommandLine)
    val dbusKotlin = dep(marekGroup, "dbus-kotlin", Vers.dbusKotlin)
    val sandboxui = dep(marekGroup, "sandboxui", Vers.sandboxui)
    val recyclerui = dep(marekGroup, "recyclerui", Vers.recyclerui)




    private fun dep(group: String, name: String, version: String? = null): String =
        if (version === null) "$group:$name" else "$group:$name:$version"

    private infix fun String.withName(name: String) = split(":")
        .mapIndexed { i: Int, s: String -> if (i == 1) name else s }
        .joinToString(":")

    private infix fun String.ver(v: String) = (split(":").take(2) + v)
        .joinToString(":")

    fun DependencyHandler.addAll(configuration: String, vararg deps: String) {
        for (dep in deps) add(configuration, dep)
    }

    val String.group get() = split(":").first()
    val String.artifact get() = split(":")[1]
}

