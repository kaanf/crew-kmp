# Coding standards

Observed conventions and preserved project guidance, reviewed 2026-11-04.
`.editorconfig` sets UTF-8/LF, four-space indentation, final newline, ktlint official style
and Kotlin trailing commas. An active ktlint/Detekt task was not found; see [COMMANDS](COMMANDS.md).

## Kotlin and API design

Use names and types that express domain meaning. Model meaningful states explicitly; avoid
parallel booleans that permit invalid combinations. Prefer immutable externally observable
state and limit mutable ownership. Follow existing visibility/serialization conventions.
Use the project's actual date/time, ID, money and result/error abstractions.

## Coroutines and Flow

- Use structured concurrency with an identifiable scope owner and lifetime.
- Preserve cancellation. Broad catches/result wrappers must not turn cancellation into a
  normal domain failure; inspect the API's behavior before wrapping suspend calls.
- Avoid blocking the main thread; use appropriate existing dispatchers for blocking work.
- Define collection ownership and cleanup across UI/lifecycle boundaries.
- Consider repeated subscriptions, event loss/replay, concurrent state writes and stale results.
- Use virtual-time/test-dispatcher patterns already present where timing matters.

## Error handling and data

Translate transport/storage failures at existing boundaries; show actionable UI states without
leaking implementation details. Do not hide failures with empty catch blocks or fabricated
success values. Validate external inputs and keep nullable/default semantics explicit.

## Style and comments

Use configured tooling for mechanical style. Comments explain constraints and reasons;
avoid narrating obvious code. No broad reformatting in unrelated feature changes.
Preserved project preferences: keep functions small; favor extension functions over utility
classes, sealed action/event interfaces and data-class state. Prefer descriptive names over
comments and avoid deep nesting, speculative abstraction and premature optimization.

## Existing examples (repository-relative)

- State/action/event: `feature/home/presentation/src/commonMain/kotlin/com/kaanf/home/presentation/dashboard/`.
- Domain filtering/sorting: `feature/home/domain/src/commonMain/kotlin/com/kaanf/home/domain/usecase/GetEventsUseCase.kt`.
- Results/errors: `core/domain/src/commonMain/kotlin/com/kaanf/core/domain/util/Result.kt`
  and `core/domain/src/commonMain/kotlin/com/kaanf/core/domain/util/DataError.kt`.
- Cancellation-aware mapping: `mapCatching` in
  `core/data/src/commonMain/kotlin/com/kaanf/core/data/networking/HttpClientExt.kt`.
- Concurrent refresh/storage: `core/data/src/commonMain/kotlin/com/kaanf/core/data/networking/SessionRefresher.kt`
  and `core/data/src/commonMain/kotlin/com/kaanf/core/data/session/DataStoreSessionStorage.kt`.
- User-visible errors: `core/presentation/src/commonMain/kotlin/com/kaanf/core/presentation/snackbar/`.

Preserve the existing date/time and ID types; the dashboard uses `kotlin.time.Clock`.
Do not add a mandatory use-case layer where an existing ViewModel uses a domain repository.
Architecture boundaries and current exceptions are recorded in [ARCHITECTURE](ARCHITECTURE.md).
