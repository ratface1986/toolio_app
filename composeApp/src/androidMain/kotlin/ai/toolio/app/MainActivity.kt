package ai.toolio.app

import ai.toolio.app.di.AppSessions
import ai.toolio.app.spec.AndroidPhotoPicker
import ai.toolio.app.utils.NativeFeatures
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCaller

class MainActivity : ComponentActivity(), ActivityResultCaller {

    private lateinit var permissionsCheck: () -> Boolean
    private lateinit var launchPermissions: () -> Unit
    // Always create AndroidPhotoPicker at construction time
    private lateinit var photoPicker: AndroidPhotoPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Register the picker in onCreate *before* any async logic!
        photoPicker = AndroidPhotoPicker(this)

        AppSessions.init(applicationContext)

        val permissions = buildPermissionsList()
        val grantedState = mutableStateOf(
            permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
        )

        val permissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            grantedState.value = result.all { it.value }
        }

        permissionsCheck = {
            permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
        }

        launchPermissions = {
            permissionsLauncher.launch(permissions.toTypedArray())
        }

        setContent {
            val granted by grantedState

            LaunchedEffect(granted) {
                if (!granted) {
                    launchPermissions()
                }
            }
            if (granted) {
                App(
                    NativeFeatures(
                        photoPicker = photoPicker
                    )
                )
            } else {
                PermissionRequiredScreen {
                    launchPermissions()
                }
            }
        }
    }

    private fun buildPermissionsList(): List<String> {
        val perms = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        val imagePermission = if (android.os.Build.VERSION.SDK_INT >= 33) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        perms.add(imagePermission)
        return perms
    }
}

@Composable
fun PermissionRequiredScreen(onRequest: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    "This app requires camera and microphone permissions to work. Please grant them.",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(24.dp))
                Button(onClick = onRequest) {
                    Text("Grant permissions")
                }
            }
        }
    }
}

@Preview
@Composable
fun PermissionRequiredScreenPreview() {
    PermissionRequiredScreen {}
}