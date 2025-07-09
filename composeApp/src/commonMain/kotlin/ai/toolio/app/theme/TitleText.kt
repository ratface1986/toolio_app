package ai.toolio.app.theme

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import toolio.composeapp.generated.resources.Res
import toolio.composeapp.generated.resources.Satoshi_Bold

@Composable
fun TitleText(text: String, textColor: Color = Color.Black) {
    Text(
        text = text,
        fontFamily = FontFamily(
            Font(
                resource = Res.font.Satoshi_Bold,
                weight = FontWeight.Bold,
                style = FontStyle.Normal
            )
        ),
        fontSize = 24.sp,
        color = textColor,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun TitleMediumText(
    text: String,
    color: Color = Color.Black,
    alignment: TextAlign = TextAlign.Start
) {
    val fontSize = when {
        text.length < 11 -> 18.sp
        text.length < 20 -> 16.sp
        else -> 14.sp
    }

    Text(
        text = text,
        fontFamily = FontFamily(
            Font(
                resource = Res.font.Satoshi_Bold,
                weight = FontWeight.Bold,
                style = FontStyle.Normal
            )
        ),
        textAlign = alignment,
        fontSize = fontSize,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}