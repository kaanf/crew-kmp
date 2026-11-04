# Project map

Reviewed 2026-11-04 against HEAD `24710a2d7b6f03f51583c9d1026ce6339ac55aab` **and the current working tree**.
Existing uncommitted profile board/UI changes are included as observed implementation,
not claimed as committed, released or runtime-verified. Module declarations:
`settings.gradle.kts`. Tool availability is tracked in [AI_TOOLS](AI_TOOLS.md).

## Modules

The dependency column records observed project dependencies, including presentation
convention defaults. It is not a newly enforced dependency policy.

| Module / path | Responsibility | Targets | Observed project dependencies | Build source |
| --- | --- | --- | --- | --- |
| `:composeApp` / `composeApp/` | Shared app composition, root navigation, platform bridges and Koin composition root | Android, iOS ARM64, iOS simulator ARM64 | `:core:data`, `:core:domain`, `:core:designsystem`, `:core:presentation`, `:feature:auth:domain`, `:feature:auth:data`, `:feature:auth:presentation`, `:feature:game:domain`, `:feature:game:data`, `:feature:game:presentation`, `:feature:home:domain`, `:feature:home:data`, `:feature:home:presentation` | `composeApp/build.gradle.kts` |
| `:core:presentation` / `core/presentation/` | UI models, lifecycle/event helpers, permissions, media and QR monitoring | Android, iOS ARM64, iOS simulator ARM64 | `:core:domain` | `core/presentation/build.gradle.kts` |
| `:core:domain` / `core/domain/` | Shared models, repository/service interfaces and Result/DataError | Android, iOS ARM64, iOS simulator ARM64 | See target/convention configuration | `core/domain/build.gradle.kts` |
| `:core:data` / `core/data/` | Ktor, DTOs/mappers, session/settings storage and shared repositories | Android, iOS ARM64, iOS simulator ARM64 | `:core:domain` | `core/data/build.gradle.kts` |
| `:core:designsystem` / `core/designsystem/` | Theme, typography, common controls, QR and profile board UI | Android, iOS ARM64, iOS simulator ARM64 | `:core:domain`, `:core:presentation` | `core/designsystem/build.gradle.kts` |
| `:core:palette` / `core/palette/` | Event cover palette geometry, contrast and backdrop rendering | Android, iOS ARM64, iOS simulator ARM64 | See target/convention configuration | `core/palette/build.gradle.kts` |
| `:feature:auth:presentation` / `feature/auth/presentation/` | Authentication screens, ViewModels and social sign-in adapters | Android, iOS ARM64, iOS simulator ARM64 | `:feature:auth:domain`, `:core:domain`, `:core:designsystem`, `:core:presentation` | `feature/auth/presentation/build.gradle.kts` |
| `:feature:auth:domain` / `feature/auth/domain/` | Auth repository contract, validation and use cases | Android, iOS ARM64, iOS simulator ARM64 | `:core:domain` | `feature/auth/domain/build.gradle.kts` |
| `:feature:auth:data` / `feature/auth/data/` | Auth HTTP implementation and DTOs | Android, iOS ARM64, iOS simulator ARM64 | `:core:data`, `:core:domain`, `:feature:auth:domain` | `feature/auth/data/build.gradle.kts` |
| `:feature:home:presentation` / `feature/home/presentation/` | Dashboard, event detail, ticket QR, profile and board screens | Android, iOS ARM64, iOS simulator ARM64 | `:feature:home:domain`, `:core:domain`, `:core:designsystem`, `:core:presentation`, `:core:palette` | `feature/home/presentation/build.gradle.kts` |
| `:feature:home:data` / `feature/home/data/` | Event/ticket HTTP repositories | Android, iOS ARM64, iOS simulator ARM64 | `:core:data`, `:core:domain`, `:feature:home:domain` | `feature/home/data/build.gradle.kts` |
| `:feature:home:domain` / `feature/home/domain/` | Event/ticket contracts and use cases | Android, iOS ARM64, iOS simulator ARM64 | `:core:domain` | `feature/home/domain/build.gradle.kts` |
| `:feature:game:domain` / `feature/game/domain/` | Match, task, snapshot and event connection contracts/models | Android, iOS ARM64, iOS simulator ARM64 | `:core:domain` | `feature/game/domain/build.gradle.kts` |
| `:feature:game:data` / `feature/game/data/` | Match HTTP repository and lifecycle-aware event WebSocket | Android, iOS ARM64, iOS simulator ARM64 | `:core:data`, `:core:domain`, `:feature:game:domain` | `feature/game/data/build.gradle.kts` |
| `:feature:game:presentation` / `feature/game/presentation/` | Lobby, opponent scan, match phases, leaderboard and memories | Android, iOS ARM64, iOS simulator ARM64 | `:feature:game:domain`, `:core:domain`, `:core:designsystem`, `:core:presentation` | `feature/game/presentation/build.gradle.kts` |
| `:androidApp` / `androidApp/` | Android application, Activity/Application, push delivery and packaging | Android | `:composeApp`, `:core:domain`, `:core:presentation`, `:feature:auth:presentation` | `androidApp/build.gradle.kts` |
| `:benchmark` / `benchmark/` | Android startup/navigation benchmarks and baseline profile generation | Android | See target/convention configuration | `benchmark/build.gradle.kts` |
| `:screenshots:shared` / `screenshots/shared/` | Fixed fixtures and shared production-screen routing for screenshots | Android + iOS simulator ARM64; opt-in | `:core:domain`, `:core:presentation`, `:core:designsystem`, `:core:palette`, `:feature:home:domain`, `:feature:home:presentation`, `:feature:game:domain`, `:feature:game:presentation` | `screenshots/shared/build.gradle.kts` |
| `:screenshots:android` / `screenshots/android/` | Independent debug-only Android screenshot host | Android; opt-in | `:screenshots:shared` | `screenshots/android/build.gradle.kts` |

`build-logic/` is a separate included Gradle build providing convention plugins;
`iosApp/` is the native Xcode host, not a Gradle module. Screenshot modules are included
only with `-PcrewScreenshots=true`. No desktop, web or `iosX64` target is declared by
the current conventions; catalog desktop entries/comments do not create targets.

## Entry points and representative code (repository-relative paths)

| Need | Source | Pattern / responsibility |
| --- | --- | --- |
| Android startup | `androidApp/src/main/java/com/kaanf/crew/androidapp/CrewApplication.kt`, `androidApp/src/main/java/com/kaanf/crew/androidapp/MainActivity.kt` | Koin/push initialization and Compose `App()` |
| iOS startup | `iosApp/iosApp/iOSApp.swift`, `iosApp/iosApp/ContentView.swift`, `composeApp/src/iosMain/kotlin/com/kaanf/crew/MainViewController.kt` | SwiftUI hosts `ComposeUIViewController`; Koin and push bridge |
| Shared root | `composeApp/src/commonMain/kotlin/com/kaanf/crew/App.kt`, `composeApp/src/commonMain/kotlin/com/kaanf/crew/MainViewModel.kt` | Theme, auth/update/connectivity state and board QR monitor |
| DI composition | `composeApp/src/commonMain/kotlin/com/kaanf/crew/di/KoinHelper.kt` | Core + auth/home/game + app Koin modules |
| Navigation | `composeApp/src/commonMain/kotlin/com/kaanf/crew/navigation/NavigationRoot.kt`, `feature/home/presentation/src/commonMain/kotlin/com/kaanf/home/presentation/navigation/HomeGraph.kt` | Typed graphs, callbacks and routes |
| Representative feature | `feature/home/presentation/src/commonMain/kotlin/com/kaanf/home/presentation/dashboard/DashboardViewModel.kt`, `feature/home/domain/src/commonMain/kotlin/com/kaanf/home/domain/usecase/GetEventsUseCase.kt`, `feature/home/data/src/commonMain/kotlin/com/kaanf/home/data/repository/EventRepositoryImpl.kt` | StateFlow/action/event → use case → repository → Ktor |
| HTTP and refresh | `core/data/src/commonMain/kotlin/com/kaanf/core/data/networking/HttpClientFactory.kt`, `core/data/src/commonMain/kotlin/com/kaanf/core/data/networking/SessionRefresher.kt` | JSON, bearer auth, bounded timeouts and guarded session refresh |
| Session storage | `core/data/src/commonMain/kotlin/com/kaanf/core/data/session/DataStoreSessionStorage.kt` | Secure-store session + legacy Preferences migration |
| Reusable UI | `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/theme/CrewTheme.kt`, `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/component/button/BaseButton.kt` | Theme and shared controls |
| Representative tests | `core/data/src/commonTest/kotlin/com/kaanf/core/data/networking/SessionRefreshFlowTest.kt`, `core/domain/src/commonTest/kotlin/com/kaanf/core/domain/model/profileboard/BoardStickerPlacementTest.kt` | Session races and pure geometry; source presence is not execution evidence |

## Build and external boundaries

- Wrapper: `gradle/wrapper/gradle-wrapper.properties`; catalog: `gradle/libs.versions.toml`.
- Conventions: `build-logic/convention/src/main/kotlin/`; see [KMP](KMP.md).
- Apple project: `iosApp/iosApp.xcodeproj/project.pbxproj`; shared scheme:
  `iosApp/iosApp.xcodeproj/xcshareddata/xcschemes/iosApp.xcscheme`.
- CI: `.github/workflows/pr-tests.yml`; release lanes: `fastlane/Fastfile`.
- Compose resource accessors and BuildKonfig sources are generated under module build
  directories. Change resource/catalog/convention inputs, not generated output.
- The HTTP/WebSocket backend is the sibling `../crew-api` repository, confirmed by the
  owner on 2026-11-04. Its actual controllers, services and tests are implementation
  evidence; its README is outdated and must not be used as contract evidence.
  `core/data/src/commonMain/kotlin/com/kaanf/core/data/networking/UrlConstants.kt` defines
  client endpoints. A deployed-version match and canonical OpenAPI artifact remain unverified.
  Selected backend sources and base revision are recorded in [API_CONTRACTS](API_CONTRACTS.md).
- Camera, permission, secure storage, push and review APIs cross platform boundaries;
  [KMP](KMP.md) identifies real adapters. Local/service/signing files are not reproduced.
