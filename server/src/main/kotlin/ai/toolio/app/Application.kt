package ai.toolio.app

import ai.toolio.app.ToolioConfig.jdbcUrl
import ai.toolio.app.api.handleDefaultLogin
import ai.toolio.app.api.handleGoogleLogin
import ai.toolio.app.api.handleOpenAIImagePrompt
import ai.toolio.app.db.*
import ai.toolio.app.misc.Roles
import ai.toolio.app.models.*
import ai.toolio.app.services.deleteImageFromLocalStorage
import ai.toolio.app.services.saveImageToLocalStorage
import callOpenAI
import callWhisperTranscription
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)

}

val followUpQuestionModule = SerializersModule {
    polymorphic(FollowUpQuestion::class) {
        subclass(FollowUpQuestion.WallTypeQuestion::class, FollowUpQuestion.WallTypeQuestion.serializer())
        subclass(FollowUpQuestion.TvSizeQuestion::class, FollowUpQuestion.TvSizeQuestion.serializer())
        subclass(FollowUpQuestion.WeightClassQuestion::class, FollowUpQuestion.WeightClassQuestion.serializer())
        subclass(FollowUpQuestion.LightTypeQuestion::class, FollowUpQuestion.LightTypeQuestion.serializer())
        subclass(FollowUpQuestion.LockTypeQuestion::class, FollowUpQuestion.LockTypeQuestion.serializer())
        subclass(FollowUpQuestion.CeilingTypeQuestion::class, FollowUpQuestion.CeilingTypeQuestion.serializer())
        subclass(FollowUpQuestion.DrainTypeQuestion::class, FollowUpQuestion.DrainTypeQuestion.serializer())
        subclass(FollowUpQuestion.OutletTypeQuestion::class, FollowUpQuestion.OutletTypeQuestion.serializer())
        subclass(FollowUpQuestion.ShelfTypeQuestion::class, FollowUpQuestion.ShelfTypeQuestion.serializer())
        subclass(FollowUpQuestion.WindowWidthQuestion::class, FollowUpQuestion.WindowWidthQuestion.serializer())

    }
}

val json = Json {
    serializersModule = followUpQuestionModule
    classDiscriminator = "type"
    ignoreUnknownKeys = true
    encodeDefaults = true
}


val HttpClientKey = AttributeKey<HttpClient>("HttpClient")
val ApplicationCall.httpClient: HttpClient
    get() = this.application.attributes[HttpClientKey]
fun Application.module() {
    install(ContentNegotiation) {
        json(json)
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

    routing {
        staticFiles("/uploads", File(ToolioConfig.storagePath))

        get("/") {
            println("==> GET /")
            call.respondText("OK")
        }

        post("/login") {
            handleDefaultLogin()
        }

        post("/loginWithGoogle") {
            handleGoogleLogin()
        }

        post("/openai") {
            val request = call.receive<ChatGptRequest>()

            insertChatMessage(
                userId = request.userId,
                sessionId = request.sessionId,
                role = Roles.USER,
                content = request.prompt
            )

            val response = callOpenAI(
                httpClient = call.httpClient,
                request = ChatGptRequest(userId = request.userId, prompt = request.prompt, sessionId = request.sessionId)
            )

            insertChatMessage(
                userId = request.userId,
                sessionId = request.sessionId,
                role = Roles.ASSISTANT,
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

        post("/openai-system") {
            val request = call.receive<ChatGptRequest>()
            val session = loadSpecificSessions(request.sessionId)

            if (session == null) {
                call.respond(HttpStatusCode.NotFound, "Session not found")
                return@post
            }

            // 1. SYSTEM message
            insertChatMessage(
                userId = request.userId,
                sessionId = request.sessionId,
                role = Roles.SYSTEM,
                content = session.initialPrompt
            )

            // 2. GPT response
            val response = callOpenAI(
                httpClient = call.httpClient,
                request = ChatGptRequest(
                    userId = request.userId,
                    prompt = session.initialPrompt,
                    sessionId = request.sessionId
                )
            )

            insertChatMessage(
                userId = request.userId,
                sessionId = request.sessionId,
                role = Roles.ASSISTANT,
                content = response.content
            )

            call.respond(HttpStatusCode.OK, response)
        }


        post("/speechToText") {
            val multipart = call.receiveMultipart()

            var audioBytes: ByteArray? = null
            var userId = "unknownUser"
            var sessionId = "unknownSession"
            var language: String? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "userId" -> userId = part.value
                            "sessionId" -> sessionId = part.value
                            "language" -> language = part.value
                        }
                    }

                    is PartData.FileItem -> {
                        if (part.name == "file") {
                            val inputStream = part.provider().toInputStream()
                            audioBytes = inputStream.readBytes()
                        }
                    }

                    else -> Unit
                }
                part.dispose()
            }

            if (audioBytes == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing required audio data")
                return@post
            }

            val response = callWhisperTranscription(
                httpClient = call.httpClient,
                request = ChatGptRequest(
                    prompt = "",
                    userId = userId,
                    sessionId = sessionId,
                    contentByteArray = audioBytes,
                    language = language
                )
            )

            /*insertChatMessage(
                userId = userId,
                sessionId = sessionId,
                role = response.role,
                content = response.content
            )*/

            call.respond(HttpStatusCode.OK, response)
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

            if (userId == null) {
                logger.warn("Missing user_id")
                call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                return@post
            }

            logger.debug("Received multipart data")

            if (promptText == null || imageBytes == null || fileName == null) {
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
                        userId = userId,
                        prompt = fullPrompt,
                        sessionId = sessionId.orEmpty(),
                        contentByteArray = imageBytes
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

        post("/save-session") {
            val request = call.receive<SaveSessionRequest>()
            saveTaskSession(request.userId, request.session)
            call.respond(HttpStatusCode.Created)
        }

        post("/update-settings") {
            val requestUserProfile = call.receive<UserProfile>()
            val success = updateUserSettings(requestUserProfile)

            if (success) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to update settings")
            }
        }
    }


}

