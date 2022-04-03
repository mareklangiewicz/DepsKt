package pl.mareklangiewicz.hello.mpp.app

import kotlinx.html.*
import kotlinx.html.stream.*
import pl.mareklangiewicz.hello.*

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
