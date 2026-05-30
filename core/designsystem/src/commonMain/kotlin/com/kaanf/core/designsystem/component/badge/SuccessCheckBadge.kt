package com.kaanf.core.designsystem.component.badge

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SuccessCheckBadge(
    modifier: Modifier = Modifier,
    diameter: Dp = 96.dp,
    accent: Color = Color(0xFFAEEA00),
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .size(diameter * 2.2f)
            .drawBehind {
                val r = size.minDimension / 3f
                drawCircle(
                    brush = Brush.radialGradient(
                        0.00f to accent.copy(alpha = 0.85f),
                        0.45f to accent.copy(alpha = 0.20f),
                        1.00f to Color.Transparent,
                        center = center,
                        radius = r,
                    ),
                    radius = r,
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(diameter)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF2B2B2B), Color(0xFF080808)),
                    )
                )
                .border(1.dp, accent.copy(alpha = 0.12f), CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.size(diameter * 0.42f)) {
                val w = size.width; val h = size.height
                val path = Path().apply {
                    moveTo(w * 0.10f, h * 0.55f)
                    lineTo(w * 0.40f, h * 0.82f)
                    lineTo(w * 0.92f, h * 0.22f)
                }
                drawPath(
                    path = path,
                    color = accent,
                    style = Stroke(
                        width = w * 0.14f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
        }
    }
}
