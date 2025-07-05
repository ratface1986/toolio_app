package ai.toolio.app.spec

import ai.toolio.app.di.AuthResult
import ai.toolio.app.di.AuthService
import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class AndroidAuthService(private val context: Context) : AuthService {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient

    private var signInContinuation: ((AuthResult) -> Unit)? = null

    init {
        // Получаем webClientId из google-services.json
        val webClientId = "592143760637-b9mveirnlbml8vfb7mg4124fa5rb9ci2.apps.googleusercontent.com"

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    override suspend fun signInWithGoogle(): AuthResult = suspendCancellableCoroutine { continuation ->
        signInContinuation = { result ->
            signInContinuation = null
            continuation.resume(result)
        }

        val signInIntent = googleSignInClient.signInIntent

        (context as? Activity)?.startActivityForResult(signInIntent, RC_SIGN_IN)
            ?: continuation.resume(AuthResult.Error("Context is not an Activity, cannot start sign-in flow."))
    }

    fun handleGoogleSignInResult(data: Intent?): AuthResult {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        return try {
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { firebaseTask ->
                        if (firebaseTask.isSuccessful) {
                            val user = firebaseTask.result?.user
                            signInContinuation?.invoke(
                                AuthResult.Success(user?.uid ?: "", user?.displayName, user?.email)
                            )
                        } else {
                            signInContinuation?.invoke(
                                AuthResult.Error(firebaseTask.exception?.message ?: "Firebase authentication failed.")
                            )
                        }
                    }
                    .addOnCanceledListener {
                        signInContinuation?.invoke(AuthResult.Cancelled)
                    }
            } else {
                signInContinuation?.invoke(AuthResult.Error("Google ID Token is null."))
            }
            AuthResult.Error("Sign-in process started, waiting for Firebase result.") // Это временный статус
        } catch (e: com.google.android.gms.common.api.ApiException) {
            val errorMessage = "Google sign in failed: ${e.statusCode} ${e.message}"
            signInContinuation?.invoke(AuthResult.Error(errorMessage))
            AuthResult.Error(errorMessage)
        } catch (e: Exception) {
            val errorMessage = "Unexpected error during Google sign in: ${e.message}"
            signInContinuation?.invoke(AuthResult.Error(errorMessage))
            AuthResult.Error(errorMessage)
        }
    }

    override suspend fun signOut(): Boolean {
        return try {
            auth.signOut()
            googleSignInClient.signOut().await()
            true
        } catch (e: Exception) {
            println("Android sign out error: ${e.message}")
            false
        }
    }

    companion object {
        const val RC_SIGN_IN = 9001
    }
}