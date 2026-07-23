package com.kaanf.game.presentation.scanopponent.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import qrscanner.CameraLens
import qrscanner.OverlayShape
import qrscanner.QrScanner

/** iOS'ta AVFoundation varsayılan sürekli odaklamayla çalışıyor; qr-kit olduğu gibi kullanılır. */
@Composable
actual fun QrCameraScanner(
    modifier: Modifier,
    onResult: (String) -> Unit,
) {
    QrScanner(
        modifier = modifier,
        flashlightOn = false,
        cameraLens = CameraLens.Back,
        openImagePicker = false,
        onCompletion = onResult,
        imagePickerHandler = {},
        onFailure = {},
        overlayShape = OverlayShape.Rectangle,
        overlayColor = Color.Transparent,
        overlayBorderColor = Color.Transparent,
    )
}
