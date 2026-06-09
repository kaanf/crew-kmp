package com.kaanf.crew

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.JetbrainsMono

@Composable
fun ShaderWarmup() {
    Box(
        modifier = Modifier
            .size(8.dp)
            .graphicsLayer { alpha = 0.01f },
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .drawWithCache {
                    val linear = Brush.linearGradient(
                        listOf(Color(0xFF21170F), Color(0xFF0B0805)),
                    )
                    val vertical = Brush.verticalGradient(
                        listOf(Color(0xFF101923), Color(0xFF07090D)),
                    )
                    val radial = Brush.radialGradient(
                        listOf(Color(0x336FB7FF), Color.Transparent),
                        center = Offset.Zero,
                        radius = size.width * 1.4f,
                    )

                    val width = size.width.toInt().coerceAtLeast(1)
                    val height = size.height.toInt().coerceAtLeast(1)
                    val baked = ImageBitmap(width, height)
                    CanvasDrawScope().draw(
                        density = this,
                        layoutDirection = layoutDirection,
                        canvas = Canvas(baked),
                        size = Size(size.width, size.height),
                    ) {
                        drawRect(brush = linear)
                    }

                    onDrawBehind {
                        drawRect(brush = vertical)
                        drawRoundRect(
                            brush = radial,
                            cornerRadius = CornerRadius(8f, 8f),
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.04f),
                            start = Offset.Zero,
                            end = Offset(size.width, size.height),
                            strokeWidth = 2f,
                        )
                        drawImage(baked)
                    }
                }
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF29435E), Color(0xFF172838)),
                    ),
                    shape = RoundedCornerShape(8.dp),
                ),
        )

        Text(
            text = "W",
            style = TextStyle(
                fontSize = 8.sp,
                shadow = Shadow(color = Color.White, blurRadius = 48f),
            ),
        )

        Text(
            text = "W",
            style = TextStyle(
                fontSize = 8.sp,
                brush = Brush.linearGradient(listOf(Color.Red, Color.Blue)),
            ),
        )

        Text(
            text = "0",
            fontFamily = JetbrainsMono,
            fontSize = 8.sp,
        )
    }
}
