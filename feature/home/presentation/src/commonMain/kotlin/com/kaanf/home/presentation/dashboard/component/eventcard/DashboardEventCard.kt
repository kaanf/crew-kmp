package com.kaanf.home.presentation.dashboard.component.eventcard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.home.presentation.model.EventDashboardUiModel
import com.kaanf.home.presentation.model.EventTiming
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private val ThumbnailSize = 64.dp
private val ScrimColor = Color(0xB30E0B08)

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
            .padding(11.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EventThumbnail(event = event)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = AccessDefaults.TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = event.date,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = event.timingText(),
                    style = MetaLabelStyle().copy(
                        color = if (event.timing == EventTiming.InGame) {
                            AccessDefaults.Accent
                        } else {
                            AccessDefaults.TextSecondary
                        },
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = "· %${event.percentage} Full",
                    style = MetaLabelStyle(),
                    maxLines = 1,
                )
            }
        }

        ActionChip(
            text = if (event.hasMyTicket) "Ticket" else event.formattedPrice,
            highlighted = event.hasMyTicket,
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

        // ponytail: düz scrim şeridi — gradient her karede boyanınca scroll takılıyor
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(ScrimColor)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = event.dayOfMonth,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = AccessDefaults.TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                ),
            )

            Text(
                text = event.monthShort,
                style = MetaLabelStyle().copy(
                    fontSize = 8.sp,
                    lineHeight = 12.sp,
                ),
            )
        }
    }
}

@Composable
private fun ActionChip(
    text: String,
    highlighted: Boolean,
) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (highlighted) AccessDefaults.Accent.copy(alpha = 0.45f) else AccessDefaults.Border,
                shape = AccessShapes.Medium,
            )
            .padding(horizontal = 11.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                color = if (highlighted) AccessDefaults.Accent else AccessDefaults.TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                lineHeight = 13.sp,
            ),
        )

        Icon(
            painter = painterResource(AccessIcons.RightChevron),
            contentDescription = null,
            tint = if (highlighted) AccessDefaults.Accent else AccessDefaults.TextPrimary,
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun MetaLabelStyle() = MaterialTheme.typography.labelMedium.copy(
    color = AccessDefaults.TextSecondary,
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
)

private fun EventDashboardUiModel.timingText(): String = when (timing) {
    EventTiming.InGame -> "In Game"
    EventTiming.DoorsOpen -> "Doors are open"
    EventTiming.BeforeDoors -> "Doors open at $doorsTime"
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
