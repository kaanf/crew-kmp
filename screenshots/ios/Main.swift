import SwiftUI
import CrewScreenshots

@main
struct CrewScreenshotApp: App {
    init() {
        UserDefaults.standard.set(["en-US"], forKey: "AppleLanguages")
        UserDefaults.standard.set("en_US", forKey: "AppleLocale")
        NSTimeZone.default = TimeZone(secondsFromGMT: 0)!
        if let index = ProcessInfo.processInfo.arguments.firstIndex(of: "--scene"),
           ProcessInfo.processInfo.arguments.indices.contains(index + 1) {
            ScreenshotRouter.shared.open(uri: "crew-screenshot://" + ProcessInfo.processInfo.arguments[index + 1])
        }
    }
    var body: some Scene {
        WindowGroup {
            ScreenshotView().ignoresSafeArea().preferredColorScheme(.dark)
                .environment(\.locale, Locale(identifier: "en_US"))
                .environment(\.dynamicTypeSize, .medium)
                .onOpenURL { ScreenshotRouter.shared.open(uri: $0.absoluteString) }
        }
    }
}
struct ScreenshotView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        ScreenshotViewControllerKt.ScreenshotViewController()
    }
    func updateUIViewController(_ controller: UIViewController, context: Context) {}
}
