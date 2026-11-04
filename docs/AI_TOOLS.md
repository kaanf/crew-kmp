# AI tools and test inventory

Initial inventory: 2026-11-03. Documentation/configuration update: 2026-11-04. Scope: instructions, skill directories, MCP examples, test tools, and scripts in this repository's working tree only. User-level settings, global skills/plugins, and session tools are excluded. Deleted files are not considered available. Existing user changes were preserved.

**Status terminology:** “Present” means a file or executable was found; “verified” means the stated read-only check succeeded. Neither implies passing tests or authorized access to remote services. No application builds, tests, MCP servers, releases, or installations were run. On 2026-11-04, offline Gradle task listing completed successfully; task availability is distinct from build/test success. See [COMMANDS](COMMANDS.md) for current command evidence. Commands below are inventory references. Secret values, account details, and device identifiers are omitted.

## Agent instructions and local skills

| Item | Purpose / when to use | Instruction or configuration path | Usage and verification |
|---|---|---|---|
| Project development instructions | Shared implementation rules and task-based documentation routing; check this inventory to select relevant tools | `AGENTS.md`; `CLAUDE.md` imports it with `@AGENTS.md` | Adapted to observed architecture on 2026-11-04. No CLI command. Architectural instructions do not guarantee that all existing code follows them. |
| Agent documentation workflow | Context maintenance, implementation, review and handoff | `docs/ai/WORKFLOW.md`, `docs/ai/REVIEW.md`, `docs/ai/CONTEXT_POLICY.md`, `docs/ai/HANDOFF_TEMPLATE.md` | Added during documentation setup; read through AGENTS task routing. |
| Claude permissions | Locally allowed tool operations | `.claude/settings.local.json` | Present; contains only `permissions`. Permission entries do not prove that tools are installed or operational. Command contents from permission entries are omitted. |
| Agent definitions | Custom subagent roles | `.claude/agents/`, `.codex/agents/` | No usable definition files found. |
| design | Directory name suggests design tasks | `.agents/skills/design/` | Subdirectories exist, but no files or `SKILL.md`. Scope and command unverified. |
| design-system | Directory name suggests design system tasks | `.agents/skills/design-system/` | Subdirectories exist, but no files or `SKILL.md`. Scope and command unverified. |

## Project MCP configurations

| Item | Purpose / task | Instruction or configuration path / command | Status |
|---|---|---|---|
| Maestro MCP example | Mobile screen inspection and UI automation through an agent | `integrations/maestro-codex-config.example.toml`; server command: `maestro mcp` | Example configuration only, not an active setup. No `.codex/config.toml` or `.mcp.json` found in the project. CLI found on PATH; MCP connectivity and device automation unverified. |
| Claude Maestro notes | Instructions for optionally registering a stdio MCP server in Claude Code | `integrations/maestro-claude-notes.md` | Setup notes, not an active server registration. Recommends checking CLI help for exact registration syntax. |

## Testing, quality, and CI

| Tool / scope | Task and source | Known command | Status / limitations |
|---|---|---|---|
| Gradle Wrapper 9.1.0 | Build and test tasks; `gradlew`, `gradle/wrapper/gradle-wrapper.properties`, `settings.gradle.kts`, `build-logic/` | `./gradlew --no-daemon allTests :androidApp:compileDebugKotlin` | Wrapper present; offline task listing succeeded on 2026-11-04. CI command not executed; build/test success unverified. |
| Kotlin common tests | `core/*/src/commonTest`, `feature/*/*/src/commonTest`; models/validation, mappers, repositories, sessions/networking, UI state, and semantics | `./gradlew allTests`; target-specific example: `./gradlew :core:domain:iosSimulatorArm64Test` | `kotlin-test` configured; some modules use `kotlinx-coroutines-test`. KMP tests primarily use the iOS simulator target; JVM execution alone does not cover the full suite. Normal task names were listed on 2026-11-04; tests were not executed. |
| Palette host tests | Geometry/contrast; `core/palette/build.gradle.kts`, `core/palette/src/commonTest/` | `./gradlew :core:palette:allTests` | `withHostTest {}` enabled; an exception to the general absence of KMP host tests. Not run. |
| Android JUnit tests | ViewModel and sample unit tests; `androidApp/src/test/`, `feature/game/domain/src/androidHostTest/` | `./gradlew :androidApp:testDebugUnitTest` | Android app JUnit dependencies present. The game domain source directory alone does not prove its host test task is enabled. |
| Android device tests | Instrumentation/Compose UI infrastructure; `androidApp/src/androidTest/`, `feature/game/domain/src/androidDeviceTest/`, `androidApp/build.gradle.kts` | `./gradlew :androidApp:connectedDebugAndroidTest` | Runner and Compose UI test dependencies present. Some sources are sample tests; broad UI coverage is not implied. No connected Android device found. |
| iOS Keychain integration | Real secure storage roundtrip; `core/data/src/iosIntegrationTest/`, `core/data/build.gradle.kts` | Flag enabling these sources: `-PcrewKeychainIntegration=true` | Disabled by default. Requires a signed iOS app host; the standalone Kotlin/Native runner lacks Keychain entitlements. A working host command was not verified. |
| Maestro onboarding/events | Registration/login, profile, events, tickets, guest lists, and account deletion; `.maestro/README.md`, `.maestro/e2e_*.yaml`, `.maestro/subflows/` | `maestro test -e EVENT_TITLE='<fixture>' .maestro/e2e_onboarding_to_profile.yaml`; `maestro test -e EVENT_TITLE='<fixture>' -e EVENT_STATE=locked .maestro/e2e_events.yaml` | CLI present; requires English UI, a device, and suitable backend fixtures. Flows can modify data/accounts. Share/image checks are optional and lack device verification according to the documentation. |
| Match runner | Real backend with an opponent bot; `tools/maestro/run_match.py`, `.maestro/match/` | `python3 tools/maestro/run_match.py` | Requires `MATCH_API_BASE`, `MATCH_EVENT_ID`, `MATCH_PLAYER_TOKEN`, and `MATCH_BOT_TOKEN`; optional `MATCH_DEVICE` and `MATCH_TIMEOUT`. Tokens are read from the environment; no automatic login/refresh. Camera scanning is not tested. Running the YAML steps independently as a batch can break bot sequencing. |
| Full match runner | New user, test event registration/check-in, bot match, and account cleanup; `tools/maestro/run_full_match.py` | `python3 tools/maestro/run_full_match.py` | Bot credentials supplied interactively or through `MATCH_BOT_EMAIL` / `MATCH_BOT_PASSWORD`. Defaults to the live API; select the test environment explicitly. Modifies account/match data. `.maestro/README.md` reports Android success on 2026-10-29 after resuming from intermediate failures; a fresh uninterrupted run, the latest UI deletion step, and iOS remain unverified. Not rerun during this review. |
| Macrobenchmark | Cold start and Welcome→Register navigation; `benchmark/build.gradle.kts`, `benchmark/src/main/.../SmoothnessBenchmark.kt` | `./gradlew :benchmark:connectedBenchmarkReleaseAndroidTest` | AndroidX Macrobenchmark/UI Automator configured; requires a physical device. Post-login dashboard/event scrolling is outside its scope. Not run. |
| Baseline profile | Generate startup profiles; `benchmark/src/main/.../BaselineProfileGenerator.kt`, `benchmark/build.gradle.kts`, `androidApp/build.gradle.kts` | `./gradlew :androidApp:generateReleaseBaselineProfile` | Defines the `pixel6Api34` managed device, API 34 `aosp-atd`, and minimum API 28. A code comment says the image is installed; this was not verified during the review. |
| Android Lint | Android static analysis; `config/lint/lint.xml`, AGP | Listed task: `./gradlew :androidApp:lintDebug` | Configuration file present; its build integration and task success unverified. |
| Detekt / ktlint | Kotlin static analysis and style | `config/detekt/detekt.yml`, `gradle/libs.versions.toml` | Plugin versions are listed in the catalog, but no application in the build was found. `detekt`, `ktlintCheck` and `androidLint` were absent from the normal task listing on 2026-11-04. |
| GitHub Actions | Automated PR/main builds and tests; `.github/workflows/pr-tests.yml` | Workflow command: `./gradlew --no-daemon allTests :androidApp:compileDebugKotlin` | macOS + JDK 21. PR job runs only for source branch `dev`; main pushes and manual triggers are also configured. Job name mentions lint, but the command has no separate lint task. CI results were not queried. Reports: `**/build/reports/tests/**`, `**/build/test-results/**`. |

## Screenshots and helper scripts

| Tool / script | Task | Source and command | Status / requirements |
|---|---|---|---|
| Standalone screenshot apps | Open production Compose screens with fixed fixtures for store screenshots and UX inspection | `screenshots/README.md`, `screenshots/shared/`, `screenshots/android/`, `screenshots/ios/`; `./gradlew -PcrewScreenshots=true :screenshots:android:assembleDebug` | Modules are excluded unless the flag is set. Uses a separate app identifier. Current APK build success unverified. |
| iOS build script | Build simulator ARM64 framework and SwiftUI host | `screenshots/scripts/build-ios.sh`; `./screenshots/scripts/build-ios.sh` | Requires Xcode, a suitable simulator SDK/runtime, and Gradle; output: `screenshots/build/ios/CrewScreenshots.app`. Recreates the output directory; not run. |
| Android preparation | Set deterministic screen size, density, font, theme, and animation settings | `screenshots/scripts/prepare-android.sh`; `./screenshots/scripts/prepare-android.sh <emulator-id>` | Accepts emulator identifiers only. Changes emulator settings; not run during this review. |
| Capture / scene launch | Select a fixture screen and capture PNGs | `screenshots/README.md`; `adb -s <emulator-id> shell am start -W -a android.intent.action.VIEW -d crew-screenshot://quest com.kaanf.crew.screenshots`; `xcrun simctl openurl booted crew-screenshot://quest` | Capture: `adb -s <emulator-id> exec-out screencap -p > <output.png>` or `xcrun simctl io booted screenshot <output.png>`. Requires a running device and installed host; unavailable during this review. |
| Pixel comparison | Compare repeatability of the six store scenes | `screenshots/scripts/compare-captures.cjs`; `node screenshots/scripts/compare-captures.cjs <baseline-dir> <actual-dir> [--ignore-top=<pixels>]` | Node present; `@napi-rs/canvas` could not be resolved from the repository root. Not ready for direct use in this environment. Additional profile-board scenes are outside its fixed six-scene list. |
| Isolation check | Check screenshot APK manifest and production APK DEX/resource separation | `screenshots/scripts/verify-isolation.py`; `python3 screenshots/scripts/verify-isolation.py <production.apk>` | Python present; reads the screenshot APK from a fixed path and requires `apkanalyzer`. APK and analyzer availability unverified. |
| Fixture artwork | Generate local avatar/event/memory PNGs | `screenshots/scripts/generate-art.cjs`, `screenshots/ASSETS.md`; `node screenshots/scripts/generate-art.cjs` | Requires `@napi-rs/canvas`, which could not be resolved from the repository root. Writes existing resource files; not run. |
| Goldie / Argent remnants | Files potentially associated with previous visual capture automation | `goldie*.config.ts`, `goldie.design.json`, `.argent/flows/` | Related tracked files are deleted in the working tree. Without current configuration/flows, these are not considered usable project tools. |

## Release tools

Sources: `Gemfile`, `Gemfile.lock`, `fastlane/Fastfile`, `fastlane/Appfile`, `fastlane/README.md`. Ruby, Bundler, and fastlane are on PATH; bundle dependency integrity, signing, and service access remain unverified. None of the following operations were run.

| Lane | Task | Command | Effects / verification limits |
|---|---|---|---|
| `bump` | Increment build number; optionally update marketing version | `bundle exec fastlane bump` or `bundle exec fastlane bump version:1.1` | Modifies project files. |
| `ios_upload` | Create an archive and upload to App Store Connect | `bundle exec fastlane ios_upload` | Requires signing and private service credentials; uploads to a remote service. |
| `android_apk` | Build a signed release APK | `bundle exec fastlane android_apk` | Requires Android signing configuration. |
| `release` | Bump + iOS upload + Android APK | `bundle exec fastlane release` | Affects local files and remote release state. |

Firebase App Distribution is also declared in `androidApp/build.gradle.kts` and the root plugin declarations. Distribution tasks, authentication, and usability were not verified through execution. Contents of `local.properties`, service JSON/plist files, and signing material are omitted.

## Project tool availability checks

The version/PATH/device/package checks below were recorded on 2026-11-03; they were not rerun on 2026-11-04. They cover prerequisites for the project tools above. Other tools installed on the machine are outside this inventory.

- Java 21.0.11 and Xcode 27.0 version commands succeeded for the project's build prerequisites. Node 26.3.0 and Python 3.14.8 are accessible for repository helper scripts.
- Maestro and Android SDK `adb` commands were found on PATH; no end-to-end tests were run. No connected Android device/emulator or booted iOS simulator was listed.
- Ruby, Bundler, and fastlane prerequisites were found on PATH; dependencies and release access remain unverified.
- The screenshot scripts' required `@napi-rs/canvas` package could not be resolved from the repository root.
- On 2026-11-04, the owner reported a Galaxy S24+ and iPhone 11 available for physical testing; see [TESTING](TESTING.md). This does not supersede the earlier disconnected-device observation or constitute a fresh connection/test check. OS versions and lower-end Android coverage remain unverified.
- The normal Gradle task graph was listed successfully on 2026-11-04. SDK packages, emulator images, MCP connections, test fixtures, and CI results remain unverified. No passing test results are claimed.
