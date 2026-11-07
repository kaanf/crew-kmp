**Tech Stack :** Kotlin, Kotlin Multiplatform, KMP, Compose Multiplatform, Jetpack Compose, Android, iOS, SwiftUI, Material 3, Navigation Compose, Gradle Kotlin DSL, Ktor Client, REST API, WebSocket, Kotlinx Serialization, Coroutines, Flow, Koin Dependency Injection, DataStore Preferences, Coil, QR Code, Clean Architecture, MVVM, Modular Architecture, Detekt, Ktlint, Fastlane.
Declared Detekt/Ktlint versions do not establish active checks; see [COMMANDS](docs/COMMANDS.md).

**Platforms & Architecture:** Android SDK 36, iOS, Shared Kotlin Codebase, Multi-module Architecture, Feature-based Modules, Repository Pattern, Bearer Token Authentication, Refresh Token Flow, Real-time Event Updates.

# Crew

Crew is a Kotlin Multiplatform mobile app that turns real-world social events into an interactive game experience. Users can create an account, verify their email, discover events, access ticket/QR flows, check in at the venue, and join real-time game sessions during the event.

The app shares Kotlin and Compose code across Android and iOS. Android runs with Compose directly, while iOS hosts the shared `ComposeApp` framework inside a SwiftUI shell.

## Highlights

- Shared UI, domain, and data layers for Android and iOS with Kotlin Multiplatform.
- Consistent mobile interface built with Compose Multiplatform, Material 3, and a custom design system.
- Feature-based modular structure with dedicated `auth`, `home`, and `game` modules.
- Feature-organized `presentation`, `domain`, and `data` layers; current dependency exceptions are documented in [ARCHITECTURE](docs/ARCHITECTURE.md).
- MVVM state management using `ViewModel`, `StateFlow`, and event/action/state models.
- REST API integration with Ktor Client, JSON serialization, and centralized error handling.
- Bearer token authentication, guarded refresh, and platform secure session storage with migration from legacy DataStore Preferences.
- Real-time event and game updates through Ktor WebSocket.
- QR ticket, event code check-in, opponent scan, and game result flows.
- Dependency injection with Koin.
- Development workflow includes Android Lint, tests and Fastlane. Detekt/ktlint catalog entries exist, but active tasks were not found in the inspected task listing.

## Product Flow

- **Auth:** Welcome, login, register, forgot password, email verification, and deep-link-based verification result screens.
- **Home:** Event dashboard, event detail, ticket creation/viewing, QR access, and event code check-in.
- **Game:** Lobby, personal match QR, opponent scan, ready state, winner/loser confirmations, challenge selection, active task, and reveal screens.

## Architecture

```text
composeApp                  # KMP app entry point, navigation root, Android/iOS bridge
core:designsystem           # Theme, typography, buttons, cards, sheets, QR/image components
core:presentation           # Shared UI models, permission helpers, snackbar utilities
core:domain                 # Shared domain models, result/error models, repository contracts
core:data                   # Ktor client, session storage, DTO mappers, platform data providers
feature:auth                # Authentication presentation/domain/data
feature:home                # Event, ticket, and check-in presentation/domain/data
feature:game                # Real-time game presentation/domain/data
build-logic                 # Convention plugins and shared Gradle configuration
iosApp                      # SwiftUI host app
```

## Technologies

| Area | Technologies |
| --- | --- |
| Language | Kotlin 2.3.20, Swift |
| Cross-platform | Kotlin Multiplatform, Compose Multiplatform |
| Mobile UI | Jetpack Compose, Material 3, SwiftUI host |
| Architecture | Clean Architecture, MVVM, Modular Architecture |
| Async | Kotlin Coroutines, Flow, StateFlow |
| Networking | Ktor Client, REST API, WebSocket |
| Serialization | Kotlinx Serialization |
| Dependency Injection | Koin |
| Persistence | AndroidX DataStore Preferences for settings/board caches; Android Keystore-backed session storage and iOS Keychain |
| Media/UI Utilities | Coil, Chaintech QR Kit, ConfettiKit |
| Build & Quality | Gradle Kotlin DSL, convention plugins, Android Lint; Detekt/ktlint activation unverified |
| Release | Fastlane versioning, TestFlight upload and Android release APK lanes |

## Requirements

- JDK 21 (matches CI; Xcode integration has a JDK 17 fallback)
- Android SDK 36 (minimum supported Android API configured as 26)
- Android Studio
- Xcode and an ARM64 iOS simulator/device (app deployment target 18.2)
- Kotlin Multiplatform-compatible Gradle environment
- Active API server: the HTTP and WebSocket URLs defined in `core/data/src/commonMain/kotlin/com/kaanf/core/data/networking/UrlConstants.kt` must be reachable.

## Running

Android debug build:

```bash
./gradlew :androidApp:assembleDebug
```

Android lint and focused host tests (tasks discovered; not executed during documentation setup):

```bash
./gradlew :androidApp:lintDebug :androidApp:testDebugUnitTest
./gradlew :core:palette:testAndroidHostTest
```

The previous `ktlintCheck`, `detekt` and `androidLint` commands were not found in the
normal project task listing. See [command evidence](docs/COMMANDS.md) before running checks.

Open the iOS project with Xcode:

```bash
open iosApp/iosApp.xcodeproj
```

iOS release archive and TestFlight upload (requires signing, credentials and release authorization;
not a local development launch command):

```bash
bundle exec fastlane ios_upload
```

These build/release commands were not executed during documentation setup. The offline
Gradle task listing succeeded; see [COMMANDS](docs/COMMANDS.md) for exact evidence and prerequisites.

## Notes

- `androidApp` is included in Gradle settings and packages the Android application. `composeApp` is the shared Compose library and exports the static iOS framework.
- Backend URLs are currently defined directly in source. Environment-specific configuration can be moved to `UrlConstants` or a BuildKonfig-based setup.
- The project is organized by feature, so new screens or flows should keep the existing `presentation/domain/data` boundaries inside the relevant feature module.


## Development documentation

- [AI contributor instructions and task routing](AGENTS.md)
- [Branch strategy and agent commit/push/PR workflow](docs/BRANCH_STRATEGY.md)
- [Project/module map](docs/PROJECT_MAP.md), [architecture](docs/ARCHITECTURE.md), [KMP boundaries](docs/KMP.md)
- [Commands and execution evidence](docs/COMMANDS.md), [testing](docs/TESTING.md), [project tool inventory](docs/AI_TOOLS.md)
- [Design system](docs/DESIGN_SYSTEM.md), [data](docs/DATA.md), [API integrations](docs/API_CONTRACTS.md)
- [Contributing](CONTRIBUTING.md), [release scripts](docs/RELEASING.md), [open decisions](docs/ai/OPEN_QUESTIONS.md)
- Policy drafts: [security](SECURITY.md), [support](SUPPORT.md), [conduct](CODE_OF_CONDUCT.md), [governance](GOVERNANCE.md)

Documentation was reviewed against HEAD `24710a2d7b6f03f51583c9d1026ce6339ac55aab` and
the working tree on 2026-11-04. Uncommitted feature code is not a release claim. The screenshot gallery was removed
at the owner's request on 2026-11-04; the separate screenshot tools remain available.

The owner confirmed on 2026-11-04 that Crew remains a closed-source project under their
ownership. No open-source license has been assigned. External contribution terms remain
unspecified. Current application code is the design reference; Figma may be connected later.

The backend is the sibling `../crew-api` repository. Inspect its actual controllers,
services and tests for related work; its README is outdated and is not contract evidence.
