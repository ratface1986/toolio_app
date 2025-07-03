package ai.toolio.app.models

import ai.toolio.app.misc.MeasureType
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val userId: String,
    val inventory: Map<String, ToolData>,
    var settings: UserSettings,
    val sessions: MutableList<RepairTaskSession> = mutableListOf()
) {
    fun getTool(tool: Tool): ToolData? = inventory[tool.name]
}

@Serializable
data class ToolData(
    val name: String,
    val description: String,
    val imageUrl: String,
    val confirmed: Boolean
)

@Serializable
data class UserSettings(
    val nickname: String,
    val email: String,
    val language: String,
    val measure: MeasureType
)
