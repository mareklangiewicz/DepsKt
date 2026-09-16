// The root project is an empty aggregator on purpose: :deps, :templatefun and :sourcefun are siblings.
// Nothing is published from here. See docs/design/lib-details-denesting.md.
//
// Note: a subproject cannot supply plugins to a sibling's build script anyway -- build-script
// plugins resolve through pluginManagement (buildSrc, included builds, repositories), and
// include(":templatefun") only makes it a project. :deps does apply templatefun and sourcefun, but
// the PUBLISHED ones, which is not circular. This root applies neither it nor the deps plugin: it has
// no sources. The settings plugin (published, pinned in settings.gradle.kts) is all it needs.

// It deliberately does NOT call defaultGroupAndVerAndDescription either. That would give this
// root project.group = pl.mareklangiewicz.deps and project.name = DepsKt -- the exact coordinate
// :deps publishes. Gradle's composite-build substitution matches included-build projects by those
// coordinates, so a consumer's includeBuild("../DepsKt") would bind to THIS project, which has no
// sources and no variants, and fail with "No variants exist". Measured, from KGround. An
// aggregator that publishes nothing must not claim a published coordinate.

// The one exception to "this root applies nothing": the Kotlin JVM plugin is DECLARED here, with
// `apply false`, and the siblings then ask for it WITHOUT a version (plugs.KotlinJvmNoVer).
// Declaring it with a version in more than one subproject makes Gradle load the Kotlin plugin
// several times in one build ("which is not supported and may break the build"); this is the
// remedy Gradle's own message prescribes. `apply false` means declared-and-resolved, not applied --
// this project still has no sources and builds nothing.
plugins {
  plug(plugs.KotlinJvm) apply false
}
