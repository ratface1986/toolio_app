package ai.toolio.app

object SupabaseConfig {
    val url = System.getenv("SUPABASE_URL") ?: error("SUPABASE_URL not set")
    val apiKey = System.getenv("SUPABASE_API_KEY") ?: error("SUPABASE_API_KEY not set")
    val bucket = "chat-images"

    val storageBaseUrl = "$url/storage/v1/object"
    val publicBaseUrl = "$storageBaseUrl/public/$bucket"
}