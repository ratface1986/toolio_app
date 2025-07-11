import ai.toolio.app.db.loadChatMessagesForUser
import ai.toolio.app.ext.toUUID
import ai.toolio.app.misc.Roles
import ai.toolio.app.models.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

suspend fun callOpenAI(httpClient: HttpClient, request: ChatGptRequest): ToolioChatMessage {
    val log = LoggerFactory.getLogger("OpenAIDebug")
    val apiKey = System.getenv("OPENAI_API_KEY") ?: error("Missing OPENAI_API_KEY")

    val messages = mutableListOf<ChatMessageOut>()

    // Загружаем историю сообщений
    messages += loadChatMessagesForUser(request.sessionId.toUUID()).map { msg ->
        ChatMessageOut(
            role = msg.role.role,
            content = listOf(ContentPart.Text(msg.content))
        )
    }

    // Добавляем user-сообщение, если есть текст или изображение
    if (request.prompt.isNotBlank() || request.contentByteArray != null) {
        val parts = mutableListOf<ContentPart>()

        if (request.prompt.isNotBlank()) {
            parts += ContentPart.Text(request.prompt)
        }

        val byteArray = request.contentByteArray
        if (byteArray != null) {
            val base64 = byteArray.encodeBase64()
            val imageDataUrl = "data:image/jpeg;base64,$base64"
            parts += ContentPart.ImageUrl(ImagePayload(url = imageDataUrl))
        }
        
        messages += ChatMessageOut(
            role = "user",
            content = parts
        )
    }

    log.info("MYDATA GPT-messages:\n${messages.joinToString("\n")}")

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

    val rawBody = response.bodyAsText()

    // Проверка статуса ответа
    if (!response.status.isSuccess()) {
        log.error("❌ OpenAI HTTP ${response.status.value}:\n$rawBody")
        return ToolioChatMessage(
            sessionId = request.sessionId,
            content = "OpenAI error: ${response.status.value}",
            role = Roles.SYSTEM
        )
    }

    return try {
        val parsed = Json { ignoreUnknownKeys = true }
            .decodeFromString<OpenAIChatResponse>(rawBody)

        val reply = parsed.choices.firstOrNull()?.message?.content ?: "OpenAI hasn't responded."

        ToolioChatMessage(
            sessionId = request.sessionId,
            content = reply,
            role = Roles.ASSISTANT,
            tokensUsed = parsed.usage?.totalTokens
        )
    } catch (e: Exception) {
        log.error("💥 Parse error OpenAI-ответа: ${e.message}\nRAW:\n$rawBody")
        ToolioChatMessage(
            sessionId = request.sessionId,
            content = "Ошибка: ${e.message.orEmpty()}",
            role = Roles.SYSTEM
        )
    }
}

suspend fun callWhisperTranscription(httpClient: HttpClient, request: ChatGptRequest): ToolioChatMessage {
    val log = LoggerFactory.getLogger("OpenAIDebug")
    val apiKey = System.getenv("OPENAI_API_KEY") ?: error("Missing OPENAI_API_KEY")
    val boundary = "ToolioBoundary123456"

    val formData = MultiPartFormDataContent(
        formData {
            append("model", "whisper-1")
            append(
                "file",
                request.contentByteArray!!,
                Headers.build {
                    append(HttpHeaders.ContentType, "audio/m4a")
                    append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"recording.m4a\"")
                }
            )
        },
        boundary = boundary
    )

    val response = httpClient.post("https://api.openai.com/v1/audio/transcriptions") {
        header(HttpHeaders.Authorization, "Bearer $apiKey")
        setBody(formData)
    }

    val result = response.bodyAsText()

    return try {
        val transcript = Json.decodeFromString<WhisperTranscriptionResponse>(result).text
        ToolioChatMessage(
            sessionId = "",
            content = transcript,
            role = Roles.USER
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
