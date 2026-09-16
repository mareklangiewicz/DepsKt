@file:Suppress("unused", "PackageDirectoryMismatch")

package pl.mareklangiewicz.defaults

import org.gradle.api.*
import pl.mareklangiewicz.deps.*

fun v(major: Int = 0, minor: Int = 0, patch: Int = 1, patchLength: Int = 2, suffix: String = "") =
  "$major.$minor.${patch.toString().padStart(patchLength, '0')}$suffix"


fun Project.defaultGroupAndVerAndDescription(lib: Lib) {
  group = lib.info.group
  version = lib.info.version.str
  description = lib.info.description
}


