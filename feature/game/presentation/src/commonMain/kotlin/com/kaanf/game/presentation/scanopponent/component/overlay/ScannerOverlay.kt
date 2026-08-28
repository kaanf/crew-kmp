package com.kaanf.game.presentation.scanopponent.component.overlay

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.lerp

@Composable
fun ScannerOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.58f))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            ScannerFrame(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f)
            )

            Text(
                text = buildAnnotatedString {
                    append("Have them open ")
                    withStyle(
                        SpanStyle(
                            color = AccessDefaults.Accent,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Match QR")
                    }
                    append(" and point\nyour camera at it.")
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    color = AccessDefaults.TextSecondary,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ScannerFrame(
    modifier: Modifier = Modifier,
    cornerColor: Color = Color(0xFFA6E22E),
    scanColor: Color = Color(0xFFB6F000),
    scanCoreColor: Color = Color(0xFFEAFFB0), // çizginin tepesi (neredeyse beyaz-yeşil)
    cornerLength: Dp = 56.dp,
    cornerRadius: Dp = 28.dp,
    strokeWidth: Dp = 4.dp,
    scanLineWidth: Dp = 2.5.dp,
    durationMillis: Int = 2200,
) {
    val transition = rememberInfiniteTransition(label = "scanner")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scanProgress",
    )

    // Köşe path'i ve çizgi brush'ı boyuta bağlı, ilerlemeye değil: drawWithCache'te bir kez
    // kurulur. Eskiden her karede yeni bir Path (native) ve 5 duraklı gradient (native shader)
    // tahsis ediliyordu — hem de canlı kamera önizlemesi ve ML Kit analizi ile aynı anda.
    Spacer(
        modifier = modifier.drawWithCache {
            val stroke = strokeWidth.toPx()
            val arm = cornerLength.toPx()
            val r = cornerRadius.toPx()
            val o = stroke / 2f
            val w = size.width
            val h = size.height

            val corners = Path().apply {
                moveTo(o, o + arm)
                lineTo(o, o + r)
                quadraticTo(o, o, o + r, o)
                lineTo(o + arm, o)
                moveTo(w - o - arm, o)
                lineTo(w - o - r, o)
                quadraticTo(w - o, o, w - o, o + r)
                lineTo(w - o, o + arm)
                moveTo(w - o, h - o - arm)
                lineTo(w - o, h - o - r)
                quadraticTo(w - o, h - o, w - o - r, h - o)
                lineTo(w - o - arm, h - o)
                moveTo(o + arm, h - o)
                lineTo(o + r, h - o)
                quadraticTo(o, h - o, o, h - o - r)
                lineTo(o, h - o - arm)
            }
            val cornerStroke = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)

            val lineBrush = Brush.horizontalGradient(
                colorStops = arrayOf(
                    0.00f to scanColor.copy(alpha = 0f),
                    0.10f to scanColor.copy(alpha = 0.55f),
                    0.38f to scanCoreColor,                 // parlak tepe
                    0.66f to scanColor.copy(alpha = 0.75f),
                    1.00f to scanColor.copy(alpha = 0f),
                ),
                startX = o + 100,
                endX = w - (o + 100),
            )

            val m = 32.dp.toPx()
            val lineTop = o + m
            val lineBottom = h - o - m
            val lineStroke = scanLineWidth.toPx()

            onDrawBehind {
                drawPath(path = corners, color = cornerColor, style = cornerStroke)

                // Karede değişen tek şey bu: ilerleme yalnız burada okunur, dolayısıyla
                // yeniden compose değil yalnız draw geçersiz kılınır.
                val y = lerp(lineTop, lineBottom, progress)
                drawLine(
                    brush = lineBrush,
                    start = Offset(o, y),
                    end = Offset(w - o, y),
                    strokeWidth = lineStroke,
                    cap = StrokeCap.Round,
                )
            }
        },
    )
}

@Composable
@Preview
fun ScannerOverlayPreview() {
    CrewTheme {
        ScannerOverlay(
        )
    }
}
