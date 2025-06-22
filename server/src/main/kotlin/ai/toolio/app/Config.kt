package ai.toolio.app

object ToolioConfig {
    val databaseUrl = System.getenv("DATABASE_URL") ?: error("DATABASE_URL not set")
    val storagePath = System.getenv("STORAGE_PATH") ?: "/app/storage"
}
