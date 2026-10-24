package com.kaanf.home.presentation.ticketqr.component.countdown

import androidx.compose.foundation.background
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.BricolageGrotesque
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

    CountdownDisplay(days, hours, minutes, seconds, modifier)
}

@Composable
private fun CountdownDisplay(
    days: Long,
    hours: Long,
    minutes: Long,
    seconds: Long,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CountdownItem(
            value = days.toString().padStart(2, '0'),
            label = stringResource(Res.string.ticket_qr_countdown_days_label),
            modifier = Modifier.weight(1f),
        )
        CountdownDivider()
        CountdownItem(
            value = hours.toString().padStart(2, '0'),
            label = stringResource(Res.string.ticket_qr_countdown_hours_label),
            modifier = Modifier.weight(1f),
        )
        CountdownDivider()
        CountdownItem(
            value = minutes.toString().padStart(2, '0'),
            label = stringResource(Res.string.ticket_qr_countdown_minutes_label),
            modifier = Modifier.weight(1f),
        )
        CountdownDivider()
        CountdownItem(
            value = seconds.toString().padStart(2, '0'),
            label = stringResource(Res.string.ticket_qr_countdown_seconds_label),
            modifier = Modifier.weight(1f),
            highlighted = true,
        )
    }
}

@Composable
private fun CountdownItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
) {
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = BricolageGrotesque,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp,
                lineHeight = 40.sp,
                letterSpacing = (-1).sp,
                fontFeatureSettings = "tnum",
            ),
            autoSize = TextAutoSize.StepBased(minFontSize = 18.sp, maxFontSize = 36.sp),
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center,
            color = if (highlighted) AccessDefaults.Accent else AccessDefaults.TextPrimary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.4.sp,
            ),
            textAlign = TextAlign.Center,
            color = AccessDefaults.TextSecondary,
        )
    }
}

@Composable
private fun CountdownDivider() {
    Spacer(
        Modifier
            .size(width = 1.dp, height = 28.dp)
            .background(AccessDefaults.TextPrimary.copy(alpha = 0.12f)),
    )
}

@Preview(name = "Countdown · compact", widthDp = 280)
@Preview(name = "Countdown · phone", widthDp = 360)
@Composable
private fun CountdownPreview() {
    CrewTheme {
        Column(
            modifier = Modifier.background(AccessDefaults.Background).padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            CountdownDisplay(days = 8, hours = 7, minutes = 20, seconds = 48)
            CountdownDisplay(days = 0, hours = 0, minutes = 4, seconds = 9)
        }
    }
}
