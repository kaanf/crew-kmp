package com.kaanf.home.presentation.dashboard.component.eventcard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.home.presentation.model.EventDashboardUiModel
import com.kaanf.home.presentation.model.EventTiming
import org.jetbrains.compose.ui.tooling.preview.Preview

private val ThumbnailSize = 64.dp

@Composable
fun DashboardEventCard(
    modifier: Modifier = Modifier,
    onClicked: (eventId: String) -> Unit,
    event: EventDashboardUiModel,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AccessDefaults.Surface,
                shape = AccessShapes.Large,
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClicked.invoke(event.id) },
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EventThumbnail(event = event)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = AccessDefaults.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    letterSpacing = (-0.4).sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = event.date,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 11.5.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                modifier = Modifier.padding(top = 3.dp),
                text = event.timingText(),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = if (event.timing == EventTiming.InGame) {
                        AccessDefaults.Accent
                    } else {
                        AccessDefaults.TextSecondary
                    },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        ActionChip(
            text = if (event.hasMyTicket) "Ticket" else event.formattedPrice,
            filled = event.hasMyTicket,
        )
    }
}

@Composable
private fun EventThumbnail(event: EventDashboardUiModel) {
    Box(
        modifier = Modifier
            .size(ThumbnailSize)
            .clip(AccessShapes.Medium)
            .background(AccessDefaults.SurfaceHigh),
    ) {
        event.imageUrl?.let { url ->
            BaseImage(
                modifier = Modifier.fillMaxSize(),
                imageUrl = url,
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun ActionChip(
    text: String,
    filled: Boolean,
) {
    val chipModifier = if (filled) {
        Modifier.background(
            color = AccessDefaults.Accent,
            shape = AccessShapes.Medium,
        )
    } else {
        Modifier.border(
            width = 1.dp,
            color = AccessDefaults.Border,
            shape = AccessShapes.Medium,
        )
    }

    Text(
        modifier = chipModifier.padding(horizontal = 11.dp, vertical = 8.dp),
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            color = if (filled) AccessDefaults.OnAccent else AccessDefaults.TextPrimary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            lineHeight = 12.sp,
        ),
    )
}

private fun EventDashboardUiModel.timingText(): String = when (timing) {
    EventTiming.InGame -> "In game"
    EventTiming.DoorsOpen -> "Doors are open"
    EventTiming.BeforeDoors -> "Doors $doorsTime"
}

@Preview
@Composable
fun DashboardEventCardPreview() {
    CrewTheme {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardEventCard(
                event = EventDashboardUiModel(
                    id = "1",
                    title = "Bar Skutek",
                    date = "Saturday, 30 May",
                    formattedPrice = "490 Kč",
                    percentage = 82,
                    isFeatured = false,
                    hasMyTicket = false,
                    dayOfMonth = "30",
                    monthShort = "MAY",
                    doorsTime = "20:00",
                    timing = EventTiming.BeforeDoors,
                ),
                onClicked = {},
            )

            DashboardEventCard(
                event = EventDashboardUiModel(
                    id = "2",
                    title = "Cross Club",
                    date = "Saturday, 6 June",
                    formattedPrice = "490 Kč",
                    percentage = 18,
                    isFeatured = false,
                    hasMyTicket = true,
                    dayOfMonth = "6",
                    monthShort = "JUN",
                    doorsTime = "20:00",
                    timing = EventTiming.InGame,
                ),
                onClicked = {},
            )
        }
    }
}
