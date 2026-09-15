package pl.mareklangiewicz.templatefun

import org.gradle.api.Project
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

// region [[Lib — build-script facing helpers]]

/*
 * What used to be LibInfo.kt and most of Lib.kt is gone: the five sibling types, the
 * bundle, the factory and both derivations now ship in DepsKt 0.4.26 as Lib / LibInfo / LibFlags /
 * LibCompose / LibAndro / LibRepos. See ~/code/kotlin/DepsKt/docs/design/lib-details-denesting.md.
 *
 * Only genuinely KGround-side helpers remain here.
 */

/**
 * Adjust one sibling of this build's [Lib], leaving the rest alone, with the root named ONCE.
 *
 * `adjustFlags` is last so it stays the trailing lambda (the common case); adjust identity with
 * `myLib(adjustInfo = { it.copy(name = "...") })`.
 *
 * Be precise about what is doing the work here. [Lib] is a BUNDLE, so `lib.copy(flags =
 * lib.flags.copy(..))` is still two levels — this helper hides that, it is not removed by the data
 * shape alone. The nested model had an equivalent helper on this branch (`settings: LibSettings.()
 * -> LibSettings` on entry points) and it was reverted as insufficient.
 *
 * The difference is depth, and it is the whole point: from the bundle EVERY sibling is exactly one
 * level away, so one helper shape covers all five. Nested, `compose` and `andro` sat three levels
 * down, so the same helper left a consumer writing `settings = { copy(compose = compose!!.copy(..)) }`
 * — deeper, and with a `!!`.
 *
 * Named `myLib`, not `lib`, so it does not collide with DepsKt's [lib] factory it is built on.
 */
fun Project.myLib(
  adjustInfo: (LibInfo) -> LibInfo = { it },
  adjustFlags: (LibFlags) -> LibFlags = { it },
): Lib = gradle.extLib.let {
  it.copy(info = adjustInfo(it.info), flags = adjustFlags(it.flags))
}

// endregion [[Lib — build-script facing helpers]]
