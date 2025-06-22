package ai.toolio.app.ext

import java.util.UUID

internal fun String?.toUUID() = UUID.fromString(this)