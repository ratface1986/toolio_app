package ai.toolio.app.di

interface AuthService {
    suspend fun signInWithGoogle(): AuthResult // Определи AuthResult как Data class для успеха/ошибки
    // suspend fun signOut(): Boolean // Если нужен выход
}

sealed class AuthResult {
    data class Success(val userId: String, val displayName: String?, val email: String?) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Cancelled : AuthResult()
}