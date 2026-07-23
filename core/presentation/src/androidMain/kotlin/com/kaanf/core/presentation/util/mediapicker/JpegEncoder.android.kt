package com.kaanf.core.presentation.util.mediapicker

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

actual suspend fun encodeJpeg(
    image: ImageBitmap,
    quality: Int,
): ByteArray = withContext(Dispatchers.Default) {
    val output = ByteArrayOutputStream()
    image.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, quality, output)
    output.toByteArray()
}
