package com.kaanf.home.presentation.dashboard.component.emptystate

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarStack
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.UserAvatar
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.dashboard_empty_description
import crew.feature.home.presentation.generated.resources.dashboard_empty_eyebrow
import crew.feature.home.presentation.generated.resources.dashboard_empty_notified_cta
import crew.feature.home.presentation.generated.resources.dashboard_empty_notify_cta
import crew.feature.home.presentation.generated.resources.dashboard_empty_title
import crew.feature.home.presentation.generated.resources.dashboard_empty_waiting_highlight
import crew.feature.home.presentation.generated.resources.dashboard_empty_waiting_rest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DashboardEmptyState(
    modifier: Modifier = Modifier,
) {
    var notified by rememberSaveable { mutableStateOf(false) }

    val waitingAvatars = listOf(
        UserAvatar("M", AccessDefaults.Accent),
        UserAvatar("J", AccessDefaults.Sky),
        UserAvatar("K", AccessDefaults.Coral),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // RadarPulse()

        /*
        Text(
            text = stringResource(Res.string.dashboard_empty_eyebrow),
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.sp,
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))
         */

        Text(
            text = stringResource(Res.string.dashboard_empty_title),
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.dashboard_empty_description),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextMuted,
                lineHeight = 21.sp,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 300.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AvatarStack(
                avatars = waitingAvatars,
                avatarSize = 32,
                extraCount = 90,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = AccessDefaults.TextPrimary,
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append(stringResource(Res.string.dashboard_empty_waiting_highlight))
                    }
                    append(" ")
                    append(stringResource(Res.string.dashboard_empty_waiting_rest))
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 12.5.sp,
                ),
            )
        }
    }
}

@Composable
private fun RadarPulse(
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "radar")

    Box(
        modifier = modifier.size(150.dp),
        contentAlignment = Alignment.Center,
    ) {
        listOf(
            150.dp to 0,
            98.dp to 600,
            46.dp to 1200,
        ).forEach { (ringSize, delayMillis) ->
            PulsingRing(
                transition = transition,
                ringSize = ringSize,
                delayMillis = delayMillis,
            )
        }

        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    color = AccessDefaults.Accent.copy(alpha = 0.14f)
                        .compositeOver(AccessDefaults.Surface),
                    shape = CircleShape,
                )
                .border(
                    width = 1.dp,
                    color = AccessDefaults.Accent.copy(alpha = 0.34f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(AccessIcons.Pin),
                contentDescription = null,
                tint = AccessDefaults.Accent,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun PulsingRing(
    transition: androidx.compose.animation.core.InfiniteTransition,
    ringSize: Dp,
    delayMillis: Int,
) {
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = EaseOut),
            initialStartOffset = StartOffset(delayMillis),
        ),
        label = "ringPulse",
    )

    Box(
        modifier = Modifier
            .size(ringSize)
            .graphicsLayer {
                val scale = 0.72f + 0.33f * progress
                scaleX = scale
                scaleY = scale
                alpha = 0.9f * (1f - progress)
            }
            .border(
                width = 1.dp,
                color = AccessDefaults.Accent.copy(alpha = 0.22f),
                shape = CircleShape,
            ),
    )
}

@Preview
@Composable
private fun DashboardEmptyStatePreview() {
    CrewTheme(isDarkTheme = true) {
        DashboardEmptyState()
    }
}
