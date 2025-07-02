package ai.toolio.app.ui.dashboard

import ai.toolio.app.data.toColor
import ai.toolio.app.data.toDisplayText
import ai.toolio.app.data.toDrawableResource
import ai.toolio.app.models.CategoryType
import ai.toolio.app.models.Task
import ai.toolio.app.models.TaskStatus
import ai.toolio.app.theme.HeadlineLargeText
import ai.toolio.app.theme.ListItemView
import ai.toolio.app.theme.TaskView
import ai.toolio.app.theme.TitleText
import ai.toolio.app.ui.shared.ScreenWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MainMenuScreen(
    lastActiveTask: Task?,
    completedTaskNames: List<String>,
    onContinueTask: (() -> Unit)? = null,
    onStartNewProject: () -> Unit
) {
    ScreenWrapper {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            HeadlineLargeText("Welcome to Toolio")

            Spacer(modifier = Modifier.height(32.dp))
            TitleText("Last Active Session")

            if (lastActiveTask == null) {
                Text(
                    text = "You are about to fix something",
                    color = Color(0xFF4B1E22),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 6.dp)
                )
            } else {
                TaskView(
                    header = lastActiveTask.name,
                    subHeader = lastActiveTask.status.toDisplayText(),
                    subHeaderColor = lastActiveTask.status.toColor(),
                    icon = CategoryType.FIX.toDrawableResource(),
                    showButton = lastActiveTask.status == TaskStatus.IN_PROGRESS,
                    onClick = { onContinueTask?.invoke() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            TitleText("Completed Tasks")

            if (completedTaskNames.isEmpty()) {
                Text(
                    text = "You haven't completed tasks yet.",
                    color = Color(0xFF4B1E22),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 18.dp, horizontal = 6.dp)
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    completedTaskNames.forEach { name ->
                        ListItemView(
                            text = name,
                            textColor = Color.White,
                            alignment = Alignment.CenterStart,
                            isLarge = false
                        )
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

@Preview
@Composable
fun PreviewMainMenuScreen() {
    MainMenuScreen(
        lastActiveTask = Task("", "Fix shelve", emptyList(), emptyList()), // Task? object
        completedTaskNames = listOf("Hang shelf", "Install TV"), // or emptyList()
        onContinueTask = { /* handle continue */ },
        onStartNewProject = { /* begin new project */ }
    )
}