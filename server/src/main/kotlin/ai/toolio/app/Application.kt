package ai.toolio.app

import ai.toolio.app.db.confirmTool
import ai.toolio.app.db.findUserByNickname
import ai.toolio.app.db.getUserInventory
import ai.toolio.app.db.insertTool
import ai.toolio.app.db.insertUser
import ai.toolio.app.models.ChatGptRequest
import ai.toolio.app.models.ToolData
import ai.toolio.app.models.ToolRecognitionResult
import ai.toolio.app.models.UserProfile
import ai.toolio.app.services.deleteFromStorage
import ai.toolio.app.services.uploadToStorage
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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonPrimitive
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

    val logger = LoggerFactory.getLogger("MYDATA:")


    attributes.put(HttpClientKey, httpClient)

    routing {
        get("/") {
            println("==> GET /")
            call.respondText("OK")
        }

        /*post("/openai") {
            println("==> POST /openai")
            try {
                val request = call.receive<ChatGptRequest>()
                println("==> Request prompt: ${request.prompt}")
                val response = callOpenAI(call.httpClient, request)
                delay(3000)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                println("💥 ERROR in /openai: ${e.message}")
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Internal error: ${e.message}")
            }
        }*/
        post("/openai") {
            val multipart = call.receiveMultipart()
            var promptText: String? = null
            var imageUrl: String? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "prompt") promptText = part.value
                    }
                    is PartData.FileItem -> {
                        if (part.contentType?.contentType == "image") {
                            val fileName = "${UUID.randomUUID()}.jpg"
                            val imageBytes = part.provider().readRemaining().readByteArray()
                            imageUrl = uploadToStorage(call.httpClient, imageBytes, fileName)
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (promptText == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing prompt")
                return@post
            }

            val response = callOpenAI(
                httpClient = call.httpClient,
                request = ChatGptRequest(
                    prompt = promptText,
                    imageUrl = imageUrl.orEmpty()
                )
            )

            val historyLine = buildJsonObject {
                put("timestamp", JsonPrimitive(Clock.System.now().toString()))
                put("prompt", JsonPrimitive(promptText))
                put("image", JsonPrimitive(imageUrl))
                put("response", JsonPrimitive(response.content))
            }

            File("chat-history.jsonl").appendText( "$historyLine\n")

            call.respond(HttpStatusCode.OK, response)
        }


        post("/upload") {
            val multipart = call.receiveMultipart()
            var uploadedUrl: String? = null

            multipart.forEachPart { part ->
                if (part is PartData.FileItem && part.contentType?.contentType == "image") {
                    val fileName = "${UUID.randomUUID()}.jpg"
                    val imageBytes = part.provider().readRemaining().readByteArray()

                    uploadedUrl = uploadToStorage(
                        httpClient = call.httpClient,
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
            var userId = "mock-user-id"

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> when (part.name) {
                        "prompt" -> promptText = part.value
                        "user_id" -> userId = part.value
                    }
                    is PartData.FileItem -> if (part.contentType?.contentType == "image") {
                        fileName = "${UUID.randomUUID()}.jpg"
                        imageBytes = part.provider().readRemaining().readByteArray()
                    }
                    else -> {}
                }
                part.dispose()
            }

            logger.debug("Received multipart data")

            if (promptText == null || imageBytes == null || fileName == null || userId == null) {
                logger.warn("Missing prompt or image")
                call.respond(HttpStatusCode.BadRequest, "Missing data")
                return@post
            }

            val imageUrl = uploadToStorage(call.httpClient, imageBytes, fileName)

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
                        imageUrl = imageUrl,
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
                Json.decodeFromString<ToolRecognitionResult>(gptResponse.content).copy(
                )
            } catch (e: Exception) {
                logger.error(e.message)
                call.respond(HttpStatusCode.InternalServerError, "Failed to parse GPT response: ${e.message}")
                return@post
            }

            if (!result.matchesExpected) {
                deleteFromStorage(call.httpClient, fileName)
                call.respond(result)
                return@post
            }

            val saveSuccess = insertTool(
                httpClient = call.httpClient,
                userId = userId,
                type = result.type ?: "UNKNOWN",
                name = result.name.orEmpty(),
                description = result.description.orEmpty(),
                imageUrl = imageUrl
            )

            if (!saveSuccess) {
                deleteFromStorage(call.httpClient, fileName)
                call.respond(HttpStatusCode.InternalServerError, "Failed to save tool")
                return@post
            }

            call.respond(result)
        }

        post("/confirm-tool") {
            val request = call.receive<Map<String, String>>()
            val userId = request["user_id"]
            val toolType = request["tool_type"]

            if (userId.isNullOrBlank() || toolType.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing user_id or tool_type")
                return@post
            }

            val success = confirmTool(call.httpClient, userId, toolType)
            if (success) {
                call.respond(HttpStatusCode.OK, "Tool confirmed")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to confirm tool")
            }
        }

        post("/login") {
            val request = call.receive<Map<String, String>>()
            val nickname = request["nickname"]

            if (nickname.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing nickname")
                return@post
            }

            val user = findUserByNickname(call.httpClient, nickname)
                ?: insertUser(call.httpClient, nickname)
                ?: run {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create user")
                    return@post
                }

            val userId = user.userId

            val profile = UserProfile(
                userId = userId as String,
                nickname = nickname,
                inventory = getUserInventory(call.httpClient, userId)
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


        staticFiles("/uploads", File("uploads"))
    }


}

