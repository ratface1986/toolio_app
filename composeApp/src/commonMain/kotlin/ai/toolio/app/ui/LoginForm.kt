package ai.toolio.app.ui

import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.models.UserProfile
import ai.toolio.app.repo.ToolioRepo
import ai.toolio.app.utils.NativeFeatures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun LoginForm(nativeFeatures: NativeFeatures, onLoginSuccess: (UserProfile) -> Unit) {
    val scope = rememberCoroutineScope()
    val toolioRepo = remember { ToolioRepo.getInstance() }

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Toolio Login", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                isLoading = true
                error = null
                scope.launch {
                    try {
                        val profile = toolioRepo.login("Rustem")
                        AppEnvironment.init(
                            userProfile = profile,
                            nativeFeatures = nativeFeatures,
                            repo = toolioRepo
                        )
                        onLoginSuccess(profile)
                    } catch (e: Exception) {
                        error = e.message ?: "Something went wrong"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(0.7f)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                }

                error != null -> {
                    Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Retry Login")
                }

                else -> {
                    Text("Login")
                }
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
