package ai.toolio.app.ext

import java.util.*

internal fun String?.toUUID() = UUID.fromString(this)