package ai.toolio.app

import ai.toolio.app.ui.MainScreenController
import androidx.compose.material3.*
import androidx.compose.runtime.*
import ai.toolio.app.ui.theme.AppDarkColorScheme
import ai.toolio.app.ui.wizard.model.Tasks
import ai.toolio.app.utils.NativeFeatures
import ai.toolio.app.utils.PhotoPicker
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(settings: NativeFeatures) {
    MaterialTheme(
        colorScheme = AppDarkColorScheme
    ) {
        MainScreenController(
            categories = Tasks.categories,
            settings = settings,
        )
    }
}

@Preview
@Composable
fun AppPreview() {
    App(
        settings = NativeFeatures(photoPicker = object : PhotoPicker {
            override fun pickPhoto(onResult: (ByteArray?) -> Unit) {
                TODO("Not yet implemented")
            }
        })
    )
}