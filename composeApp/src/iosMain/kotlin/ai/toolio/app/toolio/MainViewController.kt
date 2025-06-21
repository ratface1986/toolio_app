package ai.toolio.app

import ai.toolio.app.toolio.spec.IOSPhotoPicker
import ai.toolio.app.utils.NativeFeatures
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() =
    ComposeUIViewController {
        App(
            NativeFeatures(
                photoPicker = IOSPhotoPicker()
            )
        )
    }