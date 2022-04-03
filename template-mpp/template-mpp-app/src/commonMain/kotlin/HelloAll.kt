package pl.mareklangiewicz.hello

import kotlinx.html.*
import kotlinx.html.stream.*
import pl.mareklangiewicz.hello.*

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
