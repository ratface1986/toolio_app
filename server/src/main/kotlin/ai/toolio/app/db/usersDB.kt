package ai.toolio.app.db

import ai.toolio.app.db.tables.Tools
import ai.toolio.app.db.tables.Users
import ai.toolio.app.ext.toUUID
import ai.toolio.app.misc.MeasureType
import ai.toolio.app.models.Tool
import ai.toolio.app.models.ToolData
import ai.toolio.app.models.UserProfile
import ai.toolio.app.models.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

suspend fun updateTool(
    userId: String,
    type: String,
    name: String,
    description: String,
    imageUrl: String,
    confirmed: Boolean
): Boolean = withContext(Dispatchers.IO) {
    try {
        transaction {
            Tools.update({ Tools.userId eq userId.toUUID() and (Tools.type eq type) }) {
                it[Tools.name] = name
                it[Tools.description] = description
                it[Tools.imageUrl] = imageUrl
                it[Tools.confirmed] = confirmed
                it[Tools.createdAt] = LocalDateTime.now()
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
        val email = userRow[Users.email].orEmpty()
        val language = userRow[Users.language].orEmpty()
        val measure = userRow[Users.measure]

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
            inventory = inventory,
            settings = UserSettings(
                nickname = nickname,
                email = email,
                language = language,
                measure = measure
            )
        )
    }
}

suspend fun findUserByUserId(googleUserId: String): UserProfile? = withContext(Dispatchers.IO) {
    transaction {
        val userRow = Users
            .selectAll().where { Users.googleUserId eq googleUserId }
            .limit(1)
            .firstOrNull() ?: return@transaction null

        val userId = userRow[Users.id]
        val email = userRow[Users.email].orEmpty()
        val language = userRow[Users.language].orEmpty()
        val measure = userRow[Users.measure]

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
            inventory = inventory,
            settings = UserSettings(
                nickname = "",
                email = email,
                language = language,
                measure = measure
            )
        )
    }
}


suspend fun insertUser(nickname: String, email: String = "rust.m@gmail.com"): UserProfile? = withContext(Dispatchers.IO) {
    val userId = UUID.randomUUID()
    val now = LocalDateTime.now()

    transaction {
        // 1. Вставка пользователя
        Users.insert {
            it[id] = userId
            it[Users.nickname] = nickname
            it[createdAt] = now
            it[Users.email] = email
            it[language] = "en"
            it[measure] = MeasureType.INCH
            it[textSessions] = 0
            it[premiumSessions] = 1
        }

        // 2. Вставка всех инструментов как "заготовка"
        Tool.entries.forEach { tool ->
            Tools.insert {
                it[id] = UUID.randomUUID()
                it[Tools.userId] = userId
                it[Tools.type] = tool.name
                it[Tools.name] = ""
                it[Tools.description] = ""
                it[Tools.imageUrl] = ""
                it[Tools.confirmed] = false
                it[Tools.createdAt] = now
            }
        }

        UserProfile(
            userId = userId.toString(),
            inventory = Tool.entries.associate {
                it.name to ToolData(it.displayName, "", "", false)
            },
            settings = UserSettings(
                nickname = nickname,
                email = email,
                language = "en",
                measure = MeasureType.INCH
            ),
            textSessions = 0,
            premiumSessions = 1
        )
    }
}

suspend fun insertUserWithUserId(newGoogleUserId: String, nickname: String, email: String): UserProfile? = withContext(Dispatchers.IO) {
    val userId = UUID.randomUUID()
    val now = LocalDateTime.now()

    transaction {
        // 1. Вставка пользователя
        Users.insert {
            it[id] = userId
            it[Users.nickname] = nickname
            it[createdAt] = now
            it[Users.email] = email
            it[language] = "en"
            it[measure] = MeasureType.INCH
            it[googleUserId] = newGoogleUserId
        }

        // 2. Вставка всех инструментов как "заготовка"
        Tool.entries.forEach { tool ->
            Tools.insert {
                it[id] = UUID.randomUUID()
                it[Tools.userId] = userId
                it[Tools.type] = tool.name
                it[Tools.name] = ""
                it[Tools.description] = ""
                it[Tools.imageUrl] = ""
                it[Tools.confirmed] = false
                it[Tools.createdAt] = now
            }
        }

        UserProfile(
            userId = userId.toString(),
            inventory = Tool.entries.associate {
                it.name to ToolData(it.displayName, "", "", false)
            },
            settings = UserSettings(
                nickname = nickname,
                email = email,
                language = "en",
                measure = MeasureType.INCH
            ),
            textSessions = 0,
            premiumSessions = 1
        )
    }
}

suspend fun updateUserSettings(userProfile: UserProfile): Boolean = withContext(Dispatchers.IO) {
    try {
        transaction {
            Users.update({ Users.id eq userProfile.userId.toUUID() }) {
                it[nickname] = userProfile.settings.nickname
                it[email] = userProfile.settings.email
                it[language] = userProfile.settings.language
                it[measure] = userProfile.settings.measure
                it[premiumSessions] = userProfile.premiumSessions
                it[textSessions] = userProfile.textSessions
            }
        }
        true
    } catch (e: Exception) {
        false
    }
}