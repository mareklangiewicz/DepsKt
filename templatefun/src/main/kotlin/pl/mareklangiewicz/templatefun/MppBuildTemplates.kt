package pl.mareklangiewicz.templatefun

import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.compose.*
import org.jetbrains.compose.desktop.*
import org.jetbrains.compose.resources.*
import org.jetbrains.compose.desktop.application.dsl.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.gradle.kotlin.dsl.*
import com.android.build.api.dsl.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.defaults.*

// region [[Full MPP Lib Build Template]]

fun Project.defaultBuildTemplateForFullMppLib(
  lib: Lib = gradle.extLib,
  addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
): Unit = context(lib.info, lib.flags) {
  if (lib.andro != null) {
    // Since AGP 9 'com.android.library' cannot be combined with KMP; the android target of a
    // KMP library comes from 'com.android.kotlin.multiplatform.library' instead, exactly as
    // template-raw already does.
    apply(plugin = plugs.AndroKmpNoVer.group) // group is actually id for plugins
  }
  defaultBuildTemplateForComposeMppLib(
    lib = lib,
    ignoreAndroConfig = true, // andro configured below
    ignoreAndroPublish = true, // andro publishing configured below (or ignored again, but below in defaultAndroLib)
    addCommonMainDependencies = addCommonMainDependencies,
  )

  // `withAndro` is gone: presence IS the scope, so the guard and the value arrive together, and the
  // body below cannot be entered without one. Three reads of `details.settings.withAndro` became one.
  lib.andro?.let { andro ->
    extensions.configure<KotlinMultiplatformExtension> {
      context(andro) { androDefault() }
    }

    // The KMP android target names its configurations per source set, so the plain
    // "implementation"/"testImplementation" of the old com.android.library path are gone -- which
    // means these helpers now declare into MPP source-set configurations. That IS the "mpp way";
    // the 2023-12-17 note that used to sit here ("it would be more correct to configure everything
    // mpp way, but it is more important to reuse defaultAndroDeps") posed a tradeoff that no longer
    // exists, so both halves of it are satisfied at once and the note is retired.
    dependencies {
      // compose is configured the MPP way already, so we simply do NOT open a compose scope here:
      // the compose-android deps live in defaultComposeAndroDeps and are unreachable without one.
      // That is the composeConfiguredByMpp routing, expressed by omission instead of by a boolean.
      context(andro) {
        defaultAndroDeps(configuration = "androidMainImplementation")
        defaultAndroTestDeps(configuration = "androidHostTestImplementation")
        // The device-test compilation needs its own deps: source sets get configurations per source
        // set, so androidHostTest's do NOT reach it. template-raw restated a hand-written list here;
        // reusing the same flag-driven helper instead is the whole point of having one template.
        defaultAndroTestDeps(
          configuration = "androidDeviceTestImplementation",
          withJUnit4 = lib.flags.withTestJUnit4OnAndroidDevice,
          withJUnit5 = false, // JUnit5 is not supported on android device tests.
        )
      }
    }
  }
}


// endregion [[Full MPP Lib Build Template]]

// region [[MPP Module Build Template]]

/**
 * Only for very standard small libs. In most cases it's better to not use this function.
 *
 * These ignoreXXX flags are hacky, but needed. see [allDefault] kdoc for details.
 */
fun Project.defaultBuildTemplateForBasicMppLib(
  lib: Lib = gradle.extLib,
  ignoreCompose: Boolean = false, // so user have to explicitly say THAT he wants to ignore compose settings here.
  ignoreAndroConfig: Boolean = false, // so user have to explicitly say THAT he wants to ignore it.
  ignoreAndroPublish: Boolean = false, // so user have to explicitly say THAT he wants to ignore it.
  addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
): Unit = context(lib.info, lib.flags) {
  // These three still guard CONTENT, not presence, which is why they stay here and not lower down:
  // this is the level that can see whether a compose/andro scope exists at all. See the kdoc below.
  require(ignoreCompose || lib.compose == null) { "defaultBuildTemplateForBasicMppLib can not configure compose stuff" }
  lib.andro?.let {
    require(ignoreAndroConfig) { "defaultBuildTemplateForBasicMppLib can not configure android stuff (besides just adding target)" }
    require(ignoreAndroPublish || it.publishNoVariants) { "defaultBuildTemplateForBasicMppLib can not publish android stuff YET" }
  }
  repositories { context(lib.repos) { addRepos() } }
  defaultGroupAndVerAndDescription(lib)
  extensions.configure<KotlinMultiplatformExtension> {
    // Four booleans became zero: allDefault sees the platform/testing flags and nothing else.
    // Whether compose or andro get configured is decided HERE, by which scopes are opened, not
    // there by which booleans were forwarded.
    allDefault(addCommonMainDependencies = addCommonMainDependencies)
  }
  configurations.checkVerSync(warnOnly = true)
  tasks.defaultKotlinCompileOptions(jvmTargetVer = null) // jvmVer is set in fun allDefault using jvmToolchain
  tasks.defaultTestsOptions(onJvmUseJUnitPlatform = lib.flags.withTestJUnit5)
  if (plugins.hasPlugin("com.vanniktech.maven.publish")) defaultPublishing()
  else println("MPP Module ${name}: publishing (and signing) disabled")
}


/**
 * Only for very standard small libs. In most cases it's better to not use this function.
 *
 * MIGRATED to the sibling model ([LibFlags]). ALL FOUR `ignoreXxx` flags are gone from this
 * function, and so are the three `require`s they guarded:
 *
 * ```
 * require(ignoreCompose || compose == null) { "allDefault can not configure compose stuff" }
 * andro?.let {
 *   require(ignoreAndroConfig) { "allDefault can not configure android stuff (besides just adding target)" }
 *   require(ignoreAndroPublish || it.publishNoVariants) { "allDefault can not publish android stuff YET" }
 * }
 * ```
 *
 * The old kdoc explained the flags as needed "because we want to inject this code also to such build
 * files, where plugins for compose and/or android are not applied at all". [LibFlags] has no
 * `compose` and no `andro` to begin with, so this body cannot name either one, and the caller opts
 * out by not opening those scopes rather than by passing a boolean.
 *
 * Note what did NOT simply vanish. `ignoreAndroPublish` guarded a constraint on the CONTENT of the
 * andro settings (`publishNoVariants`), not on their presence, so it has to live where the content
 * is visible: [defaultBuildTemplateForBasicMppLib], which still holds the whole [Lib].
 * Nothing had to be moved there, because it was ALREADY there — all three `require`s deleted here
 * were verbatim duplicates of checks the entry point performs immediately before calling this
 * function. That duplication is itself a symptom of the nesting: both levels were handed the same
 * over-broad object, so both had to re-assert the same things about it.
 *
 * `ignoreAndroTarget` was already dead here before this migration — the `androidTarget` block it
 * guarded is commented out (see below). With this change it is dead in the whole MPP chain: the
 * entry point now only forwards it to nothing. Removing it from those signatures is a follow-up.
 *
 * kmp andro publishing is in the middle of big changes, so let's not support it yet, and let's wait
 * for more clarity regarding:
 * https://youtrack.jetbrains.com/issue/KT-61575/Publishing-a-KMP-library-handles-Android-target-inconsistently-requiring-an-explicit-publishLibraryVariants-call-to-publish
 * https://youtrack.jetbrains.com/issue/KT-60623/Deprecate-publishAllLibraryVariants-in-kotlin-android
 */
context(flags: LibFlags)
fun KotlinMultiplatformExtension.allDefault(
  addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
) = with(flags) {
  if (withJvm) jvm()
  if (withJs) jsDefault()
  if (withLinuxX64) linuxX64()
  // if (withAndro && !ignoreAndroTarget) androidTarget {
  //   // TODO_someday some kmp andro publishing. See kdoc above why not yet.
  // }
  withJvmVer?.let { jvmToolchain(it.toInt()) } // works for jvm and android
  sourceSets {
    commonMain {
      dependencies {
        if (withKotlinxHtml) implementation(KotlinX.html)
        addCommonMainDependencies()
      }
    }
    commonTest {
      dependencies {
        implementation(Kotlin.test)
        if (withTestUSpekX) implementation(Langiewicz.uspekx)
      }
    }
    if (withJvm) {
      jvmTest {
        dependencies {
          if (withTestJUnit4) implementation(JUnit.junit)
          if (withTestJUnit5) {
            implementation(Org.JUnit.Jupiter.junit_jupiter_engine)
            runtimeOnly(Org.JUnit.Platform.junit_platform_launcher)
          }
          if (withTestUSpekX) {
            implementation(Langiewicz.uspekx)
            if (withTestJUnit4) implementation(Langiewicz.uspekx_junit4)
            if (withTestJUnit5) implementation(Langiewicz.uspekx_junit5)
          }
          if (withTestGoogleTruth) implementation(Com.Google.Truth.truth)
          if (withTestMockitoKotlin) implementation(Org.Mockito.Kotlin.mockito_kotlin)
        }
      }
    }
    if (withLinuxX64) {
      linuxX64Main
      linuxX64Test
    }
  }
}


fun KotlinMultiplatformExtension.jsDefault(
  withBrowser: Boolean = true,
  withNode: Boolean = false,
  testWithChrome: Boolean = true,
  testHeadless: Boolean = true,
) {
  js {
    if (withBrowser) browser {
      testTask {
        useKarma {
          when (testWithChrome to testHeadless) {
            true to true -> useChromeHeadless()
            true to false -> useChrome()
          }
        }
      }
    }
    if (withNode) nodejs()
  }
}

// endregion [[MPP Module Build Template]]

// region [[MPP App Build Template]]

fun Project.defaultBuildTemplateForBasicMppApp(
  lib: Lib = gradle.extLib,
  ignoreCompose: Boolean = false, // so user have to explicitly say THAT he wants to ignore compose settings here.
  ignoreAndroConfig: Boolean = false, // so user have to explicitly say THAT he wants to ignore it.
  addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
): Unit = context(lib.info, lib.flags) {
  defaultBuildTemplateForBasicMppLib(
    lib = lib,
    ignoreCompose = ignoreCompose,
    ignoreAndroConfig = ignoreAndroConfig,
    ignoreAndroPublish = true,
    addCommonMainDependencies = addCommonMainDependencies,
  )
  extensions.configure<KotlinMultiplatformExtension> {
    if (lib.flags.withJvm) jvm {
      mainRun {
        mainClass = lib.info.run { "$appMainPackage.$appMainClass" }
        logger.info("MPP App ${project.name}: MPP plugin (without compose) just adds jvmRun task (experimental). No executable.")
      }
    }
    if (lib.flags.withJs) js {
      binaries.executable()
    }
    if (lib.flags.withLinuxX64) linuxX64 {
      binaries {
        executable {
          entryPoint = lib.info.run { "$appMainPackage.$appMainFun" }
        }
      }
    }
  }
}

// endregion [[MPP App Build Template]]

// region [[Compose MPP Module Build Template]]

/** Only for very standard compose mpp libs. In most cases, it's better to not use this function. */
fun Project.defaultBuildTemplateForComposeMppLib(
  lib: Lib = gradle.extLib,
  ignoreAndroConfig: Boolean = false, // so user have to explicitly say THAT he wants to ignore it.
  ignoreAndroPublish: Boolean = false, // so user have to explicitly say THAT he wants to ignore it.
  addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
): Unit = context(lib.info, lib.flags) {
  // The compose boundary, and the only check: from here down compose is a SCOPE, so nothing below
  // re-tests its presence and nothing carries `settings.compose!!`.
  val compose = lib.compose ?: error("Compose settings not set.")
  if (compose.withComposeTestUiJUnit5)
    logger.warn("Compose UI Tests with JUnit5 are not supported yet! Configuring JUnit5 anyway.")
  defaultBuildTemplateForBasicMppLib(
    lib = lib,
    ignoreCompose = true,
    ignoreAndroConfig = ignoreAndroConfig,
    ignoreAndroPublish = ignoreAndroPublish,
    addCommonMainDependencies = addCommonMainDependencies,
  )
  extensions.configure<KotlinMultiplatformExtension> {
    context(compose) { allDefaultSourceSetsForCompose() }
  }

  // Both of these came from template-raw's own copy of this template, and both are load-bearing:
  // without them :<lib>:compileAndroidDeviceTest fails. They are here rather than there because
  // there is one template now.
  if (lib.andro != null) {
    // Upstream bug: CopyResourcesToAndroidAssetsTask is registered for the device-test compilation
    // without an outputDirectory, so merely CONFIGURING it fails with "Value not set". Nothing in
    // these templates has compose resources for android device tests anyway.
    // Note: `./gradlew build` does NOT reach that compilation, so this failure is invisible to the
    // normal gate -- it takes :<lib>:compileAndroidDeviceTest to see it.
    tasks.matching { it.name == "copyAndroidDeviceTestComposeResourcesToAndroidAssets" }
      .configureEach { enabled = false }
  }

  ((extensions.getByName("compose") as ComposeExtension) as ExtensionAware)
    .extensions.configure<ResourcesExtension> {
      // generateResClass = always
      generateResClass = never
    }
}


/**
 * Normal fun KotlinMultiplatformExtension.allDefault ignores compose stuff,
 * because it's also used for libs without compose plugin.
 * This one does the rest, so it has to be called additionally for compose libs, after .allDefault */
context(flags: LibFlags, compose: LibCompose)
fun KotlinMultiplatformExtension.allDefaultSourceSetsForCompose(
) = with(compose) {
  // Finding #6 in miniature: a context parameter does not shadow a receiver, but it DOES occupy its
  // own name. `compose` was already taken here by the Gradle ComposeExtension, so the extension is
  // the one that had to be renamed -- exactly the collision the DepsKt migration will hit with
  // `repos`. The settings win the bare name because `with` spells their flags out below.
  val composeExt = project.extensions.getByName("compose") as ComposeExtension

  // Manual dependsOn edges below suppress KGP's automatic application of the default hierarchy
  // template, so it has to be applied explicitly -- template-raw's template already does this.
  applyDefaultHierarchyTemplate()

  // Compose UI on js needs the Skiko runtime bundled by webpack, and compose's own
  // checkComposeUiTestConfigurationForJs fails the build when it is not: "no executable binary is
  // declared, so the Skiko runtime required by Compose UI cannot be loaded" (CMP-4906). So the flag
  // carries its own consequence rather than leaving the caller to discover this -- declaring UI on
  // js and declaring the binary that makes UI on js work are one decision, not two.
  if (flags.withJs && withComposeUiOnJs) js { binaries.executable() }

  sourceSets {
    // Compose UI does NOT belong in commonMain: commonMain reaches every target by construction, so
    // js inherited ui/foundation/material and with them skiko, which it cannot bundle without an
    // executable binary (checkComposeUiTestConfigurationForJs). Split it the way template-raw does:
    //
    //   commonMain -> composeMain (runtime only) -> composeUiMain (ui, foundation, material, ...)
    //
    // and hang each platform off the right level: jvm/android take composeUiMain, js takes only
    // composeMain plus compose-html, linuxX64 takes neither (no Compose UI for it upstream yet).
    val composeMain = create("composeMain") {
      dependsOn(commonMain.get())
      dependencies { implementation(ComposeJb.runtime) }
    }
    val composeUiMain = create("composeUiMain") {
      dependsOn(composeMain)
      dependencies {
        if (withComposeUi) {
          implementation(ComposeJb.ui)
          implementation(ComposeJb.componentsResources)
        }
        if (withComposeFoundation) implementation(ComposeJb.foundation)
        if (withComposeFullAnimation) {
          implementation(ComposeJb.animation)
          implementation(ComposeJb.animationGraphics)
        }
        if (withComposeMaterial2) implementation(ComposeJb.material)
        if (withComposeMaterial3) implementation(ComposeJb.material3)
      }
    }
    // The test side mirrors the main side, so compose UI test deps stay off js exactly the way
    // compose UI itself does: jvmTest takes composeUiTest, jsTest only composeTest.
    val composeTest = create("composeTest") {
      // NOT dependsOn(composeMain): template-raw found that edge unnecessary and warning-generating.
      dependsOn(commonTest.get())
    }
    val composeUiTest = create("composeUiTest") {
      // Likewise NOT dependsOn(composeUiMain) -- see template-raw's note on the same pair.
      dependsOn(composeTest)
      dependencies {
        if (withComposeTestUi) implementation(ComposeJb.uiTest)
      }
    }
    // androidMain does not exist yet: the android target is created by androDefault(), which
    // defaultBuildTemplateForFullMppLib runs AFTER this function. configureEach also sees source
    // sets added later, so the edge lands whenever (and only if) the target appears.
    configureEach { if (name == "androidMain") dependsOn(composeUiMain) }
    // Compose UI test deps for android DEVICE tests. These are the androidx artifacts, not the JB
    // ones: on a device it is androidx compose that is actually running. Same configureEach reason
    // as androidMain above -- the android target's source sets do not exist yet at this point.
    configureEach {
      if (name == "androidDeviceTest") dependencies {
        if (withComposeTestUi) implementation(AndroidX.Compose.Ui.test)
        if (withComposeTestUiJUnit4) implementation(AndroidX.Compose.Ui.test_junit4)
      }
    }
    if (flags.withLinuxX64) linuxX64Main {
      // composeMain, NOT composeUiMain: there is no Compose UI for linuxX64 upstream, but the compose
      // compiler plugin IS applied to every target, and it fails the compilation outright when the
      // runtime is missing (IncompatibleComposeRuntimeVersionException, "minimum runtime version
      // 1.0.0"). composeMain carries the runtime alone, so this edge is what makes withLinuxX64
      // usable together with compose at all. linuxX64Test needs no matching edge: implementation
      // deps of linuxX64Main already reach the test compilation of the same target.
      dependsOn(composeMain)
    }
    if (flags.withJvm) {
      jvmMain {
        dependsOn(composeUiMain)
        dependencies {
          if (withComposeUi) {
            implementation(ComposeJb.uiTooling)
            implementation(ComposeJb.preview)
          }
          if (withComposeMaterialIconsExtended) implementation(ComposeJb.materialIconsExtended)
          if (withComposeDesktop) {
            implementation(ComposeJb.desktopCommon)
            implementation(composeExt.dependencies.desktop.currentOs)
          }
          if (withComposeDesktopComponents) {
            implementation(ComposeJb.componentsSplitPane)
          }
        }
      }
      jvmTest {
        dependsOn(composeUiTest)
        dependencies {
          @Suppress("DEPRECATION")
          if (withComposeTestUiJUnit4) implementation(ComposeJb.uiTestJUnit4)
        }
      }
    }
    if (flags.withJs) {
      jsMain {
        // composeMain, NOT composeUiMain: compose-html only, so skiko never reaches js -- unless
        // the lib says it wants Compose UI on a canvas there, which UWidgets does on purpose.
        dependsOn(if (withComposeUiOnJs) composeUiMain else composeMain)
        dependencies {
          if (withComposeHtmlCore) implementation(ComposeJb.htmlCore)
          if (withComposeHtmlSvg) implementation(ComposeJb.htmlSvg)
        }
      }
      jsTest {
        // Mirrors jsMain: compose UI test deps stay off js unless Compose UI is deliberately there.
        dependsOn(if (withComposeUiOnJs) composeUiTest else composeTest)
        dependencies {
          if (withComposeTestHtmlUtils) implementation(ComposeJb.htmlTestUtils)
        }
      }
    }
  }
}

// endregion [[Compose MPP Module Build Template]]

// region [[Compose MPP App Build Template]]

/** Only for very standard compose mpp apps. In most cases it's better to not use this function. */
fun Project.defaultBuildTemplateForComposeMppApp(
  lib: Lib = gradle.extLib,
  ignoreAndroConfig: Boolean = false, // so user have to explicitly say THAT he wants to ignore it.
  addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
): Unit = context(lib.info, lib.flags) {
  val composeExt = extensions.getByName("compose") as ComposeExtension
  val desktop = (composeExt as ExtensionAware).extensions.getByName("desktop") as DesktopExtension
  defaultBuildTemplateForComposeMppLib(
    lib = lib,
    ignoreAndroConfig = ignoreAndroConfig,
    ignoreAndroPublish = true,
    addCommonMainDependencies = addCommonMainDependencies,
  )
  extensions.configure<KotlinMultiplatformExtension> {
    if (lib.flags.withJs) js {
      binaries.executable()
    }
    if (lib.flags.withLinuxX64) linuxX64 {
      binaries {
        executable {
          entryPoint = "${lib.info.appMainPackage}.${lib.info.appMainFun}"
        }
      }
    }
  }
  if (lib.flags.withJvm) {
    desktop.application {
        mainClass = lib.info.run { "$appMainPackage.$appMainClass" }
        nativeDistributions {
          targetFormats(TargetFormat.Deb)
          packageName = lib.info.name
          packageVersion = lib.info.version.str
          description = lib.info.description
        }
      }
  }
}

// endregion [[Compose MPP App Build Template]]
