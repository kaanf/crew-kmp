package com.kaanf.core.designsystem.component.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import crew.core.designsystem.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import qrgenerator.qrkitpainter.PatternType
import qrgenerator.qrkitpainter.QrBallType
import qrgenerator.qrkitpainter.QrFrameType
import qrgenerator.qrkitpainter.QrKitBrush
import qrgenerator.qrkitpainter.QrKitColors
import qrgenerator.qrkitpainter.QrKitLogo
import qrgenerator.qrkitpainter.QrKitLogoPadding
import qrgenerator.qrkitpainter.QrKitShapes
import qrgenerator.qrkitpainter.QrPixelType
import qrgenerator.qrkitpainter.customBrush
import qrgenerator.qrkitpainter.getSelectedFrameShape
import qrgenerator.qrkitpainter.getSelectedPattern
import qrgenerator.qrkitpainter.getSelectedPixel
import qrgenerator.qrkitpainter.getSelectedQrBall
import qrgenerator.qrkitpainter.rememberQrKitPainter
import qrgenerator.qrkitpainter.solidBrush

@Composable
fun LogoQrScreen(
    modifier: Modifier = Modifier,
    inputText: String,
) {
    val centerLogo = painterResource(AccessIcons.User)

    val painter = rememberQrKitPainter(inputText) {
        shapes = QrKitShapes(
            ballShape = getSelectedQrBall(QrBallType.RoundCornersQrBall(radius = 10f)),
            darkPixelShape = getSelectedPixel(QrPixelType.SquarePixel()),
            frameShape = getSelectedFrameShape(QrFrameType.RoundCornersFrame(corner = 0.25f)),
            codeShape = getSelectedPattern(PatternType.SquarePattern),
        )
        colors = QrKitColors(
            darkBrush = QrKitBrush.solidBrush(AccessDefaults.Surface),
        )
        logo = QrKitLogo(centerLogo, padding = QrKitLogoPadding.Natural(0.1f))
    }

    Image(
        modifier = modifier,
        painter = painter,
        contentDescription = "Logo QR",
    )
}

