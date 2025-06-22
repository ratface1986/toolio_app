package ai.toolio.app.ui.shared

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object ImageCompressor {

    fun compressJpegByteArray(original: ByteArray, quality: Int = 70): ByteArray {
        val bitmap = BitmapFactory.decodeByteArray(original, 0, original.size)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }

    fun compressAndResize(
        original: ByteArray,
        targetWidth: Int,
        quality: Int = 70
    ): ByteArray {
        val originalBitmap = BitmapFactory.decodeByteArray(original, 0, original.size)
        val aspectRatio = originalBitmap.height.toFloat() / originalBitmap.width
        val targetHeight = (targetWidth * aspectRatio).toInt()
        val resized = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

        val outputStream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }
}