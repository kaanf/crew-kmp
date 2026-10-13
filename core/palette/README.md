# core:palette — event cover backdrop

Draws the Luma-style backdrop behind the event detail screen. Colour *extraction* happens in
crew-api when a cover is uploaded (`infra/color/CoverColors.kt`, Material Color Utilities' Celebi
quantizer + Crew's role policy). The app only renders the colours it receives.

## Data flow

`EventDetailDto.coverColors` (`["#RRGGBB", …]`, role order: base, bottom, middle, top-end, 0–4
entries) → `toArgbColors()` in core:data (all-or-nothing: a malformed entry drops the list so roles
can't shift) → `EventDetail.coverColors` → `BackdropPalette.fromRoleOrder` in the UI model →
`CoverBackdrop`. Empty list → `null` palette → the screen keeps its flat `Background` colour.

## Rendering (verified Luma reference)

- Base: vertical gradient from `colors[0]` in HSV, value clamped to 0.20–0.85; second stop keeps
  the hue, saturation `0` stays `0` else `min(1, s/2 + 0.75)`, value × 0.75.
- Circles, drawn bottom to top, each in its own clipped layer with `BlurEffect(…, TileMode.Clamp)`,
  alpha × 0.5:
  `colors[3]` R = 0.35W at (W − R + R/3, R − R/3), blur 90dp ·
  `colors[2]` R = 0.5W at (W/2 − W/3, H/2 − W/10), blur 120dp ·
  `colors[1]` R = 0.75W at (0.25W, H − 0.05W), blur 150dp.
  Android HWUI and Skiko both map blur radius to sigma = 0.57735·r + 0.5, so dp values carry over.
- Overlay: theme `Background` × 0.45, raised in 0.05 steps (max 0.9) only until
  `TextSecondary` reaches 4.5:1 on the brightest backdrop spot. Bright covers end up around 0.70.
- W/H = the whole window (behind status bar, top bar and nav bar); fixed, not scrolling.
- Android < 12 (`RenderEffect` is a no-op): circles become radial gradients shaped like a
  Gaussian-blurred disc; no CLAMP edge spill there.
- Palette changes cross-fade over 400 ms (a dissolve, acceptable under reduce motion).

## Render strategy

Circle layers are recorded once per size/palette in `drawWithCache` and replayed each frame.
Measured on Android emulators (API 34 / 36, 2026-10-13): scrolling over the live layers was
indistinguishable from a flat background (API 34: p50 ≈ 22 ms for every variant, GPU ≈ 16.8 ms),
while pre-rendering a bitmap cost 13–52 ms of GPU work and 0.6–10 MB per screen with no frame
benefit, so it was dropped. Not yet measured on a physical device or on iOS.

## Tests

`./gradlew :core:palette:testAndroidHostTest` — gradient formulas, circle geometry and draw
order, density scaling (1.5 → 135/180/225 px), missing roles, overlay contrast rule.
Hex parsing: `core/data/src/commonTest/.../CoverColorsMapperTest.kt` (runs on iOS simulator).
