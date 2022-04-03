package pl.mareklangiewicz.hello

import kotlinx.html.*
import kotlinx.html.stream.*

fun main() = helloAll()

fun helloAll() {
    helloCommon()
    helloPlatform()
    val someHtml = buildString {
        appendHTML().html {
            body {
                h1 { +"Template MPP Jvm App" }
                p { +helloCommon() }
                p { +helloPlatform() }
            }
        }
    }
    println(someHtml)
}
