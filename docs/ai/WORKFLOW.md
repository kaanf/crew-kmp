# AI implementation workflow

## 1. Establish context

Read applicable instruction files and route by AGENTS.md. Inspect the touched files,
representative implementation, callers and relevant tests. Confirm the requested behavior
and platform impact. Use PROJECT_MAP as an index, not proof that stale paths are correct.
For implementation, read [BRANCH_STRATEGY](../BRANCH_STRATEGY.md), inspect Git state
and use a task branch based on current `dev`. Preserve unrelated work, using an isolated
worktree if needed. Do not substitute `main` when the remote `dev` branch is missing.

## 2. Define the change

For a simple fix, proceed directly. For cross-cutting work, state a compact plan and testable
acceptance criteria. Identify data/API/platform/migration impact before implementation.
Clarify genuinely blocking product decisions; continue independent authorized work.
Do not turn routine reversible work into an unnecessary approval ceremony.

## 3. Implement

Use existing patterns and the smallest coherent change. Keep state, lifecycle/cancellation,
errors and platform boundaries explicit. Avoid incidental framework/toolchain changes.
Preserve unrelated work. Do not modify tests or instructions solely to conceal a defect.

## 4. Verify

Run relevant established checks. Inspect the diff for missed call sites and behavior changes.
For UI work verify appropriate states and platform behavior; for performance work collect
comparable measurements. Document blockers without fabricating success.

## 5. Finish

Summarize resulting behavior, significant tradeoffs, actual checks and coverage limitations.
Update canonical documentation only where invalidated. Create a concise handoff if the task
is incomplete; do not leave vague “continue later” notes without state and next action.
For completed implementation, follow BRANCH_STRATEGY: commit only related changes,
push the task branch and open a PR to `dev` without waiting for a separate push request.
Respect local-only requests; disclose failed/blocked checks and use draft status for
incomplete work. Report the branch, commit and PR link. The owner merges; store uploads
and release tags are separate authorized actions. Questions/read-only reviews do not
trigger this publishing workflow.
