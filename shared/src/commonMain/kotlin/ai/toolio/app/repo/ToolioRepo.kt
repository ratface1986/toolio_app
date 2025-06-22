package ai.toolio.app.repo

import ai.toolio.app.models.ChatGptResponse
import ai.toolio.app.models.ChatImageRecognitionResult
import ai.toolio.app.models.ChatMessage
import ai.toolio.app.models.OpenAIChatResponse
import ai.toolio.app.models.OpenAIRequest
import ai.toolio.app.models.ToolData
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

    suspend fun confirmTool(userId: String, toolType: String, toolData: ToolData?): Boolean {
        val response = client.post("$baseUrl/confirm-tool") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "user_id" to userId,
                    "tool_type" to toolType,
                    "name" to toolData?.name.orEmpty(),
                    "description" to toolData?.description.orEmpty(),
                    "image_url" to toolData?.imageUrl.orEmpty(),
                    "confirmed" to toolData?.confirmed.toString()
                )
            )
        }
        return response.status.isSuccess()
    }

    suspend fun chatGpt(prompt: String, imageBytes : ByteArray): ChatImageRecognitionResult {
        val parts = formData {
            append("prompt", prompt)
            append("image", imageBytes, Headers.build {
                append(HttpHeaders.ContentType, "image/jpeg")
                append(HttpHeaders.ContentDisposition, "filename=\"msg.jpg\"")
            })
        }

        val response = client.submitFormWithBinaryData(
            url = "$baseUrl/openaiImagePrompt",
            formData = parts
        )

        return response.body()
    }

    suspend fun chatGpt(prompt: String): ChatGptResponse {
        val response = client.post("$baseUrl/openai") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("prompt" to prompt))
        }

        return response.body()
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