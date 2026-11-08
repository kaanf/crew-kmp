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
| Owner tile board (inline editing controls) | `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/profileboard/ProfileBoardTilesContent.kt`; the sticker canvas `ProfileBoardContent.kt` still renders the match QR home board |
| Cover backdrop | `core/palette/src/commonMain/`, `core/palette/README.md` |
| Navigation/transition ownership | `composeApp/src/commonMain/kotlin/com/kaanf/crew/navigation/NavigationRoot.kt`, feature graph implementations |

`CrewTheme` chooses light/dark Material and extended schemes and applies shared typography.
`Font.kt` defines Bricolage Grotesque, Inter, JetBrains Mono and additional families, using
remembered FontFamily/typography objects. Existing `AccessDefaults` and `CrewColors`
coexist; reuse the nearest established component rather than inventing a parallel palette.
A universal spacing/elevation token scale was not verified; existing components contain
local values. A token unification would be a separate design decision.

The owner tile board's empty-board reminder is currently hidden. Identity bottom
spacing is 8 dp, before the board's inner padding.
Below the fixed owner-board controls with a 12 dp gap, a full-width 1 dp `BorderSoft` divider fades
in over 150 ms while the board is scrolled and fades out at the top. It overlays
the scroll viewport without shifting content, in both viewing and editing modes.
Editing uses each tile’s inline controls, without a bottom tool dock. Placeholder
emoji are 16 sp within 40 dp circles; touch areas retain their existing size.
Each tool opens a `ContainerBottomSheet` (`profileboard/component/BoardToolSheets.kt`);
filled tiles live in `BoardFilledTiles.kt`. The song tile uses `MusicRepository` and is saved
immediately, independent of the board's Done/Cancel. Ask-me is kept only for the lifetime of the board screen's ViewModel. Loves are saved
with the board on Done and restored on Cancel, including removal of all selections.

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

### Profile board QR image retry (2026-11-07)

The shared QR artwork retries a failed themed-image download once after one second
before refreshing QR status and reporting a terminal image failure. Retry state
resets when the theme or image URL changes and is cancelled when the artwork leaves
composition. The regular scannable QR remains the fallback. A successful storage
response alone does not establish that the mobile image request succeeded.

### Profile board inline editing (2026-11-07)

Edit board enters editing without opening a sheet or showing the bottom tool dock.
Each tile's own add/change control opens its tool sheet. An empty Loves area renders its placeholder from the first frame, including loading,
with interaction disabled until editing is allowed. It also
offers an add tile while viewing an editable board; tapping it enters editing and
opens Loves directly. The filled song tile keeps its small Spotify label icon; the separate play icon is
removed. Song and film tiles do not open external provider links. While viewing,
a tap leaves the board unchanged; holding enters edit mode. Filled Loves chips
render without the Loves doing heading.

The Ask-me tile uses the standard stepped pixel frame with no bottom tail or
turquoise notch.

The owner profile board reserves the bottom safe area outside its dotted backdrop,
letting AppScaffold fill the navigation-bar area with the same background as the
dashboard. Bottom insets are consumed once in ProfileBoardRoot; edit/view modes
keep the same safe-area treatment.

### Profile board film selection (2026-11-07)

Film opens a shared bottom sheet with a 300 ms debounced search after two characters,
localized title/year/poster results, selection, apply, pagination and error retry. Selecting
applies immediately through its own endpoint, independent of Done/Cancel board changes.
The film tile preserves the standard layered white/ink/coral pixel sticker frame.
The poster fills its interior; a dark bottom caption shows a two-line title and
coral release year, without film-strip perforations or a separate cream panel. The poster and caption
are clipped to the same stepped inner contour so the accent remains visible around
all edges and corners. A pixel film glyph
is visible when the poster is missing or fails to load; viewing keeps the user on the board, editing opens
the sheet and offers removal. Failed saves/removals keep the previous film and show an error.
Writes are serialized and dismissal/navigation are blocked while a film write is pending.
Late favorite refreshes do not replace editing state. The sheet retains its state and
drag-handle height while writes temporarily disable dismissal. Film saves do not trigger
the unrelated QR loading indicator. Settings exposes a small Credits button opening a shared dialog
with TMDB's official unmodified logo and required notice; `ic_tmdb.xml` preserves the
path/gradient from the approved `blue_short` SVG at
[TMDB attribution](https://www.themoviedb.org/about/logos-attribution).

### Tile board long press (2026-11-08)

Holding a QR, empty tile, song, film, Ask me about tile or Loves chip enters board
edit mode and emits one impact using the existing shared `ImpactHaptics` adapter.
The gesture is available only while the board is editable and idle in view mode.
Tap and hold share a single recognizer, so holding does not also open a provider
link or tool sheet; scrolling cancels recognition. The long-click accessibility
action is labelled with the existing Edit board resource. Android and iOS retain
their existing native feedback implementation; no new dependency is introduced.
