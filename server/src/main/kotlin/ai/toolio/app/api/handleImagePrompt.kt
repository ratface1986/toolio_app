package ai.toolio.app.api

import ai.toolio.app.db.insertChatMessage
import ai.toolio.app.httpClient
import ai.toolio.app.misc.Roles
import ai.toolio.app.models.ChatGptRequest
import ai.toolio.app.services.saveImageToLocalStorage
import callOpenAI
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import java.util.UUID

suspend fun RoutingContext.handleOpenAIImagePrompt() {
    val imagePromptMultipart = call.receiveMultipart()
    var promptText: String? = null
    var imageBytes: ByteArray? = null
    var imageUrl: String? = null
    var sessionId: String? = null

    imagePromptMultipart.forEachPart { part ->
        when (part) {
            is PartData.FormItem -> {
                when (part.name) {
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

    if (imageBytes == null || imageUrl == null || sessionId.isNullOrBlank()) {
        call.respond(HttpStatusCode.BadRequest, "Missing image or sessionId")
        return
    }

    insertChatMessage(
        sessionId = sessionId,
        role = Roles.USER.name.lowercase(),
        content = promptText.orEmpty(),
        imageUrl = imageUrl
    )

    val response = callOpenAI(
        httpClient = call.httpClient,
        request = ChatGptRequest(
            prompt = "check photo and let me know what do you think",
            sessionId = sessionId,
            imageBytes = imageBytes
        )
    )

    insertChatMessage(
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
