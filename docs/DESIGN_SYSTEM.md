# Design system and UI behavior

Observed 2026-11-04. Shared Compose UI on both Android and iOS is confirmed; see
[KMP](KMP.md). The owner confirmed on 2026-11-04 that current application code is the
canonical design reference. Figma is a planned future connection; no active canonical
file/node mapping is established. Do not replace current UI from an unapproved design.

## Inventory (repository-relative source paths)

| Concern | Canonical implementation |
| --- | --- |
| Material and extended theme | `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/theme/CrewTheme.kt`, `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/theme/Theme.kt` |
| Color constants / AccessDefaults | `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/theme/Color.kt`, `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/theme/BaseTheme.kt` |
| Fonts and typography | `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/theme/Font.kt`, `core/designsystem/src/commonMain/composeResources/font/` |
| Shapes | `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/theme/Shape.kt` |
| Buttons and inputs | `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/component/button/`, `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/component/textfield/` |
| Sheets and dialogs | `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/component/sheet/`, `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/component/dialog/` |
| Layout and snackbar | `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/component/layout/`, `core/presentation/src/commonMain/kotlin/com/kaanf/core/presentation/snackbar/` |
| Profile board / QR | `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/profileboard/`, `core/domain/src/commonMain/kotlin/com/kaanf/core/domain/model/profileboard/` |
| Cover backdrop | `core/palette/src/commonMain/`, `core/palette/README.md` |
| Navigation/transition ownership | `composeApp/src/commonMain/kotlin/com/kaanf/crew/navigation/NavigationRoot.kt`, feature graph implementations |

`CrewTheme` chooses light/dark Material and extended schemes and applies shared typography.
`Font.kt` defines Bricolage Grotesque, Inter, JetBrains Mono and additional families, using
remembered FontFamily/typography objects. Existing `AccessDefaults` and `CrewColors`
coexist; reuse the nearest established component rather than inventing a parallel palette.
A universal spacing/elevation token scale was not verified; existing components contain
local values. A token unification would be a separate design decision.

## Implementation rules

Prefer Material 3 and existing controls, hoisted immutable state and stateless screen
composables. Use lifecycle-aware collection in roots; avoid recomputation/image decoding
on every composition. Use `remember`/`rememberSaveable` where appropriate, not as blanket
performance annotations. Keep business logic/navigation out of rendering functions.

Cover relevant loading, content, empty, error/retry, disabled, permission and offline states.
Preserve keyboard/IME, back gestures, dismissal, safe areas and insets. Check long strings,
font scaling, image failures and both configured appearance modes. Add a shared variant
only when it addresses a reusable need.

## Visual evidence

Use the separate screenshot hosts described in [screenshots README](../screenshots/README.md)
and [AI_TOOLS](AI_TOOLS.md). Fixed scene fixtures are useful for visual review but do not
verify authentication, navigation effects or production services. Record scene/state,
device/OS, build, locale, text size and reference/baseline. Baseline approval remains with the project owner. Figma alignment becomes applicable
only after the owner supplies and designates a reference. Use synthetic content and redact live QR/user data.
