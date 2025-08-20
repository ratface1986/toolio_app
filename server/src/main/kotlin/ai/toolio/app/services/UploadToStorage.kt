package ai.toolio.app.services

import ai.toolio.app.ToolioConfig
import java.io.File

fun saveImageToLocalStorage(imageBytes: ByteArray, fileName: String): String {
    val storagePath = ToolioConfig.storagePath // по умолчанию "/app/storage"
    val dir = File(storagePath)

    if (!dir.exists()) {
        val created = dir.mkdirs()
        println("MYDATA Storage dir created=$created at $storagePath")
    } else {
        println("MYDATA Storage dir already exists at $storagePath")
    }

    val file = File(dir, fileName)
    file.writeBytes(imageBytes)

    // Проверим, что файл реально записался
    val exists = file.exists()
    val length = if (exists) file.length() else -1

    println("MYDATA Saving image to: ${file.absolutePath} (${imageBytes.size} bytes)")
    println("MYDATA File exists=$exists, actualSize=$length")
    println("MYDATA Return url: /uploads/$fileName")

    return "/uploads/$fileName"
}



fun deleteImageFromLocalStorage(fileName: String) {
    val storagePath = ToolioConfig.storagePath
    val file = File("$storagePath/$fileName")

    if (file.exists()) {
        file.delete()
    }
}
