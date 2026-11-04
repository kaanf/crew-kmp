# Kotlin Multiplatform boundaries

Observed 2026-11-04; evidence revision: [PROJECT_MAP](PROJECT_MAP.md).

## Actual targets and source sets

- `build-logic/convention/src/main/kotlin/KmpLibraryConventionPlugin.kt` applies the
  Kotlin Multiplatform and AGP `com.android.kotlin.multiplatform.library` plugins.
- `build-logic/convention/src/main/kotlin/com/kaanf/crew/convention/KotlinMultiplatform.kt`
  configures Android (compile SDK 36, minimum 26), `iosArm64()` and `iosSimulatorArm64()`.
  Shared code lives in `commonMain`; platform adapters in `androidMain` / `iosMain`.
  Kotlin's hierarchy provides intermediate native/Apple/iOS source sets. Comments mentioning
  `iosX64` are not declarations of an Intel simulator target.
- `build-logic/convention/src/main/kotlin/CmsApplicationConventionPlugin.kt` contains
  `CmpApplicationConventionPlugin`; its filename and class name differ. Together with
  `build-logic/convention/src/main/kotlin/com/kaanf/crew/convention/KotlinIosTargets.kt`
  it builds static `ComposeApp` frameworks for both ARM64 iOS targets.
- `composeApp/build.gradle.kts` is a shared Android **library**, not the Android APK host.
  `androidApp/build.gradle.kts` packages the Android app.
- `screenshots/shared/build.gradle.kts` declares Android and only `iosSimulatorArm64`
  with static `CrewScreenshots`; no physical iOS screenshot target. These modules are opt-in.

## Apple integration

`iosApp/iosApp/ContentView.swift` implements `UIViewControllerRepresentable` and calls
`MainViewControllerKt.MainViewController()`. The implementation in
`composeApp/src/iosMain/kotlin/com/kaanf/crew/MainViewController.kt` returns
`ComposeUIViewController { App() }`. UI sharing on iOS is confirmed from these files.

`iosApp/iosApp/iOSApp.swift` initializes Koin and push bridging. The Xcode build phase in
`iosApp/iosApp.xcodeproj/project.pbxproj` invokes
`:composeApp:embedAndSignAppleFrameworkForXcode` and selects JDK 21, falling back to 17.
The configured iOS deployment target is 18.2 and Swift language setting is 5.0.
Firebase Messaging is referenced through Swift Package Manager. No new bridging library
is required by this documentation. Changes to Swift-visible declarations require checking
Swift call sites and framework build/link behavior; neither was executed in this setup.

## Platform adapters

| Capability | Real source locations (repository-relative) |
| --- | --- |
| Session credentials | `core/data/src/androidMain/kotlin/com/kaanf/core/data/session/AndroidSecureSessionStore.kt`, `core/data/src/iosMain/kotlin/com/kaanf/core/data/session/IosSecureSessionStore.kt` |
| App review UI | `core/presentation/src/commonMain/kotlin/com/kaanf/core/presentation/review/`, `core/presentation/src/androidMain/kotlin/com/kaanf/core/presentation/review/`, `core/presentation/src/iosMain/kotlin/com/kaanf/core/presentation/review/` |
| Connectivity | `core/domain/src/commonMain/kotlin/com/kaanf/core/domain/networking/ConnectivityObserver.kt`; Android/iOS adapters in `core/data/src/androidMain/kotlin/com/kaanf/core/data/networking/`, `core/data/src/iosMain/kotlin/com/kaanf/core/data/networking/` |
| HTTP engines / platform DI | `core/data/src/androidMain/kotlin/com/kaanf/core/data/di/CoreDataModule.android.kt`, `core/data/src/iosMain/kotlin/com/kaanf/core/data/di/CoreDataModule.ios.kt` |
| Preferences path | `core/data/src/androidMain/kotlin/com/kaanf/core/data/storage/DataStoreFactory.android.kt`, `core/data/src/iosMain/kotlin/com/kaanf/core/data/storage/DataStoreFactory.ios.kt` |
| Permissions/media | `core/presentation/src/commonMain/kotlin/com/kaanf/core/presentation/permission/`, `core/presentation/src/androidMain/`, `core/presentation/src/iosMain/` |
| Opponent scanner | `feature/game/presentation/src/androidMain/kotlin/com/kaanf/game/presentation/scanopponent/component/QrCameraScanner.android.kt`, `feature/game/presentation/src/iosMain/kotlin/com/kaanf/game/presentation/scanopponent/component/QrCameraScanner.ios.kt` |
| Push bridge | `androidApp/src/main/java/com/kaanf/crew/androidapp/PushTokenSync.kt`, `composeApp/src/iosMain/kotlin/com/kaanf/crew/push/PushTokenBridge.kt`, `iosApp/iosApp/iOSApp.swift` |

Common code can only use APIs available to all consuming targets. Prefer existing DI or
expect/actual boundaries for platform capabilities; keep portable logic shared. Preserve
permission denial, cancellation, background/foreground and observer cleanup behavior on
each platform independently. Android behavior is not proof of equivalent iOS behavior.

## Verification

Use exact task names in [COMMANDS](COMMANDS.md). Pure shared rules need meaningful
common tests on configured targets; platform adapters need integration evidence; exported
API changes need framework/link and Swift call-site checks. `commonTest` source presence
does not prove tests ran. Most KMP modules lack Android host/device test configuration;
`core:palette` explicitly enables host tests. Keychain integration is separately gated.
