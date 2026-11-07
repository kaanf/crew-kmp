# Documentation context and maintenance policy

## Source ownership

| Information | Canonical location |
| --- | --- |
| Short AI working rules / task routing | AGENTS.md |
| Branches, commits, agent push/PR completion and release merges | docs/BRANCH_STRATEGY.md |
| Project tools / configured versus usable | docs/AI_TOOLS.md |
| Actual structure / representative paths | docs/PROJECT_MAP.md |
| Exact setup/build/check commands | docs/COMMANDS.md |
| Current architecture | docs/ARCHITECTURE.md |
| Confirmed business invariants | docs/DOMAIN.md or linked feature spec |
| Accepted significant technical decisions | docs/decisions |
| Unresolved facts/decisions | docs/ai/OPEN_QUESTIONS.md |
| Community contribution/reporting policies | Root community documents |

## Documentation updates required with implementation

Adding, changing or removing a project tool, SDK, service, integration or build workflow
includes updating affected existing documentation in the same branch/change, without a
separate user reminder. This is an AI contributor responsibility, not an installed Git hook
or an automatic scanner. Do not mark implementation complete while its relevant documents
still describe the old setup. Confirmed by the owner on 2026-11-06.

| Change affects | Update existing canonical files as applicable |
| --- | --- |
| Tool/service availability and purpose | `docs/AI_TOOLS.md` |
| SDK/dependency versions and target support | `docs/DEPENDENCIES.md` |
| Module ownership, source locations or platform bridges | `docs/PROJECT_MAP.md`, `docs/ARCHITECTURE.md`, `docs/KMP.md` |
| Setup, configuration, commands or execution evidence | `docs/COMMANDS.md` and the existing integration guide, such as `docs/SENTRY.md` or `docs/POSTHOG.md` |
| Privacy, credentials, data collection or release requirements | `docs/SECURITY_ENGINEERING.md`, `docs/RELEASING.md` and the integration guide |
| Test coverage or verification workflow | `docs/TESTING.md` and dated command evidence |

Select only affected documents; do not copy the full setup into every file. Extend an
existing guide before creating another. Record purpose, source/configuration locations,
safe setting names, platform scope and actual validation. Distinguish configured, built,
locally tested, owner-reported and remotely verified status; do not include tokens, DSNs,
private account data or live QR payloads. Validate links/source paths and the documentation
diff before finishing. New commands need evidence, not guesses.

## Fact status

Use Confirmed (with code/contract/owner evidence), Proposed, UNVERIFIED or Superseded.
These are documentation labels, not a custom agent feature. Record dates/commits for
inventories likely to drift. A template, old chat or agent guess is not current code evidence.

If documentation and code disagree, inspect the affected behavior and record the discrepancy.
Code establishes current implementation; it does not automatically establish intended
product behavior. Do not silently change a requirement to match a bug.

## Context budget

Keep always-loaded files compact. Link detailed documents and read them when their task
route applies. Do not import the entire docs directory at startup. Avoid duplicating the
same policy in CLAUDE, Copilot and nested files. Thin adapters have no independent policy.

## Scoped instructions

Use nested AGENTS only for a real directory with distinct constraints. Codex discovery is
based on the working directory/path chain; do not assume every descendant instruction
is automatically loaded during a root-started session. Explicitly inspect relevant nested
instructions before editing. Claude rules and Copilot applyTo use tool-specific mechanisms;
a Markdown link alone does not make a file universally auto-load.

## Instruction changes

Edit guidance when the task authorizes it or when documentation must reflect an actual
accepted change. Do not weaken quality/security rules to justify a shortcut. Keep a clear
diff and explain significant policy changes. Do not store personal/session scratch notes
or sensitive data as permanent repository policy.


## Bootstrap scope

The system was adapted on 2026-11-04 from supplied templates against local source,
not from template assumptions. Existing CLAUDE rules were consolidated into AGENTS,
CODING_STANDARDS, DESIGN_SYSTEM and REVIEW. SQLDelight wording was corrected to actual
DataStore/platform secure stores; a mandatory universal use-case layer was not invented.
No nested/path rules were installed without distinct constraints. The owner subsequently
confirmed that Copilot is not used; no Copilot adapter is needed. The existing `.gitignore` ignores CLAUDE.md; this setup leaves that
policy intact (Q13). No license, CI, repository setting, dependency or application code changed.
