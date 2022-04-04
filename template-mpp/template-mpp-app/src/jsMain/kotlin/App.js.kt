import kotlinx.browser.*
import kotlinx.dom.*
import org.w3c.dom.*
import pl.mareklangiewicz.hello.*

fun main() {
    console.log("TemplateMPPWebApp started.")
    console.log("Kotlin version: ${KotlinVersion.CURRENT}")
    helloEveryOneWithSomeHtml()
    tryToInstallAppIn(document.getElementById("rootForTemplateMPPWebApp"))
}

fun tryToInstallAppIn(rootElement: Element?) {
    when (rootElement as? HTMLElement) {
        null -> console.warn("TemplateMPPWebApp: Incorrect rootElement")
//        else -> rootElement.append {
//            h1 { +"Template MPP Web App" }
//            p { +helloCommon() }
//            p { +helloPlatform() }
//        }
        else -> rootElement.appendElement("div") {
            textContent = "FIXME: example content is disabled because of issues with kotlinx.html on kotlin 1.6.10 (it works on 1.6.20 - even with linuxX64, but compose doesn't)"
        }
    }
}
