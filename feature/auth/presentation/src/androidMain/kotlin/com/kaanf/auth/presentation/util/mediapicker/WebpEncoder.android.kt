package com.kaanf.auth.presentation.util.mediapicker

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

actual suspend fun encodeWebp(
    image: ImageBitmap,
    quality: Int,
): ByteArray = withContext(Dispatchers.Default) {
    val output = ByteArrayOutputStream()
    val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Bitmap.CompressFormat.WEBP_LOSSY
    } else {
        @Suppress("DEPRECATION")
        Bitmap.CompressFormat.WEBP
    }
    image.asAndroidBitmap().compress(format, quality, output)
    output.toByteArray()
}
