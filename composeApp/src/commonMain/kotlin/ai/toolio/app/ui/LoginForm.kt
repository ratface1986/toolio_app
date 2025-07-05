package ai.toolio.app.ui

import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.di.AppSessions
import ai.toolio.app.di.AuthResult
import ai.toolio.app.di.AuthService
import ai.toolio.app.models.UserProfile
import ai.toolio.app.repo.ToolioRepo
import ai.toolio.app.theme.HeadlineLargeText
import ai.toolio.app.ui.shared.MyAlertDialog
import ai.toolio.app.ui.shared.ScreenWrapper
import ai.toolio.app.utils.NativeFeatures
import ai.toolio.app.utils.PhotoPicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoginForm(nativeFeatures: NativeFeatures, onLoginSuccess: (UserProfile, Boolean) -> Unit) {
    val scope = rememberCoroutineScope()
    val toolioRepo = remember { ToolioRepo.getInstance() }

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var isLoginFailed by remember { mutableStateOf(false) }
    var isLoginFailedMessage by remember { mutableStateOf("AAAAA") }
    //var fb: FirebaseAuth

    fun onLoginClick(isUserExists: Boolean = false) {
        isLoading = true
        error = null
        scope.launch {
            try {
                //val userNickname = AppSessions.getUserNickname().takeIf { it.isNotBlank() } ?: name.trim()
                val userNickname = name.trim()
                val profile = toolioRepo.login(userNickname)
                AppSessions.saveUserId(profile.userId)
                AppSessions.saveUserNickname(profile.settings.nickname)
                AppEnvironment.init(
                    userProfile = profile,
                    nativeFeatures = nativeFeatures,
                    repo = toolioRepo
                )
                onLoginSuccess(profile, isUserExists)
            } catch (e: Exception) {
                error = e.message ?: "Something went wrong"
            } finally {
                isLoading = false
            }
        }
    }

    fun onGoogleSignInClick(isUserExists: Boolean = false) {
        isLoading = true
        scope.launch {
            val result = nativeFeatures.authService.signInWithGoogle()
            when (result) {
                is AuthResult.Success -> {
                    val nickname = result.displayName ?: "No nickname provided by Google."
                    val email = result.email ?: "No email provided by Google."
                    val profile = toolioRepo.loginWithGoogle(result.userId, nickname, email)

                    AppSessions.saveUserId(profile.userId)
                    AppSessions.saveUserNickname(nickname)

                    AppEnvironment.init(
                        userProfile = profile.copy(
                            settings = profile.settings.copy(
                                nickname = nickname,
                                email = email,
                            )
                        ),
                        nativeFeatures = nativeFeatures,
                        repo = toolioRepo
                    )
                    isLoading = false
                    onLoginSuccess(profile, isUserExists)
                }

                is AuthResult.Error -> {
                    println("Sign-in error: ${result.message}")
                    isLoginFailed = true
                    error = result.message
                }

                AuthResult.Cancelled -> {
                    println("Sign-in cancelled.")
                    error = "Sign-in cancelled."
                    isLoginFailed = true
                }
            }
        }
    }

    if (AppSessions.getUserNickname().isNotBlank()) {
        //onLoginClick(true)
    }

    ScreenWrapper { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoginFailed) {
                MyAlertDialog(
                    title = "Login Failed",
                    contentText = isLoginFailedMessage,
                    onConfirm = {
                        isLoginFailed = false
                    },
                    onDismiss = {
                        isLoginFailed = false
                    }
                )
            } else {


                HeadlineLargeText("Toolio")
                Spacer(Modifier.height(48.dp))

                Text("What's your name?", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.padding(innerPadding),
                    color = Color.Transparent // не затираем основной фон
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Name", color = Color(0xFF554433)) }, // тёплый нейтральный
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedPlaceholderColor = Color(0xFF6E4C2F),
                            unfocusedPlaceholderColor = Color(0xFF987654),
                            focusedContainerColor = Color.White.copy(alpha = 0.95f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                val buttonEnabled = !isLoading && name.trim().length >= 2

                Button(
                    onClick = {
                        //onLoginClick()
                        onGoogleSignInClick()
                    },
                    enabled = buttonEnabled,
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(0.7f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (buttonEnabled) Color(0xFF443A94) else Color(0xFFa47e5c),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFa47e5c),
                        disabledContentColor = Color.White.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    when {
                        isLoading -> CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )

                        error != null -> {
                            Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Retry Login")
                        }

                        else -> Text("Login")
                    }
                }

                if (error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "❌ $error",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun LoginFormPreview() {
    LoginForm(NativeFeatures(object : PhotoPicker {
        override fun pickPhoto(onResult: (ByteArray?) -> Unit) {
            onResult(null)
        }
    }, authService = object : AuthService {
        override suspend fun signInWithGoogle(): AuthResult {
            TODO("Not yet implemented")
        }

        override suspend fun signOut(): Boolean {
            TODO("Not yet implemented")
        }
    }), { _, _ -> })
}
