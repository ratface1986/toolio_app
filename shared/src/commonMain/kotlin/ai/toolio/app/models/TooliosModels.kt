package ai.toolio.app.models

import ai.toolio.app.misc.Roles
import ai.toolio.app.misc.SessionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement

@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<ChatMessageOut>,
    val temperature: Double,
    @SerialName("max_tokens") val maxTokens: Int
)

@Serializable
data class WhisperTranscriptionResponse(
    val text: String
)

@Serializable
data class Choice(
    val index: Int,
    val message: ChatMessageIn,
    @SerialName("finish_reason") val finishReason: String? = null
)

@Serializable
data class ChatMessageOut(
    val role: String,
    val content: List<ContentPart>
)

@Serializable
data class ChatMessageIn(
    val role: String,
    val content: String
)


@Serializable
data class ChatMessage(
    val role: String,
    val content: List<ContentPart>
)

@Serializable
sealed class ContentPart {
    @Serializable
    @SerialName("text")
    data class Text(val text: String) : ContentPart()

    @Serializable
    @SerialName("image_url")
    data class ImageUrl(val image_url: ImagePayload) : ContentPart()
}

@Serializable
data class ImagePayload(
    val url: String,
    val detail: String = "auto"
)

@Serializable
data class OpenAIChatResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage? = null
)

@Serializable
data class Usage(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int,
    @SerialName("total_tokens") val totalTokens: Int,
    @SerialName("prompt_tokens_details") val promptTokensDetails: JsonElement? = null,
    @SerialName("completion_tokens_details") val completionTokensDetails: JsonElement? = null
)

@Serializable
data class ChatGptRequest(
    val prompt: String,
    val userId: String,
    val sessionId: String,
    val contentByteArray: ByteArray? = null,
    val language: String? = "en",
)

@Serializable
data class ToolRecognitionResult(
    val matchesExpected: Boolean,
    val type: String? = null,
    val name: String? = null,
    val description: String? = null,
    @Transient var imageUrl: String? = null
)

@Serializable
data class ToolioChatMessage(
    val sessionId: String,
    val role: Roles,
    val content: String,
    val imageUrl: String? = null,
    val tokensUsed: Int? = null,
    val timestamp: String = ""
)

@Serializable
data class SaveSessionRequest(
    val userId: String,
    val session: RepairTaskSession
)

@Serializable
data class RepairTaskSession(
    val sessionId: String = "",
    val title: String = "",
    val category: TaskCategory = Tasks.categories.first(),
    val task: Task = category.tasks.first(),
    val answers: Map<String, String> = emptyMap(),
    val createdAt: String = "",
    val initialPrompt: String = "",
    val messages: List<ToolioChatMessage> = emptyList(),
    val sessionType: SessionType = SessionType.TEXT,
    @Transient val isSaved: Boolean = false
)


