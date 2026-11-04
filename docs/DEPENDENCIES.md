# Dependencies and toolchain

Observed 2026-11-04; canonical versions: `gradle/libs.versions.toml`. Wrapper:
`gradle/wrapper/gradle-wrapper.properties`; convention plugins: `build-logic/convention/`.
Version declarations were read locally; no external compatibility certification is implied.

| Concern | Configured version / source | Actual use / limits |
| --- | --- | --- |
| Build toolchain | Gradle 9.1.0, Kotlin 2.3.20, AGP 9.0.0, KSP 2.3.4 | Conventions and module plugins; offline task configuration passed, target builds not run |
| Shared UI | Compose Multiplatform 1.11.1, Material 3 1.9.0 | Shared Compose; Android BOM 2026.05.01 also declared |
| Navigation / lifecycle | Navigation Compose 2.9.2, Compose lifecycle 2.9.1 | Typed routes and lifecycle-aware state collection |
| DI | Koin 4.1.0 | App/feature/platform modules, factories/singletons/ViewModels |
| Async / JSON | Coroutines 1.10.2, serialization 1.9.0 | Shared flows/jobs and data DTOs |
| Networking | Ktor 3.3.3 | REST/WebSockets; Android OkHttp and iOS Darwin engines |
| Persistence | DataStore 1.1.7 | Settings and board JSON cache; secure sessions use platform APIs |
| Images | Coil 3.3.0 | Shared image loading with Ktor integration |
| QR / camera | Chaintech QR Kit 3.1.3; Android CameraX/ML Kit catalog entries | Inspect feature platform source/build dependencies for changes; catalog is not execution proof |
| Test/performance | kotlin-test; coroutine test; JUnit; AndroidX Macrobenchmark 1.5.0-rc02 | See [TESTING](TESTING.md) and actual module build files |
| Release | Gemfile/Gemfile.lock, `fastlane/Fastfile`; Firebase host integrations | Bundle/signing/service access unverified |

Catalog entries are not proof of active runtime use: Room/SQLite conventions exist without
an observed configured database, Detekt/ktlint tasks were absent, and Wiretap references
are commented out. Desktop version entries do not declare a desktop target. Stale comments
mentioning Kotlin 2.2 do not override the catalog's Kotlin 2.3.20 declaration.

Before adding/upgrading, check existing solutions and consuming targets/artifact variants.
Confirm real common APIs, Apple architecture support, compiler/plugin compatibility and
runtime/build cost using authoritative version-specific sources when a change is requested.
Do not combine unrelated Gradle/Kotlin/AGP/Xcode/UI-library upgrades. This documentation
setup installs or upgrades nothing.

For a significant choice record the problem, alternatives (including no new library),
version/targets, costs, migration, distribution/license considerations and exit path in an
[ADR](decisions/README.md). No historical rationale or approval was inferred from a dependency entry.
