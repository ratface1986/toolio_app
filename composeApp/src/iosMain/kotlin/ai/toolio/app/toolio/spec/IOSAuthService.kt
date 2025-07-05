package ai.toolio.app.toolio.spec

import ai.toolio.app.di.AuthResult
import ai.toolio.app.di.AuthService
import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRGoogleAuthProvider
import cocoapods.GoogleSignIn.GIDConfiguration
import cocoapods.GoogleSignIn.GIDSignIn
import cocoapods.GoogleSignIn.GIDSignInResult
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import kotlin.coroutines.resume


@OptIn(ExperimentalForeignApi::class)
class IOSAuthService : AuthService {
    override suspend fun signInWithGoogle(): AuthResult = suspendCancellableCoroutine { cont ->

        val clientID = "592143760637-rufhh596bsfat5vtih6psbhr463pk7ts.apps.googleusercontent.com"
        GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID)

        val presentingVC = getTopViewController() ?: run {
            cont.resume(AuthResult.Error("Cannot find top UIViewController"))
            return@suspendCancellableCoroutine
        }

        GIDSignIn.sharedInstance.signInWithPresentingViewController(
            presentingViewController = presentingVC,
            hint = null,
            completion = { result: GIDSignInResult?, error: NSError? ->
                if (error != null || result == null) {
                    cont.resume(AuthResult.Error("Google Sign-In failed: ${error?.localizedDescription}"))
                    return@signInWithPresentingViewController
                }

                val idToken = result.user.idToken?.tokenString
                val accessToken = result.user.accessToken?.tokenString

                if (idToken == null || accessToken == null) {
                    cont.resume(AuthResult.Error("Missing tokens from Google"))
                    return@signInWithPresentingViewController
                }

                //@Suppress("UNCHECKED_CAST")
                val credential = FIRGoogleAuthProvider.credentialWithIDToken(
                    idToken = idToken,
                    accessToken = accessToken
                )

                FIRAuth.auth().signInWithCredential(credential) { authResult, firebaseError ->
                    if (firebaseError != null || authResult == null) {
                        cont.resume(AuthResult.Error("Firebase sign-in failed: ${firebaseError?.localizedDescription}"))
                    } else {
                        val user = authResult.user()
                        cont.resume(
                            AuthResult.Success(
                                userId = user.uid(),
                                displayName = user.displayName(),
                                email = user.email()
                            )
                        )
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun signOut(): Boolean {
        return try {
            FIRAuth.auth().signOut(null)
            GIDSignIn.sharedInstance.signOut()
            true
        } catch (e: Throwable) {
            println("iOS sign out error: ${e.message}")
            false
        }
    }
}


fun getTopViewController(): UIViewController? {
    val windowScene = UIApplication.sharedApplication.connectedScenes
        .firstOrNull { it is UIWindowScene } as? UIWindowScene

    val keyWindow = windowScene?.windows?.firstOrNull { (it as? UIWindow)?.isKeyWindow() == true } as? UIWindow

    var topController = keyWindow?.rootViewController

    while (topController?.presentedViewController != null) {
        topController = topController.presentedViewController
    }

    return topController
}



