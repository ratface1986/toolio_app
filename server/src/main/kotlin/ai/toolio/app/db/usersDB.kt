package ai.toolio.app.db

import ai.toolio.app.SupabaseConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

suspend fun insertTool(
    httpClient: HttpClient,
    userId: String,
    type: String,
    name: String,
    description: String,
    imageUrl: String
): Boolean {
    val endpoint = "${SupabaseConfig.url}/rest/v1/tools"
    val apiKey = SupabaseConfig.apiKey

    val payload = buildJsonObject {
        put("user_id", JsonPrimitive(userId))
        put("type", JsonPrimitive(type))
        put("name", JsonPrimitive(name))
        put("description", JsonPrimitive(description))
        put("image_url", JsonPrimitive(imageUrl))
        put("confirmed", JsonPrimitive(false))
    }

    val response = httpClient.post(endpoint) {
        header("apikey", apiKey)
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        setBody(payload.toString())
    }

    return response.status.isSuccess()
}

suspend fun confirmTool(
    httpClient: HttpClient,
    toolId: String
): Boolean {
    val endpoint = "${SupabaseConfig.url}/rest/v1/tools?id=eq.$toolId"
    val apiKey = SupabaseConfig.apiKey

    val payload = buildJsonObject {
        put("confirmed", JsonPrimitive(true))
    }

    val response = httpClient.patch(endpoint) {
        header("apikey", apiKey)
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        header("Prefer", "return=representation")
        setBody(payload.toString())
    }

    return response.status.isSuccess()
}

suspend fun getUserInventory(
    httpClient: HttpClient,
    userId: String
): JsonObject {
    val endpoint = "${SupabaseConfig.url}/rest/v1/tools"
    val apiKey = SupabaseConfig.apiKey

    val response = httpClient.get(endpoint) {
        header("apikey", apiKey)
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        header(HttpHeaders.Accept, ContentType.Application.Json)
        parameter("user_id", "eq.$userId")
        parameter("order", "created_at.asc")
    }

    if (!response.status.isSuccess()) return buildJsonObject {}

    val tools = Json.parseToJsonElement(response.bodyAsText()).jsonArray

    return buildJsonObject {
        tools.forEach { element ->
            val obj = element.jsonObject
            val type = obj["type"]?.jsonPrimitive?.content ?: return@forEach

            put(type, buildJsonObject {
                put("name", obj["name"]?.jsonPrimitive?.content ?: "")
                put("description", obj["description"]?.jsonPrimitive?.content ?: "")
                put("imageUrl", obj["image_url"]?.jsonPrimitive?.content ?: "")
                put("confirmed", obj["confirmed"]?.jsonPrimitive?.boolean ?: false)
            })
        }
    }
}

suspend fun findUserByNickname(
    httpClient: HttpClient,
    nickname: String
): String? {
    val endpoint = "${SupabaseConfig.url}/rest/v1/users"
    val apiKey = SupabaseConfig.apiKey

    val response = httpClient.get(endpoint) {
        header("apikey", apiKey)
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        header(HttpHeaders.Accept, ContentType.Application.Json)
        parameter("nickname", "eq.$nickname")
        parameter("limit", "1")
    }

    if (!response.status.isSuccess()) return null

    val body = response.bodyAsText()
    val json = Json.parseToJsonElement(body).jsonArray
    if (json.isEmpty()) return null

    return json.first().jsonObject["id"]?.jsonPrimitive?.content
}

suspend fun insertUser(
    httpClient: HttpClient,
    nickname: String
): String? {
    val endpoint = "${SupabaseConfig.url}/rest/v1/users"
    val apiKey = SupabaseConfig.apiKey

    val payload = buildJsonObject {
        put("nickname", JsonPrimitive(nickname))
    }

    val response = httpClient.post(endpoint) {
        header("apikey", apiKey)
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        header("Prefer", "return=representation")
        setBody(payload.toString())
    }

    if (!response.status.isSuccess()) return null

    val body = response.bodyAsText()
    val json = Json.parseToJsonElement(body).jsonArray
    return json.firstOrNull()?.jsonObject?.get("id")?.jsonPrimitive?.content
}