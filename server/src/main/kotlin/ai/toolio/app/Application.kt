package ai.toolio.app

import ai.toolio.app.models.ChatGptRequest
import ai.toolio.app.models.OpenAIRequest
import callOpenAI
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            println("==> GET /")
            call.respondText("OK")
        }

        post("/openai") {
            println("==> POST /openai")
            try {
                val request = call.receive<ChatGptRequest>()
                println("==> Request prompt: ${request.prompt}")
                val response = callOpenAI(request)
                delay(3000)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                println("💥 ERROR in /openai: ${e.message}")
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Internal error: ${e.message}")
            }
        }
    }
}

