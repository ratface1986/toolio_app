package ai.toolio.app

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

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val permissions = buildPermissionsList()

        var permissionsGranted: Boolean? = null

        val permissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            // All must be true
            permissionsGranted = result.all { it.value }
        }

        permissionsCheck = {
            permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
        }

        launchPermissions = {
            permissionsLauncher.launch(permissions.toTypedArray())
        }

        setContent {
            // Observe permissions
            var granted by remember { mutableStateOf(permissionsCheck()) }
            LaunchedEffect(granted) {
                if (!granted) {
                    launchPermissions()
                }
            }
            // Listen to launcher result and update granted
            LaunchedEffect(permissionsGranted) {
                if (permissionsGranted != null) {
                    granted = permissionsGranted!!
                }
            }
            if (granted) {
                App(
                    NativeFeatures(
                        photoPicker = AndroidPhotoPicker(this)
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