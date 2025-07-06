package ai.toolio.app.repo

import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.di.AppSessions
import ai.toolio.app.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
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

    suspend fun loginWithGoogle(
        userId: String,
        nickname: String,
        email: String,
    ): UserProfile {
        val response = client.post("$baseUrl/loginWithGoogle") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "userId" to userId,
                "email" to email,
                "nickname" to nickname,
            ))
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

    suspend fun chatGpt(prompt: String, imageBytes : ByteArray): ToolioChatMessage {
        val parts = formData {
            append("userId", AppEnvironment.userProfile.userId)
            append("prompt", prompt)
            append("sessionId", AppEnvironment.getSessionId())
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

    suspend fun transcribeSpeech(audioBytes: ByteArray): ToolioChatMessage {
        val parts = formData {
            append("file", audioBytes, Headers.build {
                append(HttpHeaders.ContentType, "audio/wav")
                append(HttpHeaders.ContentDisposition, "filename=\"audio.wav\"")
            })
            append("userId", AppEnvironment.userProfile.userId)
            append("sessionId", AppEnvironment.getSessionId())
        }

        val response = client.submitFormWithBinaryData(
            url = "$baseUrl/speechToText",
            formData = parts
        )

        return response.body()
    }

    suspend fun chatGpt(prompt: String): ToolioChatMessage {
        val request = ChatGptRequest(
            userId = AppEnvironment.userProfile.userId,
            prompt = prompt,
            sessionId = AppEnvironment.getSessionId()
        )

        val response = client.post("$baseUrl/openai") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        return response.body()
    }

    suspend fun saveNewSession(session: RepairTaskSession): Boolean {
        return try {
            val request = SaveSessionRequest(
                userId = AppEnvironment.userProfile.userId,
                session = session
            )

            val response = client.post("$baseUrl/save-session") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun saveUserSettings(profile: UserProfile): Boolean {
        return try {
            val response = client.post("$baseUrl/update-settings") {
                contentType(ContentType.Application.Json)
                setBody(profile)
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun fetchImageByUrl(imageUrl: String): ByteArray {
        return client.get(imageUrl).body()
    }

    fun close() {
        client.close()
    }

    fun getHttpClient(): HttpClient {
        return client
    }

    companion object {
        @Volatile
        private var instance: ToolioRepo? = null

        //private const val BASE_URL = "https://toolio-api.vercel.app/api"
        private const val BASE_URL = "https://toolioapp.com"

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