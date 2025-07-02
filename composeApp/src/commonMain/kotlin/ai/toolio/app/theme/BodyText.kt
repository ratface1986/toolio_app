package ai.toolio.app.theme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import toolio.composeapp.generated.resources.Res
import toolio.composeapp.generated.resources.Satoshi_Bold
import toolio.composeapp.generated.resources.Satoshi_Medium
import toolio.composeapp.generated.resources.Satoshi_Regular

@Composable
fun BodyText(text: String, color: Color = Color.White) {
    Text(
        text = text,
        fontFamily = FontFamily(
            Font(
                resource = Res.font.Satoshi_Bold,
                weight = FontWeight.Bold,
                style = FontStyle.Normal
            )
        ),
        color = color,
        fontSize = 12.sp
    )
}

@Composable
fun BodyTextMedium(text: String, color: Color = Color.White) {
    Text(
        text = text,
        fontFamily = FontFamily(
            Font(
                resource = Res.font.Satoshi_Bold,
                weight = FontWeight.Bold,
                style = FontStyle.Normal
            )
        ),
        color = color,
        fontSize = 15.sp
    )
}

@Composable
fun BodyTextLarge(text: String, color: Color = Color.White) {
    Text(
        text = text,
        fontFamily = FontFamily(
            Font(
                resource = Res.font.Satoshi_Bold,
                weight = FontWeight.Bold,
                style = FontStyle.Normal
            )
        ),
        color = color,
        fontSize = 18.sp
    )
}