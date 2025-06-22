package ai.toolio.app.ui.shared

import ai.toolio.app.di.AppEnvironment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.jetbrains.compose.resources.decodeToImageBitmap

@Composable
fun CrossPlatformImage(
    url: String,
    modifier: Modifier = Modifier
) {
    var imageBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

    LaunchedEffect(url) {
        try {
            val bytes: ByteArray = AppEnvironment.repo.getHttpClient().get(url).body()
            imageBitmap = bytes.decodeToImageBitmap()
        } catch (e: Exception) {
            println("IMAGE LOAD FAIL: ${e.message}")
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap!!,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxSize()
        )
    } else {
        // Placeholder
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.LightGray)
        )
    }
}

