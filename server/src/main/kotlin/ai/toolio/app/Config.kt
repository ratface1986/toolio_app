package ai.toolio.app

object ToolioConfig {
    val rawUrl = System.getenv("DATABASE_URL") ?: error("DATABASE_URL not set")
    val jdbcUrl = System.getenv("JDBC_DATABASE_URL") ?: error("JDBC_DATABASE_URL not set")
    val storagePath = System.getenv("STORAGE_PATH") ?: "/app/storage"
}
