package ai.toolio.app.services

import ai.toolio.app.SupabaseConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess

suspend fun uploadToStorage(httpClient: HttpClient, imageBytes: ByteArray, fileName: String): String {
    val bucket = SupabaseConfig.bucket
    val projectUrl = SupabaseConfig.url
    val accessToken = SupabaseConfig.apiKey.let { "Bearer $it" }

    val uploadUrl = "$projectUrl/storage/v1/object/$bucket/$fileName"

    val response: HttpResponse = httpClient.put(uploadUrl) {
        header("Authorization", accessToken)
        header("Content-Type", "image/jpeg")
        setBody(imageBytes)
    }

    if (!response.status.isSuccess()) {
        error("Upload failed: ${response.status}")
    }

    return "${SupabaseConfig.publicBaseUrl}/$fileName"
}

suspend fun deleteFromStorage(httpClient: HttpClient, fileName: String) {
    val url = "${SupabaseConfig.storageBaseUrl}/${SupabaseConfig.bucket}/$fileName"
    httpClient.delete(url) {
        header(HttpHeaders.Authorization, "Bearer ${SupabaseConfig.apiKey}")
    }
}
