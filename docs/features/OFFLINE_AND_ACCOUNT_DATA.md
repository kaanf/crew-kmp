# Offline and account data behavior

Status: **owner-approved target behavior**, accepted 2026-11-04. Implementation is partial;
this documentation change does not modify application behavior. Owner: project owner.
Affected platforms: Android and iOS. Context: Q04 in [OPEN_QUESTIONS](../ai/OPEN_QUESTIONS.md).

## Accepted behavior

- When offline, show available content already held for the current user. Do not imply
  it is freshly loaded or that every screen is available offline.
- Ticket purchase/creation, check-in, match mutations and board saves are not automatically
  queued for later submission. Show failures clearly; do not present an unconfirmed server
  mutation as successful. This does not prohibit intentional read/snapshot reconciliation.
- Logout completes locally without requiring network availability. Clear credentials and
  user-specific caches so a following account cannot inherit the previous account's data.
  Best-effort server revocation must not block local sign-out.
- Account deletion is successful only after server confirmation. An offline/network/server
  failure must not display successful account deletion or silently queue the request.
- Following confirmed deletion, apply local account cleanup. Device-wide preferences such
  as language/onboarding are not automatically classified as user-specific data here;
  inventory them when implementing cleanup.

## Acceptance criteria

| Given | When | Then | Verification needed |
| --- | --- | --- | --- |
| Current user's content is already available | Connectivity is lost | Existing content can remain visible; no freshness/success claim | Android/iOS UI state and network-loss scenario |
| Offline | A ticket, check-in, match mutation or board save is attempted | Clear failure; no automatic durable mutation queue | Repository/state tests plus platform flow |
| Logged in, network unavailable | User logs out | Local sign-out completes and user caches are cleared | Storage/session tests, restart and account-switch check |
| Account A logged out | Account B logs in | A's cached content is not reused/displayed | Account-isolation regression tests |
| Offline or deletion request fails | User requests account deletion | No success indication and no queued destructive request | Error-path tests/platform flow |
| Server confirms deletion | Client receives success | Local session/account data cleared; restart remains signed out | Integration/cleanup and restart scenario |

## Existing implementation and gaps

- `core/data/src/commonMain/kotlin/com/kaanf/core/data/repository/AuthSessionRepositoryImpl.kt`
  clears the captured session before best-effort logout revocation; deletion clears it only
  on successful response. User-cache cleanup is not implemented by those methods.
- `core/data/src/commonMain/kotlin/com/kaanf/core/data/repository/ProfileBoardRepositoryImpl.kt`
  uses per-account Preferences keys; hidden/switched cache is not the same as physical deletion.
- `feature/home/presentation/src/commonMain/kotlin/com/kaanf/home/presentation/dashboard/DashboardViewModel.kt`
  retains prior content on silent refresh failure; it is not a full offline content store.
- Relevant backend deletion/session sources are listed in [API_CONTRACTS](../API_CONTRACTS.md).
  Their existence does not establish backup/CDN/media retention guarantees.

Before implementation, inventory account-owned Preferences entries, image/disk caches,
in-memory state, in-flight requests and QR render jobs. Verify that late work cannot
repopulate cleared account data. Do not remove the other account's data through a stale
completion. Choose focused tests from [TESTING](../TESTING.md).

## Remaining decisions and evidence

Server data retention/backup purge, cache expiry and device-level settings lifecycle remain
open. No runtime test, account deletion, network mutation or cache cleanup was executed
as part of accepting this target. This spec is the desired contract, not a passing test report.
