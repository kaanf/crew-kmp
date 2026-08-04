package com.kaanf.core.designsystem.component.coachmark

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.coachmark_back
import crew.core.designsystem.generated.resources.coachmark_done
import crew.core.designsystem.generated.resources.coachmark_next
import crew.core.designsystem.generated.resources.coachmark_skip
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CoachmarkBubble(
    step: CoachmarkStep,
    stepIndex: Int,
    stepCount: Int,
    isLast: Boolean,
    arrow: CoachmarkArrow?,
    arrowX: Float,
    onSkip: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = remember { lerp(AccessDefaults.Border, AccessDefaults.Accent, 0.2f) }
    Column(
        modifier = modifier
            // Balona dokunmak turu ilerletmesin; tap'i burada tüket.
            .pointerInput(Unit) { detectTapGestures { } }
            .drawBehind {
                val radius = CornerRadius(BubbleCornerRadius.toPx())
                val stroke = 1.dp.toPx()
                drawRoundRect(color = AccessDefaults.SurfaceElevated, cornerRadius = radius)
                drawRoundRect(color = borderColor, cornerRadius = radius, style = Stroke(stroke))
                if (arrow != null) {
                    val half = ArrowHalfWidth.toPx()
                    val depth = ArrowDepth.toPx()
                    // Taban 1dp içeride: kart kenarlığının ok altında kalan
                    // parçasını üçgen dolgusu örtsün.
                    val baseY = if (arrow == CoachmarkArrow.Top) stroke else size.height - stroke
                    val tipY = if (arrow == CoachmarkArrow.Top) -depth else size.height + depth
                    val path = Path().apply {
                        moveTo(arrowX - half, baseY)
                        lineTo(arrowX, tipY)
                        lineTo(arrowX + half, baseY)
                        close()
                    }
                    drawPath(path, AccessDefaults.SurfaceElevated)
                    drawLine(borderColor, Offset(arrowX - half, baseY), Offset(arrowX, tipY), stroke)
                    drawLine(borderColor, Offset(arrowX, tipY), Offset(arrowX + half, baseY), stroke)
                }
            }
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${stepIndex + 1} / $stepCount",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.TextFaint,
                    letterSpacing = 1.6.sp,
                ),
            )
            Text(
                text = stringResource(Res.string.coachmark_skip),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = AccessDefaults.TextMuted,
                    textDecoration = TextDecoration.Underline,
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSkip,
                    )
                    .padding(4.dp),
            )
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = step.title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = AccessDefaults.TextPrimary,
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = bodyWithHighlight(step),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
                lineHeight = 19.sp,
            ),
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(stepCount) { dot ->
                    val color = when {
                        dot == stepIndex -> AccessDefaults.Accent
                        dot < stepIndex -> AccessDefaults.Accent.copy(alpha = 0.35f)
                        else -> AccessDefaults.SurfaceHigh
                    }
                    Spacer(
                        Modifier
                            .width(if (dot == stepIndex) 16.dp else 6.dp)
                            .height(6.dp)
                            .background(color, CircleShape),
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (stepIndex > 0) {
                    Text(
                        text = stringResource(Res.string.coachmark_back),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = AccessDefaults.TextMuted,
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onBack,
                            )
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccessDefaults.Accent)
                        .clickable(onClick = onNext)
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(
                            if (isLast) Res.string.coachmark_done else Res.string.coachmark_next,
                        ),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = AccessDefaults.OnAccent,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    Icon(
                        painter = painterResource(
                            if (isLast) AccessIcons.Check else AccessIcons.RightChevron,
                        ),
                        contentDescription = null,
                        tint = AccessDefaults.OnAccent,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun bodyWithHighlight(step: CoachmarkStep) = buildAnnotatedString {
    val highlight = step.highlight
    val start = highlight?.let { step.body.indexOf(it) } ?: -1
    if (highlight == null || start < 0) {
        append(step.body)
    } else {
        append(step.body.substring(0, start))
        withStyle(
            SpanStyle(color = AccessDefaults.Accent, fontWeight = FontWeight.SemiBold),
        ) { append(highlight) }
        append(step.body.substring(start + highlight.length))
    }
}

private val BubbleCornerRadius = 18.dp
private val ArrowHalfWidth = 6.5.dp
private val ArrowDepth = 7.dp
