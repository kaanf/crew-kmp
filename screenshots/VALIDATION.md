# Screenshot validation — 23 October 2026

This workspace already contained the separate screenshot hosts, fixture data,
artwork, scene router and Goldie configuration. This pass inspected and retained
that implementation and repaired the capture/build issues found by running it.
Unrelated existing application changes were left in place.

## Architecture and isolation

- Production uses typed Navigation Compose graphs and Koin ViewModels with
  StateFlow. Dashboard maps events using `Clock.System.now()`; match ViewModels
  observe `EventConnectionClient` and own timed transitions. Screenshot hosts
  render their existing state-driven screen functions directly.
- `screenshotModule` contains only the immutable presentation fixture provider.
  Production Koin modules, session storage, repositories, push startup and socket
  clients are never started. Bundled `fixture://` images use a fail-closed Coil
  interceptor and are decoded before the ready marker is exposed.
- The screenshot modules are included only with `-PcrewScreenshots=true`.
  Android release variants are disabled. iOS has only the simulator ARM64 target
  and an independent SwiftUI host with a separate bundle identifier.
- RPS uses the real physical-play ready phase. Quest uses an active icebreaker.
  Tonight's Roll uses the existing leaderboard photo stack, with deterministic
  scatter and completed entrance animations.

## Executed checks

| Check | Result |
|---|---|
| `:screenshots:android:assembleDebug` with explicit screenshot property | Passed |
| `:androidApp:assembleDebug` without screenshot property | Passed |
| APK isolation script on screenshot and production APKs | Passed: no screenshot Internet/camera permission or production startup; no screenshot routes, classes, IDs or resources in production APK |
| iOS simulator framework link and Swift host build | Passed after resource sync and plist fixes |
| Goldie 0.5.0 / bundled Argent 0.27.0 schema inspection | Verified against installed source and CLI help |
| `goldie doctor` | Passed with debug-APK advisory |
| `goldie all --device iphone-6.9` | Passed: all six raw scenes and all six framed 1320×2868 PNGs verified |
| Second independent iOS capture | Passed: all six scenes captured, framed and verified again |
| iOS repeatability, app content below top 140 system pixels | Passed: zero changed pixels in every scene |
| iOS repeatability, entire image | Not exact: 2,170–5,189 changed pixels per scene, confined to the network glyph within x=1007–1096, y=64–131 |
| Android native RPS Argent flow | Passed: direct launch, ready marker and idle check |
| `goldie all --config goldie.android.config.ts` | Passed twice on software GPU: all six raw 1280×2856 scenes and framed 1080×1920 PNGs |
| Android repeatability, entire image | Passed: zero changed pixels in every scene across two independent captures |
| Final Android custom-frame render and `goldie verify` | Passed: all six 1080×1920 PNGs; clock clipping resolved |
| `goldie studio --no-open` | Started at localhost:4321; HTTP 200 |
| Shell/JavaScript/Python syntax and `git diff --check` | Passed |

The initial iOS build was missing transitive Compose resources and the mandatory
`CADisableMinimumFrameDurationOnPhone` plist key. Both were fixed. The host now
targets iOS 18.5+, matching the linked native library's deployment requirement.
Argent's shorthand `await` steps read a native tree that misses iOS Compose
semantics; flows now use its supported accessibility tools `await-ui-element`
and `await-screen-idle`. Android uses separate `android-store-*` native-wait
flows through `goldie.android.config.ts`; its raw wait tool timed out despite
the marker being present. Cold Android setup waits for boot completion before
changing settings. Scene waits allow 60 seconds for cold instrumentation.
The Mac's hardware emulator renderer subsequently crashed with a Metal
command-encoder assertion; the dedicated AVD was restarted with `-gpu swiftshader`.

## Visual review and limits

All six raw captures on both platforms were inspected. Artwork, avatars, challenge text,
leaderboard highlight, ticket CTA and roll caption render without loading states
or missing resources. Event detail includes its venue and eight guests; additional
content continues below the fold as in production. RPS intentionally retains its
simple physical-play layout. The roll uses original fictional illustrations,
not real user photos. Framed iOS RPS and Android quest marketing images were also
inspected. Goldie's bundled Pixel bezel clipped the emulator clock; the Android
config now uses an original neutral SVG frame with explicit geometry, preserving
the entire app screenshot and system bar. This changes only marketing composition.

Validation uses an iPhone 17 Pro Max on the installed iOS 27.0 runtime and a
dedicated Pixel 9 Pro profile under Goldie's `pixel-10-pro` key, API 34 ARM64,
1280×2856 at 480 dpi. Goldie pins the time to 9:41. Its iOS status-bar override
renders a green charged battery and a soft network glyph on this runtime; this
is simulator/Goldie output, not an app image-loading problem.

Goldie's debug-path advisory is generic: this native screenshot host has no
development menu or production application startup. Signed production release
artifacts and physical devices were not tested. Pixel equality is meaningful
within the same device, runtime and renderer, not across Android and iOS.

## Files involved

- `settings.gradle.kts`: explicit inclusion of the isolated screenshot modules.
- `screenshots/shared/`: scene router, Koin fixture provider, rendering host,
  bundled artwork and simulator controller; production composables are reused.
- `screenshots/android/`: dedicated APK, isolated manifest and direct URL routing;
  repeated launcher intents now reset to Dashboard safely.
- `screenshots/ios/`: independent SwiftUI executable, locale/type-size settings,
  simulator plist and screenshot URL registration.
- `screenshots/scripts/build-ios.sh`: clean build, transitive Compose resource
  sync with explicit simulator environment and missing-artwork assertion.
- `screenshots/scripts/prepare-android.sh`: device contract and boot readiness.
- `screenshots/scripts/verify-isolation.py`: packaged APK safety checks and SDK
  discovery through environment/PATH.
- `screenshots/scripts/compare-captures.cjs`: exact decoded-pixel comparison of
  all six raw scenes across capture runs.
- `.argent/flows/store-*.yaml`, `android-store-*.yaml`: six direct launch/capture
  gates per platform, using their verified wait mechanisms.
- `goldie.config.ts`, `goldie.android.config.ts`: verified Goldie configuration,
  six headlines, bundled Crew typography and brand palette.
- `screenshots/marketing/android-frame.svg`: original neutral frame for Android
  compositions; avoids clipping rectangular emulator status-bar content.
- `screenshots/README.md`, `ASSETS.md`: operating instructions and asset provenance.

Existing production reuse seams are `MatchContainerScreen`'s optional leaderboard
and memories content slots and the public `EventShareButton`; their default
production implementations remain available. The broader dirty workspace
contains other UI/domain changes that predate this validation pass.
