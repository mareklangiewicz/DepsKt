package pl.mareklangiewicz.hello

import androidx.compose.runtime.*
import org.jetbrains.compose.web.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.*

fun tryToInstallAppIn(rootElement: Element?) {
    when (rootElement as? HTMLElement) {
        null -> console.warn("TemplateMPPWebApp: Incorrect rootElement")
        else -> renderComposable(root = rootElement) {
            H1 { Text("Template MPP Web App") }
            P { Text(remember { helloCommon() }) }
            P { Text(remember { helloPlatform() }) }
            P { Text(remember { helloSomeHtml() }) }
        }
    }
}
