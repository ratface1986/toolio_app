package ai.toolio.app.utils

interface MediaInputManager {
    fun pickPhoto(onResult: (ByteArray?) -> Unit)

    fun startRecording()

    fun stopRecording(onResult: (ByteArray?) -> Unit)
}