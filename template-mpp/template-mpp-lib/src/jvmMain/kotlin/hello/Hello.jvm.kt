@file:Suppress("PackageDirectoryMismatch")

package pl.mareklangiewicz.hello

actual fun helloPlatform() = "Hello JVM World! (kotlin: ${KotlinVersion.CURRENT})".also { println(it) }