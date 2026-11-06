# ADR-002: Native PostHog adapters with a shared event vocabulary

- Status: Proposed; implemented for owner review
- Date: 2026-11-04
- Decision owner: Repository owner

## Context and options

Crew uses Kotlin 2.3.20 and a static Compose framework with direct Xcode integration.
The current official PostHog KMP SDK (0.5+) requires Kotlin 2.4 and SwiftPM linkage.
Options are deferring analytics, upgrading the toolchain for KMP, or using the official
native SDKs through thin host adapters. A custom Ktor transport would duplicate identity,
queueing and retry behavior already maintained by PostHog.

## Implemented proposal

Use Android 3.75.3 and iOS 3.59.3 (MIT), pinned in the catalog/Xcode project. Preserve
shared domain event definitions, Koin registration and ViewModel success boundaries.
Use the existing callback bridge approach to export safe shared payloads to Swift.
No toolchain upgrade or UI platform split is required. See [configuration and event
semantics](../POSTHOG.md).

## Consequences

Native SDKs add package/build/runtime cost and an offline event queue; no size or speed
claim has been measured. Two small configuration adapters and SDK property filters must
be maintained. Both SDKs support Android/iOS targets already used by Crew. This is a new,
disabled-by-default integration with no stored-data migration. Removing host setup and
bindings reverts collection. Revisit a common SDK after a separately justified Kotlin
upgrade. Production activation and privacy decisions remain with the owner.
