# Accessibility

Engineering checklist with observed source examples, reviewed 2026-11-04.
Adopted accessibility standard, supported device matrix and acceptance thresholds remain
UNVERIFIED (Q09). Owner-available devices are the Galaxy S24+ and iPhone 11; see
[TESTING](TESTING.md) for the inventory. Device availability does not establish large-text,
TalkBack or VoiceOver results. No assistive-technology session was run during this setup.

## Existing implementation evidence (repository-relative)

- Shared semantic tags: `core/presentation/src/commonMain/kotlin/com/kaanf/core/presentation/util/TestTags.kt`.
  Automation tags alone do not provide accessible names.
- Board QR semantics: `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/profileboard/component/ProfileBoardCanvas.kt`.
- Password visibility semantics test source:
  `feature/auth/presentation/src/commonTest/kotlin/com/kaanf/auth/presentation/component/textfield/PasswordVisibilitySemanticsTest.kt`.
- Board loading UI test sources:
  `core/designsystem/src/commonTest/kotlin/com/kaanf/core/designsystem/profileboard/component/ProfileBoardQrLoadingTest.kt`.

These are source observations; test presence does not establish accessibility compliance.

## Check changed interactions

- Meaningful labels, roles, selected/disabled state and accessible names for icon controls.
- Logical screen-reader focus and traversal, including sheets, dialogs and dynamic content.
- Text resizing and longer localized strings without losing essential controls/content.
- Touch target usability and contrast using adopted design tokens and actual rendered states.
- Information conveyed beyond color; errors associated with the relevant field/action.
- Reduced-motion preferences and readable alternatives to essential animation-only cues.
- Platform Back/dismiss, keyboard/IME actions and focus restoration where relevant.

## Crew-specific review prompts

Can a user access the action associated with a decorative sticker? Is scanning progress/error
announced meaningfully? Does a QR-only interaction need a product-approved alternative?
These are review questions, not unapproved changes to product requirements.

## Evidence

Record Android TalkBack and iOS VoiceOver checks separately when relevant, device/OS,
font-size settings, screen/state and observed issues. Static semantics review is useful but
not equivalent to exercising a real assistive technology flow.
