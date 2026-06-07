package com.kaanf.core.designsystem.component.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        Image(
            modifier = Modifier
                .matchParentSize(),
            painter = painter,
            contentDescription = "Logo QR",
        )
    }
}

