package ai.toolio.app.ui

import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.models.RepairTaskSession
import ai.toolio.app.models.TaskCategory
import ai.toolio.app.models.TaskStatus
import ai.toolio.app.models.Tool
import ai.toolio.app.ui.chat.ChatView
import ai.toolio.app.ui.dashboard.MainMenuScreen
import ai.toolio.app.ui.inventory.AddToolView
import ai.toolio.app.ui.inventory.QuestionsView
import ai.toolio.app.ui.inventory.RequiredToolsView
import ai.toolio.app.ui.sidemenu.SettingsView
import ai.toolio.app.ui.wizard.TaskChooserWizardScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.benasher44.uuid.uuid4
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed class AppScreen {
    object MainMenu : AppScreen()
    object Wizard : AppScreen()
    object Questions : AppScreen()
    object Inventory : AppScreen()
    object RequiredTools : AppScreen()
    object AddTool : AppScreen()
    object Chat : AppScreen()
    object Settings : AppScreen()
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun MainScreenController(
    categories: List<TaskCategory>,
    startScreen: AppScreen = AppScreen.MainMenu
) {
    var screen by remember { mutableStateOf(startScreen) }
    var requiredTool: Tool by remember { mutableStateOf(Tool.DRILL) }

    Box(
        Modifier.fillMaxSize()
    ) {
        when (screen) {
            AppScreen.MainMenu -> {
                MainMenuScreen(
                    lastActiveTask = AppEnvironment.userProfile.sessions.firstOrNull()?.task,
                    completedTaskNames = listOf("Hang shelf", "Install TV"), // or emptyList()
                    onContinueTask = { screen = AppScreen.Chat },
                    onStartNewProject = {
                        AppEnvironment.userProfile.sessions.add(RepairTaskSession())
                        screen = AppScreen.Wizard
                    }
                )
            }
            AppScreen.Wizard -> {
                TaskChooserWizardScreen(
                    categories = categories,
                    onCategoryChosen = { category, task ->
                        AppEnvironment.updateSession(
                            sessionId = uuid4().toString(),
                            title = "${category.title} - ${task.name}",
                            category = category,
                            task = task.copy(status = TaskStatus.IN_PROGRESS)
                        )
                        if (task.followUpQuestions.isEmpty()) {
                            screen = AppScreen.RequiredTools
                        } else {
                            screen = AppScreen.Questions
                        }

                    },
                    onBack = {
                        screen = AppScreen.MainMenu
                    }
                )
            }
            AppScreen.Chat -> {
                ChatView(
                    onShowSettings = {
                        screen = AppScreen.Settings
                    },
                    onBack = {
                        screen = AppScreen.MainMenu
                    }
                )
            }
            AppScreen.RequiredTools -> {
                RequiredToolsView(
                    onAddToolClicked = { tool ->
                        requiredTool = tool
                        screen = AppScreen.AddTool
                    },
                    onConfirm = {
                        screen = AppScreen.Chat
                    },
                    onBack = {
                        screen = AppScreen.Wizard
                    }
                )
            }
            AppScreen.AddTool -> {
                AddToolView(
                    tool = requiredTool,
                    onAdded = {
                        screen = AppScreen.RequiredTools
                    },
                    onNoToolClick = {
                        screen = AppScreen.RequiredTools
                    },
                    onBackClick = {
                        screen = AppScreen.RequiredTools
                    },
                )
            }
            AppScreen.Questions -> {
                QuestionsView(
                    followUpQuestions = AppEnvironment.userProfile.sessions.first().task.followUpQuestions,
                    onComplete = { answers ->
                        AppEnvironment.updateSession(
                            answers = answers.associate { it.first.question to it.second }
                        )
                        screen = AppScreen.RequiredTools
                    },
                    onBack = {
                        screen = AppScreen.Wizard
                    }
                )
            }
            AppScreen.Settings -> {
                SettingsView(
                    nickname = AppEnvironment.userProfile.nickname,
                    onNicknameChange = {},
                    language = "en",
                    languages = emptyList(),
                    onLanguageChange = {},
                    useMm = false,
                    onUnitsChange = {

                    },
                    onDeleteAllData = {

                    },
                    onBack = {
                        screen = AppScreen.Chat
                    }
                )
            }
            AppScreen.Inventory -> TODO()
        }
    }
}