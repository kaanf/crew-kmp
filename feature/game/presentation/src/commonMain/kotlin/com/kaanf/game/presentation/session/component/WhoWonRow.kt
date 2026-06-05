package com.kaanf.game.presentation.session.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.game.presentation.model.GameResultOptionUi
import com.kaanf.game.presentation.model.WhoWonAvatarUi
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Aynı satıra birden fazla avatar aynı anda gelince aralarındaki sıralı düşüş gecikmesi. */
private const val AVATAR_STAGGER_MILLIS = 90L

@Composable
fun WhoWonRow(
    optionUi: GameResultOptionUi,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    avatars: List<WhoWonAvatarUi> = emptyList(),
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.5f)
            .background(
                color = AccessDefaults.Surface,
                shape = AccessShapes.Card,
            )
            .border(
                width = 1.dp,
                color = AccessDefaults.Border,
                shape = AccessShapes.Card,
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .background(
                        color = AccessDefaults.SurfaceElevated,
                        shape = AccessShapes.Medium,
                    )
                    .border(
                        width = 1.dp,
                        color = AccessDefaults.Border,
                        shape = AccessShapes.Medium,
                    )
                    .padding(8.dp),
                text = optionUi.emoji,
                fontSize = 24.sp,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = optionUi.pointText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.TextMuted,
                        fontSize = 10.sp
                    ),
                )

                Text(
                    text = optionUi.title,
                    style = MaterialTheme.typography.headlineMedium,
                )

                Text(
                    text = optionUi.description,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.TextSecondary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    ),
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            avatars.forEachIndexed { index, avatar ->
                key(avatar.label) {
                    // İlk kompozisyonda false→true geçişi pop-in animasyonunu tetikler.
                    // Birden fazla avatar aynı anda gelirse index'e göre ufak gecikmeyle sıralı düşerler.
                    val appearState = remember { MutableTransitionState(false) }
                    LaunchedEffect(Unit) {
                        delay(index * AVATAR_STAGGER_MILLIS)
                        appearState.targetState = true
                    }

                    AnimatedVisibility(
                        visibleState = appearState,
                        enter = scaleIn(
                            initialScale = 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                        ) + fadeIn(),
                    ) {
                        WhoWonAvatar(avatar = avatar)
                    }
                }
            }
        }
    }
}

@Composable
private fun WhoWonAvatar(
    avatar: WhoWonAvatarUi,
    modifier: Modifier = Modifier,
) {
    val ringProgress = remember { Animatable(0f) }
    LaunchedEffect(avatar.highlight) {
        if (avatar.highlight) {
            ringProgress.snapTo(0f)
            ringProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            )
        }
    }

    Box(
        modifier = modifier.drawBehind {
            val progress = ringProgress.value
            if (avatar.highlight && progress > 0f && progress < 1f) {
                val baseRadius = size.minDimension / 2f
                drawCircle(
                    color = avatar.color.copy(alpha = (1f - progress) * 0.6f),
                    radius = baseRadius * (1f + progress * 0.7f),
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
        },
        contentAlignment = Alignment.Center,
    ) {
        AvatarCircle(label = avatar.label, color = avatar.color)
    }
}

@Composable
@Preview
fun WhoWonRowPreview() {
    CrewTheme {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            WhoWonRow(
                GameResultOptionUi(
                    title = "I won",
                    pointText = "+10 PTS",
                    description = "You pick a task for Mira",
                    emoji = "\uD83D\uDC51",
                ),
            )

            WhoWonRow(
                GameResultOptionUi(
                    title = "I lost",
                    pointText = "NO BONUS",
                    description = "Mira picks a task for you",
                    emoji = "\uD83D\uDE05",
                ),
            )
        }
    }
}
