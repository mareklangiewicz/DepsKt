// The root project is an empty aggregator on purpose: :deps and :templatefun are siblings.
// Nothing is published from here. See docs/design/lib-details-denesting.md.
//
// Note: this root cannot use the templatefun conventions its own :templatefun subproject provides
// (that would be circular), and it deliberately does NOT apply the deps plugin either -- it has
// no sources. The settings plugin (published, pinned in settings.gradle.kts) is all it needs.

// It deliberately does NOT call defaultGroupAndVerAndDescription either. That would give this
// root project.group = pl.mareklangiewicz.deps and project.name = DepsKt -- the exact coordinate
// :deps publishes. Gradle's composite-build substitution matches included-build projects by those
// coordinates, so a consumer's includeBuild("../DepsKt") would bind to THIS project, which has no
// sources and no variants, and fail with "No variants exist". Measured, from KGround. An
// aggregator that publishes nothing must not claim a published coordinate; :deps sets its own.
