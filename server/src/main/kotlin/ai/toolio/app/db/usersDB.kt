package ai.toolio.app.db

import ai.toolio.app.SupabaseConfig
import ai.toolio.app.models.Tool
import ai.toolio.app.models.ToolData
import ai.toolio.app.models.UserProfile
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
import io.ktor.http.contentType
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
import java.util.UUID

suspend fun insertTool(
    httpClient: HttpClient,
    userId: String,
    type: String,
    name: String,
    description: String,
    imageUrl: String
): Boolean {
    val endpoint = "${SupabaseConfig.publicBaseUrl}/tools"
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
    userId: String,
    toolType: String
): Boolean {
    val endpoint = "${SupabaseConfig.url}/tools?user_id=eq.$userId&type=eq.$toolType"
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
    val endpoint = "${SupabaseConfig.url}/tools"
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
                put("name", obj["name"]?.jsonPrimitive?.content.orEmpty())
                put("description", obj["description"]?.jsonPrimitive?.content.orEmpty())
                put("imageUrl", obj["image_url"]?.jsonPrimitive?.content.orEmpty())
                put("confirmed", obj["confirmed"]?.jsonPrimitive?.boolean ?: false)
            })
        }
    }
}

suspend fun findUserByNickname(
    httpClient: HttpClient,
    nickname: String
): UserProfile? {
    val apiKey = SupabaseConfig.apiKey
    val baseUrl = SupabaseConfig.url

    // 1. Получаем пользователя
    val userResp = httpClient.get("$baseUrl/users") {
        header("apikey", apiKey)
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        header(HttpHeaders.Accept, ContentType.Application.Json)
        parameter("nickname", "eq.$nickname")
        parameter("limit", "1")
    }

    if (!userResp.status.isSuccess()) return null
    val userJson = Json.parseToJsonElement(userResp.bodyAsText()).jsonArray.firstOrNull()?.jsonObject ?: return null
    val userId = userJson["id"]?.jsonPrimitive?.content ?: return null

    // 2. Получаем inventory пользователя
    val toolsResp = httpClient.get("$baseUrl/tools") {
        header("apikey", apiKey)
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        header(HttpHeaders.Accept, ContentType.Application.Json)
        parameter("user_id", "eq.$userId")
    }

    if (!toolsResp.status.isSuccess()) return null
    val toolsJson = Json.parseToJsonElement(toolsResp.bodyAsText()).jsonArray

    val inventory = toolsJson.associate { toolJsonElement ->
        val obj = toolJsonElement.jsonObject
        val type = obj["type"]?.jsonPrimitive?.content.orEmpty()
        val name = obj["name"]?.jsonPrimitive?.content.orEmpty()
        val description = obj["description"]?.jsonPrimitive?.content.orEmpty()
        val imageUrl = obj["image_url"]?.jsonPrimitive?.content.orEmpty()
        val confirmed = obj["confirmed"]?.jsonPrimitive?.boolean ?: false

        type to ToolData(name, description, imageUrl, confirmed)
    }

    return UserProfile(
        userId = userId,
        nickname = nickname,
        inventory = inventory
    )
}

suspend fun insertUser(
    httpClient: HttpClient,
    nickname: String
): UserProfile? {
    val baseUrl = SupabaseConfig.url
    val apiKey = SupabaseConfig.apiKey
    val authHeader = "Bearer $apiKey"
    val userId = UUID.randomUUID().toString()

    val userPayload = buildJsonObject {
        put("id", userId)
        put("nickname", nickname)
    }

    val response = httpClient.post("$baseUrl/users") {
        header("apikey", apiKey)
        header("Authorization", authHeader)
        contentType(ContentType.Application.Json)
        setBody(userPayload)
    }

    if (!response.status.isSuccess()) {
        error("Failed to create user: ${response.status}")
    }

    println("✅ User created: $userId with ${Tool.entries.size} tools")
    val toolsPayload = Tool.entries.map { tool ->
        buildJsonObject {
            put("user_id", userId)
            put("type", tool.name)
            put("confirmed", false)
            put("image_url", "")  // пока пусто
        }
    }

    val toolsResponse = httpClient.post("$baseUrl/tools") {
        header("apikey", apiKey)
        header("Authorization", authHeader)
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToString(toolsPayload))
    }

    if (!toolsResponse.status.isSuccess()) {
        error("Failed to create tools: ${toolsResponse.status}")
    }

    return UserProfile(
        userId = userId,
        nickname = nickname,
        inventory = Tool.entries.associate { it.name to ToolData(it.displayName, "", "", false) }
    )
}