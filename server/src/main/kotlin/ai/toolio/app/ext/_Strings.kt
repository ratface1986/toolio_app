package ai.toolio.app.ext

internal fun String.toSlug() = lowercase().replace("\n", " ").replace("[^a-z\\d\\s]".toRegex(), " ")

internal fun String?.orEmpty() = this ?: ""