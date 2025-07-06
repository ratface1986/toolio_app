package ai.toolio.app

import ai.toolio.app.di.AuthResult
import ai.toolio.app.di.AuthService
import ai.toolio.app.models.Tasks
import ai.toolio.app.ui.LoginForm
import ai.toolio.app.ui.MainScreenController
import ai.toolio.app.ui.onboarding.OnboardingView
import ai.toolio.app.utils.MediaInputManager
import ai.toolio.app.utils.NativeFeatures
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun App(nativeFeatures: NativeFeatures) {

    var onLoginSuccess by mutableStateOf(false)
    var onOnboardingCompleted by mutableStateOf(false)

    MaterialTheme {
        when {
            !onOnboardingCompleted && !onLoginSuccess -> {
                LoginForm(
                    nativeFeatures = nativeFeatures,
                    onLoginSuccess = { profile, isUserExists ->
                        onOnboardingCompleted = isUserExists
                        onLoginSuccess = true
                    }
                )
            }
            !onOnboardingCompleted && onLoginSuccess -> {
                OnboardingView(
                    onConfirm = { onOnboardingCompleted = true }
                )
            }
            onOnboardingCompleted && onLoginSuccess -> {
                MainScreenController(
                    categories = Tasks.categories
                )
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App(
        nativeFeatures = NativeFeatures(mediaManager = object : MediaInputManager {
            override fun pickPhoto(onResult: (ByteArray?) -> Unit) {
                TODO("Not yet implemented")
            }

            override fun startRecording() {
                TODO("Not yet implemented")
            }

            override fun stopRecording(onResult: (ByteArray?) -> Unit) {
                TODO("Not yet implemented")
            }
        }, authService = object : AuthService {
            override suspend fun signInWithGoogle(): AuthResult {
                TODO("Not yet implemented")
            }

            override suspend fun signOut(): Boolean {
                TODO("Not yet implemented")
            }
        })
    )
}