# Testing and verification

Reviewed 2026-11-04. Frameworks and source sets were inspected; application tests were
**not run** during documentation setup. Exact commands and execution statuses are in
[COMMANDS](COMMANDS.md); project automation and its limitations are in [AI_TOOLS](AI_TOOLS.md).

## Owner-available physical devices

Reported by the owner on 2026-11-04. Availability is not a completed test, a connected-device
check or a promise that these devices cover all supported hardware.

| Device | Role / limitation | OS / evidence |
| --- | --- | --- |
| Samsung Galaxy S24+ | Physical Android checks; owner identifies it as a flagship, so performance results must not be generalized to lower-end Android devices | Android/One UI version not supplied; no test run recorded |
| iPhone 11 | Physical iOS checks, including camera/QR and lifecycle where applicable | iOS version not supplied; compatibility with this checkout must be checked before running |

The owner has deferred OS-version details and will update them later. Do not infer versions
from the device models or treat missing details as verified.

For each actual run record OS/build, locale, text size, display refresh settings and test
scenario. Minimum-version and lower-end Android coverage are still gaps; simulator/emulator
checks can supplement state/layout/compatibility evidence but cannot establish physical-device
performance. No new hardware purchase, OS update, device setting or supported-version change
is prescribed. Performance and accessibility acceptance criteria remain open (Q08/Q09).

## Existing coverage (repository-relative source paths)

| Area | Examples / current limitation |
| --- | --- |
| Domain rules | `feature/auth/domain/src/commonTest/` validators/sign-in methods; `core/domain/src/commonTest/` Result and board placement |
| Data/session/network | `core/data/src/commonTest/` secure-store migration, refresh races, logging redaction, mappers and board revision/account isolation |
| Presentation helpers | `core/presentation/src/commonTest/` sampled image sizing and QR monitor; `feature/home/presentation/src/commonTest/` Markdown; `feature/game/presentation/src/commonTest/` quest grouping |
| UI semantics/editor | `feature/auth/presentation/src/commonTest/` password visibility semantics; `core/designsystem/src/commonTest/` board editor/QR loading |
| Palette geometry | `core/palette/src/commonTest/`; Android host tests explicitly enabled |
| Android app host | `androidApp/src/test/java/com/kaanf/crew/androidapp/OnboardingViewModelTest.kt`, plus sample tests |
| Device tests | `androidApp/src/androidTest/` and game domain sample source directories; source presence does not guarantee configured execution |
| Keychain roundtrip | `core/data/src/iosIntegrationTest/`, opt-in `crewKeychainIntegration`; signed host still needed |
| Real UI/backend | `.maestro/` flows and `tools/maestro/` runners; require explicit fixtures/device and can mutate/delete accounts |

KMP conventions add `kotlin-test`; coroutine test dependencies are declared where used.
Tests use safe fakes and Ktor engine fixtures in the inspected data suites. No broad
mocking framework or screenshot test plugin is inferred from the templates.

## Choose checks by behavior

| Change | Useful verification |
| --- | --- |
| Pure shared rule | Focused common tests including boundary/error cases on a configured target |
| ViewModel/coroutine | State transitions, cancellation and stale/concurrent results |
| API/cache/session | Contract fixtures, mapping/errors, freshness, account isolation and retry races |
| Platform adapter | Relevant platform checks plus permission/lifecycle integration |
| Shared UI/navigation | State/semantics checks and affected Android/iOS flow |
| Regression | Minimal reproduction and a meaningful maintainable regression test |
| Documentation only | Relative links, source paths, consistency and scope of diff |

Tests should assert observable behavior, not mirror implementation details. Reuse existing
clock/dispatcher/fixture patterns where applicable; avoid sleeps to hide races. Do not
introduce a migration test for a database that is not configured in this repository.

## Execution and CI boundaries

Most common tests execute on iOS simulator targets; most shared Android targets do not
enable host/device tests. `core:palette:testAndroidHostTest` and Android app JVM tests
are distinct configured paths. The game domain sample test directories alone are not proof
of registered tasks. A common source file does not prove Android and iOS execution.

`.github/workflows/pr-tests.yml` uses macOS/JDK 21 and runs `allTests` plus Android Kotlin
compilation. PR job execution is restricted to the `dev` source branch; main push and
manual execution are also configured. Despite its name, the job invokes no explicit lint
task. It uploads test reports; latest CI results and required branch checks are UNVERIFIED.

The Maestro README records partial historical Android device evidence and explicitly
leaves scenarios unverified, including the latest UI deletion cleanup. Do not reinterpret
that as a fresh uninterrupted passing run. iOS camera/scanner/VoiceOver and physical-device
performance require separate evidence.

Record command/scenario, revision/environment, result and meaningful artifacts. Report
passed, failed, blocked and not run separately. Once relevant checks pass, broaden only
for a new concern. Never update a screenshot baseline merely to hide an unexplained change.

## Favorite music coverage (2026-11-06)

`core/data/src/commonTest/kotlin/com/kaanf/core/data/MusicRepositoryTest.kt` covers
encoded search/country/pagination, input rejection, absent favorite, save/delete routes,
quota/unavailable errors, unsafe metadata links and cancellation. Its cancellation test
exposed an existing platform helper defect; explicit rethrow was added on Android and
iOS. All 44 data tests passed on iOS simulator; Android compilation passed, but these
common tests were not executed on Android.

The backend worktree `../crew-api-favorite-music` contains `SpotifyMusicCatalogTest`,
`FavoriteMusicServiceTest` and `FavoriteMusicPersistenceTest`: 12 passing mock-provider,
service and H2/API-security tests. H2 tests verify current-account-only favorite writes,
preserved profile fields, Crew auth and missing-provider configuration behavior. They
use Hibernate DDL and do not validate the PostgreSQL Flyway migration. Commands and
runtime/provider limitations are in [COMMANDS](COMMANDS.md#favorite-music-infrastructure-verification-2026-11-06).
