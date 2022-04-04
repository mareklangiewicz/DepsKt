import kotlinx.browser.*
import pl.mareklangiewicz.hello.*

fun main() {
    console.log("TemplateMPPWebApp started.")
    console.log("Kotlin version: ${KotlinVersion.CURRENT}")
    helloEveryOneWithSomeHtml()
    tryToInstallAppIn(document.getElementById("rootForTemplateMPPWebApp"))
}

