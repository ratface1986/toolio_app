package ai.toolio.app.di

interface AuthService {
    suspend fun signInWithGoogle(): AuthResult
    suspend fun signOut(): Boolean
}

sealed class AuthResult {
    data class Success(val userId: String, val displayName: String?, val email: String?) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Cancelled : AuthResult()
}