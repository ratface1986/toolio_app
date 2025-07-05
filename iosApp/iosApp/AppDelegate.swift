//
// Created by Rustem Melnichenko on 7/2/25.
//

import Foundation
import UIKit
import FirebaseCore
import FirebaseAnalytics
import GoogleSignIn

class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        Analytics.logEvent("app_launched", parameters: ["environment": "development"])
        return true
    }

    func application(_ app: UIApplication,
                     open url: URL,
                     options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {

        return GIDSignIn.sharedInstance.handle(url)
    }
}