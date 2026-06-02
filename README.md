**Tech Stack :** Kotlin, Kotlin Multiplatform, KMP, Compose Multiplatform, Jetpack Compose, Android, iOS, SwiftUI, Material 3, Navigation Compose, Gradle Kotlin DSL, Ktor Client, REST API, WebSocket, Kotlinx Serialization, Coroutines, Flow, Koin Dependency Injection, DataStore Preferences, Coil, QR Code, Clean Architecture, MVVM, Modular Architecture, Detekt, Ktlint, Fastlane.
**Platforms & Architecture:** Android SDK 36, iOS, Shared Kotlin Codebase, Multi-module Architecture, Feature-based Modules, Repository Pattern, Bearer Token Authentication, Refresh Token Flow, Real-time Event Updates.

# Crew

Crew is a Kotlin Multiplatform mobile app that turns real-world social events into an interactive game experience. Users can create an account, verify their email, discover events, access ticket/QR flows, check in at the venue, and join real-time game sessions during the event.

The app shares Kotlin and Compose code across Android and iOS. Android runs with Compose directly, while iOS hosts the shared `ComposeApp` framework inside a SwiftUI shell.

## Screenshots

<table>
  <tr>
    <td align="center">
      <img src="./docs/screenshots/crew_welcome.jpeg" alt="Crew Welcome screen" width="180" /><br />
      <sub><strong>Welcome</strong></sub>
    </td>
    <td align="center">
      <img src="./docs/screenshots/crew_login.jpeg" alt="Crew Login screen" width="180" /><br />
      <sub><strong>Login</strong></sub>
    </td>
    <td align="center">
      <img src="./docs/screenshots/crew_dashboard.jpeg" alt="Crew Dashboard screen" width="180" /><br />
      <sub><strong>Dashboard</strong></sub>
    </td>
    <td align="center">
      <img src="./docs/screenshots/crew_event_detail.jpeg" alt="Crew Event Detail screen" width="180" /><br />
      <sub><strong>Event Detail</strong></sub>
    </td>
    <td align="center">
      <img src="./docs/screenshots/crew_ticket_successful.jpeg" alt="Crew Ticket Successful screen" width="180" /><br />
      <sub><strong>Ticket QR</strong></sub>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="./docs/screenshots/crew_enter_code.jpeg" alt="Crew Enter Code screen" width="180" /><br />
      <sub><strong>Enter Code</strong></sub>
    </td>
    <td align="center">
      <img src="./docs/screenshots/crew_lobby.jpeg" alt="Crew Lobby screen" width="180" /><br />
      <sub><strong>Game Lobby</strong></sub>
    </td>
    <td align="center">
      <img src="./docs/screenshots/crew_lobby_starts.jpeg" alt="Crew Lobby Starts screen" width="180" /><br />
      <sub><strong>Lobby Starts</strong></sub>
    </td>
    <td align="center">
      <img src="./docs/screenshots/crew_game.jpeg" alt="Crew Game screen" width="180" /><br />
      <sub><strong>Game</strong></sub>
    </td>
    <td align="center">
      <img src="./docs/screenshots/crew_scan_opponent_sc.jpeg" alt="Crew Scan Opponent screen" width="180" /><br />
      <sub><strong>Scan Opponent</strong></sub>
    </td>
  </tr>
</table>

## Highlights

- Shared UI, domain, and data layers for Android and iOS with Kotlin Multiplatform.
- Consistent mobile interface built with Compose Multiplatform, Material 3, and a custom design system.
- Feature-based modular structure with dedicated `auth`, `home`, and `game` modules.
- Clean Architecture with clear `presentation`, `domain`, and `data` boundaries.
- MVVM state management using `ViewModel`, `StateFlow`, and event/action/state models.
- REST API integration with Ktor Client, JSON serialization, and centralized error handling.
- Bearer token authentication, refresh token flow, and DataStore-based session persistence.
- Real-time event and game updates through Ktor WebSocket.
- QR ticket, event code check-in, opponent scan, and game result flows.
- Dependency injection with Koin.
- Development workflow supported by Detekt, Ktlint, Android Lint, and Fastlane.

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
| Language | Kotlin 2.2, Swift |
| Cross-platform | Kotlin Multiplatform, Compose Multiplatform |
| Mobile UI | Jetpack Compose, Material 3, SwiftUI host |
| Architecture | Clean Architecture, MVVM, Modular Architecture |
| Async | Kotlin Coroutines, Flow, StateFlow |
| Networking | Ktor Client, REST API, WebSocket |
| Serialization | Kotlinx Serialization |
| Dependency Injection | Koin |
| Persistence | AndroidX DataStore Preferences |
| Media/UI Utilities | Coil, Chaintech QR Kit, ConfettiKit |
| Build & Quality | Gradle Kotlin DSL, Convention Plugins, Detekt, Ktlint, Android Lint |
| Release | Fastlane for iOS build lane |

## Requirements

- JDK 17
- Android Studio
- Xcode and an iOS simulator/device
- Kotlin Multiplatform-compatible Gradle environment
- Active API server: the HTTP and WebSocket URLs defined in `core/data/src/commonMain/kotlin/com/kaanf/core/data/networking/UrlConstants.kt` must be reachable.

## Running

Android debug build:

```bash
./gradlew :composeApp:assembleDebug
```

Android quality checks:

```bash
./gradlew ktlintCheck detekt androidLint
```

Open the iOS project with Xcode:

```bash
open iosApp/iosApp.xcodeproj
```

iOS Fastlane build:

```bash
bundle exec fastlane ios ios_build
```

## Notes

- `composeApp` is the main application module; the `androidApp` directory exists but is not included as an active module in Gradle settings.
- Backend URLs are currently defined directly in source. Environment-specific configuration can be moved to `UrlConstants` or a BuildKonfig-based setup.
- The project is organized by feature, so new screens or flows should keep the existing `presentation/domain/data` boundaries inside the relevant feature module.
