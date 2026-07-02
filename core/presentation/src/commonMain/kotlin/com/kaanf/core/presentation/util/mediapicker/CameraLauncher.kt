package com.kaanf.core.presentation.util.mediapicker

import androidx.compose.runtime.Composable

// Camera capture sibling of [rememberImagePickerLauncher]. Returns the same [PickedImageData]
// (raw full-resolution bytes + mimeType) so crop + single WebP encode downstream stay identical
// regardless of whether the source was the gallery or the camera.
@Composable
expect fun rememberCameraLauncher(
    onResult: (PickedImageData) -> Unit
): CameraLauncher

class CameraLauncher(
    private val onLaunch: () -> Unit
) {
    fun launch() {
        onLaunch()
    }
}
