package ai.toolio.app.misc

import ai.toolio.app.di.AppEnvironment

enum class SessionType {
    TEXT,
    PREMIUM
}

fun buildSystemPrompt(): String {
    val session = AppEnvironment.getLastSession()
    val task = session?.task
    val category = session?.category
    val answers = session?.answers
    val tools = task?.tools?.joinToString(", ") { it.displayName }

    val answersText = if (answers?.isNotEmpty() == true) {
        answers.entries.joinToString("\n") { (key, value) -> "- $key: $value" }
    } else {
        "No additional answers were provided."
    }

    return """
        You are Toolio, an expert assistant helping the user with a home repair or installation task.

        Task category: ${category?.title}
        Specific task: ${task?.name}
        
       User has already provided the following context:
       $answersText
        
       These are the only tools available for the job:
       $tools
        
       You must not suggest tools or equipment that are not listed above.
       Do not repeat the user's answers or describe tools.
       Be clear and efficient in your guidance.
    """.trimIndent()
}
