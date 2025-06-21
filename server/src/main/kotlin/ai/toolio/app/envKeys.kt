package ai.toolio.app

import io.github.cdimascio.dotenv.dotenv

object SupabaseConfig {
    private val env = dotenv()
    val url = env["SUPABASE_URL"]
    val apiKey = env["SUPABASE_API_KEY"]
}