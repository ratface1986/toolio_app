//
// Created by Rustem Melnichenko on 7/4/25.
//

import FirebaseAuth
import GoogleSignIn

func signInWithGoogle(presentingVC: UIViewController, onResult: @escaping (Result<User, Error>) -> Void) {
    guard let clientID = FirebaseApp.app()?.options.clientID else {
        onResult(.failure(NSError(domain: "NoClientID", code: -1)))
        return
    }

    let config = GIDConfiguration(clientID: clientID)

    GIDSignIn.sharedInstance.signIn(with: config, presenting: presentingVC) { user, error in
        if let error = error {
            onResult(.failure(error))
            return
        }

        guard
            let authentication = user?.authentication,
            let idToken = authentication.idToken,
            let accessToken = authentication.accessToken
        else {
            onResult(.failure(NSError(domain: "GoogleSignIn", code: -1)))
            return
        }

        let credential = GoogleAuthProvider.credential(withIDToken: idToken, accessToken: accessToken)

        Auth.auth().signIn(with: credential) { result, error in
            if let error = error {
                onResult(.failure(error))
            } else if let user = result?.user {
                onResult(.success(user))
            }
        }
    }
}

