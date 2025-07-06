package ai.toolio.app.data

import ai.toolio.app.models.CategoryType
import ai.toolio.app.models.TaskStatus
import ai.toolio.app.models.TaskStatus.*
import ai.toolio.app.models.Tool
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource
import toolio.composeapp.generated.resources.*

internal fun CategoryType.toDrawableResource() =
    when (this) {
        CategoryType.MOUNT -> Res.drawable.mount
        CategoryType.FIX -> Res.drawable.fix
        CategoryType.INSTALL -> Res.drawable.install
        CategoryType.DECORATE -> Res.drawable.decorate
        CategoryType.MAINTAIN -> Res.drawable.maintain
    }

internal fun Tool.toDrawableResource() =
    when (this) {
        Tool.DRILL -> Res.drawable.ic_drill
        Tool.SCREWDRIVER -> Res.drawable.ic_screwdriver
        Tool.HAMMER -> Res.drawable.ic_hammer
        Tool.UTILITY_KNIFE -> Res.drawable.ic_utility_knife
        Tool.TAPE_MEASURE -> Res.drawable.ic_tape_measuring
        Tool.WIRE_STRIPPER -> Res.drawable.ic_wire_stripper
        Tool.ELECTRICAL_TAPE -> Res.drawable.ic_electricity
        Tool.LEVEL -> Res.drawable.ic_level
        Tool.PLIERS -> Res.drawable.ic_pliers
        Tool.SCREWS -> Res.drawable.ic_screws
        Tool.STUD_FINDER -> Res.drawable.ic_stud_finder
        Tool.WALL_PLUGS -> Res.drawable.ic_dowel
        Tool.WRENCH -> Res.drawable.ic_wrench
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