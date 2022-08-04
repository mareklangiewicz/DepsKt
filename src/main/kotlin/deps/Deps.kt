@file:Suppress("unused", "DEPRECATION", "SpellCheckingInspection", "MemberVisibilityCanBePrivate", "PackageDirectoryMismatch")

package pl.mareklangiewicz.deps

import org.gradle.api.artifacts.dsl.DependencyHandler

val libs = LibsDetails // instead of import - to avoid circular init problem
val vers = Vers // instead of import - to avoid circular init problem

/**
 * Common dependencies for java/kotlin/android projects
 *
 * @see <a href="https://github.com/langara/deps.kt">https://github.com/langara/deps.kt</a>
 */
object Deps {
    private val kotlin = dep("org.jetbrains.kotlin", "", vers.kotlin)
    val kotlinGradlePlugin = kotlin withName "kotlin-gradle-plugin"
    val kotlin14GradlePlugin = kotlinGradlePlugin ver vers.kotlin14
    val kotlin15GradlePlugin = kotlinGradlePlugin ver vers.kotlin15
    val androidGradlePlugin = dep("com.android.tools.build", "gradle", vers.androidGradlePlugin)

    @Deprecated("Use https://developer.android.com/studio/build/maven-publish-plugin")
    val androidMavenGradlePlugin = dep("com.github.dcendents", "android-maven-gradle-plugin", vers.androidMavenGradlePlugin)
    val nexusPublishGradlePlugin = dep("io.github.gradle-nexus", "publish-plugin", vers.nexusPublishGradlePlugin)

    val osackyDoctorPlugin = dep("com.osacky.doctor", "doctor-plugin", vers.osackyDoctorPlugin)

    val kotlinStdlib7 = kotlin withName "kotlin-stdlib-jdk7"
    val kotlinStdlib8 = kotlin withName "kotlin-stdlib-jdk8"
    val kotlinScriptRuntime = kotlin withName "kotlin-script-runtime"
    val kotlinReflect = kotlin withName "kotlin-reflect"
    val kotlinTestCommon = kotlin withName "kotlin-test-common"
    val kotlinTestAnnotationsCommon = kotlin withName "kotlin-test-annotations-common"
    val kotlinTestJUnit = kotlin withName "kotlin-test-junit"
    val kotlinTestJs = kotlin withName "kotlin-test-js"

    val composeJbGradlePlugin = dep("org.jetbrains.compose", "compose-gradle-plugin", vers.composeJb)

    val composeCompilerAndroidx = dep("androidx.compose.compiler", "compiler", vers.composeCompiler)
    val composeCompilerAndroidxDev = dep("androidx.compose.compiler", "compiler", vers.composeCompiler)

    // No version to use it in hotfix with module substitution (see: template-mpp/build.gradle/kts)
    val composeCompilerJbDev = dep("org.jetbrains.compose.compiler", "compiler")

    val composeAndroidAnimation = dep("androidx.compose.animation", "animation", vers.composeAndroid)
    val composeAndroidAnimationCore = composeAndroidAnimation withName "animation-core"
    val composeAndroidFoundation = dep("androidx.compose.foundation", "foundation", vers.composeAndroid)
    val composeAndroidFoundationLayout = composeAndroidFoundation withName "foundation-layout"
    val composeAndroidFoundationShape = composeAndroidFoundation withName "foundation-shape"
    val composeAndroidFoundationText = composeAndroidFoundation withName "foundation-text"
    val composeAndroidMaterial3 = dep("androidx.compose.material3", "material3", vers.composeAndroidMaterial3)
    val composeAndroidMaterial = dep("androidx.compose.material", "material", vers.composeAndroid)
    val composeAndroidMaterialIcons = composeAndroidMaterial withName "material-icons"
    val composeAndroidRuntime = dep("androidx.compose.runtime", "runtime", vers.composeAndroid)
    val composeAndroidRuntimeDispatch = composeAndroidRuntime withName "runtime-dispatch"
    val composeAndroidRuntimeFrames = composeAndroidRuntime withName "runtime-frames"
    val composeAndroidUi = dep("androidx.compose.ui", "ui", vers.composeAndroid)
    val composeAndroidUiGeometry = composeAndroidUi withName "ui-geometry"
    val composeAndroidUiGraphics = composeAndroidUi withName "ui-graphics"
    val composeAndroidUiPlatform = composeAndroidUi withName "ui-platform"
    val composeAndroidUiTest = composeAndroidUi withName "ui-test"
    val composeAndroidUiTestJUnit4 = composeAndroidUi withName "ui-test-junit4"
    val composeAndroidUiTestManifest = composeAndroidUi withName "ui-test-manifest"
    val composeAndroidUiTooling = composeAndroidUi withName "ui-tooling"
    val composeAndroidUiToolingPreview = composeAndroidUi withName "ui-tooling-preview"

    private val googleAccompanist = dep("com.google.accompanist", "", vers.googleAccompanist)
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
    val googleAccompanistImageLoadingCore = googleAccompanist withName "accompanist-imageloading-core" ver vers.googleAccompanistImage
    val googleAccompanistGlide = googleAccompanist withName "accompanist-glide" ver vers.googleAccompanistImage
    val googleAccompanistCoil = googleAccompanist withName "accompanist-coil" ver vers.googleAccompanistImage
    val googleAccompanistPicasso = googleAccompanist withName "accompanist-picasso" ver vers.googleAccompanistPicasso

    val kotlinxDateTime = dep("org.jetbrains.kotlinx", "kotlinx-datetime", vers.kotlinxDateTime)

    val kotlinxSerializationCore = dep("org.jetbrains.kotlinx", "kotlinx-serialization-core", vers.kotlinxSerialization)
    val kotlinxSerializationJson = kotlinxSerializationCore withName "kotlinx-serialization-json"

    val kotlinxAtomicFuCommon = dep("org.jetbrains.kotlinx", "atomicfu-common", vers.kotlinxAtomicFu)
    val kotlinxAtomicFuGradlePlugin = kotlinxAtomicFuCommon withName "atomicfu-gradle-plugin"

    val kotlinxHtml = dep("org.jetbrains.kotlinx", "kotlinx-html", vers.kotlinxHtml)
    val kotlinxHtmlJvm = kotlinxHtml withName "kotlinx-html-jvm"
    val kotlinxHtmlJs = kotlinxHtml withName "kotlinx-html-js"

    val kotlinxNodeJs = dep("org.jetbrains.kotlinx", "kotlinx-nodejs", vers.kotlinxNodeJs)

    private val kotlinxCoroutines = dep("org.jetbrains.kotlinx", "", vers.kotlinxCoroutines)
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
    const val gradleUrlBin = "$gradleBaseUrl/gradle-${vers.gradle}-bin.zip"
    const val gradleUrlAll = "$gradleBaseUrl/gradle-${vers.gradle}-all.zip"

    val androidxCore = dep("androidx.core", "core", vers.androidxCore)
    val androidxCoreKtx = dep("androidx.core", "core-ktx", vers.androidxCore)
    val androidxActivityKtx = dep("androidx.activity", "activity-ktx", vers.androidxActivity)
    val androidxActivityCompose = dep("androidx.activity", "activity-compose", vers.androidxActivity)
    val androidxAppcompat = dep("androidx.appcompat", "appcompat", vers.androidxAppcompat)
    val androidxRecyclerview = dep("androidx.recyclerview", "recyclerview", vers.androidxRecyclerview)
    val androidxCardview = dep("androidx.cardview", "cardview", vers.androidxCardview)
    val androidMaterial = dep("com.google.android.material", "material", vers.androidMaterial)
    val androidxAnnotation = dep("androidx.annotation", "annotation", vers.androidxAnnotation)
    val androidxPreference = dep("androidx.preference", "preference", vers.androidxPreference)
    val androidxPreferenceKtx = androidxPreference withName "preference-ktx"
    val androidxBrowser = dep("androidx.browser", "browser", vers.androidxBrowser)
    val androidxBrowserHelper = dep("com.google.androidbrowserhelper", "androidbrowserhelper", vers.androidxBrowserHelper)
    val androidxPercentLayout = dep("androidx.percentlayout", "percentlayout", vers.androidxPercentLayout)
    val androidxFlexboxLayout = dep("com.google.android", "flexbox", vers.androidxFlexboxLayout)
    val androidxConstraint1 = dep("androidx.constraintlayout", "constraintlayout", vers.androidxConstraint1)
    val androidxConstraint2 = androidxConstraint1 ver vers.androidxConstraint2
    val androidxConstraint = androidxConstraint1
    val androidxConstraint1Solver = androidxConstraint1  withName "constraintlayout-solver"
    val androidxConstraint2Solver = androidxConstraint1Solver ver vers.androidxConstraint2
    val androidxConstraintSolver = androidxConstraint1Solver

    val androidxNavigationUiKtx = dep("androidx.navigation", "navigation-ui-ktx", vers.androidxNavigation)
    val androidxNavigationFragmentKtx = androidxNavigationUiKtx withName "fragment-ktx"
    val androidxNavigationDynamicFeaturesFragment = androidxNavigationUiKtx withName "navigation-dynamic-features-fragment"
    val androidxNavigationTesting = androidxNavigationUiKtx withName "navigation-testing"
    val androidxNavigationCompose = androidxNavigationUiKtx withName "navigation-compose"

    val androidxLifecycleCommon = dep("androidx.lifecycle", "lifecycle-common", vers.androidxLifecycle)
    val androidxLifecycleCompiler = androidxLifecycleCommon  withName "lifecycle-compiler"
    val androidxLifecycleExtensions = androidxLifecycleCommon  withName "lifecycle-extensions"
    val androidxLifecycleRuntime = androidxLifecycleCommon  withName "lifecycle-runtime"
    val androidxLifecycleRuntimeKtx = androidxLifecycleCommon  withName "lifecycle-runtime-ktx"
    val androidxLifecycleLiveData = androidxLifecycleCommon  withName "lifecycle-livedata"
    val androidxLifecycleLiveDataCore = androidxLifecycleCommon  withName "lifecycle-livedata-core"
    val androidxLifecycleLiveDataCoreKtx = androidxLifecycleCommon  withName "lifecycle-livedata-core-ktx"
    val androidxLifecycleViewModel = androidxLifecycleCommon  withName "lifecycle-viewmodel"
    val androidxLifecycleViewModelKtx = androidxLifecycleCommon  withName "lifecycle-viewmodel-ktx"

    private val androidxCamera = dep("androidx.camera", "", vers.androidxCamera)
    val androidxCameraCore = androidxCamera withName "camera-core"
    val androidxCameraCamera2 = androidxCamera withName "camera-camera2"
    val androidxCameraLifecycle = androidxCamera withName "camera-lifecycle"
    val androidxCameraVideo = androidxCamera withName "camera-video"
    val androidxCameraExtensions = androidxCamera withName "camera-extensions" ver vers.androidxCameraExtensions
    val androidxCameraView = androidxCamera withName "camera-view" ver vers.androidxCameraView

    val androidxRoomRuntime = dep("androidx.room", "room-runtime", vers.androidxRoom)
    val androidxRoomCompiler = dep("androidx.room", "room-compiler", vers.androidxRoom)
    val androidxRoomKtx = dep("androidx.room", "room-ktx", vers.androidxRoom)
    val androidxRoomRxJava2 = dep("androidx.room", "room-rxjava2", vers.androidxRoom)
    val androidxRoomTesting = dep("androidx.room", "room-testing", vers.androidxRoom)

    val androidxEspressoAccessibility = dep("androidx.test.espresso", "espresso-accessibility", vers.androidxEspresso)
    val androidxEspressoContrib = dep("androidx.test.espresso", "espresso-contrib", vers.androidxEspresso)
    val androidxEspressoCore = dep("androidx.test.espresso", "espresso-core", vers.androidxEspresso)
    val androidxEspressoIdlingResource = dep("androidx.test.espresso", "espresso-idling-resource", vers.androidxEspresso)
    val androidxEspressoIntents = dep("androidx.test.espresso", "espresso-intents", vers.androidxEspresso)
    val androidxEspressoRemote = dep("androidx.test.espresso", "espresso-remote", vers.androidxEspresso)
    val androidxEspressoWeb = dep("androidx.test.espresso", "espresso-web", vers.androidxEspresso)

    val androidCommonsEspresso = dep("com.github.elpassion.android-commons", "espresso", vers.androidCommons)
    val androidCommonsRxJavaTest = dep("com.github.elpassion.android-commons", "rxjava-test", vers.androidCommons)
    val androidCommonsSharedPrefs = dep("com.github.elpassion.android-commons", "shared-preferences", vers.androidCommons)
    val androidCommonsSharedPrefsMoshi = dep("com.github.elpassion.android-commons", "shared-preferences-moshi-converter-adapter", vers.androidCommons)
    val androidCommonsSharedPrefsGson = dep("com.github.elpassion.android-commons", "shared-preferences-gson-converter-adapter", vers.androidCommons)
    val androidCommonsView = dep("com.github.elpassion.android-commons", "view", vers.androidCommons)
    val androidCommonsPager = dep("com.github.elpassion.android-commons", "pager", vers.androidCommons)
    val androidCommonsRecycler = dep("com.github.elpassion.android-commons", "recycler", vers.androidCommons)

    val rxjava2 = dep("io.reactivex.rxjava2", "rxjava", vers.rxjava2)
    val rxjava3 = dep("io.reactivex.rxjava3", "rxjava", vers.rxjava3)
    val rxkotlin = dep("io.reactivex.rxjava3", "rxkotlin", vers.rxkotlin)
    val rxandroid = dep("io.reactivex.rxjava3", "rxandroid", vers.rxandroid)
    val rxrelay = dep("com.jakewharton.rxrelay3", "rxrelay", vers.rxrelay)
    val rxbinding = dep("com.jakewharton.rxbinding4", "rxbinding", vers.rxbinding)
    val rxbindingCore = rxbinding  withName "rxbinding-core"
    val rxbindingAppCompat = rxbinding  withName "rxbinding-appcompat"
    val rxbindingDrawerLayout = rxbinding  withName "rxbinding-drawerlayout"
    val rxbindingLeanback = rxbinding  withName "rxbinding-leanback"
    val rxbindingRecyclerView = rxbinding  withName "rxbinding-recyclerview"
    val rxbindingSlidingPaneLayout = rxbinding  withName "rxbinding-slidingpanelayout"
    val rxbindingSwipeRefreshLayout = rxbinding  withName "rxbinding-swiperefreshlayout"
    val rxbindingViewPager = rxbinding  withName "rxbinding-viewpager"
    val rxlifecycleComponents = dep("com.trello.rxlifecycle2", "rxlifecycle-components", vers.rxlifecycle)
    val rxlifecycleKotlin = dep("com.trello.rxlifecycle2", "rxlifecycle-kotlin", vers.rxlifecycle)
    val retrofit = dep("com.squareup.retrofit2", "retrofit", vers.retrofit)
    val retrofitMoshi = dep("com.squareup.retrofit2", "converter-moshi", vers.retrofit)
    val retrofitRxjava = dep("com.squareup.retrofit2", "adapter-rxjava2", vers.retrofit)
    val okhttp = dep("com.squareup.okhttp3", "okhttp", vers.okhttp)
    val okhttpLogging = dep("com.squareup.okhttp3", "logging-interceptor", vers.okhttp)
    val okioBoM = dep("com.squareup.okio", "okio-bom", vers.okio)
    val okio = dep("com.squareup.okio", "okio", vers.okio)
    val okioNodeFileSystem = dep("com.squareup.okio", "okio-nodefakefilesystem-js", vers.okio)
    val okioFakeFileSystem = dep("com.squareup.okio", "okio-fakefilesystem", vers.okio)
    val dbusJava = dep("com.github.hypfvieh", "dbus-java", vers.dbusJava)
    val dbusJavaOsgi = dep("com.github.hypfvieh", "dbus-java-osgi", vers.dbusJava)
    val dbusJavaUtils = dep("com.github.hypfvieh", "dbus-java-utils", vers.dbusJava)
    val javaWebsocket = dep("org.java-websocket", "java-websocket", vers.javaWebsocket)
    val slf4jSimple = dep("org.slf4j", "slf4j-simple", vers.slf4jSimple)
    val log4j2api = dep("org.apache.logging.log4j", "log4j-api", vers.log4j2)
    val log4j2core = dep("org.apache.logging.log4j", "log4j-core", vers.log4j2)

    val googleServicesPlugin = dep("com.google.gms", "google-services", vers.googleServicesPlugin)
    val googlePlayServicesBase = dep("com.google.android.gms", "play-services-base", vers.googlePlayServicesBase)

    val firebaseGitliveAuth = dep("dev.gitlive", "firebase-auth", vers.firebaseGitlive)
    val firebaseGitliveDB = dep("dev.gitlive", "firebase-database", vers.firebaseGitlive)
    val firebaseGitliveFirestore = dep("dev.gitlive", "firebase-firestore", vers.firebaseGitlive)
    val firebaseGitliveFunctions = dep("dev.gitlive", "firebase-functions", vers.firebaseGitlive)
    val firebaseGitliveMessaging = dep("dev.gitlive", "firebase-messaging", vers.firebaseGitlive)
    val firebaseGitliveStorage = dep("dev.gitlive", "firebase-storage", vers.firebaseGitlive)
    val firebaseCrashlyticsPlugin = dep("com.google.firebase", "firebase-crashlytics-gradle", vers.firebaseCrashlyticsPlugin)

    val firebaseAdmin = dep("com.google.firebase", "firebase-admin", vers.firebaseAdmin)

    val firebaseAndroidBoM = dep("com.google.firebase", "firebase-bom", vers.firebaseAndroidBoM)
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

    val firebaseUiAuth = dep("com.firebaseui", "firebase-ui-auth", vers.firebaseUiAuth)

    val googleCloudBoM = dep("com.google.cloud", "libraries-bom", vers.googleCloudBoM)
        // FIXME: some extension functions for BoM deps, so its easier to add it to multiplatform projects than:
        // implementation(project.dependencies.platform(deps.googleCloudBoM))
        // and to make it impossible to mistakenly add it as normal dependency like:
        // implementation(deps.googleCloudBoM)

    val googleCloudStorage = dep("com.google.cloud", "google-cloud-storage")
    val googleCloudFirestore = dep("com.google.cloud", "google-cloud-firestore")

    val googleAuthCredentials = dep("com.google.auth", "google-auth-library-credentials", vers.googleAuth)
    val googleAuthOAuth2Http = dep("com.google.auth", "google-auth-library-oauth2-http", vers.googleAuth)
    val googleAuthAppEngine = dep("com.google.auth", "google-auth-library-appengine", vers.googleAuth)

    val googleGuava = dep("com.google.guava", "guava") // ver from googleCloudBoM
    val googleGuavaJre = googleGuava ver vers.googleGuavaJre
    val googleGuavaAndroid = googleGuava ver vers.googleGuavaAndroid

    val googleGuavaMissingMetadataPlugin = dep("de.jjohannes.gradle", "missing-metadata-guava", vers.googleGuavaMissingMetadataPlugin)

    val picasso = dep("com.squareup.picasso", "picasso", vers.picasso)
    val materialDialogs = dep("com.afollestad.material-dialogs", "core", vers.materialDialogs)
    val leakcanary = dep("com.squareup.leakcanary", "leakcanary-android", vers.leakcanary)
    val paperwork = dep("hu.supercluster", "paperwork", vers.paperwork)
    val paperworkPlugin = dep("hu.supercluster", "paperwork-plugin", vers.paperwork)
    val junit4 = dep("junit", "junit", vers.junit4)
    val junit5 = dep("org.junit.jupiter", "junit-jupiter-api", vers.junit5)
    val junit5engine = dep("org.junit.jupiter", "junit-jupiter-engine", vers.junit5)

    val googleTruth = dep("com.google.truth", "truth", vers.googleTruth)

    val mockitoCore2 = dep("org.mockito", "mockito-core", vers.mockitoCore2)
    val mockitoCore3 = dep("org.mockito", "mockito-core", vers.mockitoCore3)
    val mockitoCore4 = dep("org.mockito", "mockito-core", vers.mockitoCore4)
    val mockitoCore = mockitoCore4

    val mockitoKotlin2 = dep("org.mockito.kotlin", "mockito-kotlin", vers.mockitoKotlin2)
    val mockitoKotlin3 = dep("org.mockito.kotlin", "mockito-kotlin", vers.mockitoKotlin3)
    val mockitoKotlin4 = dep("org.mockito.kotlin", "mockito-kotlin", vers.mockitoKotlin4)
    val mockitoKotlin = mockitoKotlin4

    val mockitoAndroid2 = dep("org.mockito", "mockito-android", vers.mockitoAndroid2)
    val mockitoAndroid3 = dep("org.mockito", "mockito-android", vers.mockitoAndroid3)
    val mockitoAndroid4 = dep("org.mockito", "mockito-android", vers.mockitoAndroid4)
    val mockitoAndroid = mockitoAndroid4

    val androidxTestRunner = dep("androidx.test", "runner", vers.androidxTestRunner)
    val androidxTestRules = dep("androidx.test", "rules", vers.androidxTestRules)
    val androidxTestExtTruth = dep("androidx.test.ext", "truth", vers.androidxTestExtTruth)
    val androidxTestExtJUnit = dep("androidx.test.ext", "junit", vers.androidxTestExtJUnit)
    val androidxTestExtJUnitKtx = dep("androidx.test.ext", "junit-ktx", vers.androidxTestExtJUnit)
    val realmGradlePlugin = dep("io.realm", "realm-gradle-plugin", vers.realm)
    private val ktor = dep("io.ktor", "", vers.ktor)
    val ktorServerCore = ktor withName "ktor-server-core"
    val ktorServerCio = ktor withName "ktor-server-cio"
    val ktorServerNetty = ktor withName "ktor-server-netty"
    val ktorTlsCertificates = ktor withName "ktor-network-tls-certificates"
    val ktorAuth = ktor withName "ktor-auth"
    val ktorClientCore = ktor withName "ktor-client-core"
    val ktorClientJs = ktor withName "ktor-client-js"
    val ktorClientCio = ktor withName "ktor-client-cio"
    val ktorClientApache = ktor withName "ktor-client-apache"
    private val rsocket = dep("io.rsocket.kotlin", "", vers.rsocket)
    val rsocketCore = rsocket withName "rsocket-core"
    val rsocketKtor = rsocket withName "rsocket-transport-ktor"
    val rsocketKtorClient = rsocket withName "rsocket-transport-ktor-client"
    val rsocketKtorServer = rsocket withName "rsocket-transport-ktor-server"
    val splitties = dep("com.louiscad.splitties", "splitties-fun-pack-android-material-components-with-views-dsl", vers.splitties)
    val docoptJava = dep("com.offbytwo", "docopt", vers.docoptJava)

    private const val kotlinJsWrappersGroup = "org.jetbrains.kotlin-wrappers"
    val kotlinJsWrappersBoM = dep(kotlinJsWrappersGroup, "kotlin-wrappers-bom", vers.kotlinJsWrappersBoM)
    val kotlinJsWrappersReact = dep(kotlinJsWrappersGroup, "kotlin-react")
    val kotlinJsWrappersReactDom = dep(kotlinJsWrappersGroup, "kotlin-react-dom")
    val kotlinJsWrappersStyled = dep(kotlinJsWrappersGroup, "kotlin-styled")



    const val marekGroup = "pl.mareklangiewicz"

    val uspek = libs.USpek.dep("uspek")
    val uspekx = libs.USpek.dep("uspekx")
    val uspekxJUnit4 = libs.USpek.dep("uspekx-junit4")
    val uspekxJUnit5 = libs.USpek.dep("uspekx-junit5")

    val smokk = libs.SMokK.dep("smokk")
    val smokkx = libs.SMokK.dep("smokkx")
    val rxmock = libs.RxMock.dep()

    val abcdk = libs.AbcdK.dep()
    val tuplek = libs.TupleK.dep()
    val upue = libs.UPue.dep()
    val upueTest = libs.UPue.dep("upue-test")
    val kommandLine = libs.KommandLine.dep()
    val uwidgets = libs.UWidgets.dep()
    val areakim = libs.AreaKim.dep()
    val dbusKotlin = dep(marekGroup, "dbus-kotlin", vers.dbusKotlin)
    val sandboxui = dep(marekGroup, "sandboxui", vers.sandboxui)
    val recyclerui = dep(marekGroup, "recyclerui", vers.recyclerui)
    val templateMPP = libs.TemplateMPP.dep()
    val templateAndro = libs.TemplateAndro.dep("template-andro-lib")




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