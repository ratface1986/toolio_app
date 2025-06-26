package ai.toolio.app.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import toolio.composeapp.generated.resources.Res
import toolio.composeapp.generated.resources.Satoshi_Medium

@Composable
fun TitleText(text: String) {
    Text(
        text = text,
        fontFamily = FontFamily(
            Font(
                resource = Res.font.Satoshi_Medium,
                weight = FontWeight.Bold,
                style = FontStyle.Normal
            )
        ),
        fontSize = 24.sp,
        color = Color.White,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun TitleMediumText(text: String, color: Color = Color.White, alignment: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        fontFamily = FontFamily(
            Font(
                resource = Res.font.Satoshi_Medium,
                weight = FontWeight.Bold,
                style = FontStyle.Normal
            )
        ),
        textAlign = alignment,
        fontSize = 18.sp,
        color = color
    )
}