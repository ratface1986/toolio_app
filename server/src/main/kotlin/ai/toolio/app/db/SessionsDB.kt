package ai.toolio.app.db

import ai.toolio.app.db.tables.ChatMessages.sessionId
import ai.toolio.app.db.tables.TaskSessions
import ai.toolio.app.ext.toUUID
import ai.toolio.app.models.RepairTaskSession
import ai.toolio.app.models.TaskStatus
import ai.toolio.app.models.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

suspend fun saveTaskSession(clientUserId: String, session: RepairTaskSession) = withContext(Dispatchers.IO) {
    transaction {
        val data: TaskSessions.(UpdateBuilder<*>) -> Unit = {
            it[userId] = clientUserId.toUUID()
            it[title] = session.title
            it[category] = session.category.id
            it[categoryType] = session.category.type.name
            it[task] = session.task.name
            it[taskStatus] = session.task.status.name
            it[answers] = Json.encodeToString(session.answers)
            it[startPrompt] = session.initialPrompt
            it[isSaved] = true
            it[sessionType] = session.sessionType
        }

        val exists = TaskSessions.selectAll()
            .where { TaskSessions.id eq session.sessionId.toUUID() }
            .any()

        if (exists) {
            TaskSessions.update({ TaskSessions.id eq session.sessionId.toUUID() }, body = data)
        } else {
            TaskSessions.insert {
                it[id] = session.sessionId.toUUID()
                data(it)
            }
        }
    }
}

suspend fun loadTaskSessions(userId: String): List<RepairTaskSession> = withContext(Dispatchers.IO) {
    val rows = transaction {
        TaskSessions
            .selectAll()
            .where { TaskSessions.userId eq userId.toUUID() }
            .orderBy(TaskSessions.createdAt to SortOrder.ASC)
            .toList()
    }

    rows.mapNotNull { mapRowToRepairTaskSession(it) }
}

suspend fun loadSpecificSessions(sessionId: String): RepairTaskSession? = withContext(Dispatchers.IO) {
    val row = transaction {
        TaskSessions
            .selectAll().where { TaskSessions.id eq sessionId.toUUID() }
            .limit(1)
            .firstOrNull()
    }

    row?.let { mapRowToRepairTaskSession(it) }
}

suspend fun mapRowToRepairTaskSession(row: ResultRow): RepairTaskSession? {
    val categoryId = row[TaskSessions.category]
    val taskName = row[TaskSessions.task]

    val category = Tasks.categories.find { it.id == categoryId } ?: return null
    val task = category.tasks.find { it.name == taskName } ?: return null
    val sessionId = row[TaskSessions.id]

    val answers: Map<String, String> = try {
        Json.decodeFromString(row[TaskSessions.answers])
    } catch (e: Exception) {
        println("⚠️ Failed to parse answers for session $sessionId: ${e.message}")
        emptyMap()
    }

    val messages = loadChatMessagesForUser(sessionId)

    return RepairTaskSession(
        sessionId = sessionId.toString(),
        title = row[TaskSessions.title],
        category = category,
        task = task.copy(status = TaskStatus.valueOf(row[TaskSessions.taskStatus])),
        answers = answers,
        createdAt = row[TaskSessions.createdAt].toString(),
        initialPrompt = row[TaskSessions.startPrompt] ?: "",
        messages = messages,
        isSaved = row[TaskSessions.isSaved],
        sessionType = row[TaskSessions.sessionType]
    )
}




