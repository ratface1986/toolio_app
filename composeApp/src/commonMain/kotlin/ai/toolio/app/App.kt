package ai.toolio.app

import ai.toolio.app.ui.MainScreenController
import androidx.compose.material3.*
import androidx.compose.runtime.*
import ai.toolio.app.ui.chat.ChatView
import ai.toolio.app.ui.theme.AppDarkColorScheme
import ai.toolio.app.ui.wizard.TaskCategory
import ai.toolio.app.ui.wizard.Tasks
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme(
        colorScheme = AppDarkColorScheme
    ) {
        MainScreenController(categories = Tasks.categories)
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}