import ai.toolio.app.misc.Roles
import ai.toolio.app.models.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

suspend fun callOpenAI(httpClient: HttpClient, request: ChatGptRequest): ToolioChatMessage {
    val log = LoggerFactory.getLogger("OpenAIDebug")
    val apiKey = System.getenv("OPENAI_API_KEY") ?: error("Missing OPENAI_API_KEY")

    val messages = if (request.imageBytes != null) {
        val base64 = request.imageBytes!!.encodeBase64()
        val imageDataUrl = "data:image/jpeg;base64,$base64"

        listOf(
            ChatMessageOut(
                role = "user",
                content = listOf(
                    ContentPart.Text(request.prompt),
                    ContentPart.ImageUrl(ImagePayload(url = imageDataUrl))
                )
            )
        )
    } else {
        listOf(
            ChatMessageOut(
                role = "user",
                content = listOf(ContentPart.Text(request.prompt))
            )
        )
    }

    val openAIRequest = OpenAIRequest(
        model = "gpt-4o",
        temperature = 0.7,
        maxTokens = 1000,
        messages = messages
    )

    val jsonPayload = Json.encodeToString(OpenAIRequest.serializer(), openAIRequest)
    log.info("MYDATA REQUEST OpenAI:\n$jsonPayload")

    val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        setBody(TextContent(jsonPayload, ContentType.Application.Json))
    }

    return try {
        val parsed = Json { ignoreUnknownKeys = true }
            .decodeFromString<OpenAIChatResponse>(response.bodyAsText())

        val reply = parsed.choices.firstOrNull()?.message?.content ?: "OpenAI ничего не вернул"

        ToolioChatMessage(
            sessionId = "",
            content = reply,
            role = Roles.ASSISTANT,
            tokensUsed = parsed.usage?.totalTokens
        )
    } catch (e: Exception) {
        log.error("💥 Ошибка парсинга OpenAI-ответа: ${e.message}")
        ToolioChatMessage(
            sessionId = "",
            content = "Ошибка: ${e.message.orEmpty()}",
            role = Roles.SYSTEM
        )
    }
}


