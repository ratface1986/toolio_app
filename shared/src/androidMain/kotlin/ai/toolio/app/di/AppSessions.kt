package ai.toolio.app.di

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

@SuppressLint("StaticFieldLeak")
actual object AppSessions {
    private lateinit var prefs: SharedPreferences
    lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context
        prefs = context.getSharedPreferences("toolio_prefs", Context.MODE_PRIVATE)
    }

    actual fun saveUserId(id: String) {
        prefs.edit { putString("saved_user_id", id) }
    }

    actual fun getUserId(): String {
        return prefs.getString("saved_user_id", "").orEmpty()
    }

    actual fun saveUserNickname(nickname: String) {
        prefs.edit { putString("saved_nickname", nickname) }
    }

    actual fun getUserNickname(): String {
        return prefs.getString("saved_nickname", "").orEmpty()
    }
}