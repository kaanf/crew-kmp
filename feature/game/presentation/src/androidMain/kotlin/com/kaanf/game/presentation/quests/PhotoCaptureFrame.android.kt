package com.kaanf.game.presentation.quests

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import kotlinx.coroutines.awaitCancellation
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun PhotoCaptureFrame(
    modifier: Modifier,
    onCaptured: (ByteArray) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnCaptured by rememberUpdatedState(onCaptured)

    var useFrontCamera by remember { mutableStateOf(false) }
    var torchOn by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            // Üstüne deklanşör ve pinler çizildiği için SurfaceView değil TextureView.
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    var camera by remember { mutableStateOf<Camera?>(null) }

    LaunchedEffect(useFrontCamera) {
        val provider = ProcessCameraProvider.awaitInstance(context)
        val preview = Preview.Builder().build()
            .apply { surfaceProvider = previewView.surfaceProvider }

        provider.unbindAll()
        camera = provider.bindToLifecycle(
            lifecycleOwner,
            if (useFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageCapture,
        )

        try {
            awaitCancellation()
        } finally {
            provider.unbindAll()
        }
    }

    // Kamera değişince torch state'i cihazla birlikte sıfırlanır; ön kamerada flaş yok.
    LaunchedEffect(camera, torchOn) {
        camera?.takeIf { it.cameraInfo.hasFlashUnit() }?.cameraControl?.enableTorch(torchOn)
    }

    Box(modifier = modifier) {
        AndroidView(modifier = Modifier.fillMaxSize(), factory = { previewView })

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp),
        ) {
            if (camera?.cameraInfo?.hasFlashUnit() == true) {
                CaptureControl(
                    icon = AccessIcons.Lightning,
                    tint = if (torchOn) AccessDefaults.Accent else Color.White,
                    onClick = { torchOn = !torchOn },
                )
            }
            CaptureControl(
                icon = AccessIcons.Refresh,
                tint = Color.White,
                onClick = {
                    torchOn = false
                    useFrontCamera = !useFrontCamera
                },
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp)
                .size(62.dp)
                .background(Color.White.copy(alpha = 0.22f), CircleShape)
                .border(3.dp, Color.White, CircleShape)
                .clickable(enabled = !isCapturing) {
                    isCapturing = true
                    imageCapture.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val bytes = image.toJpegBytes()
                                image.close()
                                isCapturing = false
                                currentOnCaptured(bytes)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                // Kare gelmedi: kutu kamerada kalır, kullanıcı tekrar dener.
                                isCapturing = false
                            }
                        },
                    )
                },
        )
    }
}

@Composable
private fun CaptureControl(
    icon: org.jetbrains.compose.resources.DrawableResource,
    tint: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(17.dp),
        )
    }
}

/**
 * ImageCapture'ın in-memory çıktısı tek düzlemli JPEG'dir; EXIF'i de içinde gelir, o yüzden
 * döndürme burada değil ortak hattaki decode adımında (decodeImageForCrop) uygulanır.
 */
private fun ImageProxy.toJpegBytes(): ByteArray {
    val buffer = planes[0].buffer
    return ByteArray(buffer.remaining()).also(buffer::get)
}
