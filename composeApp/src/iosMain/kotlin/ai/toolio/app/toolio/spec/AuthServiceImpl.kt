package ai.toolio.app.toolio.spec

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import GoogleSignIn
import GoogleSignIn.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.Foundation.NSURL
import platform.Foundation.NSError

// Используем StateFlow для связи с UI
class IOSAuthService : AuthService {

    private val auth = FirebaseAuth.auth()

    // Нужно для инициализации GIDSignIn.sharedInstance
    init {
        // GIDSignIn.sharedInstance.scopes = listOf("profile", "email") // Если нужны дополнительные скоупы
    }

    override suspend fun signInWithGoogle(): AuthResult = suspendCancellableCoroutine { continuation ->
        val signIn = GIDSignIn.sharedInstance()
        if (signIn == null) {
            continuation.resume(AuthResult.Error("GIDSignIn.sharedInstance is null."))
            return@suspendCancellableCoroutine
        }

        // Необходимо передать UIViewController, чтобы Google Sign-In мог показать свой UI
        val presentingViewController: UIViewController? = UIApplication.sharedApplication.keyWindow?.rootViewController

        if (presentingViewController == null) {
            continuation.resume(AuthResult.Error("Could not find a presenting UIViewController."))
            return@suspendCancellableCoroutine
        }

        signIn.signInWithPresentingViewController(presentingViewController) { user, error ->
            if (error != null) {
                // Если user.isCancelled
                if (error.localizedDescription?.contains("cancelled") == true) {
                    continuation.resume(AuthResult.Cancelled)
                } else {
                    continuation.resume(AuthResult.Error(error.localizedDescription ?: "Unknown Google sign-in error."))
                }
            } else if (user != null) {
                val authentication = user.authentication
                val idToken = authentication.idToken
                if (idToken != null) {
                    val credential = GoogleAuthProvider.credentialWithIDToken(idToken, null)
                    auth.signInWithCredential(credential) { authResult, firebaseError ->
                        if (firebaseError != null) {
                            continuation.resume(AuthResult.Error(firebaseError.localizedDescription ?: "Firebase authentication failed."))
                        } else if (authResult != null) {
                            val currentUser = authResult.user
                            continuation.resume(AuthResult.Success(currentUser?.uid ?: "", currentUser?.displayName, currentUser?.email))
                        } else {
                            continuation.resume(AuthResult.Error("Firebase sign-in failed with no user or error."))
                        }
                    }
                } else {
                    continuation.resume(AuthResult.Error("Google ID Token is null after successful sign-in."))
                }
            } else {
                continuation.resume(AuthResult.Error("Google sign-in user is null."))
            }
        }
    }

    // Необходимо для AppDelegate
    fun handleURL(url: NSURL): Boolean {
        return GIDSignIn.sharedInstance().handleURL(url)
    }
}

// Фабрика для удобного создания сервиса (если нужно)
actual fun getAuthService(): AuthService = IOSAuthService()