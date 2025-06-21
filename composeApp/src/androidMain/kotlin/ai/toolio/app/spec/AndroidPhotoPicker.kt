package ai.toolio.app.spec

import ai.toolio.app.utils.PhotoPicker
import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File

class AndroidPhotoPicker(private val activity: Activity) : PhotoPicker {
    private var pickPhotoLauncher: ActivityResultLauncher<String>? = null
    private var onPickPhotoResult: ((ByteArray?) -> Unit)? = null

    init {
        registerPickPhotoLauncher(activity as ActivityResultCaller)
    }
    /**
     * Must be called in Activity's onCreate or before first picking, only once.
     * Pass the Activity or Fragment that implements ActivityResultCaller.
     */
    fun registerPickPhotoLauncher(caller: ActivityResultCaller) {
        pickPhotoLauncher = caller.registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            val result = uri?.let { getBytesFromUri(activity, it) }
            onPickPhotoResult?.invoke(result)
            onPickPhotoResult = null
        }
    }

    override fun pickPhoto(onResult: (ByteArray?) -> Unit) {
        if (pickPhotoLauncher == null) {
            // If not registered, fail gracefully
            onResult(null)
            return
        }
        onPickPhotoResult = onResult
        pickPhotoLauncher?.launch("image/*")
    }
}

// — Utility functions remain the same —

private fun createImageFile(context: Context): File {
    val storageDir = context.externalCacheDir ?: context.cacheDir
    return File.createTempFile("IMG_", ".jpg", storageDir)
}

private fun createImageFileUri(context: Context): Uri {
    val photoFile = createImageFile(context)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )
}

private fun getBytesFromUri(context: Context, uri: Uri): ByteArray? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        }
    } catch (e: Exception) {
        null
    }
}