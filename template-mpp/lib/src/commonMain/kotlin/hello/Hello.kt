@file:Suppress("PackageDirectoryMismatch")

package pl.mareklangiewicz.hello

fun helloCommon() = println("Hello Pure Common World!")

expect fun helloPlatform()