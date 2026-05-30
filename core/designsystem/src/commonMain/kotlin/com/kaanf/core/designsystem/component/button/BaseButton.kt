package com.kaanf.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.rotate
import com.kaanf.core.designsystem.theme.AccessShapes
import kotlin.math.hypot
@Composable
fun BaseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    loadingText: String = "AUTHENTICATING...",
    filled: Boolean = false,
    animatedBorder: Boolean = false,
) {
    val outerShape = AccessShapes.Medium
    val borderWidth = 1.5.dp
    val innerShape = RoundedCornerShape(12.dp - borderWidth)
    val interactionSource = remember { MutableInteractionSource() }

    val borderColor = if (filled) AccessDefaults.Accent else AccessDefaults.Border
    val backgroundColor =
        when {
            isLoading -> AccessDefaults.FieldFocusedBackground
            filled -> AccessDefaults.Accent
            else -> AccessDefaults.Surface
        }
    val contentColor =
        when {
            isLoading -> AccessDefaults.LoadingButtonText
            filled && enabled -> AccessDefaults.OnAccent
            enabled -> AccessDefaults.TextPrimary
            else -> AccessDefaults.TextFaint
        }

    val borderAnimated = animatedBorder && enabled && !isLoading

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(52.dp)
                .alpha(if (enabled) 1f else 0.5f)
                .clip(outerShape),
        contentAlignment = Alignment.Center,
    ) {
        if (borderAnimated) {
            AnimatedBorderPaint(
                modifier = Modifier.matchParentSize(),
                color = AccessDefaults.Accent,
            )
        } else {
            Box(Modifier.matchParentSize().background(borderColor))
        }

        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .padding(borderWidth)
                    .clip(innerShape)
                    .background(backgroundColor)
                    .clickable(
                        enabled = enabled && !isLoading,
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                    .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isLoading) loadingText else text,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                ),
            )
        }
    }
}

@Composable
private fun AnimatedBorderPaint(
    color: Color,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "border")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "borderAngle",
    )

    Spacer(
        modifier = modifier.drawBehind {
            drawRect(color = color.copy(alpha = 0.15f))

            val side = hypot(size.width, size.height)
            val topLeft = Offset((size.width - side) / 2f, (size.height - side) / 2f)
            rotate(degrees = angle) {
                drawRect(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            color.copy(alpha = 0.3f),
                            color,
                            color.copy(alpha = 0.3f),
                            Color.Transparent,
                        ),
                        center = center,
                    ),
                    topLeft = topLeft,
                    size = Size(side, side),
                )
            }
        },
    )
}
@Preview
@Composable
private fun BaseButtonPreview() {
    CrewTheme(isDarkTheme = true) {
        Column(
            modifier =
                Modifier
                    .background(Color(0xFF0E0B08))
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BaseButton(
                text = "Primary",
                onClick = {},
                filled = true,
            )
            BaseButton(
                text = "Secondary",
                onClick = {},
            )
            BaseButton(
                text = "Disabled secondary",
                onClick = {},
                enabled = false,
            )
            BaseButton(
                text = "Disabled primary",
                onClick = {},
                enabled = false,
                filled = true,
            )
            BaseButton(
                text = "Loading",
                onClick = {},
                isLoading = true,
                loadingText = "Loading...",
                filled = true,
            )
        }
    }
}
