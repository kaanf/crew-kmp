package com.kaanf.game.presentation.scanopponent.component

import android.hardware.camera2.CameraMetadata
import android.util.Size
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
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
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
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.awaitCancellation

private val ANALYSIS_RESOLUTION = Size(1280, 720)

/** ML Kit QR küçük göründüğünde yakınlaştırma önerir; bu tavanın üstüne çıkılmaz. */
private const val MAX_SUGGESTED_ZOOM = 3f

// Odak kalibrasyonu: tetikleme sonrası bu süre dolunca kontrol sürekli AF'ye geri döner.
private const val FOCUS_AUTO_CANCEL_SECONDS = 3L

@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
@Composable
actual fun QrCameraScanner(
    modifier: Modifier,
    onResult: (String) -> Unit,
    onError: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnResult by rememberUpdatedState(onResult)
    val currentOnError by rememberUpdatedState(onError)

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            // Üstüne ScannerOverlay çizildiği için SurfaceView değil TextureView.
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val analyzer = remember {
        QrImageAnalyzer(
            onResult = { currentOnResult(it) },
            // ML Kit listener'ları ana thread'de; "kare yok" analiz thread'inden gelir.
            onError = { message -> previewView.post { currentOnError(message) } },
        )
    }
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
            // Varsayılan 640x480'de ekrandaki QR'ın modülleri eski sensörlerde birbirine karışıyordu.
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setResolutionStrategy(
                        ResolutionStrategy(
                            ANALYSIS_RESOLUTION,
                            ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER,
                        ),
                    )
                    .build(),
            )
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply { setAnalyzer(analysisExecutor, analyzer) }

        provider.unbindAll()
        camera = try {
            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            currentOnError("Camera failed to start: ${e.describe()}")
            return@LaunchedEffect
        }
        analyzer.camera = camera

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
 *
 * Uygulamadaki QR koyu zeminde açık renkli (ters) çiziliyor; ML Kit ters QR'ı bazı cihazlarda
 * okuyamıyor (S24 okuyor, eski cihazlar okumuyor). Bu yüzden kareler sırayla normal ve
 * parlaklığı ters çevrilmiş halde verilir; her kare yine tek kez işlenir, ek maliyet yalnız
 * Y düzleminin bir kopyası.
 */
private class QrImageAnalyzer(
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
) : ImageAnalysis.Analyzer {

    /** Bağlanınca set edilir; ML Kit'in yakınlaştırma önerisi buna uygulanır. */
    @Volatile
    var camera: Camera? = null

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .setZoomSuggestionOptions(
                ZoomSuggestionOptions.Builder { ratio -> applyZoom(ratio) }
                    .setMaxSupportedZoomRatio(MAX_SUGGESTED_ZOOM)
                    .build(),
            )
            .build(),
    )

    private var invertNext = false

    // KEEP_ONLY_LATEST: önceki kare kapanmadan analyze çağrılmaz, tampon güvenle yeniden kullanılır.
    private var invertedBuffer = ByteArray(0)

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        if (mediaImage == null) {
            onError("Camera frame unavailable (format ${image.format})")
            image.close()
            return
        }

        val rotation = image.imageInfo.rotationDegrees
        invertNext = !invertNext
        val input = if (invertNext) image.toInvertedLuminance(rotation) else InputImage.fromMediaImage(mediaImage, rotation)

        scanner.process(input)
            .addOnSuccessListener { barcodes ->
                barcodes.firstNotNullOfOrNull { it.rawValue }?.let(onResult)
            }
            .addOnFailureListener { e -> onError("QR decoder failed: ${e.describe()}") }
            .addOnCompleteListener { image.close() }
    }

    /**
     * Y düzlemini ters çevirip NV21 olarak verir; QR çözümü yalnız parlaklığa bakar, renk
     * düzlemi nötr griyle (128) doldurulur. rowStride genişlikten büyük olabildiği için satır satır.
     */
    private fun ImageProxy.toInvertedLuminance(rotation: Int): InputImage {
        val yPlane = planes[0]
        val ySize = width * height
        val total = ySize + ySize / 2
        if (invertedBuffer.size != total) {
            invertedBuffer = ByteArray(total)
            invertedBuffer.fill(128.toByte(), ySize, total)
        }
        val buffer = yPlane.buffer
        val rowStride = yPlane.rowStride
        val pixelStride = yPlane.pixelStride
        var out = 0
        for (row in 0 until height) {
            val rowStart = row * rowStride
            for (col in 0 until width) {
                invertedBuffer[out++] = (255 - (buffer.get(rowStart + col * pixelStride).toInt() and 0xFF)).toByte()
            }
        }
        return InputImage.fromByteArray(invertedBuffer, width, height, rotation, InputImage.IMAGE_FORMAT_NV21)
    }

    private fun applyZoom(ratio: Float): Boolean {
        val camera = camera ?: return false
        val max = camera.cameraInfo.zoomState.value?.maxZoomRatio ?: return false
        camera.cameraControl.setZoomRatio(ratio.coerceAtMost(max))
        return true
    }

    fun close() = scanner.close()
}

private fun Throwable.describe(): String {
    val code = (this as? MlKitException)?.errorCode?.let { " (code $it)" }.orEmpty()
    return "${this::class.simpleName}$code: ${message.orEmpty()}"
}
