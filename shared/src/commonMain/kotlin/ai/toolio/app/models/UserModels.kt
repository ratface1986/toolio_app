package ai.toolio.app.models

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val userId: String,
    val nickname: String,
    val inventory: Map<String, ToolData>,
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
