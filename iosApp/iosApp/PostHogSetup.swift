import Foundation
import PostHog
import ComposeApp

func initializePostHog() {
    let info = Bundle.main.infoDictionary ?? [:]
    guard info["CrewPostHogEnabled"] as? String == "true" else { return }
    #if DEBUG
    guard info["CrewPostHogDebugEnabled"] as? String == "true" else { return }
    #endif
    guard let key = info["CrewPostHogKey"] as? String, key.hasPrefix("phc_"),
          let host = info["CrewPostHogHost"] as? String,
          ["https://eu.i.posthog.com", "https://us.i.posthog.com"].contains(host) else { return }
    let config = PostHogConfig(projectToken: key, host: host)
    config.captureApplicationLifecycleEvents = false
    config.captureScreenViews = false
    config.captureElementInteractions = false
    config.enableSwizzling = false
    config.sessionReplay = false
    config.surveys = false
    config.preloadFeatureFlags = false
    config.sendFeatureFlagEvent = false
    config.setDefaultPersonProperties = false
    config.personProfiles = .never
    config.errorTrackingConfig.autoCapture = false
    let names: Set<String> = ["app_opened", "screen_viewed", "task_selected", "match_started",
                              "match_completed", "match_abandoned", "quest_opened"]
    let properties: Set<String> = ["screen_name", "task_category", "platform", "app_version",
                                   "$session_id", "$lib", "$lib_version", "$process_person_profile"]
    config.setBeforeSend { event in
        guard names.contains(event.event) else { return nil }
        event.properties = event.properties.filter { properties.contains($0.key) }
        event.properties["$geoip_disable"] = true
        return event
    }
    PostHogSDK.shared.setup(config)
    let version = info["CFBundleShortVersionString"] as? String ?? "unknown"
    AnalyticsBridgeKt.configureProductAnalytics { name, properties in
        var values: [String: Any] = properties
        values["platform"] = "ios"
        values["app_version"] = version
        PostHogSDK.shared.capture(name, properties: values)
    }
}
