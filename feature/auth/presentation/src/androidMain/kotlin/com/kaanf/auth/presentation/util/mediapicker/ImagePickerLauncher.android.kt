package com.kaanf.auth.presentation.util.mediapicker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
actual fun rememberImagePickerLauncher(
    onResult: (PickedImageData) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if(uri != null) {
            val parser = ContentUriParser(context)

            scope.launch {
                val rawBytes = parser.readUri(uri) ?: return@launch
                onResult(
                    PickedImageData(
                        bytes = downscaleToJpeg(rawBytes),
                        mimeType = "image/jpeg"
                    )
                )
            }
        }
    }

    return remember {
        ImagePickerLauncher(
            onLaunch = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        )
    }
}
