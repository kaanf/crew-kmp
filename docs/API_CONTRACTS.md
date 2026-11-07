# API integration boundaries

Reviewed 2026-11-04. This is a client integration inventory, not a canonical server contract.
The owner identified the sibling backend repository `../crew-api` on 2026-11-04.
Inspect current source/controllers/services/tests; its README is outdated and is not
contract evidence. The owner controls the project; deployed-version correspondence,
a canonical OpenAPI artifact and compatibility guarantees remain UNVERIFIED (Q03).
Client source paths below are relative to this mobile repository.

## Integration inventory

| Concern | Observed client source | Observed operations |
| --- | --- | --- |
| HTTP configuration | `core/data/src/commonMain/kotlin/com/kaanf/core/data/networking/HttpClientFactory.kt` | JSON with unknown-key tolerance, bearer authentication, 20-second request/socket timeouts, WebSockets plugin |
| Route construction / errors | `core/data/src/commonMain/kotlin/com/kaanf/core/data/networking/HttpClientExt.kt`, `core/domain/src/commonMain/kotlin/com/kaanf/core/domain/util/DataError.kt` | HTTP helpers, response/business error mapping and cancellation-aware `mapCatching` |
| Endpoints | `core/data/src/commonMain/kotlin/com/kaanf/core/data/networking/UrlConstants.kt` | HTTP and event WebSocket base locations; do not assume a selectable staging environment |
| Auth | `feature/auth/data/src/commonMain/kotlin/com/kaanf/auth/data/repository/AuthRepositoryImpl.kt`, `core/data/src/commonMain/kotlin/com/kaanf/core/data/networking/SessionRefresher.kt` | Auth requests and `/auth/refresh`; DTOs remain in data |
| Session end / deletion | `core/data/src/commonMain/kotlin/com/kaanf/core/data/repository/AuthSessionRepositoryImpl.kt` | Logout clears local state then best-effort revocation; `DELETE /profile` uses the captured account bearer and disables automatic auth retry |
| Events | `feature/home/data/src/commonMain/kotlin/com/kaanf/home/data/repository/EventRepositoryImpl.kt` | Event list/detail/participants and share-code resolution |
| Tickets / check-in | `feature/home/data/src/commonMain/kotlin/com/kaanf/home/data/repository/TicketRepositoryImpl.kt` | POST event tickets, GET my-ticket and POST check-in with entry code |
| Match / tasks | `feature/game/data/src/commonMain/kotlin/com/kaanf/game/data/repository/MatchRepositoryImpl.kt` | Participant token, invites, ready/results, task offers/confirmation, snapshots and scoreboard |
| Real-time events | `feature/game/data/src/commonMain/kotlin/com/kaanf/game/data/repository/EventConnectionClientImpl.kt` | Lifecycle-aware connection/reconnect and shared session refresh; no server protocol guarantee inferred |
| Board / QR render | `core/data/src/commonMain/kotlin/com/kaanf/core/data/repository/ProfileBoardRepositoryImpl.kt`, `core/data/src/commonMain/kotlin/com/kaanf/core/data/dto/ProfileBoardDto.kt` | GET/PUT `/profile/board`, GET `/profile/board/qr`, POST `/profile/board/qr/recover` (re-render when the READY image is missing from storage; called once per failed image URL), revision-aware local caching and bounded render polling |

## Backend source inspected

Backend base HEAD: `7c16ed69598299bb710ac79288b209e54c69f00f`; observations concern
its working-tree source, not a verified production deployment. No backend files were
modified, services started or tests executed. The backend README was not used.
Paths below are relative to the backend repository root (`../crew-api` from this repo):

| Source | Observed responsibility |
| --- | --- |
| `src/main/kotlin/com/kaanf/crew/api/controller/UserController.kt` | Profile endpoint wiring, including account deletion |
| `src/main/kotlin/com/kaanf/crew/service/UserService.kt` | Account-row deletion, active match forfeiture, seat release, participant graph cleanup and best-effort profile image removal |
| `src/main/kotlin/com/kaanf/crew/service/ParticipantGraphDeleter.kt` | Ordered invite/match/participant/ticket deletion |
| `src/main/kotlin/com/kaanf/crew/service/AuthService.kt` | Refresh session locking/rotation and logout session revocation |
| `src/test/kotlin/com/kaanf/crew/service/AuthSessionRevocationTest.kt` | Session revocation test sources; not executed here |
| `src/test/kotlin/com/kaanf/crew/service/ProfileBoardValidationTest.kt`, `src/test/kotlin/com/kaanf/crew/service/ProfileBoardPersistenceTest.kt` | Board validation/persistence test sources; not executed here |

This targeted read does not establish a complete data-retention, backup-purge, event/payment
or QR security contract. Inspect affected backend implementations and migrations before
resolving the remaining invariants. Local code alone does not establish deployed behavior.

## Rules for changes

Inspect the actual request/response DTO and mapper for the changed call. Record null,
missing and unknown-field semantics, units/time zones, IDs, pagination and business codes
from real evidence. Preserve transport/domain boundaries and cancellation. A client guard
or DTO default is not server-side authorization or a promised default in the backend.

Do not add blanket retries to tickets, check-in, match mutations or deletion. Confirm
idempotency and old-client/new-server behavior first. Refresh is centralized and guarded
against stale session writes; bypassing it needs explicit justification. Keep credentials,
live QR values and personal records out of fixtures, logs and documentation.

Focused existing fixtures: `core/data/src/commonTest/kotlin/com/kaanf/core/data/networking/`
and `feature/game/data/src/commonTest/kotlin/com/kaanf/game/data/mappers/`.
A future contract change must link an authoritative schema/owner decision. No endpoint,
SLA, retry guarantee or backend test result is invented by this document.
