# Crew store screenshots

This is a separate, simulator/emulator-only application using Crew's production
Compose screens. It is not a flag in the shipping app. Both screenshot hosts use
`com.kaanf.crew.screenshots`, so they cannot read or overwrite Crew's installed
session or database. No authentication, API, socket, payment, push or analytics
startup is invoked.

## Build

From the repository root (JDK 21, Android SDK 36; Xcode on Apple Silicon and an
iOS 18.5+ Simulator runtime for iOS):

```sh
./gradlew -PcrewScreenshots=true :screenshots:android:assembleDebug
./screenshots/scripts/build-ios.sh
```

Android produces `screenshots/android/build/outputs/apk/debug/android-debug.apk`.
iOS produces `screenshots/build/ios/CrewScreenshots.app`. The iOS script compiles
an independent SwiftUI simulator host against `CrewScreenshots.framework` and
syncs transitive Compose resources with `syncComposeResourcesForIos`; it
does not change or run the production Xcode target, Firebase or entitlements.
There is no iOS device architecture and no Android release variant. Omitting
`-PcrewScreenshots=true` excludes both screenshot modules from Gradle entirely.

## Capture

Launch a scene using the commands below, wait for its entrance animations and
local images to settle, then capture it with the platform tools:

```sh
adb -s emulator-5554 exec-out screencap -p > screenshots/build/android-scene.png
xcrun simctl io booted screenshot screenshots/build/ios-scene.png
```

The output directory is created by the build steps above. Use a dedicated
simulator or emulator for these captures.

## Open a scene directly

```sh
adb -s emulator-5554 shell am start -W -a android.intent.action.VIEW \
  -d crew-screenshot://quest com.kaanf.crew.screenshots
xcrun simctl openurl booted crew-screenshot://quest
# iOS also accepts a cold launch argument:
xcrun simctl launch booted com.kaanf.crew.screenshots --scene quest
```

Additional development fixtures: `profile-board`, `match-board`, `board-editor`, `profile-board-loading`, and `profile-qr-loading`.

Routes, in store order: `dashboard`, `event-detail`, `rps`, `quest`, `leaderboard`,
`tonights-roll`. Unknown routes fail explicitly. Reopening even the same route
resets composition state, scroll position and the photo stack. The
`screenshot-ready-<scene>` semantic marker appears after all local images decode
and the longest entrance animation has settled. Image decode or unknown image errors fail
the run instead of silently capturing placeholders.

## Architecture and UI reuse

Production is split into `composeApp`, `androidApp`, `iosApp`, core domain/data/
presentation/designsystem/palette, and auth/home/game domain/data/presentation.
`commonMain` holds Compose screens, typed navigation graphs, Koin modules and
ViewModels exposing StateFlow. Platform source sets implement media, permissions,
network lifecycle, system bars and sharing. `App`/`MainViewModel` gate navigation
on `SessionStorage`, check updates and observe connectivity. `CrewApplication`
and Swift `iOSApp` initialize production Koin and push registration. The game
`EventConnectionClientImpl` owns the shared WebSocket, registered by
`gameDataModule`; the match ViewModel subscribes to it for the active event.

Production Android has debug/release plus baseline-profile variants; iOS has
Debug/Release configurations. Android flavors would not isolate iOS, which is why
this implementation uses two independent screenshot hosts and one shared fixture
module. Production source sets never depend on screenshot code or assets.

The screenshot Koin module registers immutable `ScreenshotFixtures`, not the
production DI modules. Existing presentation state classes and game domain models
feed the actual screen composables. Bypassing ViewModels is intentional: dashboard
uses current time, match owns timers/socket state, roots request notification
permission or navigate automatically. Faking those subsystems would add unnecessary
interfaces and still leave nondeterministic lifecycle behavior.

| Scene | Reused implementation | Fixture choice |
|---|---|---|
| Dashboard | DashboardScreen, AppScaffold, AppTopBar | Featured ticketed event and upcoming event |
| Event detail | EventDetailScreen, AppTopBar, EventShareButton | Complete ticketed event, eight guests, venue, cover, ticket CTA |
| RPS | MatchContainerScreen → RpsReadyPhase | Two initialized players, ready action, no waiting/countdown |
| Quest | MatchContainerScreen → LoserActiveTaskPhase | Icebreaker task, points, opponent and reject action |
| Leaderboard | MatchContainerScreen + LeaderboardContent | Eight ranked players, current user fourth, confetti complete |
| Tonight's Roll | MatchContainerScreen + MemoriesStackScreen | Four local illustrated memories and task caption |

RPS is played physically: there are no in-app animated hand selection mechanics
to fabricate. Quest here means an active social challenge, not the separate
achievement checklist (`QuestsScreen`). Tonight's Roll is the existing photo stack
within the leaderboard container, not a new navigation destination in production.
Dashboard exposes capacity percentage rather than a numeric attendance count;
the event guest list is populated where the UI supports it. Some event information
is below the fold, as in production; capture does not shrink or redesign the UI.

Only two production UI seams were changed: MatchContainerScreen accepts content
slots whose defaults remain its existing ViewModel-backed tabs, and the existing
EventShareButton is public for reuse. Screenshot actions are inert; the app is a
capture harness, not an offline game implementation. Native gestures such as roll
swiping remain available, and a direct scene launch resets them.

Fixture state can be reused by future Compose visual tests without introducing
Roborazzi/Paparazzi now. See ASSETS.md for artwork provenance.

## Check repeatability and isolation

Capture the same scenes twice on the same platform and runtime, then compare
folders containing `<scene>.png` files:

```sh
node screenshots/scripts/compare-captures.cjs <baseline-dir> <actual-dir>
python3 screenshots/scripts/verify-isolation.py androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

The comparison script requires `@napi-rs/canvas` to be available to Node. It fails
on changed pixels or missing scenes. The optional `--ignore-top=<pixels>` argument
excludes system chrome explicitly. Keep device settings and rendering backend
consistent between captures.

The isolation check inspects the merged APK manifest and the production APK's DEX
and resources. Build the production APK without `-PcrewScreenshots=true` first.
