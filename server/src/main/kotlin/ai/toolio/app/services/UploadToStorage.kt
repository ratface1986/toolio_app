package ai.toolio.app.services

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess

suspend fun uploadToStorage(httpClient: HttpClient, imageBytes: ByteArray, fileName: String): String {
    val bucket = "chat-images"
    val projectUrl = "https://feelmhmnayhaktidaiuf.supabase.co"
    val accessToken = "Bearer 2ff4acdb073f48f2c2d8d31e47e6ccc4"

    val uploadUrl = "$projectUrl/storage/v1/object/$bucket/$fileName"

    val response: HttpResponse = httpClient.put(uploadUrl) {
        header("Authorization", accessToken)
        header("Content-Type", "image/jpeg")
        setBody(imageBytes)
    }

    if (!response.status.isSuccess()) {
        error("Upload failed: ${response.status}")
    }

    return "$projectUrl/storage/v1/object/public/$bucket/$fileName"
}

suspend fun deleteFromStorage(httpClient: HttpClient, fileName: String) {
    val bucket = "chat-images"
    val url = "https://feelmhmnayhaktidaiuf.supabase.co/storage/v1/object/$bucket/$fileName"
    val accessToken = "Bearer 2ff4acdb073f48f2c2d8d31e47e6ccc4"

    httpClient.delete(url) {
        header(HttpHeaders.Authorization, accessToken)
    }
}