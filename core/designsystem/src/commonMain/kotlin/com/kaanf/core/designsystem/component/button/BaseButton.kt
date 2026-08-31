package com.kaanf.core.designsystem.component.button

import androidx.compose.foundation.background
import com.kaanf.core.designsystem.markImmutable
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.kaanf.core.designsystem.theme.AccessIcons
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.presentation.util.rememberIsResumed
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.hypot

private const val GLOW_RASTER_PX = 256
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
    leadingIcon: DrawableResource? = null,
    tintLeadingIcon: Boolean = true,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    contentColor: Color? = null,
) {
    val outerShape = AccessShapes.Medium
    val borderWidth = 1.5.dp
    val innerShape = RoundedCornerShape(12.dp - borderWidth)
    val interactionSource = remember { MutableInteractionSource() }
    val isInteractionEnabled = enabled && !isLoading

    val resolvedBorderColor = borderColor
        ?: if (filled) AccessDefaults.Accent else AccessDefaults.Border
    val resolvedBackgroundColor = backgroundColor
        ?: when {
            filled -> AccessDefaults.Accent
            else -> AccessDefaults.Surface
        }
    val resolvedContentColor = contentColor
        ?: when {
            filled && (isInteractionEnabled || isLoading) -> AccessDefaults.OnAccent
            isInteractionEnabled || isLoading -> AccessDefaults.TextPrimary
            else -> AccessDefaults.TextFaint
        }

    val isResumed by rememberIsResumed()
    val borderAnimated = animatedBorder && isInteractionEnabled && isResumed

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(52.dp)
                // Loading shares the dimmed "disabled" look: normal background at 0.5 alpha.
                .alpha(if (isInteractionEnabled) 1f else 0.5f)
                .clip(outerShape),
        contentAlignment = Alignment.Center,
    ) {
        if (borderAnimated) {
            AnimatedBorderPaint(
                modifier = Modifier.matchParentSize(),
                color = AccessDefaults.Accent,
            )
        } else {
            Box(Modifier.matchParentSize().background(resolvedBorderColor))
        }

        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .padding(borderWidth)
                    .clip(innerShape)
                    .background(resolvedBackgroundColor)
                    .clickable(
                        enabled = isInteractionEnabled,
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                    .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = resolvedContentColor,
                    strokeWidth = 2.dp,
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (leadingIcon != null) {
                        if (tintLeadingIcon) {
                            Icon(
                                painter = painterResource(leadingIcon),
                                contentDescription = null,
                                tint = resolvedContentColor,
                                modifier = Modifier.size(20.dp),
                            )
                        } else {
                            Image(
                                painter = painterResource(leadingIcon),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = resolvedContentColor,
                        ),
                    )
                }
            }
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
        modifier = modifier.drawWithCache {
            val side = hypot(size.width, size.height)
            val sideInt = side.toInt().coerceAtLeast(1)
            val topLeft = Offset((size.width - side) / 2f, (size.height - side) / 2f)

            // Sweep gradient'i her karede shader olarak değerlendirmek yerine bir kez
            // bitmap'e rasterize edip dönüşte sadece basıyoruz. Yumuşak bir parlama
            // olduğu için küçük rasterize edip büyütmek gözle ayırt edilmiyor.
            val gradientImage = ImageBitmap(GLOW_RASTER_PX, GLOW_RASTER_PX)
            CanvasDrawScope().draw(
                density = this,
                layoutDirection = layoutDirection,
                canvas = Canvas(gradientImage),
                size = Size(GLOW_RASTER_PX.toFloat(), GLOW_RASTER_PX.toFloat()),
            ) {
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
                )
            }
            gradientImage.markImmutable()

            val glowSource = IntSize(GLOW_RASTER_PX, GLOW_RASTER_PX)
            val glowDestination = IntSize(sideInt, sideInt)
            val glowOffset = IntOffset(topLeft.x.toInt(), topLeft.y.toInt())

            onDrawBehind {
                drawRect(color = color.copy(alpha = 0.15f))

                rotate(degrees = angle) {
                    drawImage(
                        image = gradientImage,
                        srcOffset = IntOffset.Zero,
                        srcSize = glowSource,
                        dstOffset = glowOffset,
                        dstSize = glowDestination,
                        filterQuality = FilterQuality.Low,
                    )
                }
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
                text = "With icon",
                onClick = {},
                filled = true,
                leadingIcon = AccessIcons.QR,
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
