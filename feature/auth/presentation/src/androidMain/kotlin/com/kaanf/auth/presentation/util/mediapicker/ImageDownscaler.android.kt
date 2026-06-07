package com.kaanf.auth.presentation.util.mediapicker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

actual suspend fun downscaleToJpeg(
    bytes: ByteArray,
    maxSize: Int,
    quality: Int,
): ByteArray = withContext(Dispatchers.Default) {
    // 1) Read the dimensions only, without decoding any pixels, so a huge original never
    //    allocates a full-resolution bitmap on the heap.
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@withContext bytes

    // 2) Decode already downsampled (power-of-two), so the bitmap that hits memory is small.
    val decodeOptions = BitmapFactory.Options().apply {
        inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxSize)
    }
    val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOptions)
        ?: return@withContext bytes

    // 3) Apply EXIF orientation; raw pixels are not rotated, only the gallery honours the tag.
    val oriented = applyExifOrientation(decoded, bytes)

    // 4) Scale the longest edge down to exactly maxSize, preserving aspect ratio.
    val scaled = scaleToMaxSize(oriented, maxSize)

    // 5) Encode as JPEG.
    val output = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, quality, output)
    scaled.recycle()

    output.toByteArray()
}

private fun calculateInSampleSize(width: Int, height: Int, maxSize: Int): Int {
    var inSampleSize = 1
    var w = width
    var h = height
    while (w / 2 >= maxSize && h / 2 >= maxSize) {
        w /= 2
        h /= 2
        inSampleSize *= 2
    }
    return inSampleSize
}

private fun scaleToMaxSize(bitmap: Bitmap, maxSize: Int): Bitmap {
    val longest = maxOf(bitmap.width, bitmap.height)
    if (longest <= maxSize) return bitmap

    val scale = maxSize.toFloat() / longest
    val newWidth = (bitmap.width * scale).toInt().coerceAtLeast(1)
    val newHeight = (bitmap.height * scale).toInt().coerceAtLeast(1)
    val scaled = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    if (scaled !== bitmap) bitmap.recycle()
    return scaled
}

private fun applyExifOrientation(bitmap: Bitmap, bytes: ByteArray): Bitmap {
    val orientation = ByteArrayInputStream(bytes).use { input ->
        ExifInterface(input).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL,
        )
    }

    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.postRotate(90f)
            matrix.postScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.postRotate(270f)
            matrix.postScale(-1f, 1f)
        }
        else -> return bitmap
    }

    val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    if (rotated !== bitmap) bitmap.recycle()
    return rotated
}
