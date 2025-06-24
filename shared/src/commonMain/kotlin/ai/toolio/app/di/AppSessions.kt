package ai.toolio.app.di

expect object AppSessions {
    fun createNewSessionId(): String
    fun getLastSessionId(): String?
    fun setLastSessionId(id: String)
}