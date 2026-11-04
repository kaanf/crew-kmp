# Sentry and API request correlation

Scope: native Android/iOS crash reporting, explicit API 5xx reports and an opaque
`X-Request-ID` shared with the Spring backend. PostHog and distributed tracing are
not part of this change. SDKs: Android/JVM 8.62.0, Cocoa 9.30.2.

## Configuration

Create mobile and backend projects in the same Sentry organization. No DSN means
reporting is disabled; networking and request IDs still work. Do not commit actual
configuration or embed an auth token in a mobile binary.

- Android: provide `CREW_SENTRY_MOBILE_DSN` in the build environment. It becomes an
  app BuildConfig value. For local builds, the ignored root `local.properties` can
  instead contain `sentry.mobile.dsn`; the environment variable takes precedence.
  Rebuild after changing it.
- iOS: copy `iosApp/Configuration/Sentry.example.xcconfig` to the ignored
  `Sentry.local.xcconfig` in the same directory and set `CREW_SENTRY_MOBILE_DSN`.
  Config.xcconfig includes it optionally. Use the example's `https:/$()/` escaping
  because `//` starts an xcconfig comment. CI can instead pass the Xcode build setting.
  Info.plist expands it into CrewSentryDSN. A shell environment value alone is not
  an Xcode build setting.
- Backend: runtime `SENTRY_DSN`, `SENTRY_ENVIRONMENT` and `SENTRY_RELEASE`.
  Environment defaults to development; release defaults to crew-api@local. Set both
  explicitly in production, with a release containing the deployed revision.

Mobile environment is development in Debug and production in Release. Both platforms
use `crew-mobile@<version>+<build>` as release. Keep development data out of production
views using the environment filter.

## Correlation

The shared Ktor plugin creates a fresh UUID for Crew HTTPS API calls, never for
external upload/media hosts. The backend accepts only UUID-shaped supplied IDs,
otherwise generates a new one, and returns it in X-Request-ID. It stores the ID in
request attributes and request-scoped logging MDC, restoring MDC after the call.

Mobile stores safe HTTP breadcrumbs and emits one warning per observed 5xx response.
Its request_id tag matches the backend exception's request_id tag. Search that tag in
both Sentry projects. These are separate events; this does not create a distributed
trace or an automatic cross-project link. Backend logs also include the ID.

Backend uses the core JVM SDK with explicit capture in the storage and unexpected-exception
handler. This avoids a Spring starter automatically reporting handled domain errors
or attaching request bodies. Scheduled/job/WebSocket handled failures are not newly
instrumented. Requests that never reach the server cannot have a matching server event.

## Data and quotas

No HTTP bodies, headers, full URLs, queries, QR payloads or user identities are added.
Request/user/extra fields and exception messages are removed before sending; exception
types and stack traces remain. Automatic breadcrumbs are dropped; only the allowlisted
crew.http breadcrumbs are retained. Resource names are allowlisted; path IDs are omitted.
Replay, screenshots, view hierarchy, session tracking and trace sampling are disabled.
Sentry issue/event counts are different from PostHog product analytics counts.

## Release and verification

DSN delivery and Sentry dashboard ingestion require owner configuration. In a test
environment, induce a controlled API 500 and confirm matching request_id tags on both
projects; test native crashes on Android and iOS separately after restart. Never add
an always-on production crash button. Verify the final event payload in Sentry.

Release symbolication is a remaining deployment prerequisite: Android minified builds
need their exact R8 mapping registered with Sentry; iOS archives need matching dSYMs.
The CLI/upload credentials and release pipeline are not configured here. Until those
are uploaded and device crashes checked, source-level release stack traces are not
verified. Native iOS reporting alone does not guarantee rich Kotlin exception details.

## Implementation checks (2026-11-04)

Passed: Android debug Kotlin compilation; shared iOS simulator compilation and debug
framework link; complete unsigned Debug iOS simulator Xcode build; 33 core:data iOS
simulator tests (including request IDs, cross-host redirects and callback isolation);
4 focused backend tests (MDC cleanup, input validation, redaction and capture policy).
Physical-device crashes, live Sentry ingestion and release symbolication were not tested.

## Controlled local verification

The full app keeps its normal API address. A separate, unauthenticated verification
client uses only a local HTTP origin (localhost, 127.0.0.1, or Android emulator host
10.0.2.2); redirects are disabled. This avoids sending account tokens to a test server.
The debug binary flag is checked before creating the client. Its request still runs
through the same request-ID and native Sentry callbacks as normal API traffic.

In the sibling crew-api checkout run `./gradlew sentryVerificationServer`. This task
loads only SENTRY_DSN from its ignored .env (or the process environment), uses the
development Sentry environment, and starts an isolated Spring MVC server on
127.0.0.1:18096. It imports the real exception handler, request-ID filter and Sentry
reporter, but no database, Redis, auth/session or other application services.
The synthetic controller is in test sources outside the app component scan and is
excluded from production bootJar. Even in test sources it requires the explicit
sentry-verification profile and rejects the prod profile.

Android emulator:

1. Build `./gradlew :androidApp:assembleDebug`, then install the debug APK on the test
   emulator with `adb -s emulator-5554 install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk`.
2. Open `adb -s emulator-5554 shell am start -n com.kaanf.crew/.androidapp.SentryVerificationActivity`.
3. Tap **Send Sentry verification** once. The screen displays the returned request_id.
   Search that value in both Sentry projects with environment=development.
4. For USB Android testing use `adb -s <device> reverse tcp:18096 tcp:18096` and launch
   the activity with `--es base_url http://127.0.0.1:18096` instead.
   Add `-S --ez send_verification true` to explicitly send one request at launch without
   touching the screen. Ordinary launches and restored activities never auto-send.
   The debug log tag CrewSentryVerification prints only the synthetic request ID.

The Android activity and loopback-only cleartext network config exist only in debug
sources. The button requires an enabled Sentry SDK. SDK delivery diagnostics are enabled
only while sending/flushing this synthetic request and the previous setting is restored.

For an installed Debug iOS simulator app use
`xcrun simctl openurl booted crew://debug/sentry`. The simulator uses 127.0.0.1:18096;
its console prints the request_id. Debug Info.plist preprocessing enables only loopback
HTTP; Release strips the exception and the debug URL action returns false. This iOS
entry is intended for the simulator, not a physical iPhone connecting over Wi-Fi.
See Apple's [local networking documentation](https://developer.apple.com/documentation/bundleresources/information-property-list/nsapptransportsecurity/nsallowslocalnetworking).

A received HTTP 500 and matching IDs prove correlation, not remote dashboard ingestion.
Check SDK transport success and the Sentry dashboard separately. Stop the local server
with Ctrl-C when finished. This test does not intentionally crash either native app.

### Verification evidence (2026-11-04)

Passed Android debug APK build, full Debug iOS simulator Xcode build, 35 core:data
simulator tests, six focused backend tests, production bootJar exclusion of verification
classes, and Debug/Release Info.plist preprocessing checks. The built Debug plist contains
the loopback exception; the Release preprocessed plist does not.

Physical check: Galaxy S24+ (SM-S926B), Android 16. Installed debug update without clearing
app data, used adb reverse to loopback, and invoked the explicit test action. The device
received HTTP 500; mobile and backend recorded the same request_id; both Sentry SDK
transports logged “Envelope sent successfully.” The Sentry dashboard UI was not accessed.
The debug activity was checked on-device and respects system-bar insets. The temporary
server and ADB port mapping were removed after testing. No native crash was induced;
iOS runtime/physical-device delivery and release symbolication remain unverified.

### Follow-up: mobile ingestion remains blocked

A subsequent missing-event investigation on 2026-11-04 sent a synthetic JVM control
using the configured mobile DSN. Sentry returned HTTP 403 with
`event submission rejected with_reason: ProjectId`. Thus the earlier mobile transport
success log is not proof of dashboard ingestion. The current mobile DSN/project-key
association must be rechecked in Sentry Client Keys before end-to-end ingestion can
be marked verified. Backend-project ingestion was observed in the dashboard by the owner.
