package ai.toolio.app.data

import ai.toolio.app.models.CategoryType
import ai.toolio.app.models.TaskStatus
import androidx.compose.ui.graphics.Color
import toolio.composeapp.generated.resources.Res
import toolio.composeapp.generated.resources.decorate
import toolio.composeapp.generated.resources.fix
import toolio.composeapp.generated.resources.install
import toolio.composeapp.generated.resources.maintain
import toolio.composeapp.generated.resources.mount

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
        TaskStatus.IN_PROGRESS -> Color((0xFF8DEB92))
        TaskStatus.ABORTED -> Color((0xFFFF3B30))
        TaskStatus.COMPLETED -> Color((0xFF2A9DF4))
    }