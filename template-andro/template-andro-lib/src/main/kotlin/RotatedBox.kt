package pl.mareklangiewicz.templateandrolib

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier as Mod
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RotatedBox(degrees: Float = 10f) {
    Box(
        Mod
            .padding(8.dp)
            .border(1.dp, Color.Red)
            .padding(32.dp)
            .rotate(degrees)
            .border(1.dp, Color.Blue)
            .size(200.dp, 200.dp)
            .padding(8.dp)
    ) {
        Text("rotated text")
    }
}
