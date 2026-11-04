# Open questions and owner decisions

Updated 2026-11-04 after owner clarification. Confirmed choices and accepted targets below
are distinct from implemented/tested behavior. Resolve facts from current source; ask the
owner for material decisions source cannot establish. Operational proposals are in
[DECISION_WORKSHEET](DECISION_WORKSHEET.md). All source inspections in this update were read-only.

## Confirmed by the owner

| Topic | Decision | Evidence / follow-through |
| --- | --- | --- |
| Ownership | Project belongs to the owner | GOVERNANCE; no maintainer handle/contact invented |
| Repository | Remains closed source | README / CONTRIBUTING; no open-source license added |
| Merge flow | All change branches merge into `dev` first; only `dev` promotes to `main`; unspecified merge target is `dev` | Owner confirmed 2026-11-04; AGENTS / CONTRIBUTING / WORKFLOW updated; repository enforcement unverified |
| Backend | `../crew-api` (owner location: `~/Projects/crew/crew-api`) | Use current controllers/services/tests; README explicitly outdated |
| Design | Current application code is authoritative | DESIGN_SYSTEM; Figma connection planned for later |
| AI tools | Copilot is not used | No Copilot adapter installed |
| Asset origin | Fonts/icons reported as Google or AI generation | ASSET_PROVENANCE; per-file records not independently verified |
| Gallery | Remove README gallery for now | Removed; screenshot harness/assets retained |
| Contact | Temporary shared support/security email: `frtpkaan@gmail.com` | SUPPORT / SECURITY updated; delivery/monitoring untested; may change later |
| Physical devices | Samsung Galaxy S24+ and iPhone 11 | TESTING inventory; OS versions unknown; flagship Android does not represent lower-end hardware |
| Offline/account data | Available content shown offline; listed mutations not queued; local logout + user-cache cleanup; deletion requires server confirmation | Owner-approved target: [feature spec](../features/OFFLINE_AND_ACCOUNT_DATA.md); implementation partial |

## Remaining questions

| ID | Remaining decision / verification | Evidence / next step | State |
| --- | --- | --- | --- |
| Q01 | Passing build/tests and repository-enforced checks/reviews | Task listing succeeded; runtime commands/settings still need inspection | Open / UNVERIFIED |
| Q02 | Signed iOS app-host command for gated Keychain tests | `core/data/build.gradle.kts`, `core/data/src/iosIntegrationTest/`; inspect tooling | Open / UNVERIFIED |
| Q03 | Full backend contract, deployed-version match and specific event/QR/match/board/idempotency invariants | Backend location now known; inspect affected current source/tests, not README; see API_CONTRACTS | Partially resolved / UNVERIFIED remainder |
| Q04 | Implement accepted offline/logout/deletion target; settle server retention/backup purge, cache expiry and settings lifecycle | DATA + approved feature spec; current cache-cleanup gap; no app changes made | Target accepted; implementation and remaining policies open |
| Q05 | Supported security-fix release lines, mailbox delivery/monitoring and response/disclosure procedure | Temporary shared mailbox approved and documented in SECURITY; remaining process unverified | Contact resolved; process open |
| Q06 | Is an interpersonal conduct/reporting policy needed now, and should the draft be adopted? | Meaning explained; external collaboration/reporting need not yet established | Deferred pending owner decision |
| Q07 | Future Figma reference/baseline designation | Current code confirmed as source of truth; no Figma integration required now | Current source resolved; future integration deferred |
| Q08 | OS versions, lower-end/minimum-version coverage, repeatable performance scenarios and numeric budgets | Galaxy S24+ and iPhone 11 available; owner will update OS versions later; TESTING records limits; measurements not run | Devices resolved; OS details deferred by owner; coverage/baselines open |
| Q09 | Accessibility acceptance scope, OS/font/assistive technology checks on available devices | S24+/iPhone 11 known; actual TalkBack/VoiceOver/large-text checks not run | Open / UNVERIFIED |
| Q10 | Exact font/icon/art sources, generated inputs and attribution records; remote-media policy | Owner-reported origin recorded; per-file evidence incomplete | Partially resolved |
| Q11 | Support/account-request handling, mailbox monitoring, release verification/rollout/recovery and sustainable response expectations | Temporary support contact approved; owner known; operational processes still being planned | Contact resolved; operations open |
| Q12 | External contribution terms, license wording if needed, and enforced review/merge settings | Ownership/closed-source intent resolved; no open-source license or external contribution process assumed | Core ownership resolved; remaining terms/settings open |
| Q13 | Should currently ignored CLAUDE.md be shared through Git? Are distinct path rules needed? | No Copilot confirmed; `.gitignore` still ignores CLAUDE.md; no config change made | Copilot resolved; remaining workflow choice open |
| Q14 | README screenshot gallery | Owner requested removal; gallery removed from README | Resolved |

## Repository evidence

- Modules/targets/UI sharing: [PROJECT_MAP](../PROJECT_MAP.md), [KMP](../KMP.md).
- State/DI/navigation/data patterns: [ARCHITECTURE](../ARCHITECTURE.md).
- Normal Gradle task listing succeeded on 2026-11-04; this proves task discovery, not test
  success. Build/runtime checks remain explicitly unverified in [COMMANDS](../COMMANDS.md).
- Backend base HEAD `7c16ed69598299bb710ac79288b209e54c69f00f`; selected working-tree
  controllers/services/test sources inspected for session and account deletion. No backend
  README, secret configuration, test execution or remote mutation was used as evidence.

When resolving a question, update its canonical document. Keep accepted targets separate
from implementation and runtime verification; do not turn a proposal into an operational promise.
