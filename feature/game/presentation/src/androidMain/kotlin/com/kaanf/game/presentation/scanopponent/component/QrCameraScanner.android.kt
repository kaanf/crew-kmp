package com.kaanf.game.presentation.scanopponent.component

import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.awaitCancellation

// Odak kalibrasyonu: tetikleme sonrası bu süre dolunca kontrol sürekli AF'ye geri döner.
private const val FOCUS_AUTO_CANCEL_SECONDS = 3L

@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
@Composable
actual fun QrCameraScanner(
    modifier: Modifier,
    onResult: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnResult by rememberUpdatedState(onResult)

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            // Üstüne ScannerOverlay çizildiği için SurfaceView değil TextureView.
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val analyzer = remember { QrImageAnalyzer { currentOnResult(it) } }
    var camera by remember { mutableStateOf<Camera?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            analyzer.close()
            analysisExecutor.shutdown()
        }
    }

    LaunchedEffect(Unit) {
        val provider = ProcessCameraProvider.awaitInstance(context)

        val preview = Preview.Builder()
            .apply {
                Camera2Interop.Extender(this)
                    .setCaptureRequestOption(
                        CaptureRequest.CONTROL_AF_MODE,
                        CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE,
                    )
            }
            .build()
            .apply { surfaceProvider = previewView.surfaceProvider }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply { setAnalyzer(analysisExecutor, analyzer) }

        provider.unbindAll()
        camera = provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageAnalysis,
        )

        try {
            awaitCancellation()
        } finally {
            provider.unbindAll()
        }
    }

    // Açılışta merkeze tek bir AF/AE tetiklemesi; auto-cancel dolunca kontrol sürekli AF'ye döner.
    // ponytail: periyodik tetikleme lensi sürekli aramada tutup görüntüyü bulanıklaştırıyor —
    // gerekirse "bir süredir decode yok" koşuluna bağlı yeniden tetiklemeye çevrilir.
    LaunchedEffect(camera) {
        val cameraControl = camera?.cameraControl ?: return@LaunchedEffect
        val centerPoint = SurfaceOrientedMeteringPointFactory(1f, 1f).createPoint(0.5f, 0.5f)

        cameraControl.startFocusAndMetering(
            FocusMeteringAction
                .Builder(centerPoint, FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE)
                .setAutoCancelDuration(FOCUS_AUTO_CANCEL_SECONDS, TimeUnit.SECONDS)
                .build(),
        )
    }

    AndroidView(modifier = modifier, factory = { previewView })
}

/**
 * qr-kit'in analyzer'ı ML Kit görüntüyü asenkron işlerken [ImageProxy]'i kapatıyor; burada
 * kapatma işlem bitince yapılır, böylece ML Kit gerçekten devreye girer.
 */
private class QrImageAnalyzer(
    private val onResult: (String) -> Unit,
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build(),
    )

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        if (mediaImage == null) {
            image.close()
            return
        }

        scanner.process(InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees))
            .addOnSuccessListener { barcodes ->
                barcodes.firstNotNullOfOrNull { it.rawValue }?.let(onResult)
            }
            .addOnCompleteListener { image.close() }
    }

    fun close() = scanner.close()
}
