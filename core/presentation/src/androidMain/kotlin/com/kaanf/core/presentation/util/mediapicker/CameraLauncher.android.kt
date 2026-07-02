package com.kaanf.core.presentation.util.mediapicker

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

@Composable
actual fun rememberCameraLauncher(
    onResult: (PickedImageData) -> Unit
): CameraLauncher {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // URI of the temp file the camera writes into. Held across the launch/result round-trip so
    // we can read the captured bytes back once TakePicture reports success.
    var captureUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = captureUri
        if (success && uri != null) {
            val parser = ContentUriParser(context)

            scope.launch {
                // Same contract as the gallery picker: pass the raw full-resolution bytes
                // straight through; cropping and the single WebP encode happen later.
                val rawBytes = parser.readUri(uri) ?: return@launch
                onResult(
                    PickedImageData(
                        bytes = rawBytes,
                        mimeType = parser.getMimeType(uri)
                    )
                )
            }
        }
    }

    val launchCapture: () -> Unit = {
        val uri = ComposeFileProvider.createImageUri(context)
        captureUri = uri
        cameraLauncher.launch(uri)
    }

    // The manifest declares android.permission.CAMERA, so the system requires it to be granted
    // at runtime before honoring ACTION_IMAGE_CAPTURE — otherwise the launch throws
    // SecurityException. Request it on demand and only capture once granted.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCapture()
        }
    }

    return remember {
        CameraLauncher(
            onLaunch = {
                val alreadyGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (alreadyGranted) {
                    launchCapture()
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        )
    }
}
