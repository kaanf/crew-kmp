package com.kaanf.core.designsystem.component.qr

import androidx.compose.foundation.background
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
    val centerLogo = painterResource(AccessIcons.LogoLetter)

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
        logo = QrKitLogo(centerLogo, padding = QrKitLogoPadding.Natural(0.2f), shape = QrKitLogoKitShape.Default)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .background(
                AccessDefaults.SurfaceElevated,
                shape = AccessShapes.Large
            )
            .padding(
                all = 12.dp
            ),
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
                            cachedQrKey = key
                            cachedQr = image
                        }

                    onDrawBehind { drawImage(qrImage) }
                },
        )
    }
}

private data class QrCacheKey(val data: String, val width: Int, val height: Int)

// QR'ı çizmek ~55ms sürüyor (yüzlerce daire modül + logo) ve painter'ın kendi buffer'ı
// ekran composition'dan düşünce ölüyor; Quests'e her gidip gelişte yeniden ödeniyordu.
// Rasterize edilmiş kare composition'a bağlı olmadığı için burada saklanabiliyor.
//
// Anahtar = veri + boyut. Token yenilenirse ya da kart boyutu değişirse kare otomatik
// yeniden üretilir, bayat QR gösterilemez. Renk anahtara girmiyor çünkü AccessDefaults
// sabit paletli; çalışma anında tema değişimi eklenirse renk de anahtara girmeli.
//
// ponytail: tek girişlik cache. Kullanıcının kendi QR'ı tek, token değişince yenisi
// eskisini düşürür. Aynı anda birden çok QR gösterilirse LRU'ya çıkılır.
// Kare süreç boyunca tutulur (~6MB); süreç ölürse baştan üretilir, sorun olmaz.
private var cachedQrKey: QrCacheKey? = null
private var cachedQr: ImageBitmap? = null

