## Problem and resulting behavior

Describe the trigger and what the user/system does after this change.

## Scope

Link the issue/spec. Note relevant API, schema, dependency or design changes; omit sections
that do not apply. Avoid recounting conversation history or abandoned approaches.
Follow [branch strategy](../docs/BRANCH_STRATEGY.md): task PRs target `dev` (squash);
release PRs use `dev` → `main` (merge commit). Hotfix PRs target `main`; synchronization
PRs use `main` → `dev` (merge commit). Use a conventional title for task/hotfix PRs.

## Validation

| Check / scenario | Platform and environment | Result / evidence |
| --- | --- | --- |
| Fill with actual checks | Include device/simulator/build as relevant | Passed / Failed / Blocked / Not run |

## UI evidence (when relevant)

Include redacted screenshots/video, loading/empty/error behavior and accessibility checks.

## Compatibility / rollout (when relevant)

Describe migration, old-client behavior, feature flags and recovery/rollback constraints.

## Review notes

- [ ] Scope matches the request; unrelated changes are excluded.
- [ ] Evidence accurately states Android and iOS coverage.
- [ ] Changed contracts/documentation are updated where relevant.
- [ ] Fixtures/logs/assets contain no secrets or private user data.

Unchecked items need explanation; do not mark checks complete merely because code compiles.
