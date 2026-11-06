# Architecture

Observed 2026-11-04; revision and working-tree caveat: [PROJECT_MAP](PROJECT_MAP.md).
This describes the existing implementation, not a proposed architecture migration.

## Current design

| Concern | Current approach | Repository-relative source |
| --- | --- | --- |
| UI ownership | Shared Compose on Android and iOS; thin Android and SwiftUI hosts | `androidApp/src/main/java/com/kaanf/crew/androidapp/MainActivity.kt`, `iosApp/iosApp/ContentView.swift` |
| Presentation | ViewModels, immutable state data classes, sealed actions/events, StateFlow and Channels | `feature/home/presentation/src/commonMain/kotlin/com/kaanf/home/presentation/dashboard/` |
| DI | Koin singletons, factories and ViewModel registrations; platform modules included through expect/actual | `composeApp/src/commonMain/kotlin/com/kaanf/crew/di/KoinHelper.kt`, `core/data/src/commonMain/kotlin/com/kaanf/core/data/di/CoreDataModule.kt` |
| Navigation | Typed serializable routes and Navigation Compose graphs; root owns cross-feature transitions | `composeApp/src/commonMain/kotlin/com/kaanf/crew/navigation/NavigationRoot.kt`, `feature/home/presentation/src/commonMain/kotlin/com/kaanf/home/presentation/navigation/` |
| Network | Ktor REST and event WebSocket, Kotlin serialization and domain Result/errors | `core/data/src/commonMain/kotlin/com/kaanf/core/data/networking/`, `feature/game/data/src/commonMain/kotlin/com/kaanf/game/data/repository/EventConnectionClientImpl.kt` |
| Persistence | Preferences DataStore for settings/board caches; platform secure store for session credentials | `core/data/src/commonMain/kotlin/com/kaanf/core/data/session/`, `core/data/src/commonMain/kotlin/com/kaanf/core/data/repository/ProfileBoardRepositoryImpl.kt` |

`composeApp` composes the core and feature modules. Feature presentation depends on
its domain and shared UI; data implements domain interfaces using `core:data` helpers.
Auth/home/game domain modules depend on `core:domain`, without data, Ktor, DataStore
or Koin dependencies. Game domain also declares coroutines for its Flow contracts.
ConnectivityObserver is a `core:domain` interface with Android/iOS implementations
bound by platform data DI; ViewModels consume the interface. App review UI adapters
live in `core:presentation`. Some ViewModels call repository interfaces directly
(for example the board); use cases exist where needed, not universally.
`HomeRepositoryImpl` is an empty class; the real home data paths are event/ticket repositories.
No active SQLDelight implementation was found; Room catalog/convention entries alone
are not evidence of a runtime database.

## Observability boundaries (updated 2026-11-06)

Sentry and PostHog are initialized by the Android Application and SwiftUI host before
shared application flows. Official SDK dependencies live in `androidApp` and the Xcode
project; domain code has no SDK dependency.

- **Sentry:** the shared Ktor `RequestDiagnostics` plugin in `core:data` creates and
  propagates opaque Crew API request IDs and calls `HttpDiagnostics.onResponse`.
  `composeApp/HttpDiagnosticsBridge.kt` exposes the callback to Swift. Native adapters
  retain safe HTTP breadcrumbs and report 5xx warnings. It does not create distributed
  traces or join PostHog sessions to Sentry users. [SENTRY](SENTRY.md) documents redaction,
  backend correlation, configuration and verification gaps.
- **PostHog:** `core:domain` defines typed `AnalyticsEvent`/`Analytics` and foreground
  session deduplication. `core:data` binds the best-effort `ProductAnalytics` sink via
  Koin. Root typed navigation emits screen events and the match ViewModel emits action/
  success events. `composeApp/AnalyticsBridge.kt` exports the safe sink to Swift. Native
  adapters own anonymous identity, queueing and event/property filters. Replay, blanket
  autocapture and identification are disabled. [POSTHOG](POSTHOG.md) defines the vocabulary,
  activation gates, privacy decisions and counting limitations.

## Representative feature: dashboard

1. `HomeGraphRoutes.Dashboard` in
   `feature/home/presentation/src/commonMain/kotlin/com/kaanf/home/presentation/navigation/HomeGraphRoutes.kt`
   is registered by `HomeGraph.kt` in the same directory.
2. `DashboardRoot` in
   `feature/home/presentation/src/commonMain/kotlin/com/kaanf/home/presentation/dashboard/DashboardScreen.kt`
   resolves the ViewModel via Koin, collects state with `collectAsStateWithLifecycle`,
   and handles navigation events using `ObserveAsEvents`.
3. `DashboardViewModel` in that dashboard directory accepts `DashboardAction`, exposes
   combined `DashboardState`/user StateFlow, and emits `DashboardEvent` through a Channel.
   Initial loading, refresh and resume have distinct behavior; silent failures retain content.
4. `feature/home/domain/src/commonMain/kotlin/com/kaanf/home/domain/usecase/GetEventsUseCase.kt`
   calls `EventRepository`, filters ended events using the current clock, and sorts by doors time.
5. `feature/home/data/src/commonMain/kotlin/com/kaanf/home/data/repository/EventRepositoryImpl.kt`
   fetches event DTOs with Ktor and maps valid rows to domain models. Malformed list entries
   are skipped; detail conversion uses `mapCatching` error mapping.
6. DI wiring is in
   `feature/home/presentation/src/commonMain/kotlin/com/kaanf/home/presentation/di/HomePresentationModule.kt`
   and `feature/home/data/src/commonMain/kotlin/com/kaanf/home/data/di/HomeDataModule.kt`.

There is no dedicated dashboard ViewModel test in the inspected common test inventory.
Use [TESTING](TESTING.md) for meaningful coverage; do not infer test execution from this walkthrough.

## Change rules

Keep composables focused on rendering and action callbacks. Extend existing boundaries
before creating a new layer. Keep shared business logic testable; preserve cancellation,
state ownership, DTO boundaries and external contracts. Route navigation through established
graphs/events. Record significant accepted changes in [decisions](decisions/README.md);
an audit recommendation is not an accepted ADR.

## Dependency cleanup verification (2026-11-04)

`./gradlew --offline --console=plain :androidApp:compileDebugKotlin
:composeApp:compileKotlinIosSimulatorArm64` completed successfully after the domain,
review adapter and connectivity interface cleanup. Android and iOS simulator sources
compiled; application launch, device behavior and tests were not exercised.
