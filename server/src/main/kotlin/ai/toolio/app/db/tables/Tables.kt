package ai.toolio.app.db.tables

import ai.toolio.app.misc.MeasureType
import ai.toolio.app.misc.SessionType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Tools : Table("tools") {
    val id = uuid("id")
    val userId = uuid("user_id").references(Users.id)
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
    val id = uuid("id")
    val nickname = varchar("nickname", 64)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val measure = enumerationByName("measure", 10, MeasureType::class)
    val language = varchar("language", 10).nullable()
    val email = varchar("email", 120).nullable()
    val googleUserId = varchar("google_user_id", 255).nullable()
    val textSessions = integer("text_sessions")
    val premiumSessions = integer("text_sessions")

    override val primaryKey = PrimaryKey(id)
}

object TaskSessions : Table("task_sessions") {
    val id = uuid("id")
    val userId = uuid("user_id").references(Users.id)
    val title = varchar("title", 128)
    val category = varchar("category", 64)
    val categoryType = varchar("category_type", 32)
    val task = varchar("task", 64)
    val taskStatus = varchar("task_status", 32)
    val answers = text("answers")
    val startPrompt = text("start_prompt").nullable()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val isSaved = bool("is_saved")
    val sessionType = enumerationByName("session_type", 10, SessionType::class)

    override val primaryKey = PrimaryKey(id)
}


object ChatMessages : Table("chat_messages") {
    val id = integer("id").autoIncrement()
    val userId = uuid("user_id").references(Users.id)
    val sessionId = uuid("session_id").references(TaskSessions.id)
    val role = varchar("role", 64)
    val content = text("content")
    val imageUrl = text("image_url").nullable()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}


