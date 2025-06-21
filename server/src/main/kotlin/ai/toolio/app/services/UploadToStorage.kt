package ai.toolio.app.services

import ai.toolio.app.SupabaseConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess

suspend fun uploadToStorage(httpClient: HttpClient, imageBytes: ByteArray, fileName: String): String {
    val bucket = SupabaseConfig.bucket
    val uploadUrl = "${SupabaseConfig.storageBaseUrl}/$bucket/$fileName"
    val apiKey = SupabaseConfig.apiKey

    val response = httpClient.put(uploadUrl) {
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        header(HttpHeaders.ContentType, ContentType.Image.JPEG)
        header("x-upsert", "true")
        setBody(imageBytes)
    }

    if (!response.status.isSuccess()) {
        throw IllegalStateException("Upload failed: ${response.status}")
    }

    return "${SupabaseConfig.publicBaseUrl}/$fileName"
}

suspend fun deleteFromStorage(httpClient: HttpClient, fileName: String) {
    val url = "${SupabaseConfig.storageBaseUrl}/${SupabaseConfig.bucket}/$fileName"
    httpClient.delete(url) {
        header(HttpHeaders.Authorization, "Bearer ${SupabaseConfig.apiKey}")
    }
}
