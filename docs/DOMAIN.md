# Crew domain and observed behavior

Reviewed 2026-11-04; revision and uncommitted-source caveat: [PROJECT_MAP](PROJECT_MAP.md).
“Confirmed implementation” below means observed in code, not a verified backend guarantee,
release claim or newly approved product invariant.

## Glossary and implementation evidence

| Term | Current meaning | Repository-relative source |
| --- | --- | --- |
| Event | Gathering with doors/start/end times, status, capacity, price and ticket state | `core/domain/src/commonMain/kotlin/com/kaanf/core/domain/model/event/` |
| Ticket / check-in | Event ticket retrieval/creation and entry-code check-in | `feature/home/data/src/commonMain/kotlin/com/kaanf/home/data/repository/TicketRepositoryImpl.kt` |
| Match | Participant interaction with ready/result/task/scoreboard states | `feature/game/domain/src/commonMain/kotlin/com/kaanf/game/domain/model/MatchSnapshot.kt`, `feature/game/presentation/src/commonMain/kotlin/com/kaanf/game/presentation/session/MatchSnapshotReducer.kt` |
| Match QR | Participant token retrieved for an event and submitted in invite operations | `feature/game/data/src/commonMain/kotlin/com/kaanf/game/data/repository/MatchRepositoryImpl.kt` |
| Profile board | Versioned layout with QR style/placement, up to four sticker slots and revision | `core/domain/src/commonMain/kotlin/com/kaanf/core/domain/model/profileboard/`, `core/data/src/commonMain/kotlin/com/kaanf/core/data/dto/ProfileBoardDto.kt` |
| Sticker | Bundled design identified independently of enum order, with slot, position, scale and rotation | `core/domain/src/commonMain/kotlin/com/kaanf/core/domain/model/profileboard/BoardSticker.kt` |

## Confirmed implementation

- Dashboard use case filters out events whose `endsAt` is not later than the client clock
  and sorts by `doorsAt`: `feature/home/domain/src/commonMain/kotlin/com/kaanf/home/domain/usecase/GetEventsUseCase.kt`.
- Event checkout ignores repeated actions while checking out and returns for locked,
  ended or already-ticketed events:
  `feature/home/presentation/src/commonMain/kotlin/com/kaanf/home/presentation/eventdetail/EventDetailViewModel.kt`.
  This is a client guard, not proof of capacity enforcement or payment idempotency.
- Snapshot state determines ready/result/winner/loser/task/scoreboard rendering;
  unknown state maps to idle in `MatchSnapshotReducer.kt` at the path above.
  The match session ViewModel and event socket reconcile server-driven state.
- Profile boards use revisions and per-account cache keys. Older cached revisions are
  retained over older responses; save failures are returned rather than fabricated as success:
  `core/data/src/commonMain/kotlin/com/kaanf/core/data/repository/ProfileBoardRepositoryImpl.kt`.
- Board geometry constrains QR/sticker placement and scale against layout bounds/protected
  areas; tests exist in `core/domain/src/commonTest/kotlin/com/kaanf/core/domain/model/profileboard/`.
  UI editor behavior is tested in `core/designsystem/src/commonTest/kotlin/com/kaanf/core/designsystem/profileboard/`.
  These test sources were not executed during this setup.

## Questions, not adopted invariants

UNVERIFIED (Q03): authoritative event eligibility/capacity rules; simultaneous scans,
self-match, duplicate requests, QR expiry/replay/revocation and mutation idempotency;
server-side board visibility/edit rights and revision conflict semantics; durable offline
writes and retry guarantees. Client code does not settle these server/product decisions.

Before changing related behavior, confirm the backend contract or owner decision. Keep
synthetic Given/When/Then cases in an actual feature spec based on
[FEATURE_TEMPLATE](features/FEATURE_TEMPLATE.md). No unapproved requirement is introduced here.


## Accepted product target (2026-11-04)

The owner approved the [offline/account-data contract](features/OFFLINE_AND_ACCOUNT_DATA.md).
It specifies cached-content display, no automatic queue for listed mutations, offline local
logout with user-cache clearing, and server-confirmed account deletion. Implementation gaps
are explicitly listed in that spec; these are not newly claimed existing guarantees.
