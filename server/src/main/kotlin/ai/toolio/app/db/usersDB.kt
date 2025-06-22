package ai.toolio.app.db

import ai.toolio.app.db.tables.Tools
import ai.toolio.app.db.tables.Users
import ai.toolio.app.ext.toUUID
import ai.toolio.app.models.Tool
import ai.toolio.app.models.ToolData
import ai.toolio.app.models.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

suspend fun insertTool(
    userId: String,
    type: String,
    name: String,
    description: String,
    imageUrl: String
): Boolean = withContext(Dispatchers.IO) {
    try {
        transaction {
            Tools.update({ Tools.userId eq userId.toUUID() and (Tools.type eq type) }) {
                it[Tools.name] = name
                it[Tools.description] = description
                it[Tools.imageUrl] = imageUrl
                it[Tools.confirmed] = false
                it[Tools.createdAt] = LocalDateTime.now()
            }
        }
        true
    } catch (e: Exception) {
        false
    }
}

suspend fun confirmTool(userId: String, toolType: String): Boolean = withContext(Dispatchers.IO) {
    try {
        transaction {
            Tools.update({ Tools.userId eq userId.toUUID() and (Tools.type eq toolType) }) {
                it[confirmed] = true
            }
        }
        true
    } catch (e: Exception) {
        false
    }
}

suspend fun getUserInventory(userId: String): JsonObject = withContext(Dispatchers.IO) {
    transaction {
        val tools = Tools
            .selectAll().where { Tools.userId eq userId.toUUID() }
            .orderBy(Tools.createdAt to SortOrder.ASC)

        buildJsonObject {
            tools.forEach { row ->
                val type = row[Tools.type]

                put(type, buildJsonObject {
                    put("name", row[Tools.name])
                    put("description", row[Tools.description])
                    put("imageUrl", row[Tools.imageUrl])
                    put("confirmed", row[Tools.confirmed])
                })
            }
        }
    }
}

suspend fun findUserByNickname(nickname: String): UserProfile? = withContext(Dispatchers.IO) {
    transaction {
        val userRow = Users
            .selectAll().where { Users.nickname eq nickname }
            .limit(1)
            .firstOrNull() ?: return@transaction null

        val userId = userRow[Users.id]

        val inventory = Tools
            .selectAll().where { Tools.userId eq userId }
            .associate { row ->
                val type = row[Tools.type]
                val name = row[Tools.name]
                val description = row[Tools.description]
                val imageUrl = row[Tools.imageUrl]
                val confirmed = row[Tools.confirmed]

                type to ToolData(name, description, imageUrl, confirmed)
            }

        UserProfile(
            userId = userId.toString(),
            nickname = nickname,
            inventory = inventory
        )
    }
}


suspend fun insertUser(nickname: String): UserProfile? = withContext(Dispatchers.IO) {
    val userId = UUID.randomUUID()
    val now = LocalDateTime.now()

    transaction {
        // 1. Вставка пользователя
        Users.insert {
            it[id] = userId
            it[Users.nickname] = nickname
            it[createdAt] = now
        }

        // 2. Вставка всех инструментов как "заготовка"
        Tool.entries.forEach { tool ->
            Tools.insert {
                it[Tools.userId] = userId
                it[Tools.type] = tool.name
                it[Tools.name] = "" // пустое имя — заполняется позже
                it[Tools.description] = ""
                it[Tools.imageUrl] = ""
                it[Tools.confirmed] = false
                it[Tools.createdAt] = now
            }
        }

        UserProfile(
            userId = userId.toString(),
            nickname = nickname,
            inventory = Tool.entries.associate {
                it.name to ToolData(it.displayName, "", "", false)
            }
        )
    }
}