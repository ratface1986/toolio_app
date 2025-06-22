package ai.toolio.app.services

import ai.toolio.app.ToolioConfig
import java.io.File

fun saveImageToLocalStorage(imageBytes: ByteArray, fileName: String): String {
    val storagePath = ToolioConfig.storagePath // уже /app/storage по дефолту
    val file = File("$storagePath/$fileName")

    file.writeBytes(imageBytes)

    // Возвращаем относительный путь, если надо отдавать URL — зависит от логики
    return "/uploads/$fileName"
}



fun deleteImageFromLocalStorage(fileName: String) {
    val storagePath = ToolioConfig.storagePath
    val file = File("$storagePath/$fileName")

    if (file.exists()) {
        file.delete()
    }
}
