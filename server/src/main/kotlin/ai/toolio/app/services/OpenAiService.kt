import ai.toolio.app.models.ChatGptRequest
import ai.toolio.app.models.ChatGptResponse
import ai.toolio.app.models.ChatMessage
import ai.toolio.app.models.OpenAIChatResponse
import ai.toolio.app.models.OpenAIRequest
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.http.content.TextContent
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

suspend fun callOpenAI(request: ChatGptRequest): ChatGptResponse {
    val log = LoggerFactory.getLogger("OpenAIDebug")
    val apiKey = dotenv()["OPENAI_API_KEY"] ?: error("Missing OPENAI_API_KEY")

    val openAIRequest = OpenAIRequest(
        model = "gpt-3.5-turbo",
        temperature = 0.7,
        maxTokens = 1000,
        messages = listOf(
            ChatMessage("system", "Ты помощник по домашнему ремонту."),
            ChatMessage("user", request.prompt)
        )
    )

    val jsonPayload = Json.encodeToString(OpenAIRequest.serializer(), openAIRequest)
    log.info("📤 Запрос в OpenAI:\n$jsonPayload")

    val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        setBody(TextContent(jsonPayload, ContentType.Application.Json))
    }

    return try {
        val parsed = Json { ignoreUnknownKeys = true }.decodeFromString<OpenAIChatResponse>(response.bodyAsText())
        val reply = parsed.choices.firstOrNull()?.message?.content ?: "OpenAI ничего не вернул"

        return ChatGptResponse(
            content = reply,
            model = parsed.model,
            tokensUsed = parsed.usage?.totalTokens
        )
    } catch (e: Exception) {
        log.error("💥 Ошибка парсинга OpenAI-ответа: ${e.message}")
        ChatGptResponse(content = "Ошибка: ${e.message.orEmpty()}")
    }
}

