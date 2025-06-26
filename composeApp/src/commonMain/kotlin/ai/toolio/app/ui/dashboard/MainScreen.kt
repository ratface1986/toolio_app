package ai.toolio.app.ui.dashboard

import ai.toolio.app.models.CategoryType
import ai.toolio.app.models.TaskItem
import ai.toolio.app.models.TaskStatus
import ai.toolio.app.theme.BodyText
import ai.toolio.app.theme.BodyTextLarge
import ai.toolio.app.theme.HeadlineLargeText
import ai.toolio.app.theme.ListItemView
import ai.toolio.app.theme.TaskView
import ai.toolio.app.theme.TitleText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MainMenuScreen(
    lastActiveTask: TaskItem?,
    completedTaskNames: List<String>,
    onContinueTask: (() -> Unit)? = null,
    onStartNewProject: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2F403E), // верх - тёмно-зелёный
                        Color(0xFF1A1C1D)  // низ - почти чёрный
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding),
                    color = Color.Transparent
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    HeadlineLargeText("Welcome to Toolio PIDAR")

                    Spacer(modifier = Modifier.height(32.dp))
                    TitleText("Last Active Session")

                    if (lastActiveTask == null) {
                        Text(
                            text = "You are about to fix something",
                            color = Color(0xC2C2C2).copy(1f),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 20.dp, horizontal = 6.dp)
                        )
                    } else {
                        TaskView(
                            taskName = lastActiveTask.title,
                            categoryType = CategoryType.FIX,
                            taskStatus = lastActiveTask.status,
                            showButton = true,
                            onClick = { onContinueTask?.invoke() }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    TitleText("Completed Tasks")

                    if (completedTaskNames.isEmpty()) {
                        Text(
                            text = "You haven't completed tasks yet.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 18.dp, horizontal = 6.dp)
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            completedTaskNames.forEach { name ->
                                ListItemView(text = name, textColor = Color.White, alignment = Alignment.CenterStart, isLarge = false)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(80.dp)) // for bottom action button spacing
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(24.dp, 24.dp, 24.dp, 40.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = CircleShape,
                            ambientColor = Color.White.copy(alpha = 0.6f),
                            spotColor = Color.White.copy(alpha = 0.6f)
                        )
                        .background(Color.White, shape = CircleShape)
                ) {
                    Button(
                        onClick = onStartNewProject,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFFFFF),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = "Start new project", fontSize = 19.sp)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMainMenuScreen() {
    MainMenuScreen(
        lastActiveTask = TaskItem("Fix shelve", Icons.Default.Category, TaskStatus.IN_PROGRESS), // Task? object
        completedTaskNames = listOf("Hang shelf", "Install TV"), // or emptyList()
        onContinueTask = { /* handle continue */ },
        onStartNewProject = { /* begin new project */ }
    )
}