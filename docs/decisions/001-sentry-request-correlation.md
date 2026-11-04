# ADR-001: Native Sentry SDKs with shared request correlation

- Status: Proposed (implemented for owner review)
- Date: 2026-11-04
- Decision owner: Project owner; final review pending
- Related request: Basic Sentry setup and mobile/backend request correlation

## Context

Crew shares Ktor and Compose UI across Android/iOS and uses a Spring Boot 4 backend.
A failed mobile API action needs to be associated with its backend exception without
introducing a broad tracing/replay rollout or sending request credentials/content.

## Options

Keep local logs only: no remote crash reporting or cross-service search.
Use the KMP Sentry wrapper: one capture API, with additional Kotlin/Native SDK linkage
and wrapper compatibility to maintain. Use native SDKs with a small common callback:
native crash support and privacy hooks, with duplicated small startup configurations.

## Decision proposed for review

Native Android 8.62.0 and Cocoa 9.30.2 SDKs handle crashes; shared Ktor creates request
UUIDs and passes safe response summaries to native hosts. The JVM core SDK 8.62.0
captures explicit unexpected backend API failures. Avoid Spring auto-instrumentation,
replay and distributed traces initially. No UI or domain logic moves into native hosts.

## Consequences and validation

SDKs use their upstream MIT license. Runtime costs include crash handlers, local SDK
queues and network delivery when configured; no runtime performance benchmark is claimed.
SwiftPM downloads binary SDKs and increases dependency resolution/build cost. Native
startup/privacy code must be maintained on both mobile platforms. No persistence/API
body migration is needed; the optional response header works with old clients.

Removing native startup hooks, SDK dependencies, and the Ktor plugin reverses this
integration. Revisit for distributed tracing or richer Kotlin/Native error details.
See [SENTRY](../SENTRY.md) for configuration, data boundaries and release symbol limits.
