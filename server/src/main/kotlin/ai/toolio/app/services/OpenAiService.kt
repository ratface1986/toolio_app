import ai.toolio.app.db.loadChatMessagesForUser
import ai.toolio.app.ext.toUUID
import ai.toolio.app.misc.Roles
import ai.toolio.app.models.*
import io.ktor.client.*
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

suspend fun callOpenAI(httpClient: HttpClient, request: ChatGptRequest): ToolioChatMessage {
    val log = LoggerFactory.getLogger("OpenAIDebug")
    val apiKey = System.getenv("OPENAI_API_KEY") ?: error("Missing OPENAI_API_KEY")

    val messages = mutableListOf<ChatMessageOut>()

    // Загружаем историю сообщений
    val uuid = runCatching { request.sessionId.toUUID() }.getOrNull()
    if (uuid != null) {
        messages += loadChatMessagesForUser(request.sessionId.toUUID()).map { msg ->
            ChatMessageOut(
                role = msg.role.role,
                content = listOf(ContentPart.Text(msg.content))
            )
        }
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
            role = Roles.USER.role.lowercase(),
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

suspend fun callWhisperTranscription(
    httpClient: HttpClient,
    request: ChatGptRequest
): ToolioChatMessage {
    val log = LoggerFactory.getLogger("OpenAIDebug")
    val apiKey = System.getenv("OPENAI_API_KEY") ?: error("Missing OPENAI_API_KEY")
    val boundary = "ToolioBoundary123456"

    val formData = MultiPartFormDataContent(
        formData {
            append("model", "whisper-1")
            if (!request.language.isNullOrBlank()) {
                append("language", request.language!!)
            }
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

    // retry policy
    var lastError: Throwable? = null
    repeat(3) { attempt ->
        try {
            val response = httpClient.post("https://api.openai.com/v1/audio/transcriptions") {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                timeout {
                    requestTimeoutMillis = 30_000
                    connectTimeoutMillis = 10_000
                    socketTimeoutMillis = 30_000
                }
                setBody(formData)
            }

            if (!response.status.isSuccess()) {
                val errBody = response.bodyAsText()
                log.error("❌ Whisper HTTP ${response.status.value}: $errBody")
                return ToolioChatMessage(
                    sessionId = request.sessionId,
                    content = "Speech-to-text failed: HTTP ${response.status.value}",
                    role = Roles.ERROR
                )
            }

            val result = response.bodyAsText()
            val jsonDecoder = Json {
                ignoreUnknownKeys = true
            }
            val transcript = jsonDecoder.decodeFromString<WhisperTranscriptionResponse>(result).text
            return ToolioChatMessage(
                sessionId = request.sessionId,
                content = transcript,
                role = Roles.USER
            )
        } catch (e: Exception) {
            lastError = e
            log.warn("⚠️ Whisper attempt ${attempt + 1} failed: ${e.message}")
            delay((attempt + 1) * 1000L) // экспоненциальный бэкофф: 1s, 2s, 3s
        }
    }

    // все попытки упали
    return ToolioChatMessage(
        sessionId = request.sessionId,
        content = "Speech-to-text error: ${lastError?.message.orEmpty()}",
        role = Roles.SYSTEM
    )
}

