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

The config targets **Goldie 0.5.0**, which bundles **Argent 0.27.0**. A standalone
Argent executable is not required. Install that pinned Goldie version if needed:

```sh
npm install -g goldie@0.5.0
```

Install an iOS Simulator runtime in Xcode Settings → Components and create an
**iPhone 17 Pro Max** simulator. Goldie requires that exact model for its
`iphone-6.9` output. Android needs a **Pixel 9 Pro or Pixel 10 Pro** AVD; Goldie
supports both hardware profiles under `pixel-10-pro`. The local validation AVD
is named `CrewScreenshots`, running API 34 on ARM64.

```sh
# Start the dedicated AVD first; use its serial, never a personal device.
$ANDROID_HOME/emulator/emulator -avd CrewScreenshots -no-snapshot -timezone UTC -gpu swiftshader
./screenshots/scripts/prepare-android.sh emulator-5554

goldie doctor
goldie all --config goldie.android.config.ts
goldie all --device iphone-6.9
goldie studio --no-open
```

`goldie all` uses the iOS config; the Android config selects Android-specific
flows. Run both commands above to generate both platforms. This split is
intentional: Argent 0.27.0's native wait works on Android Compose, while its raw
accessibility wait tools work on iOS Compose. `goldie all` runs doctor first.
Goldie writes `out/raw/`, framed assets, and its store
manifest under `out/`; these are ignored by Git. No preview video is configured:
this task captures six stills. A watermark warning concerns video only; it can be
disabled through the bundled Argent CLI (`argent disable video-watermark`).

The software GPU flag avoids a Metal command-encoder assertion encountered on
this Mac with the hardware renderer. It changes emulator rendering, not app UI.
Use the same GPU backend for subsequent visual comparisons.
Android marketing images use the bundled original neutral SVG frame with explicit
Goldie geometry; its small screen radius preserves the emulator's status-bar clock.
Goldie's default Pixel bezel clipped that clock on this runtime. iOS uses Goldie's
silver iPhone frame. No application pixels are retouched.

Device contract: portrait, English (US), UTC, font scale 1, dark system appearance
with Crew's existing `CrewTheme(isDarkTheme = false)` (its actual warm dark palette),
Android 1280×2856 at 480 dpi, iPhone 17 Pro Max native resolution. Goldie pins status
bar time to 9:41, battery and connectivity. System safe areas and navigation bars
are preserved. Compose entrance animations finish normally, the leaderboard is
captured after its confetti, and no timed match or auto-navigation ViewModel runs.
Fixtures contain fixed dates and already formatted date labels, never wall time.

## Open a scene directly

```sh
adb -s emulator-5554 shell am start -W -a android.intent.action.VIEW \
  -d crew-screenshot://quest com.kaanf.crew.screenshots
xcrun simctl openurl booted crew-screenshot://quest
# iOS also accepts a cold launch argument:
xcrun simctl launch booted com.kaanf.crew.screenshots --scene quest
```

Routes, in store order: `dashboard`, `event-detail`, `rps`, `quest`, `leaderboard`,
`tonights-roll`. Unknown routes fail explicitly. Reopening even the same route
resets composition state, scroll position and the photo stack. Every Argent flow
launches the screenshot app and opens its URL. iOS uses `await-ui-element` for
`screenshot-ready-<scene>`, followed by `await-screen-idle`. Android uses native
`await: visible` and `await: idle`. Both wait for the scene marker and tree
stability; the iOS native tree omits Compose semantics, and Android's raw wait
tools proved unreliable in this installed Argent version. The marker appears only after all local images decode and the
longest entrance animation has settled. Image decode or unknown image errors fail
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
Roborazzi/Paparazzi now. See ASSETS.md for artwork provenance and VALIDATION.md
for executed checks and limitations.

## Check repeatability and isolation

After a first capture, copy `out/raw/<device>` to a baseline directory outside
`out/raw`. Run the same capture again on the same device/runtime, then compare
decoded pixels (using Goldie's installed canvas dependency):

```sh
NODE_PATH="$(npm root -g)/goldie/node_modules" node screenshots/scripts/compare-captures.cjs \
  screenshots/build/baseline/iphone-6.9 out/raw/iphone-6.9
python3 screenshots/scripts/verify-isolation.py androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

The comparison fails on any changed pixel or missing scene. Compare within one
platform/runtime; Android and iOS have different text rasterization and safe areas.
On iOS 27.0, Goldie's overridden network glyph varies between captures. Full-image
comparison reports that difference; explicitly adding `--ignore-top=140` compares
only the app below the system bar. All six iOS app regions matched exactly across
two independently captured runs. See VALIDATION.md for measured limitations.
All six Android raw images matched exactly, including system chrome, across two
independent software-GPU captures.
The isolation check inspects the merged APK manifest and the production APK's DEX
and resources. Build the production APK without `-PcrewScreenshots=true` first.
