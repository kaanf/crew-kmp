# AI implementation workflow

## 1. Establish context

Read applicable instruction files and route by AGENTS.md. Inspect the touched files,
representative implementation, callers and relevant tests. Confirm the requested behavior
and platform impact. Use PROJECT_MAP as an index, not proof that stale paths are correct.

Before editing a new feature, inspect the current branch and working tree, then create
its own branch from `dev`. Never implement directly on `dev` or `main`. Use a
Conventional Commit type prefix such as `feat/posthog-analytics`; commits follow
`<type>(<optional scope>): <description>`.

If a change branch is already checked out, warn the user and name it before editing.
Explain whether the work continues that feature or needs a separate branch from `dev`.
Do not silently reuse the branch or create a new feature branch from it. Preserve
existing work and keep unrelated changes on their original branch. The warning does not
require another approval for work already authorized by the user. Full policy:
[CONTRIBUTING](../../CONTRIBUTING.md#branches-and-commits).

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

When a merge is requested, target `dev` for every change branch, including fixes and
documentation. Promote changes to `main` only through `dev` → `main`, as required by
[CONTRIBUTING](../../CONTRIBUTING.md). An unspecified merge target means `dev`.
