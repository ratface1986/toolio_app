package ai.toolio.app.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class OpenAIRequest(
    @SerialName("model") val model: String,
    @SerialName("messages") val messages: List<ChatMessage>,
    @SerialName("temperature") val temperature: Double,
    @SerialName("max_tokens") val maxTokens: Int = 1000
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
    val refusal: JsonElement? = null,
    val annotations: List<JsonElement>? = null
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
data class Choice(
    val index: Int,
    val message: ChatMessage,
    val logprobs: JsonElement? = null,
    @SerialName("finish_reason") val finishReason: String? = null
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
    val prompt: String
)

@Serializable
data class ChatGptResponse(
    val content: String,
    val model: String? = null,
    val tokensUsed: Int? = null
)
