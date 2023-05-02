package pl.mareklangiewicz.templateandro

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.*
import pl.mareklangiewicz.templateandro.theme.*
import pl.mareklangiewicz.templateandrolib.*
import androidx.compose.ui.Modifier as Mod

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TemplateAndroTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Mod.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HelloStuff("Android")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TemplateAndroTheme {
        HelloStuff("Android")
    }
}