package ai.toolio.app.ui.inventory

import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.models.ToolRecognitionResult
import ai.toolio.app.ui.shared.ToolPhotoView
import ai.toolio.app.models.Tool
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.InternalResourceApi
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalEncodingApi::class, InternalResourceApi::class)
@Composable
fun AddToolView(
    tool: Tool,
    onAdded: () -> Unit = {},
    onNotHave: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var toolImageData by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var toolName by remember { mutableStateOf<String>("") }
    var tasks by remember { mutableStateOf<List<String>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun processPhoto(photoBytes: ByteArray) {
        val base64 = Base64.encode(photoBytes)
        scope.launch {
            isLoading = true
            errorMsg = null
            try {
                // Simulate sending to GPT and parsing result using toolioRepo
                val gptImagePrompt = """
                    Analyze this image and determine if it contains the expected tool: "${tool.displayName}".
                
                    Return a JSON response in the following format:
                
                    {
                      "matchesExpected": true,
                      "type": "drill",
                      "name": "Bosch PSB 1800 LI-2",
                      "description": "A cordless electric drill used for drilling holes and driving screws.",
                      "isTool": true
                    }
                
                    If no tool is present, set "isTool": false and leave all other fields null.
                    If the tool doesn't match expected, set "matchesExpected": false.
                    Only return valid JSON. No explanation.
                """.trimIndent()

                val response = AppEnvironment.repo.chatGptImage(gptImagePrompt, base64)

                response.fold(
                    onSuccess = { gptResponse ->
                        val jsonString = gptResponse.content
                        try {
                            val result = Json.decodeFromString<ToolRecognitionResult>(jsonString)
                            // Теперь у тебя есть: result.matchesExpected, result.name, etc.
                            toolName = result.name ?: ""
                            tasks = result.description?.split("\n") ?: emptyList()
                            isLoading = false
                            if (!result.matchesExpected) {
                                errorMsg = "Tool not found in image"
                            } else {
                                onAdded()
                            }
                        } catch (e: Exception) {
                            errorMsg = "Failed to parse response: ${e.message}"
                        }
                    },
                    onFailure = { error ->
                        errorMsg = error.message ?: "Unknown error"
                    }
                )
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tool Image Area
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
                    AppEnvironment.nativeFeatures.photoPicker.pickPhoto { photoBytes ->
                        if (photoBytes != null) {
                            toolImageData = photoBytes
                            processPhoto(photoBytes)
                        }
                    }
                },
                enabled = !isLoading
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Take Photo")
                Spacer(Modifier.width(8.dp))
                Text("Camera")
            }
            Spacer(Modifier.width(16.dp))
            // Library (simulate as same as camera for this demo)
            Button(
                onClick = {
                    AppEnvironment.nativeFeatures.photoPicker.pickPhoto { photoBytes ->
                        if (photoBytes != null) {
                            toolImageData = photoBytes
                            processPhoto(photoBytes)
                        }
                    }
                },
                enabled = !isLoading
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
        Text(
            "Tasks this tool fits for:",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                .padding(vertical = 6.dp)
        ) {
            if (tasks.isEmpty()) {
                item {
                    Text(
                        text = "No tasks loaded",
                        color = Color.Gray,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
            items(tasks.size) {
                Text(
                    text = tasks[it],
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 14.dp)
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        if (errorMsg != null) {
            Text(
                text = errorMsg!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        Button(
            onClick = { onAdded() },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            enabled = toolImageData != null && toolName.isNotBlank() && !isLoading,
        ) {
            Text("Add To Inventory")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { onNotHave() },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            border = ButtonDefaults.outlinedButtonBorder(true)
        ) {
            Icon(Icons.Default.Warning, contentDescription = "Don't Have", tint = Color.Red)
            Spacer(Modifier.width(8.dp))
            Text("I don't have it", color = Color.Red)
        }
    }
}

@Preview
@Composable
fun AddToolViewPreview() {
    AddToolView(
        tool = Tool.DRILL,
        onAdded = {},
        onNotHave = {}
    )
}
