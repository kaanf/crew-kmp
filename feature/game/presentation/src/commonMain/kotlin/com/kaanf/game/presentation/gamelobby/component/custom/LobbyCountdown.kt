package com.kaanf.game.presentation.gamelobby.component.custom

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import crew.feature.game.presentation.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
fun LobbyCountdown(targetEpochMillis: Long, isLive: Boolean, onFinished: () -> Unit, compact: Boolean = false) {
    var now by remember { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }
    val currentOnFinished by rememberUpdatedState(onFinished)
    LaunchedEffect(targetEpochMillis, isLive) {
        if (targetEpochMillis <= 0L || isLive) return@LaunchedEffect
        while (isActive) {
            now = Clock.System.now().toEpochMilliseconds()
            if (now >= targetEpochMillis) {
                currentOnFinished()
                break
            }
            delay(1000)
        }
    }
    val seconds = ((targetEpochMillis - now) / 1000).coerceAtLeast(0)
    Column(
        modifier = Modifier.widthIn(max = if (compact) 300.dp else Dp.Infinity)
            .fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Crossfade(targetState = isLive, animationSpec = tween(200), label = "Lobby starts") { live ->
            val time = "${(seconds / 60).toString().padStart(2, '0')}:${(seconds % 60).toString().padStart(2, '0')}"
            val characters = if (live) stringResource(Res.string.lobby_crowd_live) else time.filter { it != ':' }
            val description = if (live) {
                stringResource(Res.string.lobby_crowd_live_label) + " " + characters
            } else {
                stringResource(Res.string.lobby_crowd_countdown) + " " + time
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp)
                    .clearAndSetSemantics { contentDescription = description },
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val colors = listOf(AccessDefaults.Accent, AccessDefaults.Blush, AccessDefaults.Sky, AccessDefaults.Gold)
                val rotations = listOf(-7f, 3f, -4f, 7f)
                characters.forEachIndexed { index, character ->
                    if (index == characters.length - 2) {
                        // Keep the card sizes and positions stable when the timer becomes LIVE.
                        Text(
                            ":",
                            color = if (live) Color.Transparent else AccessDefaults.TextMuted,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                    val wiggle = remember { Animatable(0f) }
                    LaunchedEffect(live) {
                        if (live) {
                            delay(index * 70L)
                            wiggle.animateTo(
                                targetValue = 0f,
                                animationSpec = keyframes {
                                    durationMillis = 650
                                    0f at 0 using FastOutSlowInEasing
                                    -1f at 160 using FastOutSlowInEasing
                                    0.5f at 360 using FastOutSlowInEasing
                                    0f at 650
                                },
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f).aspectRatio(if (compact) 0.76f else 0.60f)
                            .graphicsLayer {
                                // Read animated state here to avoid per-frame recomposition/layout.
                                rotationZ = rotations[index % rotations.size] + wiggle.value * 4f
                                translationY = wiggle.value * 3.dp.toPx()
                            }
                            .background(colors[index % colors.size], RoundedCornerShape(16.dp))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = character.toString(),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontFamily = BricolageGrotesque, fontWeight = FontWeight.ExtraBold,
                                fontFeatureSettings = "tnum", letterSpacing = 0.sp,
                            ),
                            autoSize = TextAutoSize.StepBased(minFontSize = 20.sp, maxFontSize = 64.sp),
                            maxLines = 1,
                            color = AccessDefaults.OnAccent,
                        )
                    }
                }
            }
        }
    }
}
