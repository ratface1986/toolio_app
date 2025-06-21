package ai.toolio.app

object SupabaseConfig {
    private val baseProjectUrl = System.getenv("SUPABASE_URL")
        ?.removeSuffix("/rest/v1")
        ?: error("SUPABASE_URL not set")

    val apiKey = System.getenv("SUPABASE_API_KEY") ?: error("SUPABASE_API_KEY not set")
    val bucket = "chat-images"

    val storageBaseUrl = "$baseProjectUrl/storage/v1/object"
    val publicBaseUrl = "$storageBaseUrl/public/$bucket"
}
