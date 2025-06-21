package ai.toolio.app

object SupabaseConfig {
    val url = System.getenv("SUPABASE_URL") ?: error("SUPABASE_URL not set") // для REST / таблиц
    val apiKey = System.getenv("SUPABASE_API_KEY") ?: error("SUPABASE_API_KEY not set")
    val bucket = "chat-images"

    // отдельный URL для стораджа
    private val storageUrl = System.getenv("SUPABASE_PROJECT_URL")
        ?: error("SUPABASE_PROJECT_URL not set") // https://feelmhmnayhaktidaiuf.supabase.co

    val storageBaseUrl = "$storageUrl/storage/v1/object" // ✅ базовая сторадж ссылка
    val publicBaseUrl = "$storageBaseUrl/$bucket"        // ✅ ПУТЬ без /public
}
