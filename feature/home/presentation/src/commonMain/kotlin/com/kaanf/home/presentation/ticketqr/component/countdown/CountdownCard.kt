package com.kaanf.home.presentation.ticketqr.component.countdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.modifier.surfaceCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.ticket_qr_countdown_days_label
import crew.feature.home.presentation.generated.resources.ticket_qr_countdown_hours_label
import crew.feature.home.presentation.generated.resources.ticket_qr_countdown_minutes_label
import crew.feature.home.presentation.generated.resources.ticket_qr_countdown_seconds_label
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
fun CountdownCard(
    targetEpochMillis: Long,
    serverClockOffsetMillis: Long,
    modifier: Modifier = Modifier,
) {
    var nowMillis by remember(serverClockOffsetMillis) {
        mutableLongStateOf(Clock.System.now().toEpochMilliseconds() + serverClockOffsetMillis)
    }

    LaunchedEffect(targetEpochMillis, serverClockOffsetMillis) {
        while (isActive) {
            nowMillis = Clock.System.now().toEpochMilliseconds() + serverClockOffsetMillis
            delay(1000)
        }
    }

    val totalSeconds = ((targetEpochMillis - nowMillis) / 1000)
        .coerceAtLeast(0)

    val days = totalSeconds / 86_400
    val hours = (totalSeconds % 86_400) / 3_600
    val minutes = (totalSeconds % 3_600) / 60
    val seconds = totalSeconds % 60

    Row(
        modifier = modifier
            .surfaceCard(shape = AccessShapes.XXLarge)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CountdownItem(
            value = days.toString(),
            label = stringResource(Res.string.ticket_qr_countdown_days_label)
        )

        CountdownSeparator()

        CountdownItem(
            value = hours.toString().padStart(2, '0'),
            label = stringResource(Res.string.ticket_qr_countdown_hours_label)
        )

        CountdownSeparator()

        CountdownItem(
            value = minutes.toString().padStart(2, '0'),
            label = stringResource(Res.string.ticket_qr_countdown_minutes_label)
        )

        CountdownSeparator()

        CountdownItem(
            value = seconds.toString().padStart(2, '0'),
            label = stringResource(Res.string.ticket_qr_countdown_seconds_label)
        )
    }
}

@Composable
private fun CountdownItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.widthIn(min = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 20.sp,
                color = AccessDefaults.TextPrimary,
            )
        )

        Text(
            text = label,
            color = Color(0xFF7F776D),
            fontSize = 11.sp,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CountdownSeparator() {
    Text(
        text = ":",
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 20.sp,
            color = AccessDefaults.TextMuted,
        ),
        modifier = Modifier.padding(bottom = 16.dp)
    )
}
