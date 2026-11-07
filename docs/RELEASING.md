# Releasing

Observed scripts reviewed 2026-11-04. This describes configured operations, not a verified
release procedure or deployment authorization. The project owner is confirmed; required
checks, monitoring and recovery procedure are still being planned (Q11). See the
[planning worksheet](ai/DECISION_WORKSHEET.md) for proposals, not configured automation.

## Current implementation

- `gradle/libs.versions.toml` declares version name 1.2 / code 22 at review time.
  `fastlane/Fastfile` treats that catalog as its version source and `bump` updates version
  fields, including the Apple configuration in `iosApp/Configuration/Config.xcconfig`.
- `bundle exec fastlane bump` increments build number; optional `version:<value>` changes
  marketing version. It writes project files and is not a read-only check.
- `bundle exec fastlane ios_upload` archives through `build_app` and calls
  `upload_to_testflight`. It does not bump. App Store Connect access/signing are prerequisites;
  this is not evidence of a production App Store rollout.
- `bundle exec fastlane android_apk` invokes `androidApp:assembleRelease` and produces
  a signed APK if signing configuration is valid. It is not a Google Play upload lane.
- `bundle exec fastlane release` composes bump, iOS upload and Android APK generation.
- Firebase App Distribution is configured in `androidApp/build.gradle.kts`; actual access
  and distribution results were not verified. Do not infer an active release pipeline.
- `.github/workflows/pr-tests.yml` performs tests/Android compilation, not store publication.

Sources: `fastlane/Fastfile`, `fastlane/Appfile`, `Gemfile`, `Gemfile.lock`,
`iosApp/iosApp.xcodeproj/project.pbxproj`. Secret/signing contents are deliberately omitted.
Commands/status: [COMMANDS](COMMANDS.md). No lane or release build ran during this setup.

## Before an authorized release

Follow [BRANCH_STRATEGY](BRANCH_STRATEGY.md): integrate task PRs into `dev`, prepare
a `dev` → `main` release PR, and let the owner merge it with a merge commit after
validation. Synchronize `main` back to `dev` through a merge-commit PR. Merging is not
store distribution; run upload lanes or create release tags only when explicitly requested.

Confirm intended version/environment and actual owner/approval requirements; collect
appropriate CI and Android/iOS critical-flow evidence. Verify auth/QR/API compatibility
and relevant migration behavior. Prepare accurate release notes/store/privacy information
without inventing policy. Identify rollout, monitoring and recovery ownership before upload.

## Recovery and evidence

Backend compatibility, staged rollout, feature flags, retained artifacts and reversible
migration limits are UNVERIFIED. Do not promise a generic rollback where old clients/data
may be incompatible. Record version/commit/artifacts, environment, actual check results,
rollout decision and owner after a real release. The new changelog contains no shipped claims.

## Favorite music infrastructure (2026-11-06, not released)

No user-facing music UI is enabled. Before deploying a consumer UI, deploy the backend
contract/migration, configure server-only Spotify credentials, verify live provider
access and resolve production usage/attribution requirements. Mobile compilation and
mock-provider/H2 tests do not verify Spotify rights, production quota, migration on
PostgreSQL or a deployed endpoint. See [API_CONTRACTS](API_CONTRACTS.md#favorite-music-infrastructure-2026-11-06).
