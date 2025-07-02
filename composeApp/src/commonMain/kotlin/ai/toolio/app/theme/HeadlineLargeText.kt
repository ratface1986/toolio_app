package ai.toolio.app.theme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import toolio.composeapp.generated.resources.Res
import toolio.composeapp.generated.resources.Satoshi_Black
import toolio.composeapp.generated.resources.Satoshi_Bold

@Composable
fun HeadlineLargeText(text: String) {
    Text(
        text = text,
        color = Color(0xFF443A94),
        fontFamily = FontFamily(Font(resource = Res.font.Satoshi_Black, weight = FontWeight.Black)),
        fontSize = 38.sp,
        maxLines = 2
    )
}

@Composable
fun HeadlineMediumText(text: String) {
    Text(
        text = text,
        color = Color.Black,
        fontFamily = FontFamily(Font(resource = Res.font.Satoshi_Bold, weight = FontWeight.Bold)),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
}