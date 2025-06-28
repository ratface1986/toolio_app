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
import ai.toolio.app.ui.wizard.TaskChooserWizardScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

sealed class AppScreen {
    object MainMenu : AppScreen()
    object Wizard : AppScreen()
    object Questions : AppScreen()
    object Inventory : AppScreen()
    object RequiredTools : AppScreen()
    object AddTool : AppScreen()
    object Chat : AppScreen()
}

@Composable
fun MainScreenController(
    categories: List<TaskCategory>,
    startScreen: AppScreen = AppScreen.MainMenu
) {
    var screen by remember { mutableStateOf(startScreen) }
    var requiredTool: Tool by remember { mutableStateOf(Tool.DRILL) }
    var userProfile by remember { mutableStateOf(AppEnvironment.userProfile) }

    Box(
        Modifier.fillMaxSize()
    ) {
        when (screen) {
            AppScreen.MainMenu -> {
                MainMenuScreen(
                    lastActiveTask = userProfile.sessions.firstOrNull()?.task,
                    completedTaskNames = listOf("Hang shelf", "Install TV"), // or emptyList()
                    onContinueTask = { /* handle continue */ },
                    onStartNewProject = {
                        userProfile.sessions.add(RepairTaskSession())
                        screen = AppScreen.Wizard
                    }
                )
            }
            AppScreen.Wizard -> {
                TaskChooserWizardScreen(
                    categories = categories,
                    onCategoryChosen = { category, task ->
                        userProfile.sessions.firstOrNull()?.copy(
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
                    onBack = {
                        screen = AppScreen.Wizard
                    }
                )
            }
            AppScreen.RequiredTools -> {
                RequiredToolsView(
                    title = "Required Tools",
                    onAddToolClicked = { tool ->
                        requiredTool = tool
                        screen = AppScreen.AddTool
                    },
                    onConfirm = {
                        screen = AppScreen.Chat
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
                    followUpQuestions = userProfile.sessions.first().task.followUpQuestions,
                    onComplete = { answers ->
                        userProfile.sessions.first().copy(
                            answers = answers.associate { it.first.question to it.second }
                        )
                        screen = AppScreen.RequiredTools
                    },
                    onBack = {
                        screen = AppScreen.Wizard
                    }
                )
            }

            AppScreen.Inventory -> TODO()
        }
    }
}