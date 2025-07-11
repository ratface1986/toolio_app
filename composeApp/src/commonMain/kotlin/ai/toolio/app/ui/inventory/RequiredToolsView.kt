package ai.toolio.app.ui.inventory

import ai.toolio.app.data.toDrawableResource
import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.misc.MeasureType
import ai.toolio.app.misc.buildSystemPrompt
import ai.toolio.app.models.RepairTaskSession
import ai.toolio.app.models.Tool
import ai.toolio.app.models.UserProfile
import ai.toolio.app.models.UserSettings
import ai.toolio.app.theme.BackButton
import ai.toolio.app.theme.HeadlineMediumText
import ai.toolio.app.theme.TaskView
import ai.toolio.app.ui.shared.ScreenWrapper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RequiredToolsView(
    onAddToolClicked: (tool: Tool) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val tools by remember { mutableStateOf(AppEnvironment.userProfile.sessions.last().task.tools) }

    fun saveNewRepairTaskSession() {
        scope.launch {
            try {
                AppEnvironment.updateSession(
                    isSaved = true,
                    initialPrompt = buildSystemPrompt()
                )
                val response = AppEnvironment.repo.saveNewSession(AppEnvironment.userProfile.sessions.last())
            } catch (e: Error) {
                println("Error saving new repair task session: ${e.message}")
            } finally {
                onConfirm()
            }

        }

    }

    ScreenWrapper {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackButton(onClick = onBack)
                    Spacer(Modifier.width(16.dp))
                    HeadlineMediumText("Required Tools")
                }

                Spacer(Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(tools) { tool ->
                        val toolData = AppEnvironment.userProfile.getTool(tool)
                        TaskView(
                            header = tool.displayName,
                            subHeader = toolData?.name ?: "",
                            subHeaderColor = Color.Black,
                            icon = tool.toDrawableResource(),
                            showButton = true,
                            showChecked = toolData?.confirmed ?: false,
                            buttonLabel = if (toolData?.confirmed == true) "Edit" else "Add",
                            onClick = {
                                onAddToolClicked(tool)
                            }
                        )
                    }
                }
            }

            // Confirm button at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Button(
                    onClick = { saveNewRepairTaskSession() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Confirm", fontSize = 19.sp)
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewRequiredToolsView() {
    AppEnvironment.setUserProfile(
        UserProfile(
            userId = "123456789",
            inventory = mapOf(),
            settings = UserSettings(
                "123456789",
                "test",
                "en",
                MeasureType.INCH
            ),
            sessions = mutableListOf(
                RepairTaskSession(
                    sessionId = "123",
                    title = "test"
                )
            )
        )
    )

    RequiredToolsView(
        onAddToolClicked = {},
        onConfirm = {},
        onBack = {}
    )

}