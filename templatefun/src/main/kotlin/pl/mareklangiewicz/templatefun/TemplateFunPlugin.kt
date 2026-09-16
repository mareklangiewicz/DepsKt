package pl.mareklangiewicz.templatefun

import org.gradle.api.*

/**
 * The plugin that gives a consuming build script access to templatefun.
 *
 * Applying it is what puts templatefun (and its Lib model) on that script's compile classpath, so
 * `import pl.mareklangiewicz.templatefun.*` resolves and `defaultBuildTemplateForBasicMppLib(..)`
 * can be called. That is its whole job -- it configures nothing by itself, deliberately: the
 * templates are ordinary functions a script calls when it wants them, not conventions applied
 * behind its back.
 *
 * It replaces KGround's local `my-convention`, which did the same thing from an includeBuild,
 * and before that a precompiled `pl.mareklangiewicz.templatefun.gradle.kts` script, whose id
 * `kotlin-dsl` used to derive from the FILE NAME. Without `kotlin-dsl` the id is registered
 * explicitly in build.gradle.kts instead. The apply body is unchanged, so build output is too.
 */
class TemplateFunPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    context(project) { testContextParameters() }
  }
}
