package ai.toolio.app.ui.dashboard

import ai.toolio.app.data.toColor
import ai.toolio.app.data.toDisplayText
import ai.toolio.app.data.toDrawableResource
import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.di.SubscriptionManager
import ai.toolio.app.misc.MeasureType
import ai.toolio.app.models.RepairTaskSession
import ai.toolio.app.models.Task
import ai.toolio.app.models.TaskStatus
import ai.toolio.app.models.UserProfile
import ai.toolio.app.models.UserSettings
import ai.toolio.app.theme.HeadlineLargeText
import ai.toolio.app.theme.ListItemView
import ai.toolio.app.theme.TaskView
import ai.toolio.app.theme.TitleText
import ai.toolio.app.ui.shared.ScreenWrapper
import ai.toolio.app.ui.subscription.SubscriptionBottomSheet
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MainMenuScreen(
    lastActiveSession: RepairTaskSession?,
    completedTaskNames: List<String>,
    onContinueTask: (() -> Unit)? = null,
    onStartTextSession: () -> Unit,
    onStartPremiumSession: () -> Unit
) {
    val scope = rememberCoroutineScope()

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

            if (lastActiveSession == null) {
                Text(
                    text = "You are about to fix something",
                    color = Color(0xFF4B1E22),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 6.dp)
                )
            } else {
                TaskView(
                    header = lastActiveSession.task.name,
                    subHeader = lastActiveSession.task.status.toDisplayText(),
                    subHeaderColor = lastActiveSession.task.status.toColor(),
                    icon = lastActiveSession.category.type.toDrawableResource(),
                    showButton = lastActiveSession.task.status == TaskStatus.IN_PROGRESS,
                    showChecked = lastActiveSession.task.status == TaskStatus.COMPLETED,
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
                .padding(start = 24.dp, end = 24.dp, bottom = 40.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = Color.Black.copy(alpha = 0.05f),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                )
                .background(Color(0xFFE6580F), shape = RoundedCornerShape(24.dp))
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column {
                    TitleText(text = "Text left: ${AppEnvironment.userProfile.textSessions}", Color.White)
                    Button(
                        onClick = onStartTextSession,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8e44ad),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (AppEnvironment.userProfile.textSessions == 0) "Buy more sessions" else "Start TEXT project",
                            fontSize = 19.sp
                        )
                    }
                }

                Divider(
                    color = Color(0x22000000),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Column {
                    TitleText(
                        text = "Premium left: ${AppEnvironment.userProfile.premiumSessions}",
                        textColor = Color.White
                    )
                    Button(
                        onClick = onStartPremiumSession,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF27ae60),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (AppEnvironment.userProfile.premiumSessions == 0) "Buy more sessions" else "Start PREMIUM project",
                            fontSize = 19.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMainMenuScreen() {
    AppEnvironment.setUserProfile(
        UserProfile(
            userId = "123456789",
            inventory = mapOf(),
            settings = UserSettings(
                "123456789",
                "test",
                "en",
                MeasureType.INCH
            )
        )
    )

    MainMenuScreen(
        lastActiveSession = RepairTaskSession("", "Fix shelve", task = Task(
            "", "Fix shelve", emptyList(), emptyList()
        )), // Task? object
        completedTaskNames = listOf("Hang shelf", "Install TV"), // or emptyList()
        onContinueTask = {  },
        onStartTextSession = {    },
        onStartPremiumSession = {    }
    )
}