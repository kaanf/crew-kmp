# Branch strategy

Accepted by the project owner on 2026-11-04. This is the canonical internal Git policy.
It applies to implementation tasks; a question, audit, plan or read-only review does not
authorize committing unrelated work or publishing changes. Explicit task instructions
such as “local only” or “do not push” take precedence.

## Branches

| Branch | Purpose | Start from | PR target | Merge method |
| --- | --- | --- | --- | --- |
| `main` | Release-ready history | Long-lived | — | — |
| `dev` | Integration and testing of completed changes | Initially `main` | `main` for a release | Merge commit |
| `feat/<short-description>` | New feature | Current `origin/dev` | `dev` | Squash merge |
| `fix/<short-description>` | Normal bug fix | Current `origin/dev` | `dev` | Squash merge |
| `refactor/<short-description>` | Structural improvement | Current `origin/dev` | `dev` | Squash merge |
| `perf/<short-description>` | Performance improvement | Current `origin/dev` | `dev` | Squash merge |
| `chore/<short-description>` | Tooling, dependencies or documentation | Current `origin/dev` | `dev` | Squash merge |
| `hotfix/<short-description>` | Urgent fix for the released line | Current `origin/main` | `main` | Squash merge |

Use short lowercase names separated by hyphens, for example `feat/profile-edit`.
Do not develop directly on `dev` or `main`. Keep one coherent task per branch and PR.
Do not reuse a branch whose PR was merged; start the next task from current `origin/dev`.

## Agent implementation workflow

1. Read this policy before implementation. Inspect the current branch, working-tree
   changes and remotes; fetch the intended base before starting a new task branch.
2. Start the task branch from current `origin/dev`. Continue an existing task branch
   when it already belongs to this request. Preserve unrelated user changes. If they
   prevent a safe checkout, use an isolated worktree; do not stash, reset or discard them
   without authorization. Do not force-push or rewrite shared history.
3. Implement the request and update affected documentation. Run the relevant checks in
   [COMMANDS](COMMANDS.md) and [TESTING](TESTING.md); review the final diff.
4. When implementation is ready and relevant executable checks pass, stage only related
   changes, create conventional commits, push the task branch and open a PR targeting
   `dev`. This is the default completion behavior: no additional “push” request is needed.
   Use the [PR template](../.github/PULL_REQUEST_TEMPLATE.md). Report actual checks and
   platform limits; never describe skipped checks as passing.
5. If checks fail or required validation cannot run, resolve what is within scope.
   If still blocked, report the evidence; an incomplete change may be pushed with a
   clearly marked draft PR, but must not be presented as ready to merge. If remote/auth
   access is unavailable, retain the local result and report the exact remaining step.
6. Return the branch name, commit and PR link, with the result and validation summary.
   The owner reviews and merges. Agents do not merge PRs, tag versions or distribute
   builds unless separately instructed.

Use conventional commit messages such as `feat(profile): add editing`,
`fix(auth): preserve session` and `docs: document branch strategy`. The squash PR title
must also use this format because it becomes the commit on `dev`.

After owner merge, delete the completed task branch when no longer needed. Keep `dev`
and `main`. Never delete unrelated branches or worktrees as cleanup.

## Release: dev to main

The owner decides when the accumulated changes on `dev` are ready for release.
Prepare a `dev` → `main` PR when requested, with version/build changes, release notes,
relevant Android/iOS evidence and compatibility concerns. Keep release preparation
changes in a task branch targeting `dev`; the release PR itself contains the resulting
integration history. Freeze changes to `dev` while the release PR is being validated.
After any additional commits, rerun the affected checks.

The owner merges the release PR with a **merge commit**, not squash or rebase. This
preserves shared ancestry between the two long-lived branches. Then bring `main` back
into `dev` through a `main` → `dev` PR using a merge commit, before the next release cycle.
This keeps release and hotfix history synchronized without rewriting either branch.

Merging to `main` records release-ready code; it does not publish to either store.
Version tagging and TestFlight/store distribution are separate, explicitly authorized
operations described in [RELEASING](RELEASING.md).

## Hotfix

Use `hotfix/...` only when the owner requests an urgent fix to the released line.
Branch from current `origin/main`, validate and open a PR to `main`. The owner merges
and separately authorizes any release. After merge, synchronize `main` → `dev` with a
merge-commit PR so the fix is included in future releases. Prefer this to duplicating
the fix through independent cherry-picks. Verify conflicts against current development.

## Adoption and enforcement

This policy does not itself create branches or configure GitHub. Before the first task
uses the new workflow, create the remote `dev` branch from the agreed current `main`.
Do not silently fall back to targeting `main` if `origin/dev` is absent. Existing work
must be reviewed before moving it to the new flow; do not reset branches to adopt it.

Required CI coverage is task PRs into `dev`, release PRs into `main`, synchronization
PRs into `dev` and hotfix PRs into `main`. Protect both long-lived branches with PRs and
successful required checks; block direct pushes, force-pushes and deletion. A second
person's approval is not required for this owner-controlled project. Branch protection
must still permit both squash merges for task PRs and merge commits for synchronization
and releases; a linear-history-only rule would conflict with this strategy.

At adoption, `.github/workflows/pr-tests.yml` targets `main` and skips PR jobs unless
the source is `dev`. It does **not** yet cover this policy: add `dev` as a PR target and
remove the source-branch restriction. Required checks must actually run, not merely
appear as skipped. Remote `dev`, CI coverage and branch protections are setup tasks;
their existence/enforcement is not established by this document.
