package pl.mareklangiewicz.hello

// It's ok that it repeats helloCommon and helloPlatform twice (also in helloSomeHtml)
fun helloEveryOneWithSomeHtml() {
    helloCommon()
    helloPlatform()
    helloSomeHtml()
}

fun helloSomeHtml(): String =
    "FIXME: helloSomeHtml is disabled because of issues with kotlinx.html on kotlin 1.6.10 (it works on 1.6.20 - even with linuxX64, but compose doesn't)"
//    buildString {
//        appendHTML().html {
//            body {
//                h1 { +"Some H1 in Template MPP App" }
//                p { +"Some paragraph" }
//                p { +"Some other paragraph" }
//                p { +helloCommon() }
//                p { +helloPlatform() }
//            }
//        }
//    }
.also { println(it) }
