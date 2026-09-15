@file:Suppress("unused", "PackageDirectoryMismatch", "DEPRECATION")

package pl.mareklangiewicz.defaults

import org.gradle.api.*
import pl.mareklangiewicz.deps.*

fun v(major: Int = 0, minor: Int = 0, patch: Int = 1, patchLength: Int = 2, suffix: String = "") =
  "$major.$minor.${patch.toString().padStart(patchLength, '0')}$suffix"


@Deprecated(
  "Use defaultGroupAndVerAndDescription",
  replaceWith = ReplaceWith("defaultGroupAndVerAndDescription(libs.name)"),
)
fun Project.defaultGroupAndVer(dep: String) {
  val (g, _, v) = dep.split(":")
  group = g
  version = v
}

fun Project.defaultGroupAndVerAndDescription(lib: Lib) {
  group = lib.info.group
  version = lib.info.version.str
  description = lib.info.description
}

/**
 * Shim for callers still holding a nested [LibDetails]. Neither overload has a default any more:
 * the sibling form's `lib: Lib = rootExtLib` is gone in 0.4.29 (it was dead at every call site, and
 * it reached for ambient ext state), which also retires the ambiguity these two used to risk.
 * See `docs/design/lib-details-denesting.md`, trap 2. Deleted in step 4.
 */
@Deprecated("Pass a Lib instead.", ReplaceWith("defaultGroupAndVerAndDescription(lib.toLib())"))
fun Project.defaultGroupAndVerAndDescription(lib: LibDetails) = defaultGroupAndVerAndDescription(lib.toLib())

