package ai.toolio.app.models

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val userId: String,
    val nickname: String,
    val inventory: Map<String, ToolData>
)

@Serializable
data class ToolData(
    val name: String,
    val description: String,
    val imageUrl: String,
    val confirmed: Boolean
)
