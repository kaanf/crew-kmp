package com.kaanf.core.presentation.util.mediapicker

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

actual suspend fun encodeWebp(
    image: ImageBitmap,
    quality: Int,
): ByteArray = withContext(Dispatchers.Default) {
    val skiaImage = Image.makeFromBitmap(image.asSkiaBitmap())
    val data = skiaImage.encodeToData(EncodedImageFormat.WEBP, quality)
        ?: return@withContext ByteArray(0)
    data.bytes
}
