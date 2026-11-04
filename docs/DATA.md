# Data, persistence and offline behavior

Observed 2026-11-04; revision context: [PROJECT_MAP](PROJECT_MAP.md).
Current active persistence is Preferences DataStore plus platform secure session storage.
SQLDelight was mentioned in old agent guidance but is not the observed implementation.
Room exists in the dependency catalog/convention code; an active Room database/schema
was not found. This setup changes no storage implementation.

## Owner-approved target

On 2026-11-04 the owner accepted: show available current-user content offline; do not queue
ticket/check-in/match/board writes automatically; complete logout locally and clear user
caches even offline; consider account deletion successful only after server confirmation.
The exact contract, acceptance criteria and current implementation gaps are in
[OFFLINE_AND_ACCOUNT_DATA](features/OFFLINE_AND_ACCOUNT_DATA.md).
**Target acceptance is not implementation completion.** Cache cleanup remains a known gap.

## Data map (repository-relative sources)

| Category | Source of truth / storage | Lifecycle and limits | Evidence |
| --- | --- | --- | --- |
| Session credentials/user | Secure serialized session; StateFlow snapshot with generation | Migrates legacy Preferences entry, writes secure envelope before erasing plaintext; guarded updates/logout | `core/data/src/commonMain/kotlin/com/kaanf/core/data/session/DataStoreSessionStorage.kt` |
| Android session | AES-GCM ciphertext with Android Keystore key; AtomicFile under no-backup directory | Invalid/orphaned encrypted data is discarded; no plaintext fallback implemented | `core/data/src/androidMain/kotlin/com/kaanf/core/data/session/AndroidSecureSessionStore.kt` |
| iOS session | Keychain generic password, AfterFirstUnlockThisDeviceOnly | Production retains a signed-out envelope; isolated test namespace supports cleanup | `core/data/src/iosMain/kotlin/com/kaanf/core/data/session/IosSecureSessionStore.kt` |
| Current user | Derived from SessionStorage; updates preserve current auth | Not a separate database | `core/data/src/commonMain/kotlin/com/kaanf/core/data/repository/UserStoreImpl.kt` |
| Language / onboarding | Preferences DataStore | Product settings; exact retention commitments not established | `core/data/src/commonMain/kotlin/com/kaanf/core/data/settings/` |
| Profile board / QR render | Server responses cached as JSON under per-user Preferences keys | Revision comparison prevents older snapshots overwriting newer cache; observers switch with current user | `core/data/src/commonMain/kotlin/com/kaanf/core/data/repository/ProfileBoardRepositoryImpl.kt` |
| Event / match state | HTTP responses, in-memory ViewModel/socket state and snapshots | Dashboard can retain stale content after silent refresh failure; durable offline queue not verified | `feature/home/presentation/src/commonMain/kotlin/com/kaanf/home/presentation/dashboard/DashboardViewModel.kt`, `feature/game/data/src/commonMain/kotlin/com/kaanf/game/data/repository/EventConnectionClientImpl.kt` |
| Media | Coil and existing platform media adapters; local fixture PNGs for screenshots | Cache retention/eviction/privacy contract UNVERIFIED | `core/designsystem/build.gradle.kts`, `core/presentation/src/androidMain/`, `core/presentation/src/iosMain/`, `screenshots/shared/src/commonMain/composeResources/files/` |

## Consistency and migration

`SessionRefresher` uses a mutex and compare-and-set against session snapshots. Logout clears
local state before best-effort server revocation; deletion clears the captured session only
on successful response. See `core/data/src/commonMain/kotlin/com/kaanf/core/data/repository/AuthSessionRepositoryImpl.kt`.
A stale refresh must not restore a logged-out or replacement session.

Preferences path creation is centralized in
`core/data/src/commonMain/kotlin/com/kaanf/core/data/storage/DataStoreFactory.kt` with
platform-specific paths. Board DTO conversion handles supported schema versions in
`core/data/src/commonMain/kotlin/com/kaanf/core/data/dto/ProfileBoardDto.kt`; this is not
a relational database migration system. Do not edit cached serialized data as a migration shortcut.

Per-user board keys prevent ordinary observer cross-account reuse; this does **not** prove
all cached data is physically erased on logout. Whole-device backup behavior, account
retention/backup purge and downgrade guarantees remain UNVERIFIED (Q04). The owner-approved
local cleanup/offline target above is separate from those unresolved server promises.

Existing migration/race/account-isolation test sources are under `core/data/src/commonTest/`;
they were not executed during documentation setup. See [TESTING](TESTING.md).
