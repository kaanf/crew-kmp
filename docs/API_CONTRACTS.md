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
| Board / QR render | `core/data/src/commonMain/kotlin/com/kaanf/core/data/repository/ProfileBoardRepositoryImpl.kt`, `core/data/src/commonMain/kotlin/com/kaanf/core/data/dto/ProfileBoardDto.kt` | GET/PUT `/profile/board`, GET `/profile/board/qr` (checks READY images in storage and automatically requeues confirmed missing files as PENDING; no separate recovery request), revision-aware local caching and bounded render polling |

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

## Favorite music infrastructure (2026-11-06)

Implemented on `feat/favorite-music` in mobile and backend (from backend `dev` at
`a291f99`). Backend work was initially prepared/tested in `../crew-api-favorite-music`;
backend feature commit: `6659fef`. The primary `../crew-api` worktree may be on
another change branch; check Git status before selecting a working directory. This is
source/test implementation, not a deployed API or confirmation of Spotify production access.
No screens, ViewModels or navigation were changed.

All routes below require the existing **Crew bearer session**, not Spotify user login.
Backend routes include `/api`; shared Ktor helpers add that base to the paths below.

| Method / client path | Input | Success |
| --- | --- | --- |
| GET `/music/search` | `q` (trimmed, 2–100 characters, no control characters), `market` (ISO country), `offset` (0–1000, default 0) | `{tracks: [...], nextOffset: number|null}`; fixed page size 10 |
| GET `/profile/favorite-music` | `market` | `{track: object|null}`; no favorite is HTTP 200 with null |
| PUT `/profile/favorite-music` | `{trackId: string, market: string}` | `{track: object}`; server verifies the ID with Spotify before writing |
| DELETE `/profile/favorite-music` | none | HTTP 204; clearing an empty favorite succeeds |

Track shape: `{id, title, artists: string[], spotifyUrl}`. IDs are 22 alphanumeric
characters; links are canonical `https://open.spotify.com/track/{id}`. No cover art,
preview, playback URL or Spotify user data is returned. Search results contain the
provider's metadata, not a locally invented catalog. Pagination uses `nextOffset`;
null means stop. Markets are explicit, uppercased and validated server-side; clients
must choose the user's intended catalog country rather than hardcode a global default.

Errors use existing `{code,message}`: `MUSIC_INVALID_REQUEST` (400),
`MUSIC_TRACK_NOT_FOUND` (404), `MUSIC_RATE_LIMITED` (429 with `Retry-After`),
`MUSIC_UNAVAILABLE` (503). Crew auth errors remain 401. Provider 401/403 do not
become Crew 401 or log the user out. The mobile HTTP mapper represents 503 as
`DataError.Remote.SERVICE_UNAVAILABLE`; 400/404/429 retain business codes.

`MusicRepository` in `core:domain` and `MusicRepositoryImpl`/`MusicDto` in `core:data`
provide search/get/save/clear with Koin registration. They do not cache account data
or queue writes. Mapping rejects invalid track metadata/links and preserves coroutine
cancellation. A shared HTTP cancellation defect found by the new regression test was
fixed in both existing Android/iOS platform helpers.

Backend `MusicController`, `FavoriteMusicService` and `SpotifyMusicCatalog` implement
the routes. `UserRepository.updateFavoriteMusic` updates only the favorite column,
preserving unrelated concurrent profile edits. Flyway `V46__favorite_music.sql` adds
nullable `user_service.users.favorite_spotify_track_id`; existing accounts start empty,
and deleting the user removes the favorite with that row. Only the ID is persisted;
reading a nonempty favorite requires Spotify availability. Unavailable/deleted tracks
return an error without silently clearing the selection.

### Server configuration and provider limits

Set `SPOTIFY_CLIENT_ID` and `SPOTIFY_CLIENT_SECRET` on the **backend only**; these map
to `spotify.client-id` / `spotify.client-secret` in its `application.yml`. Do not add
secrets to mobile `local.properties`, BuildKonfig, xcconfig, logs or committed files.
Missing credentials leave the app running, return 503 for provider calls, and still
allow reading an empty favorite or removing a favorite. No Spotify SDK/dependency or
OAuth redirect is added to either mobile host.

The backend uses Client Credentials, caches the app token until 30 seconds before
expiry, retries a provider 401 once with a fresh token, and uses 5-second connect /
10-second read timeouts. A bounded, per-process cache holds up to 500 search pages
for 60 seconds (key: trimmed query, country, offset). These are shared public catalog
results, not favorites. Provider 429 starts a per-process cooldown using `Retry-After`
(default 60 seconds, maximum one day); no automatic retry loop or quota bypass exists.
Caches/cooldown are not distributed across server instances and do not guarantee a
particular Spotify quota. Provider error bodies/tokens are not exposed to callers.

Future UI should debounce/cancel searches, show attribution and a Spotify link, and
handle service/quota errors. Infrastructure does not approve production rights or
resolve Spotify policy applicability to Crew's game features. Confirm provider access
and usage terms before exposing this feature publicly.

Verification and unverified deployment/migration/live-provider limits: [COMMANDS](COMMANDS.md).
Provider references: [Search API](https://developer.spotify.com/documentation/web-api/reference/search),
[Client Credentials](https://developer.spotify.com/documentation/web-api/tutorials/client-credentials-flow),
[Developer Policy](https://developer.spotify.com/policy).

### Live provider check (2026-11-07)

Using the owner's backend-local environment without printing credentials/tokens,
Client Credentials token acquisition and a direct Spotify `Duman` search for market
`TR` succeeded, returning 10 tracks with ID/title/artists. This verifies those
credentials and catalog access at that time, not the deployed Crew API, favorite
persistence, production quotas or usage-policy approval. No private account data was
requested; no live Crew mutation was performed.

Before first deployment, the unapplied favorite migration was renumbered from V33 to
V46 during main/dev reconciliation because main already contained migrations V33–V45.
Existing applied migrations were preserved.

### QR status self-repair (2026-11-07)

Opening the board refreshes its QR status. GET `/profile/board/qr` checks a READY
image outside the database transaction; confirmed missing storage objects are
requeued under a row lock and returned as PENDING. Mobile caches intermediate
PENDING responses and polls until READY/FALLBACK. An image download failure may
recheck this same GET once per failed URL. The separate POST recovery route and
repository operation were removed before deployment. Storage errors other than
confirmed missing objects remain failures and do not requeue the render.

Deployment remains required: the inspected production deployment `7c16ed6` lacks
this check. No production mutation/deployment is included in local verification.

### Favorite film / TMDB (2026-11-07)

Shared `MovieRepository` / `MovieRepositoryImpl` use the existing authenticated Crew
Ktor client. GET `/movies/search` takes `q`, `language`, `page=1` and returns
`{movies,nextPage}`. GET/PUT/DELETE `/profile/favorite-movie` read/save/remove the
current user's choice; GET takes `language`, PUT sends `{movieId,language}`, DELETE
returns 204. Movie metadata is `{id,title,releaseYear,posterUrl,tmdbUrl}` with nullable
year/poster; only fixed TMDB website/image URLs are accepted. Input validation,
nonadvancing-page rejection, error mapping and cancellation follow existing data helpers.

Backend source is in `../crew-api-tmdb`, branch `feat/tmdb-movies`, based on `dev`.
`TMDB_READ_ACCESS_TOKEN` stays on the server; mobile needs no token or TMDB SDK.
Backend `V47__favorite_movie.sql` stores only a positive TMDB movie ID per user.
Search excludes adult titles and save verifies details before a short DB transaction.
Errors: `MOVIE_INVALID_REQUEST` (400), `MOVIE_NOT_FOUND` (404),
`MOVIE_RATE_LIMITED` (429), `MOVIE_UNAVAILABLE` (503). Missing provider credentials
return 503 for searches/details without breaking startup or removal. No offline writes
or metadata fallback are added. Provider failure retains the saved ID.

The API Read Access Token is configured in ignored backend environment files. A direct
live TMDB search returned 20 results and film detail lookup succeeded. Production
deployment, PostgreSQL migration execution, live Crew endpoint flow and runtime device
flows remain unverified.

## Profile board Loves (2026-11-07)

Authenticated GET/PUT `/api/profile/board` now include `hobbies`, an ordered string
array (default empty on reads). PUT accepts at most five distinct case-insensitive
values, each 1–24 Kotlin UTF-16 characters, trimmed and without control characters.
An omitted/null field preserves saved values for legacy clients; `[]` clears them.
Existing revision conflicts and authenticated-account ownership apply. Schema versions
1–3 remain supported; this is an additive field. No provider or new dependency is used.
Mobile includes the list in board DTO/domain/cache and Done/Cancel editing behavior.
Deploy the backend migration/API before relying on mobile persistence: older servers
may ignore this field. Backend worktree: `../crew-api-loves`, `feat/profileboard-loves`,
based on dev `0ceaca1`. V48 adds JSONB `profile_boards.hobbies` with an empty-array
default and array/count constraint. Account deletion follows existing board ownership.

The mobile `ProfileBoardDto.hobbies` field always encodes its default empty list.
Without this per-field override, Kotlin serialization omits `[]` and the server
correctly preserves the old selection under the legacy omission contract.
