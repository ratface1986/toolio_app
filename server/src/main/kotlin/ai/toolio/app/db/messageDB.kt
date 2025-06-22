package ai.toolio.app.db

import ai.toolio.app.db.tables.ChatMessages
import ai.toolio.app.db.tables.ChatSessions
import ai.toolio.app.models.ChatMessageIn
import org.jetbrains.exposed.sql.selectAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.UUID

suspend fun insertChatMessage(
    sessionId: String,
    role: String,
    content: String,
    imageUrl: String? = null
): Boolean = withContext(Dispatchers.IO) {
    try {
        transaction {
            ChatMessages.insert {
                it[ChatMessages.sessionId] = sessionId
                it[ChatMessages.sender] = role
                it[ChatMessages.content] = content
                it[ChatMessages.imageUrl] = imageUrl
                it[ChatMessages.createdAt] = LocalDateTime.now()
            }
        }
        true
    } catch (e: Exception) {
        false
    }
}

suspend fun insertChatSession(userId: String, sessionType: String): String = withContext(Dispatchers.IO) {
    val id = UUID.randomUUID().toString()
    val now = LocalDateTime.now()

    transaction {
        ChatSessions.insert {
            it[ChatSessions.id] = id
            it[ChatSessions.userId] = userId
            it[ChatSessions.sessionType] = sessionType
            it[ChatSessions.createdAt] = now
        }
    }

    id
}

suspend fun loadChatMessages(sessionId: String): List<ChatMessageIn> = withContext(Dispatchers.IO) {
    transaction {
        ChatMessages
            .selectAll().where { ChatMessages.sessionId eq sessionId }
            .orderBy(ChatMessages.createdAt to SortOrder.ASC)
            .mapNotNull { row ->
                val role = row[ChatMessages.sender]
                val content = row[ChatMessages.content]
                ChatMessageIn(role = role, content = content)
            }
    }
}


