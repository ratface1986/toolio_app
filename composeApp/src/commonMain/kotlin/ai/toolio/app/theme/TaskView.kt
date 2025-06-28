package ai.toolio.app.theme

import ai.toolio.app.data.toColor
import ai.toolio.app.data.toDrawableResource
import ai.toolio.app.models.CategoryType
import ai.toolio.app.models.TaskStatus
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TaskView(
    header: String,
    subHeader: String,
    subHeaderColor: Color = Color.White,
    icon: DrawableResource,
    showButton: Boolean = false,
    showChecked: Boolean = false,
    buttonLabel: String = "Continue",
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
                painter = painterResource(icon),
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
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TitleMediumText(text = header, color = Color.White)

                    if (showChecked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2ECC40)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Added",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }


                if (subHeader.isNotEmpty())
                    BodyText(text = subHeader, color = subHeaderColor)
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
                        text = buttonLabel,
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
        header = "Fix shelve",
        subHeader = TaskStatus.IN_PROGRESS.toDisplayText(),
        subHeaderColor = TaskStatus.IN_PROGRESS.toColor(),
        icon = CategoryType.FIX.toDrawableResource(),
        showButton = true,
        buttonLabel = "Edit",
        onClick = {}
    )
}