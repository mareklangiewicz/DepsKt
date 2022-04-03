@file:Suppress("PackageDirectoryMismatch")

package pl.mareklangiewicz.hello

actual fun helloPlatform(): String = "Hello Native Linux 64 World!".also { println(it) }