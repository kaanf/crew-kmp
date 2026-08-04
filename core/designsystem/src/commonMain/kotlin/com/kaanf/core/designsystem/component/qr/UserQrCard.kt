package com.kaanf.core.designsystem.component.qr

import androidx.compose.foundation.background
import com.kaanf.core.designsystem.markImmutable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import org.jetbrains.compose.resources.painterResource
import qrgenerator.qrkitpainter.PatternType
import qrgenerator.qrkitpainter.QrBallType
import qrgenerator.qrkitpainter.QrFrameType
import qrgenerator.qrkitpainter.QrKitBrush
import qrgenerator.qrkitpainter.QrKitColors
import qrgenerator.qrkitpainter.QrKitLogo
import qrgenerator.qrkitpainter.QrKitLogoKitShape
import qrgenerator.qrkitpainter.QrKitLogoPadding
import qrgenerator.qrkitpainter.QrKitShapes
import qrgenerator.qrkitpainter.QrPixelType
import qrgenerator.qrkitpainter.getSelectedFrameShape
import qrgenerator.qrkitpainter.getSelectedPattern
import qrgenerator.qrkitpainter.getSelectedPixel
import qrgenerator.qrkitpainter.getSelectedQrBall
import qrgenerator.qrkitpainter.rememberQrKitPainter
import qrgenerator.qrkitpainter.solidBrush

@Composable
fun UserQrCard(
    modifier: Modifier = Modifier,
    inputText: String,
    qrContentDescription: String = "Logo QR",
) {
    // val centerLogo = painterResource(AccessIcons.AppleLogo)

    val painter = rememberQrKitPainter(inputText) {
        shapes = QrKitShapes(
            ballShape = getSelectedQrBall(QrBallType.CircleQrBall()),
            darkPixelShape = getSelectedPixel(QrPixelType.CirclePixel(size = 0.8f)),
            frameShape = getSelectedFrameShape(QrFrameType.RoundCornersFrame(corner = 0.25f)),
            codeShape = getSelectedPattern(PatternType.SquarePattern),
        )
        colors = QrKitColors(
            darkBrush = QrKitBrush.solidBrush(AccessDefaults.TextPrimary),
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .semantics { contentDescription = qrContentDescription }
                .drawWithCache {
                    val width = size.width.toInt().coerceAtLeast(1)
                    val height = size.height.toInt().coerceAtLeast(1)
                    val key = QrCacheKey(inputText, width, height)

                    val qrImage = cachedQr?.takeIf { cachedQrKey == key }
                        ?: ImageBitmap(width, height).also { image ->
                            CanvasDrawScope().draw(
                                density = this,
                                layoutDirection = layoutDirection,
                                canvas = Canvas(image),
                                size = Size(width.toFloat(), height.toFloat()),
                            ) {
                                with(painter) { draw(this@draw.size) }
                            }
                            image.markImmutable()
                            cachedQrKey = key
                            cachedQr = image
                        }

                    onDrawBehind { drawImage(qrImage) }
                },
        )
    }
}

private data class QrCacheKey(val data: String, val width: Int, val height: Int)

private var cachedQrKey: QrCacheKey? = null
private var cachedQr: ImageBitmap? = null

