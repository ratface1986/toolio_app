package ai.toolio.app

object SupabaseConfig {
    val url = System.getenv("SUPABASE_URL") ?: error("SUPABASE_URL not set") // для REST / login
    val apiKey = System.getenv("SUPABASE_API_KEY") ?: error("SUPABASE_API_KEY not set")
    val bucket = "chat-images"

    // storageUrl берём из новой переменной
    private val storageUrl = System.getenv("SUPABASE_PROJECT_URL")
        ?: error("SUPABASE_PROJECT_URL not set") // https://feelmhmnayhaktidaiuf.supabase.co

    val storageBaseUrl = "$storageUrl/storage/v1/object"
    val publicBaseUrl = "$storageBaseUrl/public/$bucket"
}