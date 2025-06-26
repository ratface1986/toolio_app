package ai.toolio.app.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import toolio.composeapp.generated.resources.Res
import toolio.composeapp.generated.resources.Satoshi_Black
import toolio.composeapp.generated.resources.Satoshi_Bold

@Composable
fun HeadlineLargeText(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontFamily = FontFamily(Font(resource = Res.font.Satoshi_Black, weight = FontWeight.Bold)),
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun HeadlineMediumText(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontFamily = FontFamily(Font(resource = Res.font.Satoshi_Bold, weight = FontWeight.Bold)),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
}