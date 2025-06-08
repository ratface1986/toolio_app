package ai.toolio.app.repo

import ai.toolio.app.models.ChatGptResponse
import ai.toolio.app.models.ChatMessage
import ai.toolio.app.models.OpenAIChatResponse
import ai.toolio.app.models.OpenAIRequest
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.call.*
import io.ktor.client.request.*
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

    /*suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception("Login failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAccountSummary(token: String): Result<AccountSummary> {
        return try {
            val response = client.get("$baseUrl/account/summary") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                else -> Result.failure(Exception("Failed to get account summary: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }*/

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

    fun close() {
        client.close()
    }

    companion object {
        @Volatile
        private var instance: ToolioRepo? = null

        //private const val BASE_URL = "https://toolio-api.vercel.app/api"
        private const val BASE_URL = "http://10.0.2.2:8080"

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