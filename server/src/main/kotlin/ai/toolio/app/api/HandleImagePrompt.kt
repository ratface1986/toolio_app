package ai.toolio.app.api

import ai.toolio.app.db.insertChatMessage
import ai.toolio.app.httpClient
import ai.toolio.app.misc.Roles
import ai.toolio.app.models.ChatGptRequest
import ai.toolio.app.services.saveImageToLocalStorage
import callOpenAI
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import java.util.*

suspend fun RoutingContext.handleOpenAIImagePrompt() {
    val imagePromptMultipart = call.receiveMultipart()
    var userId: String? = null
    var promptText: String? = null
    var imageBytes: ByteArray? = null
    var imageUrl: String? = null
    var sessionId: String? = null

    imagePromptMultipart.forEachPart { part ->
        when (part) {
            is PartData.FormItem -> {
                when (part.name) {
                    "userId" -> userId = part.value
                    "prompt" -> promptText = part.value
                    "sessionId" -> sessionId = part.value
                }
            }
            is PartData.FileItem -> {
                if (part.contentType?.contentType == "image") {
                    val fileName = "${UUID.randomUUID()}.jpg"
                    imageBytes = part.provider().readRemaining().readByteArray()
                    imageUrl = saveImageToLocalStorage(imageBytes, fileName)
                }
            }
            else -> {}
        }
        part.dispose()
    }

    if (imageBytes == null || imageUrl == null || sessionId.isNullOrBlank() || userId.isNullOrBlank()) {
        call.respond(HttpStatusCode.BadRequest, "Missing image or sessionId")
        return
    }

    insertChatMessage(
        userId = userId,
        sessionId = sessionId,
        role = Roles.USER.name.lowercase(),
        content = promptText.orEmpty(),
        imageUrl = imageUrl
    )

    val response = callOpenAI(
        httpClient = call.httpClient,
        request = ChatGptRequest(
            userId = userId,
            prompt = "check photo and let me know what do you think",
            sessionId = sessionId,
            imageBytes = imageBytes
        )
    )

    insertChatMessage(
        userId = userId,
        sessionId = sessionId,
        role = Roles.ASSISTANT.name.lowercase(),
        content = response.content
    )

    val downloadImageURL = System.getenv("DOMAIN_URL") + imageUrl

    call.respond(
        status = HttpStatusCode.OK,
        message = response.copy(imageUrl = downloadImageURL)
    )
}
