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
}