package pl.mareklangiewicz.templateandrolib

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

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

