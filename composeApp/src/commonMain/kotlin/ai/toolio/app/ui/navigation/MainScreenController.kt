package ai.toolio.app.ui

import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.ui.wizard.TaskChooserWizardScreen
import ai.toolio.app.ui.chat.ChatView
import ai.toolio.app.ui.inventory.AddToolView
import ai.toolio.app.ui.inventory.QuestionsView
import ai.toolio.app.ui.inventory.RequiredToolsView
import ai.toolio.app.ui.wizard.model.Task
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier

import ai.toolio.app.ui.wizard.model.TaskCategory
import ai.toolio.app.ui.wizard.model.Tasks
import ai.toolio.app.models.Tool
import ai.toolio.app.utils.NativeFeatures

// Define the available screens
sealed class AppScreen {
    object Login : AppScreen()
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
    startScreen: AppScreen = AppScreen.Login,
    nativeFeatures: NativeFeatures
) {
    var screen by remember { mutableStateOf(startScreen) }
    var selectedTask: Task by remember { mutableStateOf(Tasks.categories.first().tasks.first()) }
    var requiredTool: Tool by remember { mutableStateOf(Tool.DRILL) }

    Box(Modifier.fillMaxSize()) {
        when (screen) {
            AppScreen.Login -> {
                // Replace this with your actual LoginForm composable
                LoginForm(
                    nativeFeatures = nativeFeatures,
                    onLoginSuccess = { profile ->
                        screen = AppScreen.Wizard
                    }
                )
            }
            AppScreen.Wizard -> {
                TaskChooserWizardScreen(
                    categories = categories,
                    onCategoryChosen = { task ->
                        selectedTask = task
                        if (task.followUpQuestions.isEmpty()) {
                            screen = AppScreen.RequiredTools
                        } else {
                            screen = AppScreen.Questions
                        }

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
                    task = selectedTask,
                    onAddToolClicked = { tool ->
                        requiredTool = tool
                        screen = AppScreen.AddTool
                    },
                    onConfirm = {
                        screen = AppScreen.Chat
                    },
                    isToolAdded = { tool ->
                        val isAdded = AppEnvironment
                            .userProfile
                            .inventory
                            .getOrElse(tool.name) { null }

                        isAdded?.confirmed ?: false
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
                    followUpQuestions = selectedTask?.followUpQuestions ?: emptyList(),
                    onConfirm = { question, string ->
                        screen = AppScreen.RequiredTools
                    }
                )
            }

            AppScreen.Inventory -> TODO()
        }
    }
}