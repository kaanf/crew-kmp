package com.kaanf.auth.presentation.util.mediapicker

/**
 * Downscales an image to fit within [maxSize] on its longest edge and re-encodes it as JPEG.
 *
 * Profile pictures are only ever shown in small avatars, so a large original (e.g. a 12 MP,
 * 15 MB camera photo) is wasteful to upload and, more importantly, blows up the heap when
 * decoded to a full-resolution bitmap. Shrinking to [maxSize] before it is held in state or
 * uploaded keeps both the upload size and the decoded bitmap tiny.
 *
 * Returns the original [bytes] unchanged if decoding fails for any reason.
 */
expect suspend fun downscaleToJpeg(
    bytes: ByteArray,
    maxSize: Int = 512,
    quality: Int = 80,
): ByteArray
