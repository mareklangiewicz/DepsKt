package pl.mareklangiewicz.templatefun

import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.compose.*
import org.jetbrains.compose.resources.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.gradle.kotlin.dsl.*
import com.android.build.api.dsl.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.defaults.*

// region [[Raw Lib Build Template]]

fun Project.defaultBuildTemplateForRawMppLib(
  lib: Lib = gradle.extLib,
): Unit = context(lib.info, lib.flags) {
  val flags = lib.flags

  if (lib.andro != null) {
    apply(plugin = plugs.AndroKmpNoVer.group) // group is actually id for plugins
  }
  if (lib.compose?.withComposeTestUiJUnit5 == true)
    logger.warn("Compose UI Tests with JUnit5 are not supported yet! Configuring JUnit5 anyway.")

  repositories { context(lib.repos) { addRepos() } }
  defaultGroupAndVerAndDescription(lib)

  val compose = extensions.getByName("compose") as ComposeExtension

  extensions.configure<KotlinMultiplatformExtension> {
    defaultCompiler(jvmVer = flags.withJvmVer?.toInt())

    if (flags.withJvm) jvm()
    if (flags.withLinuxX64) linuxX64()
    if (flags.withJs) jsDefault()
    lib.andro?.let { context(it) { androDefault() } }

    applyDefaultHierarchyTemplate()

    sourceSets {
      commonMain {
        dependencies {
          if (flags.withKotlinxHtml) implementation(KotlinX.html)
        }
      }
      commonTest {
        dependencies {
          implementation(Kotlin.test)
          if (flags.withTestUSpekX) implementation(Langiewicz.uspekx)
        }
      }

      val composeMain = create("composeMain") {
        dependsOn(commonMain.get())
        dependencies {
          if (lib.compose != null) implementation(ComposeJb.runtime)
        }
      }

      val composeTest = create("composeTest") {
        // dependsOn(composeMain) check if it's not needed and it even generates warnings!
        // TODO_later: understand root cause - check kotlin mpp warnings and where it's generated in sources.
        dependsOn(commonTest.get())
        dependencies {
          val settpose = lib.compose ?: return@dependencies
          // TODO_later anything here? any compose testing util not related to compose ui?
        }
      }

      val composeUiMain = create("composeUiMain") {
        dependsOn(composeMain)
        dependencies {
          val settpose = lib.compose ?: return@dependencies
          if (settpose.withComposeUi) {
            implementation(ComposeJb.ui)
            implementation(ComposeJb.componentsResources)
          }
          if (settpose.withComposeFoundation) implementation(ComposeJb.foundation)
          if (settpose.withComposeFullAnimation) {
            implementation(ComposeJb.animation)
            implementation(ComposeJb.animationGraphics)
          }
          if (settpose.withComposeMaterial2) implementation(ComposeJb.material)
          if (settpose.withComposeMaterial3) implementation(ComposeJb.material3)
        }
      }

      val composeUiTest = create("composeUiTest") {
        // dependsOn(composeUiMain) looks like not it's not needed and it even generates warnings!
          // TODO_later: understand root cause - check kotlin mpp warnings and where it's generated in sources.
        dependsOn(composeTest)
        dependencies {
          val settpose = lib.compose ?: return@dependencies
          if (settpose.withComposeTestUi) implementation(ComposeJb.uiTest)
        }
      }

      if (flags.withJvm) {
        jvmMain {
          dependsOn(composeUiMain)
          dependencies {
            val settpose = lib.compose ?: return@dependencies
            if (settpose.withComposeUi) {
              implementation(ComposeJb.uiTooling)
              implementation(ComposeJb.uiUtil)
              implementation(ComposeJb.preview)
            }
            if (settpose.withComposeMaterialIconsExtended) implementation(ComposeJb.materialIconsExtended)
            if (settpose.withComposeDesktop) {
              implementation(ComposeJb.desktopCommon)
              implementation(compose.dependencies.desktop.currentOs)
            }
            if (settpose.withComposeDesktopComponents) {
              implementation(ComposeJb.componentsSplitPane)
            }
          }
        }
        jvmTest {
          dependsOn(composeUiTest)
          dependencies {
            if (flags.withTestJUnit4) implementation(JUnit.junit)
            if (flags.withTestJUnit5) {
              implementation(Org.JUnit.Jupiter.junit_jupiter_engine)
              runtimeOnly(Org.JUnit.Platform.junit_platform_launcher)
            }
            if (flags.withTestUSpekX) {
              if (flags.withTestJUnit4) implementation(Langiewicz.uspekx_junit4)
              if (flags.withTestJUnit5) implementation(Langiewicz.uspekx_junit5)
            }
            if (flags.withTestGoogleTruth) implementation(Com.Google.Truth.truth)
            if (flags.withTestMockitoKotlin) implementation(Org.Mockito.Kotlin.mockito_kotlin)

            val settpose = lib.compose ?: return@dependencies
            if (settpose.withComposeTestUiJUnit4) implementation(ComposeJb.uiTestJUnit4)
          }
        }
      }
      if (flags.withJs) {
        jsMain {
          dependsOn(composeMain)
          dependencies {
            val settpose = lib.compose ?: return@dependencies
            if (settpose.withComposeHtmlCore) implementation(ComposeJb.htmlCore)
            if (settpose.withComposeHtmlSvg) implementation(ComposeJb.htmlSvg)
          }
        }
        jsTest {
          dependsOn(composeTest)
          dependencies {
            val settpose = lib.compose ?: return@dependencies
            if (settpose.withComposeTestHtmlUtils) implementation(ComposeJb.htmlTestUtils)
          }
        }
      }
      if (flags.withLinuxX64) {
        linuxX64Main
        linuxX64Test
      }
      if (lib.andro != null) {
        androidMain {
          dependsOn(composeUiMain)
          dependencies {
            implementation(AndroidX.Core.ktx)
            implementation(AndroidX.Activity.activity)
            implementation(AndroidX.Activity.ktx)
            implementation(AndroidX.Activity.compose)
            val settpose = lib.compose ?: return@dependencies
            if (settpose.withComposeUi) {
              implementation(AndroidX.Compose.Ui.ui)
              implementation(AndroidX.Compose.Ui.util)
              implementation(AndroidX.Compose.Ui.tooling)
              implementation(AndroidX.Compose.Ui.tooling_preview)
            }
          }
        }
        getByName("androidHostTest") {
          dependencies {
            if (flags.withTestJUnit4) implementation(JUnit.junit)
            if (flags.withTestJUnit5) {
              implementation(Org.JUnit.Jupiter.junit_jupiter_engine)
              runtimeOnly(Org.JUnit.Platform.junit_platform_launcher)
            }
            if (flags.withTestUSpekX) {
              if (flags.withTestJUnit4) implementation(Langiewicz.uspekx_junit4)
              if (flags.withTestJUnit5) implementation(Langiewicz.uspekx_junit5)
            }
          }
        }
        getByName("androidDeviceTest") {
          dependencies {
            implementation(Kotlin.test)
            if (flags.withTestJUnit4OnAndroidDevice) {
              implementation(JUnit.junit)
              implementation(AndroidX.Test.core)
              implementation(AndroidX.Test.core_ktx)
              implementation(AndroidX.Test.runner)
              implementation(AndroidX.Test.Ext.junit)
              implementation(AndroidX.Test.Ext.junit_ktx)
            }
            else if (flags.withTestJUnit5) {
              error("JUnit5 is NOT yet supported on android device tests.")
            }
            if (flags.withTestUSpekX) {
              if (flags.withTestJUnit4OnAndroidDevice) implementation(Langiewicz.uspekx_junit4)
            }
            val settpose = lib.compose ?: return@dependencies
            if (settpose.withComposeTestUi) implementation(AndroidX.Compose.Ui.test)
            if (settpose.withComposeTestUiJUnit4) implementation(AndroidX.Compose.Ui.test_junit4)
          }
        }
      }
    }
  }
  if (lib.andro != null) {
    tasks.matching { it.name == "copyAndroidDeviceTestComposeResourcesToAndroidAssets" }
      .configureEach { enabled = false }
  }

  (compose as ExtensionAware).extensions.configure<ResourcesExtension> {
    // generateResClass = always
    generateResClass = never
  }

  configurations.checkVerSync(warnOnly = true)
  tasks.defaultTestsOptions(onJvmUseJUnitPlatform = flags.withTestJUnit5)
  if (plugins.hasPlugin("com.vanniktech.maven.publish")) defaultPublishing()
  else println("MPP Module ${name}: publishing (and signing) disabled")
}


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

// endregion [[Raw Lib Build Template]]
