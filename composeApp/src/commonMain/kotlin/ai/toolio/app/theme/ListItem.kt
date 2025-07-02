package ai.toolio.app.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ListItemView(text: String, textColor: Color, alignment: Alignment, isLarge: Boolean, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFFec5b65))
            .clickable(onClick = onClick)
            .drawBehind {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFFEAEAEA).copy(alpha = 0.17f),
                            Color.White.copy(alpha = 0f)
                        )
                    ),
                    size = size,
                    cornerRadius = CornerRadius(24.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = alignment
    ) {
        if (isLarge) {
            BodyTextLarge(text = text, color = textColor)
        } else {
            BodyText(text = text, color = textColor)
        }
    }
}

@Preview
@Composable
fun ListItemViewPreview() {
    ListItemView(text = "Test", textColor = Color.Black, alignment = Alignment.Center, isLarge = true)
}