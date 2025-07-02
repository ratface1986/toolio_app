package ai.toolio.app.di

expect object AppSessions {
    fun saveUserId(id: String)
    fun getUserId(): String
    fun saveUserNickname(nickname: String)
    fun getUserNickname(): String
}