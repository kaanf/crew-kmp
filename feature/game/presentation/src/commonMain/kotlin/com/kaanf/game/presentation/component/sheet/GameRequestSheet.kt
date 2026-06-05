package com.kaanf.game.presentation.component.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.kaanf.core.designsystem.component.progressbar.ThreeDotsAnimatedCard

@Composable
fun GameRequestSheet(
    modifier: Modifier = Modifier,
    opponentName: String,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 20.dp,
                horizontal = 24.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "MATCH REQUEST SENT · JUST NOW",
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.Accent,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
            ),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = 24.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarCircle(
                label = "Y",
                color = AccessDefaults.Rose,
                avatarSize = 78,
                textSize = 30.0,
                borderColor = AccessDefaults.BorderSoft,
                borderSize = 2,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SentStatusLine(
                    modifier = Modifier.width(80.dp).height(32.dp)
                )

                Text(
                    text = "SENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.TextMuted,
                        letterSpacing = 3.sp,
                        fontSize = 9.sp
                    )
                )
            }

            AvatarCircle(
                label = "MK",
                color = AccessDefaults.Teal,
                avatarSize = 78,
                textSize = 30.0,
                borderColor = AccessDefaults.Accent,
                borderSize = 2,
            )
        }

        Text(
            text = "Waiting for $opponentName\nto accept.",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = AccessDefaults.TextPrimary,
                textAlign = TextAlign.Center
            )
        )

        Text(
            text = "Your request just landed on $opponentName's phone. The match starts the second they tap accept.\nHang tight.",
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextMuted,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            ),
        )

        Spacer(modifier = Modifier.height(1.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = 6.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThreeDotsAnimatedCard(
                dotRadius = 2.dp,
                spacing = 4.dp
            )
            Text(
                text = "Waiting for response",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextSecondary,
                    fontSize = 11.sp,
                ),
            )
        }

        /* Daha sonra koyacağız
        Text(
            text = "Cancel request",
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCancel,
            ),
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.LeftArrowColor,
                fontSize = 12.sp,
            ),
        )
        */
    }
}

@Composable
fun SentStatusLine(
    modifier: Modifier = Modifier,
    color: Color = AccessDefaults.AccentGlow,
    periodMillis: Int = 2200,
    dotRadius: Dp = 5.dp,
    tailLength: Dp? = null, // null -> 0.35 * width, so the tail stays proportional
) {
    val transition = rememberInfiniteTransition(label = "sent")
    val p by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(periodMillis, easing = LinearEasing)),
        label = "sweep",
    )

    Canvas(modifier) {
        val cy = size.height / 2f
        val dotPx = dotRadius.toPx()
        val tailPx = tailLength?.toPx() ?: (size.width * 0.35f)

        drawLine(
            brush = Brush.horizontalGradient(
                0f to Color.Transparent,
                0.12f to color.copy(alpha = 0.22f),
                0.88f to color.copy(alpha = 0.22f),
                1f to Color.Transparent,
            ),
            start = Offset(0f, cy),
            end = Offset(size.width, cy),
            strokeWidth = 1.5f,
        )

        val x = p * size.width
        val a = sweepAlpha(p)
        if (a > 0f) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, color.copy(alpha = 0.55f * a)),
                    startX = x - tailPx,
                    endX = x,
                ),
                topLeft = Offset(x - tailPx, cy - 1.5f),
                size = Size(tailPx, 3f),
            )
            drawGlowDot(Offset(x, cy), dotPx, color, a)
        }
    }
}

private fun sweepAlpha(p: Float): Float = when {
    p < 0.10f -> p / 0.10f
    p > 0.82f -> (1f - p) / 0.18f
    else -> 1f
}.coerceIn(0f, 1f)

fun DrawScope.drawGlowDot(
    center: Offset,
    radius: Float,
    color: Color,
    alpha: Float = 1f,
) {
    drawCircle(color.copy(alpha = 0.10f * alpha), radius * 3.4f, center)
    drawCircle(color.copy(alpha = 0.18f * alpha), radius * 2.2f, center)
    drawCircle(color.copy(alpha = 0.40f * alpha), radius * 1.5f, center)
    drawCircle(color.copy(alpha = 1f * alpha), radius, center)
}


@Composable
@Preview
fun GameRequestSheetPreview() {
    CrewTheme {
        GameRequestSheet(
            opponentName = "",
        )
    }
}
