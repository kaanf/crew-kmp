# Operational decisions to work through

Updated 2026-11-04. Confirmed choices come from the project owner; suggestions below are
**proposals**, not configured infrastructure, adopted policy or runtime verification.
See [OPEN_QUESTIONS](OPEN_QUESTIONS.md) for the remaining decision queue.

## Confirmed

- Owner-controlled, closed-source project. No Copilot; code is the current design source.
- Backend: sibling `../crew-api`; use current source rather than its outdated README.
- Offline/account behavior: [approved target spec](../features/OFFLINE_AND_ACCOUNT_DATA.md).
- Fonts/icons: owner reports Google or AI origin; exact asset-source records remain incomplete.
- README gallery removed for now. No screenshot tooling or assets were removed.
- Temporary shared support/security contact: `frtpkaan@gmail.com`, owner-approved.
- Physical devices available: Samsung Galaxy S24+ and iPhone 11; OS versions not yet supplied.
  The S24+ is flagship hardware and does not establish lower-end Android performance.

## Security reporting (Q05) — contact confirmed, process open

The owner selected `frtpkaan@gmail.com` as the temporary private destination.
[SECURITY](../../SECURITY.md) records the contact. Delivery/monitoring was not tested.
Supported release lines, report handling and disclosure procedure still need a decision;
no response-time promise or repository reporting setting has been added.

## Support (Q11) — shared contact confirmed

[SUPPORT](../../SUPPORT.md) uses the same temporary owner-approved mailbox for technical,
app/account/data requests. Private records should stay out of public issues. Credentials
must not be requested as diagnostic evidence. Account-request identity checks, handling
capacity, monitoring and response expectations remain to be established.

## Device, performance and accessibility coverage (Q08/Q09) — proposal

Keep configured minimums (Android API 26, iOS 18.2) as implementation facts; do not change
platform support in documentation alone. The owner has a Galaxy S24+ and iPhone 11; OS versions remain unknown. Use
emulators/simulators for repeatable state/layout checks and physical Android/iPhone hardware
for camera/QR, permissions, background/foreground and real frame/memory behavior.

Suggested initial journeys: launch/login, dashboard/event/ticket, code check-in, QR invite/
match, profile board and logout/deletion. Include network loss/reconnect, large text, a small
screen, TalkBack and VoiceOver. Record performance measurements before selecting numeric
budgets or claiming improvement. No device matrix or accessibility certification is adopted.

Next evidence: device OS versions and actual test results. [TESTING](../TESTING.md) owns
the physical-device inventory; the flagship Android device does not close lower-end coverage.
Tests and measurements have not been run for these proposals.

## Release process (Q11) — proposed stages

1. Inventory intended test/production environments, existing store access, signing ownership
   and current monitoring. Do not put secret values into documentation.
2. Establish a build/test baseline from [COMMANDS](../COMMANDS.md); a task listing alone is
   insufficient. Add focused smoke evidence for Android and iOS with synthetic test accounts.
3. Validate artifacts through existing TestFlight/Android testing paths and define a short
   release record: commit/version, environment, checks, known issues and owner decision.
4. Decide crash/error visibility, rollout controls and recovery constraints before introducing
   automatic uploads or selecting new services. No monitoring provider is chosen here.
5. Automate established steps after validating them manually; retain explicit release scope
   and avoid changing unrelated toolchains or repository protections.

Current Fastlane lanes are documented in [RELEASING](../RELEASING.md). No CI workflow,
store setting, signing configuration, service installation or release was changed.

## Community policy (Q06)

CODE_OF_CONDUCT describes interpersonal behavior and complaint handling in project spaces.
It is not a development instruction. Need/adoption is undecided for this closed project;
the existing draft remains unadopted. It can be deferred unless external collaboration
makes a real reporting process necessary. No confidential contact or alternate is invented.
