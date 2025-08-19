package ai.toolio.app.ui

import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.misc.MeasureType
import ai.toolio.app.misc.SessionType
import ai.toolio.app.models.RepairTaskSession
import ai.toolio.app.models.TaskCategory
import ai.toolio.app.models.TaskStatus
import ai.toolio.app.models.Tool
import ai.toolio.app.ui.chat.ChatView
import ai.toolio.app.ui.dashboard.MainMenuScreen
import ai.toolio.app.ui.dashboard.SubscriptionScreen
import ai.toolio.app.ui.inventory.AddToolView
import ai.toolio.app.ui.inventory.QuestionsView
import ai.toolio.app.ui.inventory.RequiredToolsView
import ai.toolio.app.ui.inventory.SearchTool
import ai.toolio.app.ui.sidemenu.SettingsView
import ai.toolio.app.ui.wizard.TaskChooserWizardScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

sealed class AppScreen {
    object MainMenu : AppScreen()
    object Wizard : AppScreen()
    object Questions : AppScreen()
    object Inventory : AppScreen()
    object RequiredTools : AppScreen()
    object AddTool : AppScreen()
    object Chat : AppScreen()
    object Settings : AppScreen()
    object SearchTool : AppScreen()
    object PurchaseSessions : AppScreen()
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun MainScreenController(
    categories: List<TaskCategory>,
    startScreen: AppScreen = AppScreen.MainMenu
) {
    val scope = rememberCoroutineScope()
    var screen by remember { mutableStateOf(startScreen) }
    var requiredTool: Tool by remember { mutableStateOf(Tool.DRILL) }

    fun updateUserProfile() {
        try {
            scope.launch {
                val result = AppEnvironment.repo.saveUserSettings(AppEnvironment.userProfile)
            }
        } catch (e: Exception) {
            println("Error saving user profile: ${e.message}")
        }
    }

    Box(
        Modifier.fillMaxSize()
    ) {
        when (screen) {
            AppScreen.MainMenu -> {
                AppEnvironment.userProfile.sessions.lastOrNull()?.isSaved?.let { isSaved ->
                    if (!isSaved) {
                        AppEnvironment.userProfile.sessions.removeLast()
                    }
                }

                MainMenuScreen(
                    lastActiveSession = AppEnvironment.userProfile.sessions.lastOrNull(),
                    completedTaskNames = listOf("Hang shelf", "Install TV"), // or emptyList()
                    onContinueTask = { screen = AppScreen.Chat },
                    onStartTextSession = {
                        if (AppEnvironment.userProfile.textSessions == 0 ) {
                            screen = AppScreen.PurchaseSessions
                        } else {
                            AppEnvironment.userProfile.sessions.add(RepairTaskSession(sessionType = SessionType.TEXT))
                            screen = AppScreen.Wizard
                        }
                    },
                    onStartPremiumSession = {
                        if (AppEnvironment.userProfile.premiumSessions == 0 ) {
                            //screen = AppScreen.PurchaseSessions
                            AppEnvironment.userProfile.sessions.add(RepairTaskSession(sessionType = SessionType.PREMIUM))
                            screen = AppScreen.Wizard
                        } else {
                            AppEnvironment.userProfile.sessions.add(RepairTaskSession(sessionType = SessionType.PREMIUM))
                            screen = AppScreen.Wizard
                        }
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
                        screen = AppScreen.SearchTool
                    },
                    onBackClick = {
                        screen = AppScreen.RequiredTools
                    },
                )
            }
            AppScreen.Questions -> {
                QuestionsView(
                    followUpQuestions = AppEnvironment.userProfile.sessions.last().task.followUpQuestions,
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
                var updatedInches by remember { mutableStateOf(AppEnvironment.userProfile.settings.measure == MeasureType.INCH) }
                var updateLanguage by remember { mutableStateOf(AppEnvironment.userProfile.settings.language) }
                var updateNickname by remember { mutableStateOf(AppEnvironment.userProfile.settings.nickname) }

                SettingsView(
                    nickname = updateNickname,
                    onNicknameChange = { nickname ->
                        updateNickname = nickname
                    },
                    language = updateLanguage,
                    languages = mapOf(
                        "en" to "English",
                        "ru" to "Русский",
                        "de" to "Deutsch",
                        "sp" to "Español",
                        "it" to "Italiano"
                    ),
                    onLanguageChange = { language ->
                        updateLanguage = language
                    },
                    useInches = updatedInches,
                    onUnitsChange = { isUseInches ->
                        updatedInches = isUseInches
                    },
                    onDeleteAllData = {
                        //
                    },
                    onBack = {
                        AppEnvironment.updateUserSettings(
                            nickname = updateNickname,
                            language = updateLanguage,
                            measure = if (updatedInches) MeasureType.INCH else MeasureType.CM,
                        )
                        updateUserProfile()
                        screen = AppScreen.Chat
                    }
                )
            }
            AppScreen.SearchTool -> {
                SearchTool(
                    tool = requiredTool,
                    onBack = {
                        screen = AppScreen.AddTool
                    }
                )
            }
            AppScreen.PurchaseSessions -> {
                SubscriptionScreen(
                    onBackClick = {
                        screen = AppScreen.MainMenu
                    }
                )
            }
            AppScreen.Inventory -> TODO()
        }
    }
}