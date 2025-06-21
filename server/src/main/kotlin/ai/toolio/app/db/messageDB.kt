package ai.toolio.app.db

import ai.toolio.app.SupabaseConfig
import ai.toolio.app.models.ChatMessageIn
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

suspend fun insertChatMessage(
    httpClient: HttpClient,
    sessionId: String,
    role: String,
    content: String,
    type: String = "text",
    imageUrl: String? = null
): Boolean {
    val endpoint = "${SupabaseConfig.url}/rest/v1/chat_messages"
    val apiKey = SupabaseConfig.apiKey

    val payload = buildJsonObject {
        put("session_id", JsonPrimitive(sessionId))
        put("role", JsonPrimitive(role))
        put("content", JsonPrimitive(content))
        put("type", JsonPrimitive(type))
        if (imageUrl != null) put("image_url", JsonPrimitive(imageUrl))
    }

    val response = httpClient.post(endpoint) {
        header("apikey", apiKey)
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        setBody(payload.toString())
    }

    return response.status.isSuccess()
}

suspend fun insertChatSession(
    httpClient: HttpClient,
    title: String? = null,
    metadata: JsonObject? = null
): String? {
    val endpoint = "${SupabaseConfig.url}/rest/v1/chat_sessions"
    val apiKey = SupabaseConfig.apiKey

    val payload = buildJsonObject {
        if (title != null) put("title", JsonPrimitive(title))
        if (metadata != null) put("metadata", metadata)
    }

    val response = httpClient.post(endpoint) {
        header("apikey", apiKey)
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        header("Prefer", "return=representation") // <- чтобы вернуть вставленную строку
        setBody(payload.toString())
    }

    if (!response.status.isSuccess()) return null

    val responseBody = response.bodyAsText()
    val json = Json.parseToJsonElement(responseBody).jsonArray
    return json.firstOrNull()?.jsonObject?.get("id")?.jsonPrimitive?.content
}

suspend fun loadChatMessages(
    httpClient: HttpClient,
    sessionId: String
): List<ChatMessageIn> {
    val endpoint = "${SupabaseConfig.url}/rest/v1/chat_messages"
    val apiKey = SupabaseConfig.apiKey

    val response = httpClient.get(endpoint) {
        header("apikey", apiKey)
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        header(HttpHeaders.Accept, ContentType.Application.Json)
        parameter("session_id", "eq.$sessionId")
        parameter("order", "created_at.asc")
    }

    if (!response.status.isSuccess()) return emptyList()

    val body = response.bodyAsText()
    val jsonArray = Json.parseToJsonElement(body).jsonArray

    return jsonArray.mapNotNull { element ->
        val obj = element.jsonObject
        val role = obj["role"]?.jsonPrimitive?.contentOrNull
        val content = obj["content"]?.jsonPrimitive?.contentOrNull

        if (role != null && content != null) {
            ChatMessageIn(role = role, content = content)
        } else null
    }
}

