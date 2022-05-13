package pl.mareklangiewicz.hello

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.window.*

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Template MPP App") {
        Column {
            Text("Hello JVM Desktop!")
            Text(helloSomeHtml())
        }
    }
}
