package ai.toolio.app.db

import ai.toolio.app.db.tables.TaskSessions
import ai.toolio.app.ext.toUUID
import ai.toolio.app.models.RepairTaskSession
import ai.toolio.app.models.TaskStatus
import ai.toolio.app.models.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

suspend fun saveTaskSession(userId: String, session: RepairTaskSession) = withContext(Dispatchers.IO) {
    transaction {
        val data: TaskSessions.(UpdateBuilder<*>) -> Unit = {
            it[TaskSessions.userId] = userId.toUUID()
            it[title] = session.title
            it[category] = session.category.id
            it[categoryType] = session.category.type.name
            it[task] = session.task.name
            it[taskStatus] = session.task.status.name
            it[answers] = Json.encodeToString(session.answers)
            it[startPrompt] = session.initialPrompt
            it[isSaved] = session.isSaved
        }

        val sessionUUID = if (session.sessionId.isBlank()) {
            UUID.randomUUID()
        } else {
            try {
                UUID.fromString(session.sessionId)
            } catch (e: IllegalArgumentException) {
                error("Invalid sessionId format: ${session.sessionId}")
            }
        }

        val exists = TaskSessions.selectAll()
            .where { TaskSessions.id eq sessionUUID }
            .any()

        if (exists) {
            TaskSessions.update({ TaskSessions.id eq sessionUUID }, body = data)
        } else {
            TaskSessions.insert {
                it[id] = sessionUUID
                data(it)
            }
        }
    }
}

suspend fun loadTaskSessions(userId: String): List<RepairTaskSession> = withContext(Dispatchers.IO) {
    transaction {
        TaskSessions
            .selectAll().where { TaskSessions.userId eq userId.toUUID() }
            .mapNotNull { row ->
                val categoryId = row[TaskSessions.category]
                val taskName = row[TaskSessions.task]

                val category = Tasks.categories.find { it.id == categoryId } ?: return@mapNotNull null
                val task = category.tasks.find { it.name == taskName } ?: return@mapNotNull null
                val sessionId = row[TaskSessions.id].toString()

                println("Sessions Task name: $task in category: $category")

                val answers: Map<String, String> = try {
                    Json.decodeFromString<Map<String, String>>(row[TaskSessions.answers])
                } catch (e: Exception) {
                    println("⚠️ Failed to parse answers for session ${row[TaskSessions.id]}: ${e.message}")
                    emptyMap()
                }

                val messages = loadChatMessagesForUser(sessionId.toUUID())

                RepairTaskSession(
                    sessionId = sessionId,
                    title = row[TaskSessions.title],
                    category = category,
                    task = task.copy(status = TaskStatus.valueOf(row[TaskSessions.taskStatus])),
                    answers = answers,
                    createdAt = row[TaskSessions.createdAt].toString(),
                    initialPrompt = row[TaskSessions.startPrompt] ?: "",
                    messages = messages,
                    isSaved = row[TaskSessions.isSaved],
                )
            }
    }
}



