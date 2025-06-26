package ai.toolio.app.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.decodeToImageBitmap

@Composable
fun ToolPhotoView(photoBytes: ByteArray?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f) // вертикальное фото
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center
    ) {
        if (photoBytes != null) {
            val imageBitmap = remember(photoBytes) {
                photoBytes.decodeToImageBitmap()
            }

            Image(
                bitmap = imageBitmap,
                contentDescription = "Tool Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Filled.AddPhotoAlternate,
                contentDescription = "Add Photo",
                tint = Color.Gray,
                modifier = Modifier.size(110.dp)
            )
        }
    }

}
