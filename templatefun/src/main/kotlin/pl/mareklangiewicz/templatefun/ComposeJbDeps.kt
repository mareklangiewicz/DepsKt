package pl.mareklangiewicz.templatefun

import pl.mareklangiewicz.deps.*

/**
 * Compose Multiplatform artifacts, taken from DepsKt's own generated catalog.
 *
 * ### Why this exists
 *
 * These used to be read off the compose gradle plugin's `compose.dependencies.*` accessors, which
 * JetBrains deprecated ("Specify dependency directly") — 46 warnings across [MppBuildTemplates] and
 * [RawLibBuildTemplates]. Specifying them directly is exactly what DepsKt is for: every one of these
 * coordinates is already in `Deps.kt`, refreshed daily by `updateGeneratedDeps`, and
 * [AndroBuildTemplates] has always done it this way for the androidx side.
 *
 * ### The version policy, and why it is PER ARTIFACT
 *
 * Do not be tempted to align these to one version the way [defaultComposeAndroDeps] aligns androidx
 * to `Vers.ComposeAndro`. Compose Multiplatform does not release in lockstep, and the plugin says so
 * itself — `org.jetbrains.compose.ComposeBuildConfig` in compose-gradle-plugin 1.12.0-beta01 holds
 * BOTH `composeVersion = "1.12.0-beta01"` AND `composeMaterial3Version = "1.9.0"`. Checked against
 * the repository, not inferred: `org.jetbrains.compose.material3:material3:1.12.0` is a **404**,
 * while `:1.9.0` resolves. Aligning everything to one version would break material3, silently at
 * first, since a missing version only errors when something actually resolves it.
 *
 * So each artifact carries its OWN version, and the policy is [verLastBeta] — the newest version
 * that is beta or more stable. Deliberately not `verLast`: that is whatever is newest in the
 * catalog, currently `1.13.0-alpha01` for most of these, and a build template must not drag every
 * consumer onto an alpha.
 *
 * `materialIconsExtended` falls out of this correctly with no special case: JetBrains pinned it at
 * 1.7.3 and stopped updating it (the deprecation on the old accessor says so, and the accessor had
 * the version baked into the string), and the catalog has only that one version for it.
 *
 * ### What this changes
 *
 * Versions now come from DepsKt's daily data instead of the applied compose plugin's pins. They are
 * usually NEWER — 1.12.0 released vs the plugin's 1.12.0-beta01. That is the intent of this repo,
 * but it is a real semantic change: a consumer applying an older compose plugin no longer gets that
 * plugin's matching artifact versions.
 *
 * ### Not here on purpose
 *
 * - `compose.desktop.currentOs` is NOT deprecated and stays an accessor. It picks a per-OS artifact
 *   (`desktop-jvm-linux-x64`, `-macos-arm64`, …) at configuration time, so it has no single
 *   coordinate to state directly. JetBrains deprecated the pass-through accessors and kept the ones
 *   that do real work.
 * - `uiUtil` is missing from the generated catalog, so it is spelled out below. See its comment.
 */
internal object ComposeJb {

  /**
   * Newest beta-or-better, per artifact. See the note on [ComposeJb] for why this is not one shared
   * version, and why it is not [Dep.verLast].
   */
  private val Dep.jb: Dep get() = withVer(verLastBeta)

  val runtime get() = Org.JetBrains.Compose.Runtime.runtime.jb
  val ui get() = Org.JetBrains.Compose.Ui.ui.jb
  val uiTest get() = Org.JetBrains.Compose.Ui.test.jb
  val uiTooling get() = Org.JetBrains.Compose.Ui.tooling.jb
  val preview get() = Org.JetBrains.Compose.Ui.tooling_preview.jb
  val foundation get() = Org.JetBrains.Compose.Foundation.foundation.jb
  val animation get() = Org.JetBrains.Compose.Animation.animation.jb
  val animationGraphics get() = Org.JetBrains.Compose.Animation.graphics.jb
  val material get() = Org.JetBrains.Compose.Material.material.jb
  val material3 get() = Org.JetBrains.Compose.Material3.material3.jb
  val materialIconsExtended get() = Org.JetBrains.Compose.Material.icons_extended.jb
  val componentsResources get() = Org.JetBrains.Compose.Components.resources.jb
  val componentsSplitPane get() = Org.JetBrains.Compose.Components.splitpane.jb
  val desktopCommon get() = Org.JetBrains.Compose.Desktop.desktop.jb
  val uiTestJUnit4 get() = Org.JetBrains.Compose.Ui.test_junit4.jb
  val htmlCore get() = Org.JetBrains.Compose.Html.core.jb
  val htmlSvg get() = Org.JetBrains.Compose.Html.svg.jb
  val htmlTestUtils get() = Org.JetBrains.Compose.Html.test_utils.jb

  /**
   * `org.jetbrains.compose.ui:ui-util` IS published (checked: 1.12.0 resolves on Maven Central), but
   * it is absent from the generated `Deps.kt`. That region is downloaded wholesale from an upstream
   * dataset and overwritten on every `updateGeneratedDeps`, so it cannot be patched by hand here --
   * and `[[Deps Selected]]`, the manual region, holds only typealiases. Spelled out until the
   * upstream data grows it; the version follows [ui] so the two cannot drift apart.
   */
  val uiUtil get() = Dep("org.jetbrains.compose.ui", "ui-util", Org.JetBrains.Compose.Ui.ui.verLastBeta)
}
