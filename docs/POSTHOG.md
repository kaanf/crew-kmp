# PostHog product analytics plan

## Purpose and status

The first, intentionally small PostHog integration is implemented for Android/iOS.
Collection remains disabled by default; no project key is committed. The owner reported
`screen_viewed` and `app_opened` in the Top events view during setup. This is owner-reported
basic ingestion, not an independently verified payload audit or complete funnel/platform test. The SDK starts only with explicit build configuration.
The aim is to learn where users get stuck and which parts of the social match/task
experience they use, without collecting sensitive content or adding broad tracking.

Sentry remains the tool for crashes and API errors. PostHog is for aggregate product
behavior and funnels; its event counts should not be used to diagnose an individual
failure.

## First questions to answer

1. Do users reach the main screen after opening/signing in?
2. Which task categories are chosen, and where do users abandon starting a match?
3. Do users complete a match after starting one?
4. Which main areas (events, quests, profile) are visited?

## Initial event set

Use a small, explicit allowlist of events. Prefer tracking meaningful user actions
over every tap or every recomposition.

| Event | When to emit | Safe properties to consider |
|---|---|---|
| `app_opened` | App enters foreground (session-level deduplication) | `app_version`, `platform` |
| `screen_viewed` | User navigates to a meaningful destination | `screen_name` from a fixed allowlist, `entry_point` from a fixed allowlist |
| `task_selected` | User confirms/selects a task in a match flow | `task_category`, `selection_position` when meaningful |
| `match_started` | Match successfully starts, not when the start button is merely tapped | `task_category`, `match_mode` if these are non-identifying enums |
| `match_completed` | Match reaches its successful terminal state | `task_category`, coarse `duration_bucket` |
| `match_abandoned` | User explicitly exits an active/incomplete match | `task_category`, coarse `phase` |
| `quest_opened` | User opens the quest area | `entry_point` |

Keep property values bounded and non-identifying. Do not send task text, event names,
free-form strings, participant names, email, phone, photos, QR contents, access/session
tokens, precise location, request/response bodies, or Sentry data. Avoid adding a
property simply because it is available in UI state.

## UX behavior and click analysis

The first version should use the explicit events above and a few meaningful screen
views. It should not enable blanket click autocapture or session replay. Those features
can record more interface or user-entered content than needed, and are not required to
answer the initial questions. If a later UX investigation needs a particular interaction,
add a named event for that action and review its payload first.

## Identity, consent, and environment

- Start with PostHog's anonymous distinct ID; do not create identity from email/name.
- If authenticated-user analysis is needed, decide and document a pseudonymous stable
  user identifier and account merge behavior before implementing `identify`.
- Keep local/debug builds disabled by default or clearly separated from production
  analytics, so developer testing does not pollute product reports.
- Before production collection, confirm the app's privacy disclosure, applicable consent
  requirements, opt-out behavior, retention, project region/hosting, and PostHog free-tier
  limits. These are owner/product decisions; this plan does not assert legal compliance.
- Keep the PostHog project key in local/build configuration and CI secrets as appropriate;
  a public client key is not a server secret, but it should not be committed as a
  personal/local configuration value. Never put a server personal API key in the app.

## Implementation direction

Crew uses a shared analytics interface and one bounded event vocabulary in Kotlin.
Native hosts initialize the official Android/iOS SDKs and install a capture callback.
Koin supplies the shared implementation to ViewModels. Explicit state/action success
boundaries emit match events; navigation entry changes emit allowlisted screen views.
Analytics errors do not interrupt app behavior. Detailed configuration and transition
semantics are recorded below.

## First useful PostHog views

After events have arrived, create only these initial reports:

1. **Activation funnel:** `app_opened` → `screen_viewed` for the main screen →
   `match_started` → `task_selected`. Task selection follows the match start/RPS result
   in the current app; the original proposed order did not match implementation.
2. **Match completion funnel:** `match_started` → `match_completed`, with
   `match_abandoned` as a separate breakdown.
3. **Task choice trend:** `task_selected` by `task_category` over time.
4. **Area usage:** `screen_viewed` by allowlisted `screen_name`.

Validate event volume and property values with a development/test account before
interpreting these reports. Small samples are directional and do not establish why a
user behaved a certain way; pair quantitative findings with usability observation.

## Rollout and acceptance checks

1. Confirm PostHog project/region, current free-tier terms, consent/opt-out decision, and
   app key configuration.
2. Implement initialization, privacy defaults, and a small shared tracking interface.
3. Add the initial event set at verified state transitions; do not add autocapture or
   replay in this rollout.
4. Verify Android and iOS events in a development environment, check that properties
   contain only the allowlisted values, and confirm analytics outages do not affect flows.
5. Build the four reports above and compare them with a small manual test flow before
   enabling production collection.

## Out of scope for the first rollout

- Session replay, heatmaps, blanket autocapture, and arbitrary tap logging
- Experiments/feature flags, surveys, push attribution, and marketing attribution
- Backend event tracking or user/session correlation with Sentry
- Sending personal data or gameplay content to analytics
- Legal compliance or current cost guarantees; build checks below cover compilation
  and linkage, not production consent, billing or complete runtime behavior

## Implemented configuration and boundaries (2026-11-04)

- Android: official `posthog-android` 3.75.3; iOS: official `posthog-ios` 3.59.3
  through Swift Package Manager. Both are MIT licensed. The current KMP SDK's 0.5+
  integration requires Kotlin 2.4; this project stays on 2.3.20 using thin native
  adapters and a shared domain `Analytics` contract. See [ADR-002](decisions/002-posthog-native-adapters.md).
- Android build environment: `CREW_POSTHOG_KEY` (public `phc_` project token),
  `CREW_POSTHOG_HOST`, `CREW_POSTHOG_ENABLED=true`. Debug additionally requires
  `CREW_POSTHOG_DEBUG_ENABLED=true`; use a separate test project.
  Gitignored `local.properties` also supports `posthog.key`, `posthog.host`,
  `posthog.enabled` and `posthog.debug.enabled`; environment values take precedence.
  Explicit debug enablement enables the debug build independently of the release gate.
  Keep `posthog.enabled=false` for local testing without release collection.
- iOS: copy `iosApp/Configuration/PostHog.example.xcconfig` to the gitignored
  `PostHog.local.xcconfig` and set the same four settings. CI may supply them as
  Xcode build settings. Only the explicit EU/US HTTPS ingestion hosts are accepted;
  an unset/unsupported host disables initialization. No region is silently selected.
- Native SDK setup happens before shared UI starts. Replay, automatic screen/lifecycle
  capture, click capture, surveys, feature flag preloading and exception capture are
  disabled. Android deep-link and push capture/registration are also disabled.
- `beforeSend` drops events outside the seven-event vocabulary and strips other
  properties. Only screen/category, platform/version and SDK library/session metadata
  are retained; `$geoip_disable=true` disables server-side IP geolocation enrichment.
  The ingestion server still receives the connection's IP address.
- Anonymous SDK identity only; no `identify`, account/user IDs or person profiles.
  No analytics data is passed to Sentry. SDK queueing/networking belongs to native SDKs,
  independently of the authenticated Crew API client. SDK errors are best effort.
- `app_opened`: first foreground, then after at least 30 minutes in the background;
  short background trips and Android Activity recreation do not create new app sessions.
- `screen_viewed`: typed navigation entry changes for dashboard, profile, profile board,
  event detail, ticket QR, game, quests and passport. No route arguments are sent.
  Reopening an area counts again; recomposition does not. Quests also emits `quest_opened`.
- `task_selected`: a different valid selection in `WinnerPicks`, with only the fixed
  category enum. `match_started`: authoritative `MatchStarted` socket message.
  `match_completed`: successful task confirmation, terminal socket message or reconciled
  completed scoreboard with `completed=true`. Duplicate start/completion messages for
  the same most recent match are suppressed for the graph ViewModel lifetime. Match IDs
  stay in memory and are never sent. Process restart can recount a restored completion;
  historical/reconnected starts are not synthesized.
- `match_abandoned`: successful explicit cancellation of an active match, once for the
  most recently cancelled match within the graph lifetime. Backgrounding, process death,
  opponent exits and failed cancellations are not counted as explicit abandonment.
- Match events deliberately omit optional category/duration/phase properties until
  their semantics are needed; no arbitrary strings or task content can enter the API.

Keep production disabled until the owner resolves the disclosure, consent, opt-out,
retention and hosting decisions above. This change adds no consent/settings UI and does
not authorize production collection. Remote dashboards require access to the selected
PostHog project and are not provisioned by the app integration.

Sources checked: [KMP requirements](https://posthog.com/docs/libraries/kmp),
[Android SDK](https://posthog.com/docs/libraries/android),
[iOS SDK](https://posthog.com/docs/libraries/ios).

## Validation performed (2026-11-04)

- Passed: `./gradlew --console=plain :androidApp:compileDebugKotlin
  :composeApp:compileKotlinIosSimulatorArm64 :core:domain:iosSimulatorArm64Test
  :core:data:iosSimulatorArm64Test`. Domain: 34 tests; data: 37; zero failures/errors/skips.
  Includes session deduplication, bounded analytics properties and a failing/missing SDK.
- Passed again after final adapter changes: `./gradlew --console=plain :androidApp:compileDebugKotlin`.
- Passed native host build: `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp
  -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator'
  -derivedDataPath /tmp/crew-posthog-xcode CODE_SIGNING_ALLOWED=NO build`.
  This compiled Swift setup and linked PostHog into the ARM64 simulator host.
- Passed: `git diff --check`.
- Not run: physical-device flows, release archive/R8, live ingestion, remote reports and
  consent/opt-out UX verification. No production collection was enabled. Build success
  does not establish device lifecycle behavior or delivery to a PostHog project.

## Reading screen usage (documented 2026-11-06)

`screen_viewed` is one event name shared by all allowlisted destinations. Screen names
are carried in the custom `screen_name` event property (for example `dashboard` or
`profile`); automatic `$screen` tracking is disabled. The default URL/Screen field can
therefore be empty. Top events counts event names and does not rank individual screens.
For screen ranking, analyze `screen_viewed` with a `screen_name` breakdown. Other events,
such as `app_opened`, do not include `screen_name`.

For general match completion use `match_started` → `match_completed`. An additional
`task_selected` step covers the participant who picks the task, so it is not a symmetric
funnel for both players. Debug traffic and anonymous unique IDs are not production
account counts. Actual field delivery and completed report configuration still need
checking in the selected PostHog project.
