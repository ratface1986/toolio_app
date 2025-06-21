import ai.toolio.app.models.ChatGptRequest
import ai.toolio.app.models.ChatGptResponse
import ai.toolio.app.models.ChatMessageOut
import ai.toolio.app.models.ContentPart
import ai.toolio.app.models.ImagePayload
import ai.toolio.app.models.OpenAIChatResponse
import ai.toolio.app.models.OpenAIRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.http.content.TextContent
import io.ktor.util.encodeBase64
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

suspend fun callOpenAI(httpClient: HttpClient, request: ChatGptRequest): ChatGptResponse {
    val log = LoggerFactory.getLogger("OpenAIDebug")
    val apiKey = System.getenv("OPENAI_API_KEY") ?: error("Missing OPENAI_API_KEY")

    val base64 = request.imageBytes?.encodeBase64()
        ?: error("Missing imageBytes for Vision request")

    val imageDataUrl = "data:image/jpeg;base64,$base64"

    val openAIRequest = OpenAIRequest(
        model = "gpt-4o",
        temperature = 0.7,
        maxTokens = 1000,
        messages = listOf(
            ChatMessageOut(
                role = "user",
                content = listOf(
                    ContentPart.Text(request.prompt),
                    ContentPart.ImageUrl(ImagePayload(url = imageDataUrl))
                )
            )
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

        ChatGptResponse(
            content = reply,
            model = parsed.model,
            tokensUsed = parsed.usage?.totalTokens
        )
    } catch (e: Exception) {
        log.error("💥 Ошибка парсинга OpenAI-ответа: ${e.message}")
        ChatGptResponse(content = "Ошибка: ${e.message.orEmpty()}")
    }
}


