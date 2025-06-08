package ai.toolio.app.ui

import ai.toolio.app.ui.wizard.TaskChooserWizardScreen
import ai.toolio.app.ui.chat.ChatView
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

import ai.toolio.app.ui.wizard.TaskCategory

// Define the available screens
sealed class AppScreen {
    object Login : AppScreen()
    object Wizard : AppScreen()
    object Chat : AppScreen()
}

@Composable
fun MainScreenController(
    categories: List<TaskCategory>, // You can provide your TaskCategory list here
    startScreen: AppScreen = AppScreen.Login // or .Wizard, .Chat
) {
    var screen by remember { mutableStateOf(startScreen) }

    Box(Modifier.fillMaxSize()) {
        when (screen) {
            AppScreen.Login -> {
                // Replace this with your actual LoginForm composable
                LoginForm(
                    onLoginSuccess = {
                        screen = AppScreen.Wizard
                    }
                )
            }
            AppScreen.Wizard -> {
                TaskChooserWizardScreen(
                    categories = categories,
                    onCategoryChosen = {
                        // Optionally you can pass parameters
                        screen = AppScreen.Chat
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
        }
    }
}

/** Dummy composable for LoginForm, replace with your real logic */
@Composable
fun LoginForm(onLoginSuccess: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login (stub)")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onLoginSuccess) {
            Text("Login")
        }
    }
}