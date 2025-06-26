package ai.toolio.app.di

import platform.Foundation.NSUUID
import platform.Foundation.NSUserDefaults

actual object AppSessions {
    private val prefs = NSUserDefaults.Companion.standardUserDefaults

    actual fun createNewSessionId(): String {
        val id = NSUUID().UUIDString()
        setLastSessionId(id)
        return id
    }

    actual fun getLastSessionId(): String {
        return prefs.stringForKey("last_session_id").orEmpty()
    }

    actual fun setLastSessionId(id: String) {
        prefs.setObject(id, forKey = "last_session_id")
    }

    actual fun setLastActiveTimestamp(timestamp: Long) {
        prefs.setInteger(timestamp, forKey = "last_active_timestamp")
    }

    actual fun getLastActiveTimestamp(): Long {
        return prefs.integerForKey("last_active_timestamp").toLong()
    }
}