package ai.toolio.app.api

import ai.toolio.app.db.findUserByNickname
import ai.toolio.app.db.findUserByUserId
import ai.toolio.app.db.getUserInventory
import ai.toolio.app.db.insertUser
import ai.toolio.app.db.insertUserWithUserId
import ai.toolio.app.db.loadTaskSessions
import ai.toolio.app.models.ToolData
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

suspend fun RoutingContext.handleGoogleLogin() {
    val request = call.receive<Map<String, String>>()
    val userId = request["userId"]
    val email = request["email"]
    val nickname = request["nickname"]

    if (userId.isNullOrBlank()) {
        call.respond(HttpStatusCode.BadRequest, "Missing nickname")
        return
    }

    val user = findUserByUserId(userId)
        ?: insertUserWithUserId(userId, nickname.orEmpty(), email.orEmpty())
        ?: run {
            call.respond(HttpStatusCode.InternalServerError, "Failed to create user")
            return
        }

    val profile = user.copy(
        userId = user.userId,
        inventory = getUserInventory(user.userId)
            .mapValues { (_, value) ->
                val obj = value.jsonObject
                ToolData(
                    name = obj["name"]?.jsonPrimitive?.content.orEmpty(),
                    description = obj["description"]?.jsonPrimitive?.content.orEmpty(),
                    imageUrl = obj["imageUrl"]?.jsonPrimitive?.content.orEmpty(),
                    confirmed = obj["confirmed"]?.jsonPrimitive?.boolean ?: false
                )
            },
        sessions = loadTaskSessions(user.userId).toMutableList()
    )

    call.respond(profile)
}