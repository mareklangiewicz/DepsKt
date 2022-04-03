@file:Suppress("PackageDirectoryMismatch")

package pl.mareklangiewicz.hello

actual fun helloPlatform() = "Hello JVM World!".also { println(it) }