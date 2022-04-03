import kotlinx.browser.*
import kotlinx.html.dom.*
import kotlinx.html.js.*
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
        else -> rootElement.append {
            h1 { +"Template MPP Web App" }
            p { +helloCommon() }
            p { +helloPlatform() }
        }
    }
}
