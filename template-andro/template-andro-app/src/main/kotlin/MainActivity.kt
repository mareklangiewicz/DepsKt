package pl.mareklangiewicz.templateandro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.mareklangiewicz.templateandro.theme.TemplateAndroTheme
import pl.mareklangiewicz.templateandrolib.RotatedBox

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TemplateAndroTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HelloStuff("Android")
                }
            }
        }
    }
}

@Composable
fun HelloStuff(name: String) {
    Column(Modifier.padding(16.dp)) {
        var rotation by remember { mutableStateOf(80f) }
        Text(text = "Hello $name! rotation:$rotation")
        RotatedBox(rotation)
        Button(onClick = { rotation += 5f }) {
            Text("Rotate more")

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