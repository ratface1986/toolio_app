package ai.toolio.app.ui.shared

import ai.toolio.app.di.AppEnvironment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.decodeToImageBitmap

@Composable
fun CrossPlatformImage(
    url: String? = null,
    bytes: ByteArray? = null,
    modifier: Modifier = Modifier
) {
    var imageBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

    LaunchedEffect(url) {
        try {
            val bytes: ByteArray = bytes ?: AppEnvironment.repo.fetchImageByUrl(url.orEmpty())
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

