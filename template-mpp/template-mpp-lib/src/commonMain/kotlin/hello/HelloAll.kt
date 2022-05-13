package pl.mareklangiewicz.hello

import kotlinx.html.*
import kotlinx.html.stream.*

// It's ok that it repeats helloCommon and helloPlatform twice (also in helloSomeHtml)
fun helloEveryOneWithSomeHtml() {
    helloCommon()
    helloPlatform()
    helloSomeHtml()
}

fun helloSomeHtml(): String =
   buildString {
       appendHTML().html {
           body {
               h1 { +"Some H1 in Template MPP App" }
               p { +"Some paragraph" }
               p { +"Some other paragraph" }
               p { +helloCommon() }
               p { +helloPlatform() }
           }
       }
   }
.also { println(it) }
