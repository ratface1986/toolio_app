package ai.toolio.app.db.tables

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

    override val primaryKey = PrimaryKey(id)
}

/*object ChatSessions : Table("chat_sessions") {
    val id = uuid("id")
    val userId = uuid("user_id").references(Users.id)
    val title = varchar("title", 128)
    val type = varchar("type", 64)
    val status = varchar("status", 32)
    val startPrompt = text("start_prompt").nullable()
    val lastActive = datetime("last_active").clientDefault { LocalDateTime.now() }
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}*/
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
    val startedAt = long("started_at")
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}


object ChatMessages : Table("chat_messages") {
    val id = integer("id").autoIncrement()
    val sessionId = uuid("session_id").references(TaskSessions.id)
    val role = varchar("role", 64)
    val content = text("content")
    val imageUrl = text("image_url").nullable()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}


