import Foundation
import Sentry
import ComposeApp

func initializeSentry() {
    guard let dsn = Bundle.main.object(forInfoDictionaryKey: "CrewSentryDSN") as? String,
          dsn.hasPrefix("https://") else { return }

    SentrySDK.start { options in
        options.dsn = dsn
        #if DEBUG
        options.environment = "development"
        #else
        options.environment = "production"
        #endif
        let version = Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String ?? "unknown"
        let build = Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") as? String ?? "unknown"
        options.releaseName = "crew-mobile@\(version)+\(build)"
        options.sendDefaultPii = false
        options.tracesSampleRate = 0
        options.enableAutoSessionTracking = false
        options.enableNetworkTracking = false
        options.enableCaptureFailedRequests = false
        options.attachScreenshot = false
        options.attachViewHierarchy = false
        options.sessionReplay.sessionSampleRate = 0
        options.sessionReplay.onErrorSampleRate = 0
        options.beforeBreadcrumb = { crumb in
            crumb.category == "crew.http" ? crumb : nil
        }
        options.beforeSend = { event in
            event.request = nil
            event.user = nil
            event.serverName = nil
            event.extra = nil
            for exception in event.exceptions ?? [] { exception.value = "[redacted]" }
            if event.tags?["crew.safe_message"] != "true" { event.message = nil }
            return event
        }
    }

    HttpDiagnosticsBridgeKt.configureHttpDiagnostics { requestId, operation, status in
        let code = status.intValue
        let crumb = Breadcrumb(level: .info, category: "crew.http")
        crumb.data = ["request_id": requestId, "operation": operation, "status": code]
        SentrySDK.addBreadcrumb(crumb)
        if code >= 500 {
            SentrySDK.capture(message: "Crew API request failed") { scope in
                scope.setLevel(.warning)
                scope.setTag(value: requestId, key: "request_id")
                scope.setTag(value: operation, key: "operation")
                scope.setTag(value: String(code), key: "http.status_code")
                scope.setTag(value: "true", key: "crew.safe_message")
            }
        }
    }
}

/// Debug URL: crew://debug/sentry. Always uses the simulator's loopback server.
func handleSentryVerification(_ url: URL) -> Bool {
    #if DEBUG
    guard url.scheme == "crew", url.host == "debug", url.path == "/sentry" else { return false }
    guard SentrySDK.isEnabled else {
        print("Sentry verification disabled: configure the mobile DSN first")
        return true
    }
    HttpDiagnosticsBridgeKt.verifyLocalSentry(baseUrl: "http://127.0.0.1:18096") { requestId, error in
        if let requestId {
            print("Sentry verification HTTP 500; request_id=\(requestId)")
        } else {
            print("Sentry verification failed; check the local server")
        }
    }
    return true
    #else
    return false
    #endif
}
