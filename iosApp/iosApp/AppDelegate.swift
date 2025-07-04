//
// Created by Rustem Melnichenko on 7/2/25.
//

import Foundation
import UIKit
import FirebaseCore
import FirebaseAnalytics

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // Firebase
        print("MYDATA DELEGATE Launched")
        FirebaseApp.configure()
        Analytics.logEvent("app_launched", parameters: ["environment": "development"])

        return true
    }

    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        var handled: Bool

        // Обработка URL для GoogleSignIn
        handled = GIDSignIn.sharedInstance.handle(url) // Если у тебя старый GIDSignIn.sharedInstance().handleURL(url)

        if handled {
            return true
        }

        // Your other URL handling goes here.
        return false
    }

}