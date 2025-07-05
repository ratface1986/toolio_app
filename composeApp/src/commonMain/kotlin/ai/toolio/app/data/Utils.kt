package ai.toolio.app.data

import ai.toolio.app.models.CategoryType
import ai.toolio.app.models.TaskStatus
import ai.toolio.app.models.TaskStatus.*
import androidx.compose.ui.graphics.Color
import toolio.composeapp.generated.resources.*

internal fun CategoryType.toDrawableResource() =
    when (this) {
        CategoryType.MOUNT -> Res.drawable.mount
        CategoryType.FIX -> Res.drawable.fix
        CategoryType.INSTALL -> Res.drawable.install
        CategoryType.DECORATE -> Res.drawable.decorate
        CategoryType.MAINTAIN -> Res.drawable.maintain
    }

internal fun TaskStatus.toColor() =
    when (this) {
        IDLE -> Color.Black
        IN_PROGRESS -> Color(0xFF8DEB92)
        ABORTED -> Color(0xFFE6005F)
        COMPLETED -> Color(0xFF443A94)
    }

internal fun TaskStatus.toDisplayText(): String = when (this) {
    IDLE -> "Idle..."
    IN_PROGRESS -> "In Progress..."
    COMPLETED -> "Completed"
    ABORTED -> "Aborted"
}