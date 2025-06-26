package ai.toolio.app.di

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID
import androidx.core.content.edit

actual object AppSessions {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("toolio_prefs", Context.MODE_PRIVATE)
    }

    actual fun createNewSessionId(): String {
        val id = UUID.randomUUID().toString()
        setLastSessionId(id)
        return id
    }

    actual fun getLastSessionId(): String {
        return prefs.getString("last_session_id", "").orEmpty()
    }

    actual fun setLastSessionId(id: String) {
        prefs.edit { putString("last_session_id", id) }
    }

    actual fun setLastActiveTimestamp(timestamp: Long) {
        prefs.edit { putLong("last_active_timestamp", timestamp) }
    }

    actual fun getLastActiveTimestamp(): Long {
        return prefs.getLong("last_active_timestamp", 0)
    }
}