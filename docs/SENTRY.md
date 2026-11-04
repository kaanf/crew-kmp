# Sentry and API request correlation

Scope: native Android/iOS crash reporting, explicit API 5xx reports and an opaque
`X-Request-ID` shared with the Spring backend. PostHog and distributed tracing are
not part of this change. SDKs: Android/JVM 8.62.0, Cocoa 9.30.2.

## Configuration

Create mobile and backend projects in the same Sentry organization. No DSN means
reporting is disabled; networking and request IDs still work. Do not commit actual
configuration or embed an auth token in a mobile binary.

- Android: provide `CREW_SENTRY_MOBILE_DSN` in the build environment. It becomes an
  app BuildConfig value. Rebuild after changing it.
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
