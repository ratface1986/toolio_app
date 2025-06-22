package ai.toolio.app

object ToolioConfig {
    val rawUrl = System.getenv("DATABASE_URL") ?: error("DATABASE_URL not set")
    val jdbcUrl = "jdbc:$rawUrl"
    val storagePath = System.getenv("STORAGE_PATH") ?: "/app/storage"
}
