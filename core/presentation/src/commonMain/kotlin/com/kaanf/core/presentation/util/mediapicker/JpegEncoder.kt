package com.kaanf.core.presentation.util.mediapicker

import androidx.compose.ui.graphics.ImageBitmap

/**
 * [encodeWebp]'in JPEG kardeşi — backend'in WebP kabul etmediği yükleme yolları için
 * (ör. event memories: sunucu mime whitelist'i jpeg/png ve ImageIO webp çözemiyor).
 */
expect suspend fun encodeJpeg(
    image: ImageBitmap,
    quality: Int = 82,
): ByteArray
