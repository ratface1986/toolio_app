package ai.toolio.app.ui.inventory

import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.models.Tool
import ai.toolio.app.models.ToolData
import ai.toolio.app.models.ToolRecognitionResult
import ai.toolio.app.theme.BackButton
import ai.toolio.app.theme.HeadlineMediumText
import ai.toolio.app.ui.shared.ScreenWrapper
import ai.toolio.app.ui.shared.ToolPhotoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class, InternalResourceApi::class)
@Composable
fun AddToolView(
    tool: Tool,
    onAdded: () -> Unit = {},
    onNoToolClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var toolImageData by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var toolName by remember { mutableStateOf<String>("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var result by remember {
        mutableStateOf<ToolRecognitionResult>(
            ToolRecognitionResult(
                matchesExpected = false,
                name = tool.displayName,
                description = "",
                imageUrl = ""
            )
        )
    }

    println("MYDATA AddToolView: userId: ${AppEnvironment.userProfile.userId}")

    fun processPhoto(photoBytes: ByteArray) {
        val prompt = tool.displayName
        val userId = AppEnvironment.userProfile.userId

        scope.launch {
            isLoading = true
            errorMsg = null

            try {
                result = AppEnvironment.repo.verifyTool(
                    userId = userId,
                    prompt = prompt,
                    imageBytes = photoBytes
                )

                if (result.matchesExpected) {
                    // Update inventory with verified tool
                    toolName = result.name ?: tool.displayName
                } else {
                    // Tool does not match expected one
                    errorMsg = "It does not match the expected: ${tool.displayName}"
                }
            } catch (e: Exception) {
                // Error during verification
                errorMsg = "Failed to verify tool: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun confirmTool() {
        val updatedInventory = AppEnvironment.userProfile.inventory.toMutableMap()
        updatedInventory[tool.name] = ToolData(
            name = result.name ?: tool.displayName,
            description = result.description ?: "",
            imageUrl = result.imageUrl ?: "",
            confirmed = result.matchesExpected
        )

        AppEnvironment.setUserProfile(
            AppEnvironment.userProfile.copy(inventory = updatedInventory)
        )

        scope.launch {
            isLoading = true
            errorMsg = null

            try {
                val confirmResult = AppEnvironment.repo.confirmTool(
                    userId = AppEnvironment.userProfile.userId,
                    toolType = tool.name,
                    toolData = AppEnvironment.userProfile.getTool(tool),
                )

                onAdded()
            } catch (e: Exception) {
                // Error during verification
                errorMsg = "Failed to verify tool: ${e.message}"
                error("MYDATA confirmTool result: $errorMsg")
            } finally {
                isLoading = false
            }
        }
    }

    ScreenWrapper(isLoading = isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onClick = onBackClick)
                Spacer(Modifier.width(16.dp))
                HeadlineMediumText("Verify Your Tool")
            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                ToolPhotoView(toolImageData)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Camera
                Button(
                    onClick = {
                        AppEnvironment.nativeFeatures.mediaManager.pickPhoto { photoBytes ->
                            if (photoBytes != null) {
                                toolImageData = photoBytes
                                processPhoto(photoBytes)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Take Photo")
                    Spacer(Modifier.width(8.dp))
                    Text("Camera")
                }
                Spacer(Modifier.width(16.dp))
                // Library (simulate as same as camera for this demo)
                Button(
                    onClick = {
                        AppEnvironment.nativeFeatures.mediaManager.pickPhoto { photoBytes ->
                            if (photoBytes != null) {
                                toolImageData = photoBytes
                                processPhoto(photoBytes)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Pick from Library")
                    Spacer(Modifier.width(8.dp))
                    Text("Gallery")
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            // Tool Name Field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text("Tool:", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.width(12.dp))
                Text(toolName.ifBlank { "..." }, fontSize = 17.sp)
            }
            Spacer(Modifier.height(18.dp))
            if (errorMsg != null) {
                Text(
                    text = errorMsg!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            val confirmEnabled = toolImageData != null && toolName.isNotBlank() && !isLoading && errorMsg == null
            AddToolButtons(
                enabled = confirmEnabled,
                onAdd = {
                    confirmTool()
                },
                onNoTool = onNoToolClick
            )
        }
    }
}

@Preview
@Composable
fun AddToolViewPreview() {
    AddToolView(
        tool = Tool.DRILL,
        onAdded = {},
        onNoToolClick = {},
        onBackClick = {}
    )
}

@Composable
fun AddToolButtons(
    enabled: Boolean,
    onAdd: () -> Unit,
    onNoTool: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // Top button
        Button(
            onClick = onAdd,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .align(Alignment.TopCenter),
            enabled = enabled
        ) {
            Text("Add To Inventory")
        }
        Spacer(Modifier.height(160.dp))
        // Bottom button
        OutlinedButton(
            onClick = onNoTool,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.BottomCenter),
            border = ButtonDefaults.outlinedButtonBorder(true)
        ) {
            Icon(Icons.Default.Warning, contentDescription = "Don't Have", tint = Color.Red)
            Spacer(Modifier.width(8.dp))
            Text("I don't have it", color = Color.Red)
        }
    }
}
