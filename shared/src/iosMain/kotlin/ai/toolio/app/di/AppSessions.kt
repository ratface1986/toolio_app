package ai.toolio.app.di

import platform.Foundation.NSUserDefaults

actual object AppSessions {
    private val prefs = NSUserDefaults.Companion.standardUserDefaults

    actual fun saveUserId(id: String) {
        prefs.setObject(id, forKey = "saved_user_id")
    }

    actual fun getUserId(): String =
        prefs.stringForKey("saved_user_id").orEmpty()

    actual fun saveUserNickname(nickname: String) {
        prefs.setObject(nickname, forKey = "saved_nickname")
    }

    actual fun getUserNickname(): String =
        prefs.stringForKey("saved_nickname").orEmpty()
}