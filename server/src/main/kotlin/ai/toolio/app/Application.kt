package ai.toolio.app

import ai.toolio.app.ToolioConfig.jdbcUrl
import ai.toolio.app.api.handleOpenAIImagePrompt
import ai.toolio.app.db.findUserByNickname
import ai.toolio.app.db.getUserInventory
import ai.toolio.app.db.insertChatMessage
import ai.toolio.app.db.insertUser
import ai.toolio.app.db.updateTool
import ai.toolio.app.misc.Roles
import ai.toolio.app.models.ChatGptRequest
import ai.toolio.app.models.ToolData
import ai.toolio.app.models.ToolRecognitionResult
import ai.toolio.app.models.UserProfile
import ai.toolio.app.services.deleteImageFromLocalStorage
import ai.toolio.app.services.saveImageToLocalStorage
import callOpenAI
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticFiles
import io.ktor.server.netty.Netty
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.util.AttributeKey
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import java.util.UUID

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)

}

val HttpClientKey = AttributeKey<HttpClient>("HttpClient")
val ApplicationCall.httpClient: HttpClient
    get() = this.application.attributes[HttpClientKey]
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    val httpClient = HttpClient {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    Database.connect(
        url = jdbcUrl,
        driver = "org.postgresql.Driver",
        user = System.getenv("POSTGRES_USER") ?: error("No POSTGRES_USER"),
        password = System.getenv("POSTGRES_PASSWORD") ?: error("No POSTGRES_PASSWORD")
    )


    val logger = LoggerFactory.getLogger("MYDATA:")


    attributes.put(HttpClientKey, httpClient)
    /*val historyLine = buildJsonObject {
                    put("timestamp", JsonPrimitive(Clock.System.now().toString()))
                    put("prompt", JsonPrimitive(promptText))
                    put("response", JsonPrimitive(response.content))
                }

                File("chat-history.jsonl").appendText( "$historyLine\n")*/
    routing {
        get("/") {
            println("==> GET /")
            call.respondText("OK")
        }

        post("/login") {
            val request = call.receive<Map<String, String>>()
            val nickname = request["nickname"]

            if (nickname.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing nickname")
                return@post
            }

            val user = findUserByNickname(nickname)
                ?: insertUser(nickname)
                ?: run {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create user")
                    return@post
                }

            val profile = UserProfile(
                userId = user.userId,
                nickname = nickname,
                inventory = getUserInventory(user.userId)
                    .mapValues { (_, value) ->
                        val obj = value.jsonObject
                        ToolData(
                            name = obj["name"]?.jsonPrimitive?.content.orEmpty(),
                            description = obj["description"]?.jsonPrimitive?.content.orEmpty(),
                            imageUrl = obj["imageUrl"]?.jsonPrimitive?.content.orEmpty(),
                            confirmed = obj["confirmed"]?.jsonPrimitive?.boolean ?: false
                        )
                    }
            )

            call.respond(profile)
        }


        post("/openai") {
            val request = call.receive<ChatGptRequest>()

            insertChatMessage(
                request.sessionId,
                role = "user",
                content = request.prompt
            )

            val response = callOpenAI(
                httpClient = call.httpClient,
                request = ChatGptRequest(prompt = request.prompt, sessionId = request.sessionId)
            )

            insertChatMessage(
                sessionId = request.sessionId,
                role = Roles.ASSISTANT.name.lowercase(),
                content = response.content
            )

            call.respond(
                status = HttpStatusCode.OK,
                message = response
            )
        }

        post("/openaiImagePrompt") {
            handleOpenAIImagePrompt()
        }

        post("/upload") {
            val multipart = call.receiveMultipart()
            var uploadedUrl: String? = null

            multipart.forEachPart { part ->
                if (part is PartData.FileItem && part.contentType?.contentType == "image") {
                    val fileName = "${UUID.randomUUID()}.jpg"
                    val imageBytes = part.provider().readRemaining().readByteArray()

                    uploadedUrl = saveImageToLocalStorage(
                        imageBytes = imageBytes,
                        fileName = fileName
                    )
                }
                part.dispose()
            }

            if (uploadedUrl == null) {
                call.respond(HttpStatusCode.BadRequest, "No image file found.")
                return@post
            }

            call.respond(HttpStatusCode.OK, mapOf("url" to uploadedUrl))
        }

        post("/verify-tool") {
            logger.info("==> POST /verify-tool")
            val multipart = call.receiveMultipart()
            var promptText: String? = null
            var imageBytes: ByteArray? = null
            var fileName: String? = null
            var userId: String? = null
            var sessionId: String? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> when (part.name) {
                        "prompt" -> promptText = part.value
                        "user_id" -> userId = part.value
                        "sessionId" -> sessionId = part.value
                    }
                    is PartData.FileItem -> if (part.contentType?.contentType == "image") {
                        fileName = "${UUID.randomUUID()}.jpg"
                        imageBytes = part.provider().readRemaining().readByteArray()
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (userId == null || sessionId.isNullOrBlank()) {
                logger.warn("Missing user_id")
                call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                return@post
            }

            logger.debug("Received multipart data")

            if (promptText == null || imageBytes == null || fileName == null || userId == null) {
                logger.warn("Missing prompt or image")
                call.respond(HttpStatusCode.BadRequest, "Missing data")
                return@post
            }

            val imageUrl = saveImageToLocalStorage(imageBytes, fileName)
            logger.debug("MYDATA Saved image to $imageUrl")

            val fullPrompt = """
                You are a technical assistant. The user claims that the object in the photo is: "$promptText".
                Analyze the image and determine the following:
                - Does the object match the expected tool?
                - Identify the type, name, and provide a brief description of the tool.
            
                Respond strictly in JSON format:
                {
                  "matchesExpected": true or false,
                  "type": "...",
                  "name": "...",
                  "description": "..."
                }
            """.trimIndent()


            val gptResponse = try {
                callOpenAI(
                    httpClient = call.httpClient,
                    request = ChatGptRequest(
                        prompt = fullPrompt,
                        sessionId = sessionId,
                        imageBytes = imageBytes
                    )
                )
            } catch (e: Exception) {
                logger.error("❌ Failed to call OpenAI: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Failed to call GPT: ${e.message}")
                return@post
            }

            logger.error(gptResponse.content)
            val result = try {
                val cleanJson = gptResponse.content
                    .trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                Json.decodeFromString<ToolRecognitionResult>(cleanJson).copy(
                    imageUrl = imageUrl
                )
            } catch (e: Exception) {
                logger.error(e.message)
                call.respond(HttpStatusCode.InternalServerError, "Failed to parse GPT response: ${e.message}")
                return@post
            }

            if (!result.matchesExpected) {
                deleteImageFromLocalStorage(fileName)
                call.respond(result)
                return@post
            }

            call.respond(result)
        }

        post("/confirm-tool") {
            val request = call.receive<Map<String, String>>()
            val userId = request["user_id"]
            val toolType = request["tool_type"]
            val name = request["name"] ?: ""
            val description = request["description"] ?: ""
            val imageUrl = request["image_url"] ?: ""
            val confirmed = request["confirmed"]?.toBooleanStrictOrNull() ?: false

            if (userId == null || toolType.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing user_id or tool_type")
                return@post
            }

            val updateSuccess = updateTool(
                userId = userId,
                type = toolType,
                name = name,
                description = description.ifBlank { "No description provided" },
                imageUrl = imageUrl,
                confirmed = confirmed
            )

            if (updateSuccess) {
                call.respond(HttpStatusCode.OK, "Tool confirmed")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to confirm tool")
            }
        }

        staticFiles("/uploads", File(ToolioConfig.storagePath))
    }


}

