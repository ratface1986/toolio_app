package ai.toolio.app.spec

import ai.toolio.app.utils.MediaInputManager
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaRecorder
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class AndroidMediaManager(private val activity: Activity) : MediaInputManager {
    private var pickPhotoLauncher: ActivityResultLauncher<String>? = null
    private var onPickPhotoResult: ((ByteArray?) -> Unit)? = null
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

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

    override fun startRecording() {
        val file = File.createTempFile("recording_", ".m4a", activity.cacheDir)
        outputFile = file

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
    }

    override fun stopRecording(onResult: (ByteArray?) -> Unit) {
        val localFile = outputFile
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            onResult(null)
            return
        } finally {
            recorder = null
        }

        val bytes = localFile?.let {
            val data = FileInputStream(it).use { input ->
                input.readBytes()
            }
            it.delete() // очищаем временный файл
            data
        }
        onResult(bytes)
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
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, output) // ← СЖАТИЕ!
        output.toByteArray()
    } catch (e: Exception) {
        null
    }
}
