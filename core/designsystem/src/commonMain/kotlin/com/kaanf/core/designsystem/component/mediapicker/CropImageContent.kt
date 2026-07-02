package com.kaanf.core.designsystem.component.mediapicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.kaanf.core.presentation.util.mediapicker.cropToSquareWebp
import com.kaanf.core.presentation.util.mediapicker.decodeImageForCrop
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val MAX_ZOOM = 6f
private const val MAX_OUTPUT_SIZE = 1024

@Composable
fun CropImageContent(
    imageBytes: ByteArray,
    onConfirm: (ByteArray) -> Unit,
    onDecodeFailed: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var image by remember(imageBytes) { mutableStateOf<ImageBitmap?>(null) }
    var decodeFailed by remember(imageBytes) { mutableStateOf(false) }
    var isProcessing by remember(imageBytes) { mutableStateOf(false) }

    var viewport by remember(imageBytes) { mutableStateOf(0f) }
    var zoom by remember(imageBytes) { mutableStateOf(1f) }
    var offset by remember(imageBytes) { mutableStateOf(Offset.Zero) }

    LaunchedEffect(imageBytes) {
        val decoded = decodeImageForCrop(imageBytes)
        if (decoded != null) image = decoded else decodeFailed = true
    }

    LaunchedEffect(decodeFailed) {
        if (decodeFailed) {
            onDecodeFailed()
        }
    }

    val loadedImage = image

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Pinch to zoom and drag to reposition.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(AccessShapes.Large)
                .clipToBounds(),
            contentAlignment = Alignment.Center,
        ) {
            if (loadedImage == null) {
                CircularProgressIndicator(color = AccessDefaults.AccentGlow)
            } else {
                val imageWidth = loadedImage.width.toFloat()
                val imageHeight = loadedImage.height.toFloat()

                fun baseScale(side: Float): Float =
                    max(side / imageWidth, side / imageHeight)

                fun clampOffset(z: Float, raw: Offset, side: Float): Offset {
                    val factor = baseScale(side) * z
                    val maxX = ((imageWidth * factor - side) / 2f).coerceAtLeast(0f)
                    val maxY = ((imageHeight * factor - side) / 2f).coerceAtLeast(0f)
                    return Offset(
                        x = raw.x.coerceIn(-maxX, maxX),
                        y = raw.y.coerceIn(-maxY, maxY),
                    )
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { sizePx ->
                            viewport = sizePx.width.toFloat()
                            offset = clampOffset(zoom, offset, viewport)
                        }
                        .pointerInput(loadedImage) {
                            detectTransformGestures { centroid, pan, gestureZoom, _ ->
                                val side = viewport
                                if (side <= 0f) return@detectTransformGestures

                                val factor = baseScale(side)
                                val oldZoom = zoom
                                val newZoom = (oldZoom * gestureZoom).coerceIn(1f, MAX_ZOOM)

                                val oldW = imageWidth * factor * oldZoom
                                val oldH = imageHeight * factor * oldZoom
                                val oldTl = Offset(
                                    x = side / 2f - oldW / 2f + offset.x,
                                    y = side / 2f - oldH / 2f + offset.y,
                                )

                                val src = Offset(
                                    x = (centroid.x - oldTl.x) / (factor * oldZoom),
                                    y = (centroid.y - oldTl.y) / (factor * oldZoom),
                                )

                                val newCentroid = centroid + pan
                                val newTl = Offset(
                                    x = newCentroid.x - src.x * factor * newZoom,
                                    y = newCentroid.y - src.y * factor * newZoom,
                                )
                                val newW = imageWidth * factor * newZoom
                                val newH = imageHeight * factor * newZoom
                                val newOffset = Offset(
                                    x = newTl.x - (side / 2f - newW / 2f),
                                    y = newTl.y - (side / 2f - newH / 2f),
                                )

                                zoom = newZoom
                                offset = clampOffset(newZoom, newOffset, side)
                            }
                        },
                ) {
                    val side = size.width
                    val factor = baseScale(side)
                    val drawWidth = imageWidth * factor * zoom
                    val drawHeight = imageHeight * factor * zoom
                    val left = side / 2f - drawWidth / 2f + offset.x
                    val top = side / 2f - drawHeight / 2f + offset.y

                    drawImage(
                        image = loadedImage,
                        dstOffset = IntOffset(left.roundToInt(), top.roundToInt()),
                        dstSize = IntSize(drawWidth.roundToInt(), drawHeight.roundToInt()),
                        filterQuality = FilterQuality.High,
                    )

                    val strokePx = 2.dp.toPx()
                    val marginPx = 16.dp.toPx()
                    val center = Offset(side / 2f, size.height / 2f)
                    val radius = side / 2f - strokePx / 2f - marginPx

                    val scrim = Path().apply {
                        addRect(Rect(0f, 0f, side, size.height))
                        addOval(
                            Rect(
                                left = center.x - radius,
                                top = center.y - radius,
                                right = center.x + radius,
                                bottom = center.y + radius,
                            ),
                        )
                        fillType = PathFillType.EvenOdd
                    }
                    drawPath(path = scrim, color = Color.Black.copy(alpha = 0.7f))

                    drawCircle(
                        color = AccessDefaults.AccentGlow,
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokePx),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        BaseButton(
            text = "Use photo",
            onClick = onClick@{
                val source = loadedImage ?: return@onClick
                if (isProcessing) return@onClick
                val side = viewport
                if (side <= 0f) return@onClick

                val base = max(side / source.width, side / source.height)
                val factor = base * zoom
                val cropSizeF = side / factor
                val cropLeftF = source.width / 2f - (side / 2f + offset.x) / factor
                val cropTopF = source.height / 2f - (side / 2f + offset.y) / factor

                val maxSquare = min(source.width, source.height)
                val cropSize = cropSizeF.roundToInt().coerceIn(1, maxSquare)
                val cropLeft = cropLeftF.roundToInt().coerceIn(0, source.width - cropSize)
                val cropTop = cropTopF.roundToInt().coerceIn(0, source.height - cropSize)
                val outputSize = cropSize.coerceAtMost(MAX_OUTPUT_SIZE)

                isProcessing = true
                scope.launch {
                    val cropped = cropToSquareWebp(
                        source = source,
                        left = cropLeft,
                        top = cropTop,
                        size = cropSize,
                        outputSize = outputSize,
                    )
                    onConfirm(cropped)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 4.dp,
                )
                .padding(
                    bottom = 20.dp
                ),
            isLoading = isProcessing,
            enabled = loadedImage != null && !isProcessing,
            filled = true,
        )
    }
}
