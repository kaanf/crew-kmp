import SwiftUI
import ComposeApp
import Foundation
import FirebaseCore
import FirebaseMessaging
import UserNotifications

class AppDelegate: NSObject, UIApplicationDelegate, MessagingDelegate, UNUserNotificationCenterDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        Messaging.messaging().delegate = self
        UNUserNotificationCenter.current().delegate = self
        // Bildirim izni burada istenmiyor: kullanicinin tek reddetme hakki var, onu acilista
        // harcamamak icin dashboard'da (login + profil fotografi sonrasi) isteniyor.
        // registerForRemoteNotifications izinden bagimsiz calisir; APNs token'i FCM icin gerekli.
        application.registerForRemoteNotifications()
        return true
    }

    // Apple'ın verdiği cihaz adresini Firebase'e tanıt; FCM token'ı bunun üzerine üretilir.
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }

    // FCM token'ı hazır olduğunda (ve her yenilendiğinde) paylaşılan koda ilet.
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        if let fcmToken {
            PushTokenBridge.shared.onNewToken(newToken: fcmToken)
        }
    }

    // Uygulama önplandayken de bildirimi banner olarak göster.
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound])
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    init() {
        KoinHelperKt.doInitKoin()
        PushTokenBridge.shared.start()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    guard url.scheme != "http", url.scheme != "https" else { return }
                    ExternalUriHandler.shared.onNewUri(uri: url.absoluteString)
                }
                .onContinueUserActivity(NSUserActivityTypeBrowsingWeb) { userActivity in
                    guard let url = userActivity.webpageURL else { return }
                    ExternalUriHandler.shared.onNewUri(uri: url.absoluteString)
                }
        }
    }
}
