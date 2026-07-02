package com.kaanf.core.presentation.util.mediapicker

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual suspend fun decodeImageForCrop(
    bytes: ByteArray,
    maxDimension: Int,
): ImageBitmap? = withContext(Dispatchers.Default) {
    // UIImage normalises EXIF orientation when drawn, and natively decodes HEIC.
    val image = UIImage.imageWithData(bytes.toNSData()) ?: return@withContext null

    val (width, height) = image.size.useContents { width to height }
    if (width <= 0.0 || height <= 0.0) return@withContext null

    val longest = maxOf(width, height)
    val scale = if (longest > maxDimension) maxDimension.toDouble() / longest else 1.0
    val targetWidth = width * scale
    val targetHeight = height * scale

    // scale 1.0 keeps the output in pixels (not point * device-scale).
    UIGraphicsBeginImageContextWithOptions(CGSizeMake(targetWidth, targetHeight), false, 1.0)
    image.drawInRect(CGRectMake(0.0, 0.0, targetWidth, targetHeight))
    val normalized = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()

    // Re-encode losslessly (PNG) only to hand the upright pixels to Skia, which builds the
    // ImageBitmap. No quality is lost here; the single lossy step is the WebP encode later.
    val pngData = UIImagePNGRepresentation(normalized ?: image) ?: return@withContext null
    runCatching { Image.makeFromEncoded(pngData.toByteArray()).toComposeImageBitmap() }.getOrNull()
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
