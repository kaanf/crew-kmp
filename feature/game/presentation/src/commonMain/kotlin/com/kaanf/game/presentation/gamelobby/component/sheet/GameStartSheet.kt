package com.kaanf.game.presentation.gamelobby.component.sheet

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.button.BaseMiniButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun GameStartSheet(
    modifier: Modifier = Modifier,
    onEnterGame: () -> Unit,
    onLeaveEvent: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "wave")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = WAVE_DURATION_MS,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wavePhase",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 6.dp,
            alignment = Alignment.CenterVertically,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Bottom,
        ) {
            EmojiType.entries.forEachIndexed { index, emojiType ->
                EmojiSquare(
                    emojiType = emojiType,
                    size = SQUARE_SIZE,
                    lift = waveLift(phase, index),
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Kapılar açıldı.",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = AccessDefaults.TextPrimary,
                textAlign = TextAlign.Center
            ),
        )

        Text(
            text = "Birini bul, oyuna gir ve gecenin ilk küçük kaosunu başlat.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        BaseMiniButton(
            text = "Enter the game",
            onClick = onEnterGame,
            filled = true,
        )

        Spacer(modifier = Modifier.height(1.dp))

        Text(
            text = "Leave event",
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onLeaveEvent,
            ),
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.LeftArrowColor,
                fontSize = 12.sp,
            ),
        )
    }
}

private fun waveLift(phase: Float, index: Int): Float {
    val local = phase - index * WAVE_STAGGER
    return if (local in 0f..WAVE_BUMP_WIDTH) {
        sin(local / WAVE_BUMP_WIDTH * PI).toFloat()
    } else {
        0f
    }
}

@Composable
private fun EmojiSquare(
    emojiType: EmojiType,
    size: Dp,
    lift: Float,
) {
    Box(
        modifier = Modifier
            .offset {
                IntOffset(x = 0, y = -(lift * WAVE_AMPLITUDE.toPx()).roundToInt())
            }
            .size(size)
            .background(
                color = AccessDefaults.SurfaceElevated,
                shape = AccessShapes.Small,
            )
            .border(
                width = 1.dp,
                color = AccessDefaults.Border,
                shape = AccessShapes.Small,
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = when (emojiType) {
                EmojiType.Rock -> "✊"
                EmojiType.Paper -> "✋"
                EmojiType.Scissors -> "✌️"
            },
            fontSize = (size.value * 0.45f).sp,
        )
    }
}

private enum class EmojiType {
    Rock, Paper, Scissors
}

private val SQUARE_SIZE = 64.dp
private val WAVE_AMPLITUDE = 8.dp
private const val WAVE_DURATION_MS = 2500
private const val WAVE_STAGGER = 0.15f
private const val WAVE_BUMP_WIDTH = 0.55f

@Composable
@Preview
fun GameStartSheetPreview() {
    CrewTheme {
        GameStartSheet(
            onEnterGame = {},
            onLeaveEvent = {}
        )
    }
}
