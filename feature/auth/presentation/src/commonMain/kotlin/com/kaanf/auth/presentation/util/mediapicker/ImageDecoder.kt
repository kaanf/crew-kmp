package com.kaanf.auth.presentation.util.mediapicker

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Decodes encoded image [bytes] (JPEG/PNG/HEIC/…) into an [ImageBitmap] for the cropper, or null if
 * decoding fails.
 *
 * The result is EXIF-oriented (so camera photos are upright) and downsampled so its longest edge is
 * at most [maxDimension] — large enough to crop and zoom without visible quality loss, but small
 * enough to keep the decoded bitmap off the heap danger zone. The final, single lossy encode happens
 * later in [encodeWebp]; this step stays lossless in memory.
 */
expect suspend fun decodeImageForCrop(
    bytes: ByteArray,
    maxDimension: Int = 2560,
): ImageBitmap?
