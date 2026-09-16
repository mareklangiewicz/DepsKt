// The root project is an empty aggregator on purpose: :deps and :templatefun are siblings.
// Nothing is published from here. See docs/design/lib-details-denesting.md.
//
// Note: a subproject cannot supply plugins to a sibling's build script anyway -- build-script
// plugins resolve through pluginManagement (buildSrc, included builds, repositories), and
// include(":templatefun") only makes it a project. :DepsKt does apply templatefun, but the
// PUBLISHED one, which is not circular. This root applies neither it nor the deps plugin: it has
// no sources. The settings plugin (published, pinned in settings.gradle.kts) is all it needs.

// It deliberately does NOT call defaultGroupAndVerAndDescription either. That would give this
// root project.group = pl.mareklangiewicz.deps and project.name = DepsKt -- the exact coordinate
// :deps publishes. Gradle's composite-build substitution matches included-build projects by those
// coordinates, so a consumer's includeBuild("../DepsKt") would bind to THIS project, which has no
// sources and no variants, and fail with "No variants exist". Measured, from KGround. An
// aggregator that publishes nothing must not claim a published coordinate; :DepsKt sets its own.
//
// This matters MORE since :deps was renamed to :DepsKt (settings.gradle.kts): two projects in this
// build are now named DepsKt, and the only thing keeping their identities apart is that this one
// has no group. Giving the root a group would make them collide exactly, silently.
