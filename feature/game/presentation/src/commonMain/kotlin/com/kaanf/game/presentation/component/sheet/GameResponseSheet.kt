package com.kaanf.game.presentation.component.sheet

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.AvatarContent
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.game.domain.model.GameSocketMessage


@Composable
fun GameResponseSheet(
    modifier: Modifier = Modifier,
    isResponding: Boolean = false,
    message: GameSocketMessage.MatchInviteReceived,
    selfPhotoUrl: String? = null,
    onAccept: () -> Unit = {},
    onDecline: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                all = 20.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "MATCH REQUEST · JUST NOW",
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
                content = avatarContentFor(
                    imageUrl = message.fromProfilePictureUrl,
                    initialsLabel = message.fromFullName.take(1).uppercase(),
                    seed = message.fromFullName,
                ),
                avatarSize = 78,
                textSize = 30.0,
                borderColor = AccessDefaults.BorderSoft,
                borderSize = 2,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PairedStatusLine(
                    modifier = Modifier.width(80.dp).height(32.dp)
                )

                Text(
                    text = "PAIRED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.TextMuted,
                        letterSpacing = 3.sp,
                        fontSize = 9.sp
                    )
                )
            }

            AvatarCircle(
                content = selfPhotoUrl?.let { AvatarContent.Image(it) }
                    ?: AvatarContent.Initials(label = "MK", color = AccessDefaults.Teal),
                avatarSize = 78,
                textSize = 30.0,
                borderColor = AccessDefaults.Accent,
                borderSize = 2,
            )
        }

        Text(
            text = "${message.fromFullName} wants to\nplay with you.",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = AccessDefaults.TextPrimary,
                textAlign = TextAlign.Center
            )
        )

        Text(
            text = "Accept and you'll get a 10‑second window to back out — no points either way. After that, the round starts.",
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextMuted,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            ),
        )

        Spacer(modifier = Modifier.height(1.dp))

        BaseButton(
            text = "Accept - start match",
            onClick = onAccept,
            enabled = !isResponding,
            isLoading = isResponding,
            loadingText = "STARTING...",
            filled = true
        )

        Text(
            text = "Not right now",
            modifier = Modifier.clickable(enabled = !isResponding, onClick = onDecline),
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextFaint,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            ),
        )
    }
}

@Composable
private fun PairedStatusLine(
    modifier: Modifier = Modifier,
    color: Color = AccessDefaults.AccentGlow,
    ringCount: Int = 3,
    periodMillis: Int = 2400,
    dotRadius: Dp = 5.5.dp,
    ringMaxRadius: Dp? = null,
) {
    val transition = rememberInfiniteTransition(label = "paired")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(periodMillis, easing = LinearEasing)),
        label = "ringProgress",
    )
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(periodMillis / 2, easing = FastOutSlowInEasing),
            RepeatMode.Reverse,
        ),
        label = "corePulse",
    )

    // State is read inside the draw lambda -> invalidates draw phase only, no recomposition.
    Canvas(modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val dotPx = dotRadius.toPx()
        val maxPx = ringMaxRadius?.toPx() ?: (size.width * 0.42f)

        drawLine(
            brush = Brush.horizontalGradient(
                0f to Color.Transparent,
                0.5f to color.copy(alpha = 0.85f),
                1f to Color.Transparent,
            ),
            start = Offset(0f, cy),
            end = Offset(size.width, cy),
            strokeWidth = 1.5f,
        )

        for (i in 0 until ringCount) {
            val f = (t + i.toFloat() / ringCount) % 1f
            val r = dotPx + f * (maxPx - dotPx)
            val a = (1f - f).coerceIn(0f, 1f)
            drawCircle(
                color = color.copy(alpha = 0.55f * a),
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(width = 1.2f),
            )
        }

        val glow = 1f + 0.25f * pulse
        drawGlowDot(Offset(cx, cy), dotPx * glow, color)
    }
}
