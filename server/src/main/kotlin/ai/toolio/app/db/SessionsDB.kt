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
import org.jetbrains.exposed.sql.transactions.transaction

suspend fun saveTaskSession(userId: String, session: RepairTaskSession) = withContext(Dispatchers.IO) {
    transaction {
        TaskSessions.insert {
            it[id] = session.sessionId.toUUID()
            it[TaskSessions.userId] = userId.toUUID()
            it[title] = session.title
            it[category] = session.category.id
            it[categoryType] = session.category.type.name
            it[task] = session.task.name
            it[taskStatus] = session.task.status.name
            it[answers] = Json.encodeToString(session.answers)
            it[startPrompt] = session.initialPrompt
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

                val answers: Map<String, String> = Json.decodeFromString(row[TaskSessions.answers])

                val messages = loadChatMessagesFromDb(sessionId.toUUID())

                RepairTaskSession(
                    sessionId = sessionId,
                    title = row[TaskSessions.title],
                    category = category,
                    task = task.copy(status = TaskStatus.valueOf(row[TaskSessions.taskStatus])),
                    answers = answers,
                    createdAt = row[TaskSessions.createdAt].toString(),
                    initialPrompt = row[TaskSessions.startPrompt] ?: "",
                    messages = messages
                )
            }
    }
}



