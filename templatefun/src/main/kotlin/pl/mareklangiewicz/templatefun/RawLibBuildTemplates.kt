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

/**
 * template-raw's lib template. Used to be a full second copy of the source-set graph, maintained by
 * hand next to [defaultBuildTemplateForFullMppLib]'s chain -- which is exactly how ComposeJb.uiUtil
 * ended up jvmMain-only here and nowhere else, and why enabling linuxX64 needed the same edge added
 * in two files. It now delegates, so there is ONE graph.
 *
 * Kept as its own name rather than folded away: template-raw's build scripts call it, and the whole
 * point of the convergence is that the two templates end up indistinguishable THROUGH their
 * templates, not that one template's scripts get rewritten to prove it.
 */
fun Project.defaultBuildTemplateForRawMppLib(
  lib: Lib = gradle.extLib,
  addCommonMainDependencies: KotlinDependencyHandler.() -> Unit = {},
): Unit = defaultBuildTemplateForFullMppLib(lib, addCommonMainDependencies)


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
