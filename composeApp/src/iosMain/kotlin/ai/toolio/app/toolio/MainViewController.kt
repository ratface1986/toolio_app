package ai.toolio.app

import ai.toolio.app.di.SubscriptionManager
import ai.toolio.app.toolio.spec.IOSAuthService
import ai.toolio.app.toolio.spec.IOSPhotoPicker
import ai.toolio.app.utils.NativeFeatures
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() =
    ComposeUIViewController {
        LaunchedEffect(Unit) {
            SubscriptionManager.initialize("appl_isJjdcilvaGPTVZljDgVgcXfmdx")
        }

        App(
            NativeFeatures(
                photoPicker = IOSPhotoPicker(),
                authService = IOSAuthService()
            )
        )
    }
