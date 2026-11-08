# Project commands and execution evidence

Reviewed 2026-11-04; HEAD and working-tree context: [PROJECT_MAP](PROJECT_MAP.md).
All commands below run from the repository root unless noted. Tool inventory:
[AI_TOOLS](AI_TOOLS.md). The original inventory below is dated 2026-11-04.
Later observability execution evidence is linked below; no commands were rerun for the
2026-11-06 documentation reconciliation.

## Prerequisites

| Requirement | Configuration evidence | Availability evidence |
| --- | --- | --- |
| Gradle / JDK | Wrapper 9.1.0; CI uses JDK 21; Xcode phase prefers 21 with 17 fallback | Offline task listing succeeded on 2026-11-04; inventory recorded Java 21.0.11 on 2026-11-03 |
| Kotlin / AGP / Compose | Catalog: Kotlin 2.3.20, AGP 9.0.0, Compose Multiplatform 1.11.1 | Build configuration succeeded; target compilation not run |
| Android SDK | compile/target 36, minimum 26; benchmark minimum 28 | Installed SDK packages not checked; prior inventory found no connected device |
| Apple toolchain | Xcode project deployment target 18.2; ARM64 device/simulator; screenshot script targets iOS 18.5 simulator | Prior inventory recorded Xcode 27.0 and no booted simulator; runtime builds not run |
| Local configuration | SDK location through `local.properties` or environment; Android service config, iOS service plist and signing as required by host | Contents not read or reproduced; validity unverified |
| Node / Python / Ruby | Screenshot scripts / Maestro runners / Gemfile and Fastlane | See dated availability checks in AI_TOOLS; canvas dependency was unresolved |

## Evidence meanings

**Discovered in configuration** or **listed** establishes a command/task exists, not that
it works. **Executed successfully** means the stated command completed successfully.
**Executed and failed**, **blocked** and **not run** are separate outcomes. A device-dependent
operation may be not run with a known prerequisite limitation; that is not a failed test.

## Registry

| Purpose | Exact command | Requirements | Evidence / status |
| --- | --- | --- | --- |
| List normal project tasks | `./gradlew --offline --no-daemon --console=plain tasks --all` | Cached wrapper/plugins and local JDK | **Executed successfully**, 2026-11-04; `BUILD SUCCESSFUL`, 7 s. No application tests/builds executed. Output reviewed locally; raw output is not committed. |
| Android debug APK | `./gradlew :androidApp:assembleDebug` | SDK and valid host service configuration | Listed; **not run** |
| CI checks | `./gradlew --no-daemon allTests :androidApp:compileDebugKotlin` | macOS, JDK 21, Apple/Android toolchains, CI placeholder service file | Discovered in `.github/workflows/pr-tests.yml`; component test/compile tasks listed; combined command **not run** |
| Android JVM tests | `./gradlew :androidApp:testDebugUnitTest` | JDK, Android build dependencies | Listed; **not run** |
| Palette Android host tests | `./gradlew :core:palette:testAndroidHostTest` | JDK, Android build dependencies | Listed and documented in `core/palette/README.md`; **not run** |
| Shared domain simulator tests | `./gradlew :core:domain:iosSimulatorArm64Test` | Apple toolchain and usable ARM64 simulator runtime | Listed; **not run** |
| Data simulator tests | `./gradlew :core:data:iosSimulatorArm64Test` | Apple toolchain/runtime; dependency resolution | Listed; **not run**; does not enable the gated Keychain suite |
| Android instrumentation | `./gradlew :androidApp:connectedDebugAndroidTest` | Connected test device/emulator | Listed; **not run**; no connected device in prior inventory |
| Android Lint | `./gradlew :androidApp:lintDebug` | Android build dependencies | Listed; **not run**. Use of `config/lint/lint.xml` remains unverified. |
| iOS debug framework | `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64` | Apple toolchain | Listed; **not run** |
| Xcode framework integration | `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode` | Run by Xcode with its build environment | Listed and present in Xcode build phase; **not run**; not a standalone simulator launch command |
| Open native project | `open iosApp/iosApp.xcodeproj` | macOS/Xcode | Project exists; **not run** |
| Startup/navigation benchmark | `./gradlew :benchmark:connectedBenchmarkReleaseAndroidTest` | Physical test device and release-like target | Listed; **not run** |
| Generate baseline profile | `./gradlew :androidApp:generateReleaseBaselineProfile` | Configured `pixel6Api34` managed device / API 34 `aosp-atd` image | Listed; **not run**; installed image unverified |
| Screenshot Android APK | `./gradlew -PcrewScreenshots=true :screenshots:android:assembleDebug` | Android SDK, opt-in modules | Discovered in screenshot build files/README; opt-in task listing **not run**, build **not run** |
| Screenshot iOS host | `./screenshots/scripts/build-ios.sh` | Apple Silicon, suitable simulator SDK and Gradle | Script inspected; **not run**; recreates its output directory |
| Compare captures | `node screenshots/scripts/compare-captures.cjs <baseline-dir> <actual-dir>` | Six scene PNGs and `@napi-rs/canvas` | Script discovered; execution **blocked** by unresolved canvas package in prior inventory; comparison not attempted |
| Verify APK isolation | `python3 screenshots/scripts/verify-isolation.py <production.apk>` | Screenshot APK at script path, production APK, `apkanalyzer` | Script discovered; **not run**, artifact/analyzer availability unverified |
| Event flow | `maestro test -e EVENT_TITLE='<fixture>' -e EVENT_STATE=locked .maestro/e2e_events.yaml` | English UI, logged-in test account, device, backend fixture | Discovered in `.maestro/README.md`; **not run** |
| Match / full match | `python3 tools/maestro/run_match.py` / `python3 tools/maestro/run_full_match.py` | Test backend, credentials and device; see AI_TOOLS | Scripts inspected; **not run**, can mutate remote data/accounts |
| Release lanes | `bundle exec fastlane bump`, `bundle exec fastlane ios_upload`, `bundle exec fastlane android_apk`, `bundle exec fastlane release` | Bundle, signing, appropriate service access and release authorization | Discovered in `fastlane/Fastfile`; **not run**; see [RELEASING](RELEASING.md) |

## Commands that must not be advertised as verified

- `ktlintCheck`, `detekt`, and `androidLint` are absent from the normal task listing.
  Catalog entries/configuration files do not activate these tasks. No formatting command
  was confirmed; `.editorconfig` remains the observed style configuration.
- `:composeApp:assembleDebug` is not the Android APK command for this library-based
  structure. Use the listed `:androidApp:assembleDebug` task.
- `fastlane ios ios_build` is not a lane in the inspected Fastfile.
- `-PcrewKeychainIntegration=true` enables extra test sources but does not create the
  signed app host needed by Keychain. A working end-to-end host command is UNVERIFIED (Q02).

For documentation-only changes validate links, source references and the diff. Do not
run broad application tests simply to populate this table. Do not clear global caches
or upgrade toolchains as an incidental response to configuration failures.

## Observability command evidence (reconciled 2026-11-06)

The following are prior recorded runs, not fresh test results from this documentation change.
[SENTRY](SENTRY.md) and [POSTHOG](POSTHOG.md) own detailed setup and platform limitations.

| Purpose | Exact command / entry point | Recorded result |
| --- | --- | --- |
| PostHog shared/native checks | `./gradlew --console=plain :androidApp:compileDebugKotlin :composeApp:compileKotlinIosSimulatorArm64 :core:domain:iosSimulatorArm64Test :core:data:iosSimulatorArm64Test` | Passed on 2026-11-04; 34 domain and 37 data tests, zero failures/errors/skips. Android compile passed again after local-setting support. |
| Unsigned Swift host with SDK linkage | `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /tmp/crew-posthog-xcode CODE_SIGNING_ALLOWED=NO build` | Passed on 2026-11-04 for PostHog, including Swift setup and ARM64 host linkage. Earlier Sentry host success is recorded in its guide. No signed archive/device launch implied. |
| Controlled Sentry API correlation | In sibling `../crew-api`: `./gradlew sentryVerificationServer`; Android debug verification activity or simulator `crew://debug/sentry` entry | Prior Android HTTP 500 correlation/device delivery evidence and corrected mobile DSN resolution are in [SENTRY](SENTRY.md). Not an automatic crash test; physical iOS delivery and release symbolication remain unverified. |

PostHog screen ranking uses `screen_viewed` broken down by custom `screen_name`; the
Top events summary groups event names. No remote-report creation or full match-funnel
verification is claimed. Never put DSNs, project tokens or personal API keys in command examples.

## Favorite music infrastructure verification (2026-11-06)

Source branch: `feat/favorite-music` in mobile and the isolated backend worktree
`../crew-api-favorite-music` (backend base `dev` at `a291f99`). No deploy/commit/push
or live Spotify call was performed. Contract/setup: [API_CONTRACTS](API_CONTRACTS.md#favorite-music-infrastructure-2026-11-06).

| Command / directory | Actual result |
| --- | --- |
| `./gradlew --console=plain compileKotlin` / backend worktree | Passed; server Kotlin compiled. |
| `./gradlew --console=plain :androidApp:compileDebugKotlin :composeApp:compileKotlinIosSimulatorArm64 :core:data:iosSimulatorArm64Test` / mobile | Passed after explicit Android/iOS cancellation rethrow fix; 44 data tests, zero failures (7 new music tests). Android and shared iOS simulator source compilation passed. |
| `./gradlew --console=plain test --tests '*SpotifyMusicCatalogTest' --tests '*FavoriteMusicServiceTest' --tests '*FavoriteMusicPersistenceTest'` / backend worktree | Passed; 12 tests covering mock Spotify requests/token expiry/cache/401/429/errors, validation, ID-only persistence/account isolation and authenticated API behavior. |

The first run of the new mobile cancellation test failed because an explicitly thrown
`CancellationException` was converted to `UNKNOWN` by existing platform HTTP helpers;
both platform implementations now rethrow cancellation. The same test and entire data
suite passed on rerun. No test was removed or weakened.

H2 integration uses Hibernate schema generation, not Flyway. PostgreSQL migration
execution, physical device/runtime UI, Swift framework linking/host build, live provider
credentials/quota and production entitlement remain **not run/unverified**. Existing
Compose preview/dependency deprecations and game iOS type-check warnings were observed;
these were not introduced or changed by the music work.

### Direct Spotify verification (2026-11-07)

A one-off Python standard-library HTTPS check loaded only the two Spotify settings from
the backend's ignored local environment file without displaying values. Token acquisition
succeeded; GET `/v1/search` with `q=Duman`, `type=track`, `market=TR`, `limit=10`,
`offset=0` returned 10 records with required metadata. This is direct-provider evidence,
not a Crew endpoint/deployment or favorite-write test. No secrets/tokens were persisted
in test reports or documentation. Production entitlement and quota guarantees remain
unverified. Prior mock-provider/compile results above were not rerun for this check.

### QR status self-repair verification (2026-11-07)

`./gradlew --console=plain :androidApp:compileDebugKotlin
:composeApp:compileKotlinIosSimulatorArm64 :core:data:iosSimulatorArm64Test
:core:presentation:iosSimulatorArm64Test` passed after correcting a new test's
expected route to include the configured `/api` prefix. Data tests cover GET-only
replacement-image refresh and PENDING-to-READY polling. Presentation tests cover
the root QR monitor.

Backend worktree `../crew-api-qr-fix`: `./gradlew --console=plain test --tests
'*QrRenderStatusServiceTest' --tests '*ProfileBoardPersistenceTest' --tests
'*QrRenderWorkerTest'` passed (13 tests). Covers authenticated-account controller
routing, missing/existing storage objects, storage errors, requeue state and render
worker requests. H2 does not validate PostgreSQL locking. No deploy, production
repair, device UI rerun or iOS host build was performed.

### TMDB film integration verification (2026-11-07)

Mobile `feat/profileboard`; backend isolated worktree `../crew-api-tmdb`, branch
`feat/tmdb-movies`, based on dev `e580025`. Existing unrelated mobile edits preserved.

- `./gradlew --console=plain :core:data:iosSimulatorArm64Test :core:designsystem:iosSimulatorArm64Test :androidApp:compileDebugKotlin :composeApp:compileKotlinIosSimulatorArm64`:
  passed; 54 data tests (8 new movie tests) and 34 design-system tests, no failures,
  errors or skips. Android and shared iOS source compilation passed.
- Backend checked-in Gradle 9.2.1 wrapper: `./gradlew --console=plain test --tests '*TmdbMovieCatalogTest' --tests '*FavoriteMovieServiceTest' --tests '*FavoriteMoviePersistenceTest'`:
  passed; 11 provider/service/H2/API security tests. Earlier verification also passed
  under the mobile wrapper's Gradle 9.1.0; final backend evidence uses its own wrapper.
- A one-off Python HTTPS check loaded only `TMDB_READ_ACCESS_TOKEN` from ignored local
  backend configuration without displaying it. Live search (`Interstellar`, `tr`, page 1)
  returned 20 results and detail lookup verified the same ID/title/date/poster.

Initial mobile compilation caught incorrect copied song types and a shadowed page
parameter; these were corrected before the passing checks. No test was weakened.
H2 does not execute Flyway. PostgreSQL migration, live Crew endpoint integration,
physical-device/emulator UI, iOS host build/link, deployment and commercial entitlement
are not verified by these checks. No commit, push or deployment performed.

## Profile board Loves verification (2026-11-07)

Mobile `feat/profileboard` (existing uncommitted work preserved):
`./gradlew --console=plain :core:data:iosSimulatorArm64Test :core:designsystem:iosSimulatorArm64Test :androidApp:compileDebugKotlin :composeApp:compileKotlinIosSimulatorArm64`
passed: 55 data and 35 design-system tests, zero failures, and both source compilations.
Backend `../crew-api-loves`, `feat/profileboard-loves` from dev `0ceaca1`:
`./gradlew --console=plain test --tests '*ProfileBoardValidationTest' --tests '*ProfileBoardPersistenceTest'`
passed: 22 tests, zero failures, including authenticated API writes, legacy omission,
invalid selections, persistence, clear, revision conflicts and account isolation.
H2 uses Hibernate DDL; PostgreSQL/Flyway execution, device UI and iOS host linking
were not run. No deployment, commit or push performed. Existing Compose/Skiko and
backend deprecation warnings remain outside this change.

### Loves clear serialization regression (2026-11-07)

The new `clearingLovesExplicitlySendsEmptyArrayWithDefaultJsonSettings` test failed
before the fix: default JSON omitted hobbies. After per-field `EncodeDefault`,
`./gradlew --console=plain :core:data:iosSimulatorArm64Test :androidApp:compileDebugKotlin :composeApp:compileKotlinIosSimulatorArm64`
passed. The test verifies explicit `hobbies: []` under the production default-omission
settings. Device UI and live authenticated clear were not run; no backend change needed.

### Profile board credits and film sheet correction (2026-11-08)

`./gradlew --console=plain :androidApp:compileDebugKotlin
:composeApp:compileKotlinIosSimulatorArm64 :core:designsystem:iosSimulatorArm64Test`
passed. Credits now open from Settings; dismissal changes preserve the shared sheet
state, and film writes no longer trigger QR loading. Initial compilation caught
internal cross-module resource access; the dialog was moved into the design-system
module before the successful run. Device interaction, sheet animation, screen readers
and the Swift host build were not exercised; compilation/tests do not verify runtime
smoothness. Existing unrelated working-tree changes were preserved.

### Tile long press and haptic feedback (2026-11-08)

`./gradlew --console=plain :androidApp:compileDebugKotlin
:composeApp:compileKotlinIosSimulatorArm64` passed after adding tile long-press
editing through the existing ImpactHaptics adapter. `git diff --check` passed.
Physical haptic feedback, touch/scroll arbitration, TalkBack/VoiceOver and Swift
host linking were not exercised. No new SDK or platform adapter was added.

### Profile board film card refinement (2026-11-08)

`./gradlew --console=plain :androidApp:compileDebugKotlin
:composeApp:compileKotlinIosSimulatorArm64` passed for the film-strip poster card,
song play-icon removal and Ask-me tail removal. `git diff --check` passed.
No device visual review, accessibility run or iOS host build was performed.

Film card follow-up: the standard layered pixel sticker frame is restored around
a full-area poster and dark bottom caption. Android and shared iOS source compilation
passed again with the same command above; device visual inspection remains not run.

Film frame follow-up: distinct coral accent and matching stepped content clipping
compiled successfully for Android and shared iOS with the same command above.
`git diff --check` passed. Device rendering remains unverified.

Board tap follow-up: Spotify/TMDB external navigation was removed from filled
tiles and the Loves doing heading removed. Android and shared iOS compilation
passed with the same command above; `git diff --check` passed. Device interaction
and visual checks were not run. Long-press editing remains wired.
