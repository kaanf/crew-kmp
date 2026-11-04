# Performance and smoothness

Reviewed 2026-11-04. Benchmark integration is present; numeric product targets and physical
device/iOS baselines remain UNVERIFIED (Q08). The owner has a Galaxy S24+ and iPhone 11;
[TESTING](TESTING.md) records the device inventory and unknown OS versions. S24+ results
cannot establish lower-end Android performance. No performance run was executed in this setup.

## Critical-flow register

| Flow | Existing implementation / measurement | Evidence status |
| --- | --- | --- |
| Cold launch | `benchmark/src/main/kotlin/com/kaanf/crew/benchmark/SmoothnessBenchmark.kt`; StartupTimingMetric | Configured, not executed here |
| Welcome→Register push/pop | Same benchmark; FrameTimingMetric, repeated measurements | Configured; does not cover authenticated dashboard/event scrolling |
| Startup profile | `benchmark/src/main/kotlin/com/kaanf/crew/benchmark/BaselineProfileGenerator.kt` | Generation task listed; not executed |
| Event cover rendering | `core/palette/README.md` records 2026-10-13 emulator observations | Historical documentation, not independently reproduced; no physical/iOS evidence |
| Shared startup shaders | `composeApp/src/commonMain/kotlin/com/kaanf/crew/ShaderWarmup.kt` | Implementation inspected by path; runtime benefit not measured here |
| QR recognition / board gestures | Platform scanners and `core/designsystem/src/commonMain/kotlin/com/kaanf/core/designsystem/profileboard/` | Candidate investigation scope; targets/baselines not established |

Exact commands: [COMMANDS](COMMANDS.md). Existing automation: [AI_TOOLS](AI_TOOLS.md).
The palette README's emulator measurements are not an app-wide smoothness guarantee.

## Diagnose before changing

Reproduce on an appropriate device/build with fixed data/scenario. Separate startup,
frame work, main-thread blocking, image decode/load, allocation, database, network and
backend latency. Use release-like/profileable builds where appropriate; record tooling
and overhead. Compare like-for-like repeated runs, not different devices or debug/release.

## Implementation review

- Avoid synchronous disk/network work and heavy parsing on the UI thread.
- Inspect state scope, unnecessary recomposition, unstable item identity and repeated work
  in composition when Compose is actually used. Measure before blanket remember/annotations.
- Bound bitmap sizes/cache behavior and check list/image/sticker memory pressure.
- Check camera resource release, repeated callbacks, navigation observer leaks and job lifetime.
- Keep motion consistent with refresh rate, accessibility and intended interaction feedback.
- Fix the measured bottleneck before proposing C++/native rewrites or architectural migrations.

## Tool selection

Use existing tools first. Android candidates include Perfetto, benchmarks and platform
profilers; iOS candidates include Instruments and platform signposts. This file does not
install or claim integration with any of them. UI automation is not a performance proof.

## Performance change report

State hypothesis, trace location, device/OS/build, scenario/data, before/after methodology,
repeat count and observed distribution. State limitations and regressions checked. Do not
claim “smooth” from a successful compile, a single screenshot or a speculative code review.
