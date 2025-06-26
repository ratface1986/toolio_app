package ai.toolio.app.theme

import ai.toolio.app.data.toColor
import ai.toolio.app.data.toDrawableResource
import ai.toolio.app.models.CategoryType
import ai.toolio.app.models.TaskStatus
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TaskView(
    taskName: String,
    categoryType: CategoryType,
    taskStatus: TaskStatus,
    showButton: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .size(66.dp)
            .background(Color(0xFF616161)) // фон карточки
            .drawBehind {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0x2BEAEAEA), // верхняя часть бордюра
                            Color.Transparent
                        )
                    ),
                    size = size,
                    cornerRadius = CornerRadius(28.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF242424))
                .border(
                    width = 1.17.dp,
                    color = Color(0xFF171A0F),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(categoryType.toDrawableResource()),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp)
            ,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                TitleMediumText(text = taskName)
                BodyText(text = taskStatus.toDisplayText(), color = taskStatus.toColor())
            }

            if (showButton) {
                Button(
                    onClick = { onClick?.invoke() },
                    shape = RoundedCornerShape(21.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Continue",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

    }
}

@Preview
@Composable
fun TaskViewPreview() {
    TaskView(
        taskName = "Fix shelve",
        categoryType = CategoryType.FIX,
        taskStatus = TaskStatus.IN_PROGRESS
    )
}