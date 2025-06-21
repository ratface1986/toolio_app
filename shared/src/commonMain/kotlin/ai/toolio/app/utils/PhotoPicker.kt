package ai.toolio.app.utils

interface PhotoPicker {
    fun pickPhoto(onResult: (ByteArray?) -> Unit)

}