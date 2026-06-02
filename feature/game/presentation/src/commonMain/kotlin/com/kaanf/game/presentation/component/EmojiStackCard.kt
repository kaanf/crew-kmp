package com.kaanf.game.presentation.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun EmojiStackCard(
    size: Dp,
    isWaving: Boolean = false
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


    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Bottom,
    ) {
        EmojiType.entries.forEachIndexed { index, emojiType ->
            EmojiSquare(
                emojiType = emojiType,
                size = size,
                lift = if (isWaving) waveLift(phase, index) else 0f,
            )
        }
    }
}

@Composable
private fun EmojiSquare(
    emojiType: EmojiType,
    size: Dp,
    lift: Float = 0f,
) {
    Box(
        modifier = Modifier
            .offset {
                IntOffset(x = 0, y = -(lift * WAVE_AMPLITUDE.toPx()).roundToInt())
            }
            .size(size)
            .background(
                color = AccessDefaults.Surface,
                shape = AccessShapes.XLarge,
            )
            .border(
                width = 1.dp,
                color = AccessDefaults.Border,
                shape = AccessShapes.XLarge,
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

private fun waveLift(phase: Float, index: Int): Float {
    val local = phase - index * WAVE_STAGGER
    return if (local in 0f..WAVE_BUMP_WIDTH) {
        sin(local / WAVE_BUMP_WIDTH * PI).toFloat()
    } else {
        0f
    }
}

private val WAVE_AMPLITUDE = 8.dp
private const val WAVE_STAGGER = 0.15f
private const val WAVE_BUMP_WIDTH = 0.55f
private const val WAVE_DURATION_MS = 2500

enum class EmojiType {
    Rock, Paper, Scissors
}
