package com.kaanf.game.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

/**
 * Mekân duyurusu pill'i (ör. "Bar pouring"), kalan süre geri sayımıyla.
 * Bitiş anı mutlak epoch olduğundan ekran/uygulama arasında gidip gelmek sayımı kaydırmaz.
 */
@Composable
fun AnnouncementChip(
    body: String,
    endsAtEpochMillis: Long,
    modifier: Modifier = Modifier,
    /** Null ise chip bilgilendirme amaçlıdır; doluysa tıklanınca kokteyl sheet'i açılır. */
    onClick: (() -> Unit)? = null,
) {
    var nowMillis by remember { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }

    LaunchedEffect(endsAtEpochMillis) {
        while (isActive) {
            nowMillis = Clock.System.now().toEpochMilliseconds()
            delay(1000)
        }
    }

    val remainingSeconds = ((endsAtEpochMillis - nowMillis) / 1000).coerceAtLeast(0)

    Row(
        modifier = modifier
            .widthIn(max = 220.dp)
            .clip(CircleShape)
            .background(color = AccessDefaults.SurfaceHigh, shape = CircleShape)
            .border(width = 1.dp, color = AccessDefaults.Blush.copy(alpha = 0.5f), shape = CircleShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color = AccessDefaults.Blush, shape = CircleShape),
        )

        Text(
            text = body,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 11.sp,
                color = AccessDefaults.TextPrimary,
                fontWeight = FontWeight.SemiBold,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = formatRemaining(remainingSeconds),
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 11.sp,
                color = AccessDefaults.Blush,
                fontWeight = FontWeight.Bold,
            ),
            maxLines = 1,
        )
    }
}

/** 05:12 / 1:05:12 — saatlik duyurular da (backend üst sınırı 24 sa) okunaklı kalsın. */
private fun formatRemaining(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val minuteSecond = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    return if (hours > 0) "$hours:$minuteSecond" else minuteSecond
}

@Preview
@Composable
fun AnnouncementChipPreview() {
    CrewTheme {
        AnnouncementChip(
            body = "Bar pouring",
            endsAtEpochMillis = Clock.System.now().toEpochMilliseconds() + 312_000,
        )
    }
}
