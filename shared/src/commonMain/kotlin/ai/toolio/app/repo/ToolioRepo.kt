package ai.toolio.app.repo

import ai.toolio.app.models.ChatGptResponse
import ai.toolio.app.models.ChatMessage
import ai.toolio.app.models.OpenAIChatResponse
import ai.toolio.app.models.OpenAIRequest
import ai.toolio.app.models.ToolRecognitionResult
import ai.toolio.app.models.UserProfile
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlin.concurrent.Volatile

class ToolioRepo(private val baseUrl: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.BODY
        }
    }

    suspend fun login(nickname: String): UserProfile {
        val response = client.post("$baseUrl/login") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("nickname" to nickname))
        }
        return response.body()
    }

    suspend fun verifyTool(
        userId: String,
        prompt: String,
        imageBytes: ByteArray
    ): ToolRecognitionResult {
        val parts = formData {
            append("user_id", userId)
            append("prompt", prompt)
            append("image", imageBytes, Headers.build {
                append(HttpHeaders.ContentType, "image/jpeg")
                append(HttpHeaders.ContentDisposition, "filename=\"tool.jpg\"")
            })
        }

        val response = client.submitFormWithBinaryData(
            url = "$baseUrl/verify-tool",
            formData = parts
        )

        return response.body()
    }

    suspend fun confirmTool(toolId: String): Boolean {
        val response = client.post("$baseUrl/confirm-tool") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("tool_id" to toolId))
        }
        return response.status.isSuccess()
    }

    suspend fun chatGpt(prompt: String): Result<ChatGptResponse> {
        return try {
            val response = client.post("$baseUrl/openai") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("prompt" to prompt))
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val data = response.body<ChatGptResponse>()
                    Result.success(data)
                }
                else -> Result.failure(Exception("Ktor returned ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun chatGptImage(prompt: String, base64: String): Result<ChatGptResponse> {
        return try {
            val response = client.post("$baseUrl/openai") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("prompt" to prompt, "image" to base64))
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val data = response.body<ChatGptResponse>()
                    Result.success(data)
                }
                else -> Result.failure(Exception("Ktor returned ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun close() {
        client.close()
    }

    companion object {
        @Volatile
        private var instance: ToolioRepo? = null

        //private const val BASE_URL = "https://toolio-api.vercel.app/api"
        private const val BASE_URL = "https://toolioapp-production.up.railway.app"

        fun getInstance(): ToolioRepo {
            return instance ?: createInstance()
        }

        private fun createInstance(): ToolioRepo {
            return ToolioRepo(BASE_URL).also { newInstance ->
                instance = newInstance
            }
        }
    }
}