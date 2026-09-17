package pl.mareklangiewicz.templatefun

import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.dsl.*
import org.gradle.api.publish.*
import org.gradle.api.publish.maven.*
import org.gradle.kotlin.dsl.*
import com.android.build.api.dsl.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.defaults.*

// region [[Andro Common Build Template]]

/**
 * MIGRATED to the sibling model: android settings arrive as a SCOPE, so the
 * `?: error("No andro settings.")` that opened this function is gone — it cannot be called at all
 * without android.
 *
 * `composeConfiguredByMpp` is gone from here too, but NOT because a scope replaced it. The
 * compose-android dependencies moved to [defaultComposeAndroDeps], which requires a compose scope,
 * so the routing decision now lives with the caller — the only place that knows BOTH whether
 * compose exists and whether it was already configured the MPP way. The flag survives at those call
 * sites, under its honest name.
 */
context(flags: LibFlags, andro: LibAndro)
fun DependencyHandler.defaultAndroDeps(
  configuration: String = "implementation",
) {
  addAll(
    configuration,
    AndroidX.Core.ktx,
    AndroidX.AppCompat.appcompat.takeIf { andro.withAppCompat },
    AndroidX.Activity.compose.takeIf { andro.withActivityCompose }, // this should NOT depend on composeConfiguredByMpp!
    AndroidX.Lifecycle.compiler.takeIf { andro.withLifecycle },
    AndroidX.Lifecycle.runtime_ktx.takeIf { andro.withLifecycle },
    // TODO_someday_maybe: more lifecycle related stuff by default (viewmodel, compose)?
    Com.Google.Android.Material.material.takeIf { andro.withMDC },
  )
}

/**
 * The compose-android dependencies [defaultAndroDeps] used to add behind `settings.compose!!`.
 * As a compose scope there is no `!!` and no presence check: having it is the precondition.
 */
context(compose: LibCompose)
fun DependencyHandler.defaultComposeAndroDeps(configuration: String = "implementation") {
  addAllWithVer(
    configuration,
    Vers.ComposeAndro,
    AndroidX.Compose.Ui.ui,
    AndroidX.Compose.Ui.tooling,
    AndroidX.Compose.Ui.tooling_preview,
    AndroidX.Compose.Material.material.takeIf { compose.withComposeMaterial2 },
  )
  addAll(
    configuration,
    AndroidX.Compose.Material3.material3.takeIf { compose.withComposeMaterial3 },
  )
}

/** Migrated like [defaultAndroDeps]: android is a scope, compose routing belongs to the caller. */
context(flags: LibFlags, andro: LibAndro)
fun DependencyHandler.defaultAndroTestDeps(
  configuration: String = "testImplementation",
  // Which JUnit reaches THIS configuration. Host tests take the plain flags; android DEVICE tests
  // have their own flag (withTestJUnit4OnAndroidDevice) and cannot take JUnit5 at all, so the
  // caller that knows which compilation it is configuring passes that in. Without this the device
  // configuration silently misses uspekx-junit4 and its @RunWith(USpekJUnit4Runner) stops resolving.
  withJUnit4: Boolean = flags.withTestJUnit4,
  withJUnit5: Boolean = flags.withTestJUnit5,
) {
  addAll(
    configuration,
    AndroidX.Test.Espresso.core.takeIf { andro.withTestEspresso },
    Com.Google.Truth.truth.takeIf { flags.withTestGoogleTruth },
    AndroidX.Test.rules,
    AndroidX.Test.runner,
    AndroidX.Test.Ext.truth.takeIf { flags.withTestGoogleTruth },
    Org.Mockito.Kotlin.mockito_kotlin.takeIf { flags.withTestMockitoKotlin },
  )

  if (withJUnit4) {
    addAll(
      configuration,
      Kotlin.test_junit.withVer(Vers.Kotlin),
      JUnit.junit,
      Langiewicz.uspekx_junit4.takeIf { flags.withTestUSpekX },
      AndroidX.Test.Ext.junit_ktx,
    )
  }
  // android doesn't fully support JUnit5, but adding deps anyway to be able to write JUnit5 dependent code
  if (withJUnit5) {
    addAll(
      configuration,
      Kotlin.test_junit5.withVer(Vers.Kotlin),
      Org.JUnit.Jupiter.junit_jupiter_api,
      Org.JUnit.Jupiter.junit_jupiter_engine,
      Langiewicz.uspekx_junit5.takeIf { flags.withTestUSpekX },
    )
  }

}

/** The compose-android TEST dependencies, likewise requiring a compose scope. */
context(flags: LibFlags, compose: LibCompose)
fun DependencyHandler.defaultComposeAndroTestDeps(configuration: String = "testImplementation") =
  addAllWithVer(
    configuration,
    vers.ComposeAndro,
    AndroidX.Compose.Ui.test,
    AndroidX.Compose.Ui.test_manifest,
    AndroidX.Compose.Ui.test_junit4.takeIf { flags.withTestJUnit4 },
  )

fun MutableSet<String>.defaultAndroExcludedResources() = addAll(
  listOf(
    "**/*.md",
    "**/attach_hotspot_windows.dll",
    "META-INF/licenses/**",
    "META-INF/AL2.0",
    "META-INF/LGPL2.1",
    "META-INF/kotlinx_coroutines_core.version",
  ),
)

fun CommonExtension.defaultCompileOptions(
  jvmVer: String? = null, // it's better to use jvmToolchain (normally done in fun allDefault)
) = compileOptions.apply {
  jvmVer?.let {
    sourceCompatibility = JavaVersion.toVersion(it)
    targetCompatibility = JavaVersion.toVersion(it)
  }
}

fun CommonExtension.defaultComposeStuff() {
  buildFeatures.compose = true
}

fun CommonExtension.defaultPackagingOptions() = packaging.apply {
  resources.excludes.defaultAndroExcludedResources()
}

/** Use template-andro/build.gradle.kts:fun defaultAndroLibPublishAllVariants() to create component with name "default". */
context(_: LibInfo)
fun Project.defaultPublishingOfAndroLib(componentName: String = "default") {
  afterEvaluate {
    extensions.configure<PublishingExtension> {
      publications.register<MavenPublication>(componentName) {
        from(components[componentName])
        pom { defaultPOM() }
      }
    }
  }
}

context(_: LibInfo)
fun Project.defaultPublishingOfAndroApp(componentName: String = "release") =
  defaultPublishingOfAndroLib(componentName)


// endregion [[Andro Common Build Template]]

// region [[Andro Lib Build Template]]

/**
 * MIGRATED. Was the design note's literal example of a helper reaching through the tree
 * (`settings.andro!!`). As a scope the `!!` is gone, and so is the `if (settings.withAndro)` guard
 * its caller needed: `lib.andro?.let { context(it) { androDefault() } }` is one expression that
 * both tests presence and supplies the value.
 */
context(info: LibInfo, andro: LibAndro)
fun KotlinMultiplatformExtension.androDefault() {
  extensions.configure<KotlinMultiplatformAndroidLibraryTarget> {
    minSdk { version = release(andro.sdkMin) }
    compileSdk {
      version = andro.sdkCompilePreview?.let { preview(it) }
        ?: release(andro.sdkCompile) { minorApiLevel = andro.sdkCompileMinor }
    }
    namespace = info.namespace
    withHostTest {
    }
    withDeviceTest {
      instrumentationRunner = andro.withTestRunner
    }
  }
}

fun Project.defaultBuildTemplateForAndroLib(
  lib: Lib = gradle.extLib,
  addAndroMainDependencies: KotlinDependencyHandler.() -> Unit = {},
): Unit = context(lib.info, lib.flags) {
  // THE boundary. One check turns "details that may or may not have android" into an andro scope;
  // everything below is statically guaranteed and carries no `!!` and no `?: error`. Presence-as-
  // scope does not delete this check, it moves it to exactly one place per entry point.
  val andro = lib.andro ?: error("No andro settings.")
  repositories { context(lib.repos) { addRepos() } }
  // Since AGP 9 the 'com.android.library' plugin cannot be combined with KMP, so an android
  // library is a KMP module with the 'com.android.kotlin.multiplatform.library' target.
  // LibraryExtension is not applied at all any more.
  extensions.configure<KotlinMultiplatformExtension> {
    context(andro) { androDefault() } // details is already in scope, so only the andro half is added
    jvmToolchain(lib.flags.withJvmVer?.toInt() ?: 17) // works for jvm and android
    sourceSets.getByName("androidMain").dependencies { addAndroMainDependencies() }
  }
  // The KMP android target names its configurations per source set, so the plain
  // "implementation"/"testImplementation" of the old com.android.library path do not exist.
  // androidDeviceTest replaces what the template script used to ask for as
  // "androidTestImplementation" -- it could not ask any more anyway, because
  // defaultAndroTestDeps takes its settings as a context parameter now.
  dependencies {
    context(andro) {
      defaultAndroDeps(configuration = "androidMainImplementation")
      defaultAndroTestDeps(configuration = "androidHostTestImplementation")
      // The device configuration states its JUnit explicitly, exactly as this function's kdoc asks
      // and as defaultBuildTemplateForFullMppLib already did. Riding on the plain flags here is
      // what the kdoc warns about: JUnit5 cannot run on a device at all, and JUnit4 has its own
      // flag, without which @RunWith(USpekJUnit4Runner) does not resolve.
      defaultAndroTestDeps(
        configuration = "androidDeviceTestImplementation",
        withJUnit4 = lib.flags.withTestJUnit4OnAndroidDevice,
        withJUnit5 = false,
      )
    }
    // compose-android deps only when compose EXISTS (scope opens) — no boolean, no !!
    lib.compose?.let { compose ->
      context(compose) { defaultComposeAndroDeps(configuration = "androidMainImplementation") }
      context(compose) { defaultComposeAndroTestDeps(configuration = "androidHostTestImplementation") }
      // Device tests were missing these entirely, so a compose UI test on a device could not
      // resolve setContent. Only visible once a lib actually HAS device-test sources.
      context(compose) { defaultComposeAndroTestDeps(configuration = "androidDeviceTestImplementation") }
    }
  }
  // Same upstream bug, same fix as in [defaultBuildTemplateForComposeMppLib]: the compose plugin
  // registers CopyResourcesToAndroidAssetsTask for the device-test compilation without an
  // outputDirectory, so merely CONFIGURING it fails with "Value not set". An android lib reaches
  // that compilation without going through the compose MPP chain, so the workaround has to exist
  // here too -- otherwise :<lib>:compileAndroidDeviceTest cannot even configure, which is what
  // kept template-andro's device tests from ever compiling.
  tasks.matching { it.name == "copyAndroidDeviceTestComposeResourcesToAndroidAssets" }
    .configureEach { enabled = false }
  configurations.checkVerSync(warnOnly = true)
  tasks.defaultKotlinCompileOptions(
    jvmTargetVer = null, // jvmVer is set jvmToolchain in fun allDefault
  )
  defaultGroupAndVerAndDescription(lib)
  if (plugins.hasPlugin("com.vanniktech.maven.publish")) defaultPublishing()
  else println("Andro Lib Module ${name}: publishing (and signing) disabled")
}

/**
 * NOTE: dead code since the AGP 9 migration — nothing calls this, because an android library is now
 * a KMP module with `com.android.kotlin.multiplatform.library` and [LibraryExtension] is never
 * applied. Migrated anyway to keep the family consistent, but UNEXERCISED by any build: the four
 * templates cannot prove it, only that it compiles.
 *
 * @param configureComposeAndro caller decided compose exists AND was not configured the MPP way.
 */
context(info: LibInfo, andro: LibAndro)
fun LibraryExtension.defaultAndroLib(
  configureComposeAndro: Boolean = false,
  ignoreAndroPublish: Boolean = false, // so user have to explicitly say IF he wants to ignore it.
) {
  andro.sdkCompilePreview?.let { compileSdkPreview = it } ?: run {
    compileSdk = andro.sdkCompile
    compileSdkMinor = andro.sdkCompileMinor
  }
  defaultCompileOptions(jvmVer = null) // actually it does nothing now. jvm ver is normally configured via jvmToolchain
  defaultDefaultConfig()
  defaultBuildTypes()
  if (configureComposeAndro) defaultComposeStuff()
  defaultPackagingOptions()
  if (!ignoreAndroPublish && andro.publishAllVariants) defaultAndroLibPublishAllVariants()
  if (!ignoreAndroPublish && andro.publishOneVariant) defaultAndroLibPublishVariant(andro.publishVariant)
}

/** Dead code alongside [defaultAndroLib]; migrated for consistency, unexercised. */
context(info: LibInfo, andro: LibAndro)
fun LibraryExtension.defaultDefaultConfig() = defaultConfig {
  namespace = info.namespace
  minSdk = andro.sdkMin
  testInstrumentationRunner = andro.withTestRunner
}

fun LibraryExtension.defaultBuildTypes() = buildTypes { release { isMinifyEnabled = false } }

fun LibraryExtension.defaultAndroLibPublishVariant(
  variant: String = "debug",
  withSources: Boolean = true,
  withJavadoc: Boolean = false,
) {
  publishing {
    singleVariant(variant) {
      if (withSources) withSourcesJar()
      if (withJavadoc) withJavadocJar()
    }
  }
}

fun LibraryExtension.defaultAndroLibPublishAllVariants(
  withSources: Boolean = true,
  withJavadoc: Boolean = false,
) {
  publishing {
    multipleVariants {
      allVariants()
      if (withSources) withSourcesJar()
      if (withJavadoc) withJavadocJar()
    }
  }
}


// endregion [[Andro Lib Build Template]]

// region [[Andro App Build Template]]

fun Project.defaultBuildTemplateForAndroApp(
  lib: Lib = gradle.extLib,
  addAndroDependencies: DependencyHandler.() -> Unit = {},
): Unit = context(lib.info, lib.flags) {
  // Same single boundary as the lib entry point above.
  val andro = lib.andro ?: error("No andro settings.")
  require(!andro.publishAllVariants) { "Only single app variant can be published" }
  val variant = andro.publishVariant.takeIf { andro.publishOneVariant }
  repositories { context(lib.repos) { addRepos() } }
  extensions.configure<ApplicationExtension> {
    context(andro) { defaultAndroApp(configureComposeAndro = lib.compose != null) }
    variant?.let { defaultAndroAppPublishVariant(it) }
  }
  dependencies {
    context(andro) {
      defaultAndroDeps()
      defaultAndroTestDeps()
    }
    lib.compose?.let { compose ->
      context(compose) { defaultComposeAndroDeps() }
      context(compose) { defaultComposeAndroTestDeps() }
    }
    add("debugImplementation", AndroidX.Tracing.ktx) // https://github.com/android/android-test/issues/1755
    addAndroDependencies()
  }
  configurations.checkVerSync(warnOnly = true)
  tasks.defaultKotlinCompileOptions(
    jvmTargetVer = null, // jvmVer is set jvmToolchain in fun allDefault
  )
  defaultGroupAndVerAndDescription(lib)
  variant?.let { defaultPublishingOfAndroApp(it) }
}


/** @param configureComposeAndro caller decided compose exists AND was not configured the MPP way. */
context(info: LibInfo, andro: LibAndro)
fun ApplicationExtension.defaultAndroApp(
  configureComposeAndro: Boolean = false,
) {
  andro.sdkCompilePreview?.let { compileSdkPreview = it } ?: run {
    compileSdk = andro.sdkCompile
    compileSdkMinor = andro.sdkCompileMinor
  }
  defaultDefaultConfig()
  defaultBuildTypes()
  if (configureComposeAndro) defaultComposeStuff()
}

context(info: LibInfo, andro: LibAndro)
fun ApplicationExtension.defaultDefaultConfig() = defaultConfig {
  applicationId = info.id
  namespace = info.namespace
  andro.sdkTargetPreview?.let { targetSdkPreview = it } ?: run { targetSdk = andro.sdkTarget }
  minSdk = andro.sdkMin
  versionCode = info.appVerCode
  versionName = info.appVerName
  testInstrumentationRunner = andro.withTestRunner
}

fun ApplicationExtension.defaultBuildTypes() = buildTypes { getByName("release") { isMinifyEnabled = false } }

fun ApplicationExtension.defaultAndroAppPublishVariant(
  variant: String = "debug",
  publishAPK: Boolean = true,
  publishAAB: Boolean = false,
) {
  require(!publishAAB || !publishAPK) { "Either APK or AAB can be published, but not both." }
  publishing { singleVariant(variant) { if (publishAPK) publishApk() } }
}

// endregion [[Andro App Build Template]]
