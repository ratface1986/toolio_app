package ai.toolio.app.db

import ai.toolio.app.db.tables.ChatMessages
import ai.toolio.app.ext.toUUID
import ai.toolio.app.misc.Roles
import ai.toolio.app.models.ToolioChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

suspend fun insertChatMessage(
    userId: String,
    sessionId: String,
    role: String,
    content: String,
    imageUrl: String = ""
): Boolean = withContext(Dispatchers.IO) {
    try {
        transaction {
            ChatMessages.insert {
                it[ChatMessages.userId] = userId.toUUID()
                it[ChatMessages.sessionId] = sessionId.toUUID()
                it[ChatMessages.role] = role
                it[ChatMessages.content] = content
                it[ChatMessages.imageUrl] = imageUrl
            }
        }
        true
    } catch (e: Exception) {
        false
    }
}

fun loadChatMessagesForUser(sessionId: UUID): List<ToolioChatMessage> {
    return ChatMessages
        .selectAll().where { ChatMessages.sessionId eq sessionId }
        .orderBy(ChatMessages.createdAt to SortOrder.ASC)
        .mapNotNull { row ->
            val roleStr = row[ChatMessages.role]
            val role = try {
                Roles.valueOf(roleStr)
            } catch (e: Exception) {
                Roles.ASSISTANT
            }

            ToolioChatMessage(
                sessionId = sessionId.toString(),
                role = role,
                content = row[ChatMessages.content],
                imageUrl = row[ChatMessages.imageUrl],
                timestamp = row[ChatMessages.createdAt].toString()
            )
        }
}


