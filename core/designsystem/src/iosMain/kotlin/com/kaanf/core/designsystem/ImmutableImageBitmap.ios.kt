package com.kaanf.core.designsystem

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap

actual fun ImageBitmap.markImmutable() {
    asSkiaBitmap().setImmutable()
}
