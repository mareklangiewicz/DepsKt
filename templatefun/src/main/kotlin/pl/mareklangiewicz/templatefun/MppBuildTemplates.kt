package pl.mareklangiewicz.templatefun

import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.compose.*
import org.jetbrains.compose.desktop.*
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
    // "implementation"/"testImplementation" of the old com.android.library path are gone.
    // Still reusing defaultAndroDeps rather than restating the list (trust me future Marek:
    // I've tried configuring it all the "mpp way" already :) ).
    dependencies {
      // compose is configured the MPP way already, so we simply do NOT open a compose scope here:
      // the compose-android deps live in defaultComposeAndroDeps and are unreachable without one.
      // That is the composeConfiguredByMpp routing, expressed by omission instead of by a boolean.
      context(andro) {
        defaultAndroDeps(configuration = "androidMainImplementation")
        defaultAndroTestDeps(configuration = "androidHostTestImplementation")
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
 * is visible: [defaultBuildTemplateForBasicMppLib], which still holds the whole [LibDetails].
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
@OptIn(ExperimentalComposeLibrary::class)
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
}


/**
 * Normal fun KotlinMultiplatformExtension.allDefault ignores compose stuff,
 * because it's also used for libs without compose plugin.
 * This one does the rest, so it has to be called additionally for compose libs, after .allDefault */
@OptIn(ExperimentalComposeLibrary::class)
context(flags: LibFlags, compose: LibCompose)
fun KotlinMultiplatformExtension.allDefaultSourceSetsForCompose(
) = with(compose) {
  // Finding #6 in miniature: a context parameter does not shadow a receiver, but it DOES occupy its
  // own name. `compose` was already taken here by the Gradle ComposeExtension, so the extension is
  // the one that had to be renamed -- exactly the collision the DepsKt migration will hit with
  // `repos`. The settings win the bare name because `with` spells their flags out below.
  val composeExt = project.extensions.getByName("compose") as ComposeExtension
  sourceSets {
    commonMain {
      dependencies {
        implementation(composeExt.dependencies.runtime)
        if (withComposeUi) {
          implementation(composeExt.dependencies.ui)
        }
        if (withComposeFoundation) implementation(composeExt.dependencies.foundation)
        if (withComposeFullAnimation) {
          implementation(composeExt.dependencies.animation)
          implementation(composeExt.dependencies.animationGraphics)
        }
        if (withComposeMaterial2) implementation(composeExt.dependencies.material)
        if (withComposeMaterial3) implementation(composeExt.dependencies.material3)
      }
    }
    if (flags.withJvm) {
      jvmMain {
        dependencies {
          if (withComposeUi) {
            implementation(composeExt.dependencies.uiTooling)
            implementation(composeExt.dependencies.preview)
          }
          if (withComposeMaterialIconsExtended) implementation(composeExt.dependencies.materialIconsExtended)
          if (withComposeDesktop) {
            implementation(composeExt.dependencies.desktop.common)
            implementation(composeExt.dependencies.desktop.currentOs)
          }
          if (withComposeDesktopComponents) {
            implementation(composeExt.dependencies.desktop.components.splitPane)
          }
        }
      }
      jvmTest {
        dependencies {
          @Suppress("DEPRECATION")
          if (withComposeTestUiJUnit4) implementation(composeExt.dependencies.desktop.uiTestJUnit4)
        }
      }
    }
    if (flags.withJs) {
      jsMain {
        dependencies {
          if (withComposeHtmlCore) implementation(composeExt.dependencies.html.core)
          if (withComposeHtmlSvg) implementation(composeExt.dependencies.html.svg)
        }
      }
      jsTest {
        dependencies {
          if (withComposeTestHtmlUtils) implementation(composeExt.dependencies.html.testUtils)
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
