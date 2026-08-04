package com.kaanf.core.presentation.util.mediapicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberCameraLauncher(
    onResult: (PickedImageData) -> Unit
): CameraLauncher {
    val scope = rememberCoroutineScope()
    val delegate = remember {
        object : NSObject(),
            UIImagePickerControllerDelegateProtocol,
            UINavigationControllerDelegateProtocol {

            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                picker.dismissViewControllerAnimated(true, null)

                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage]
                    as? UIImage ?: return

                scope.launch {
                    // Encode the captured image to JPEG; same raw-bytes contract as the gallery
                    // picker (crop + single WebP encode happen later from this full-res source).
                    // Encode off the main thread: full-res JPEG encoding blocks the render loop.
                    val rawBytes = withContext(Dispatchers.Default) {
                        val nsData = UIImageJPEGRepresentation(image, 1.0) ?: return@withContext null
                        ByteArray(nsData.length.toInt()).also {
                            memcpy(it.refTo(0), nsData.bytes, nsData.length)
                        }
                    } ?: return@launch

                    onResult(
                        PickedImageData(
                            bytes = rawBytes,
                            mimeType = "image/jpeg"
                        )
                    )
                }
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, null)
            }
        }
    }

    return remember {
        CameraLauncher(
            onLaunch = {
                // The camera source is unavailable on the simulator; guard so we present nothing
                // instead of crashing. On device this opens the native capture UI.
                val cameraAvailable = UIImagePickerController.isSourceTypeAvailable(
                    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                )
                if (cameraAvailable) {
                    val picker = UIImagePickerController().apply {
                        setSourceType(
                            UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                        )
                        setDelegate(delegate)
                    }

                    UIApplication.sharedApplication.keyWindow?.rootViewController
                        ?.presentViewController(picker, true, null)
                }
            }
        )
    }
}
