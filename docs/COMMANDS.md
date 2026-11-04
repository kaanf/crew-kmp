# Project commands and execution evidence

Reviewed 2026-11-04; HEAD and working-tree context: [PROJECT_MAP](PROJECT_MAP.md).
All commands below run from the repository root unless noted. Tool inventory:
[AI_TOOLS](AI_TOOLS.md). No application build/test/release success is claimed.

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
