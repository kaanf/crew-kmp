package com.kaanf.game.presentation.gamelobby.component.custom

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

@Composable
fun MinuteSecondCountdownCard(
    targetEpochMillis: Long,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {},
) {
    var nowMillis by remember {
        mutableLongStateOf(Clock.System.now().toEpochMilliseconds())
    }

    LaunchedEffect(targetEpochMillis) {
        while (isActive) {
            nowMillis = Clock.System.now().toEpochMilliseconds()
            delay(1000)
        }
    }

    val totalSeconds = ((targetEpochMillis - nowMillis) / 1000)
        .coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    val isFinished = totalSeconds <= 0
    LaunchedEffect(isFinished) {
        if (isFinished) onFinished()
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "GAME STARTS IN",
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 2.sp
            ),
        )

        Text(
            text = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}",
            modifier = modifier,
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.Accent,
                fontSize = 72.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp,
                shadow = Shadow(
                    color = AccessDefaults.Accent.copy(alpha = 0.7f),
                    offset = Offset.Zero,
                    blurRadius = 48f,
                ),
            )
        )
    }
}

@Composable
@Preview
fun MinuteSecondCountdownCardPreview() {
    CrewTheme {
        MinuteSecondCountdownCard(
            targetEpochMillis = Clock.System.now().toEpochMilliseconds() + 261_000
        )
    }
}
