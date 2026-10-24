All PNGs under `shared/src/commonMain/composeResources/files` are original procedural
illustrations created by `scripts/generate-art.cjs`. They depict fictional avatars,
an event poster and illustrated demo memories, not real people or user photos.
They require no network, credentials or image licenses. Regeneration uses the
@napi-rs/canvas dependency bundled with Goldie 0.5.0 and Crew's existing Bricolage
Grotesque font. The checked-in PNGs are the fixture source of truth; regeneration
is optional and font rasterization can vary between canvas versions.

Tonight's Roll supports photos in production; these clearly fictional illustrated
memories exercise the same image loader, card stack, captions and gestures.

`marketing/android-frame.svg` is an original, code-authored neutral device frame
used only by Goldie's Android composition. It contains no third-party artwork and
is not packaged in either app.
