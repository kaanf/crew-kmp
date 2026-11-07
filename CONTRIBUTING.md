# Contributing

The owner confirmed on 2026-11-04 that this is a closed-source, owner-controlled project.
This document describes internal change preparation and review. It does not invite external
contributions or grant usage rights. External contribution terms and repository-enforced
checks remain unestablished; see [OPEN_QUESTIONS](docs/ai/OPEN_QUESTIONS.md).
No open-source license is assigned by this file.

## Before changing code

Read [AGENTS](AGENTS.md), [PROJECT_MAP](docs/PROJECT_MAP.md) and the relevant task route.
Select existing project tools using [AI_TOOLS](docs/AI_TOOLS.md). For a bug record observed
versus expected behavior with platform/build details; for a feature define acceptance
criteria and follow a real implementation pattern before adding abstractions.
Confidential vulnerability details do not belong in public bug reports; see the
[security reporting draft](SECURITY.md), whose private contact is still unresolved.

## Branches and commits

Before starting a new feature, inspect the current branch and working tree. Create a
separate feature branch from `dev` before editing; do not implement directly on `dev`
or `main`. Prefix branch names with the Conventional Commit type and a slash, such as
`feat/posthog-analytics`, `fix/session-refresh` or `docs/branch-workflow`.

If already on a change branch, warn the owner before editing and name the current branch.
State whether this is a continuation of its feature or a separate task that needs a new
branch from `dev`. Do not silently reuse a branch for another feature or base the new
feature on that change branch. Preserve uncommitted work and keep unrelated changes on
their original branch; do not discard or carry them into another feature accidentally.
The warning does not add an approval requirement to already authorized work.

Write commits using Conventional Commits: `<type>(<optional scope>): <description>`,
for example `feat(analytics): add anonymous PostHog events`. These branch and commit
rules were confirmed by the owner on 2026-11-04. Creating a branch does not itself
request a commit or push; follow the owner's requested operation.

## Changes and verification

Keep one coherent purpose and preserve unrelated work. Use existing KMP/CMP, DI, navigation
and Result patterns; keep compatibility unless migration is explicitly in scope. Document
configuration names without secret values. AI-assisted work has the same review/evidence
requirements as other work. Do not submit real user records, credentials or live QR payloads.

Use [TESTING](docs/TESTING.md) and [COMMANDS](docs/COMMANDS.md) for the smallest relevant
checks. Report passed, failed, blocked and not run separately, including platform limits.
UI work needs appropriate states/accessibility and Android/iOS evidence; performance claims
need comparable device/build/scenario measurements. A task listing is not a passing build.

Tool, SDK, service, integration and build workflow changes must include updates to the
affected existing project documentation in the same change. AI contributors do this
without a separate reminder; see the [documentation routing](docs/ai/CONTEXT_POLICY.md#documentation-updates-required-with-implementation).
Record safe configuration names, platform limits and actual verification; never commit
local credentials or claim that an SDK declaration proves live service delivery.

## Pull requests and decisions

Use the [PR template](.github/PULL_REQUEST_TEMPLATE.md). Lead with the concrete problem
and resulting behavior; include actual checks and migration/release impact where relevant.
Follow the accepted [branch strategy](docs/BRANCH_STRATEGY.md): task branches start
from `dev`, target `dev` and are squash-merged by the owner. Releases use a `dev` → `main`
PR with a merge commit. Agents complete implementation tasks through commit, push and
PR creation by default; explicit local-only instructions take precedence. Merge, tagging
and distribution require separate instructions.

The existing CI only runs PR jobs for `dev` → `main`; expanding coverage to `dev` targets
and hotfix/synchronization PRs remains necessary. Policy adoption does not configure
branch protection or create the remote `dev` branch; see the strategy's setup requirements.

An unspecified merge target means `dev`; all changes reach `main` through `dev`.

Update the canonical affected document and record significant accepted tradeoffs through
[ADRs](docs/decisions/README.md). Community policy drafts below require owner adoption;
see [CODE_OF_CONDUCT](CODE_OF_CONDUCT.md) and [GOVERNANCE](GOVERNANCE.md).
