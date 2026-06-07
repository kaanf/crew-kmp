package com.kaanf.auth.presentation.util.mediapicker

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual suspend fun downscaleToJpeg(
    bytes: ByteArray,
    maxSize: Int,
    quality: Int,
): ByteArray = withContext(Dispatchers.Default) {
    // UIImage normalises EXIF orientation when drawn, so no manual rotation is needed.
    val image = UIImage.imageWithData(bytes.toNSData()) ?: return@withContext bytes

    val (width, height) = image.size.useContents { width to height }
    if (width <= 0.0 || height <= 0.0) return@withContext bytes

    val longest = maxOf(width, height)
    val scale = if (longest > maxSize) maxSize.toDouble() / longest else 1.0
    val targetWidth = width * scale
    val targetHeight = height * scale

    // scale 1.0 keeps the output in pixels (not point * device-scale), so we get exactly maxSize.
    UIGraphicsBeginImageContextWithOptions(CGSizeMake(targetWidth, targetHeight), false, 1.0)
    image.drawInRect(CGRectMake(0.0, 0.0, targetWidth, targetHeight))
    val resized = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()

    val jpeg = UIImageJPEGRepresentation(resized ?: image, quality / 100.0)
        ?: return@withContext bytes

    jpeg.toByteArray()
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun ByteArray.toNSData(): NSData = usePinned { pinned ->
    NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val result = ByteArray(size)
    if (size > 0) {
        result.usePinned { pinned ->
            memcpy(pinned.addressOf(0), bytes, length)
        }
    }
    return result
}
