@file:Suppress("unused", "PackageDirectoryMismatch")

package pl.mareklangiewicz.defaults

import org.gradle.api.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.rootExtLibDetails

fun v(major: Int = 0, minor: Int = 0, patch: Int = 1, patchLength: Int = 2, suffix: String = "") =
    "$major.$minor.${patch.toString().padStart(patchLength, '0')}$suffix"


@Deprecated("Use defaultGroupAndVerAndDescription", replaceWith = ReplaceWith("defaultGroupAndVerAndDescription(libs.name)"))
fun Project.defaultGroupAndVer(dep: String) {
    val (g, _, v) = dep.split(":")
    group = g
    version = v
}

fun Project.defaultGroupAndVerAndDescription(lib: LibDetails = rootExtLibDetails) {
    group = lib.group
    version = lib.version.ver
    description = lib.description
}

