@file:Suppress("unused", "PackageDirectoryMismatch", "DEPRECATION")

package pl.mareklangiewicz.defaults

import org.gradle.api.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.rootExtLib

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

fun Project.defaultGroupAndVerAndDescription(lib: Lib = rootExtLib) {
  group = lib.info.group
  version = lib.info.version.str
  description = lib.info.description
}

/**
 * Shim for callers still holding a nested [LibDetails]. It has NO default for [lib] on purpose:
 * two fully-defaulted overloads of the same entry point are ambiguous, and the default belongs to
 * the sibling form, because that is the one build scripts should call.
 * See `docs/design/lib-details-denesting.md`, trap 2. Deleted in step 4.
 */
@Deprecated("Pass a Lib instead.", ReplaceWith("defaultGroupAndVerAndDescription(lib.toLib())"))
fun Project.defaultGroupAndVerAndDescription(lib: LibDetails) = defaultGroupAndVerAndDescription(lib.toLib())

