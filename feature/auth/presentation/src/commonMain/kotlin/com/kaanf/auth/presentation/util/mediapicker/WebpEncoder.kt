package com.kaanf.auth.presentation.util.mediapicker

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Encodes an [ImageBitmap] to lossy WebP bytes. WebP is ~25–35% smaller than JPEG at the same
 * visual quality with fewer block artefacts, which matters for faces. The pixel work lives in
 * platform code because neither Skia (iOS) nor Android expose a common encoder.
 */
expect suspend fun encodeWebp(
    image: ImageBitmap,
    quality: Int = 82,
): ByteArray
