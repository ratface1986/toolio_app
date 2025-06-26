package ai.toolio.app.db

import ai.toolio.app.db.tables.ChatMessages
import ai.toolio.app.db.tables.ChatSessions
import ai.toolio.app.ext.toUUID
import ai.toolio.app.misc.Roles
import ai.toolio.app.models.ToolioChatMessage
import org.jetbrains.exposed.sql.selectAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

suspend fun insertChatMessage(
    sessionId: String,
    role: String,
    content: String,
    imageUrl: String = ""
): Boolean = withContext(Dispatchers.IO) {
    try {
        transaction {
            ChatMessages.insert {
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

suspend fun insertChatSession(
    userId: String,
    title: String,
    type: String,
    status: String,
    startPrompt: String = ""
): UUID = withContext(Dispatchers.IO) {
    val id = UUID.randomUUID()

    transaction {
        ChatSessions.insert {
            it[ChatSessions.id] = id
            it[ChatSessions.userId] = userId.toUUID()
            it[ChatSessions.title] = title
            it[ChatSessions.type] = type
            it[ChatSessions.status] = status
            it[ChatSessions.startPrompt] = startPrompt
            it[ChatSessions.lastActive] = LocalDateTime.now()
            it[ChatSessions.createdAt] = LocalDateTime.now()
        }
    }

    id
}

suspend fun loadChatMessages(sessionId: String): List<ToolioChatMessage> = withContext(Dispatchers.IO) {
    transaction {
        ChatMessages
            .selectAll().where { ChatMessages.sessionId eq sessionId.toUUID() }
            .orderBy(ChatMessages.createdAt to SortOrder.ASC)
            .mapNotNull { row ->
                val roleStr = row[ChatMessages.role]
                val role = Roles.entries.find { it.role.equals(roleStr, ignoreCase = true) } ?: Roles.SYSTEM

                ToolioChatMessage(
                    sessionId = sessionId,
                    role = role,
                    content = row[ChatMessages.content],
                    imageUrl = row[ChatMessages.imageUrl],
                    timestamp = row[ChatMessages.createdAt].atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                )
            }
    }
}


