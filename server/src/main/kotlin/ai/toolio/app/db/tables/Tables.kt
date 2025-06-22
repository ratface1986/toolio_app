package ai.toolio.app.db.tables

import org.jetbrains.exposed.sql.javatime.timestamp
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Tools : Table("tools") {
    val id = integer("id").autoIncrement()
    val userId = varchar("user_id", 64).references(Users.id)
    val type = varchar("type", 64)
    val name = varchar("name", 255)
    val description = text("description")
    val imageUrl = text("image_url")
    val confirmed = bool("confirmed")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
    init {
        index(true, userId, type) // уникальный индекс на user + type
    }
}

object Users : Table("users") {
    val id = varchar("id", 64)
    val nickname = varchar("nickname", 64)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}

object ChatSessions : Table("chat_sessions") {
    val id = varchar("id", 64)
    val userId = varchar("user_id", 64).references(Users.id)
    val sessionType = varchar("session_type", 64)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

object ChatMessages : Table("chat_messages") {
    val id = integer("id").autoIncrement()
    val sessionId = varchar("session_id", 64).references(ChatSessions.id)
    val sender = varchar("sender", 64) // "user" или "assistant"
    val content = text("content")
    val imageUrl = text("image_url").nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}


