package com.kaanf.home.presentation.dashboard.component.eventcard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.badge.RoundedBadge
import com.kaanf.core.designsystem.component.progressbar.BaseProgressBar
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.home.presentation.model.EventDashboardUiModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DashboardEventCard(
    modifier: Modifier = Modifier,
    onClicked: (eventId: String) -> Unit,
    event: EventDashboardUiModel
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                color = AccessDefaults.Surface,
                shape = RoundedCornerShape(16.dp),
            )
            .border(
                width = 1.dp,
                color = AccessDefaults.BorderSoft,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClicked.invoke("") },
            )
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(
                        space = 6.dp,
                        alignment = Alignment.CenterVertically
                    )
                ) {
                    Text(
                        text = event.date,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccessDefaults.TextMuted,
                            fontSize = 12.sp
                        )
                    )

                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = AccessDefaults.TextPrimary,
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )

                    Text(
                        text = "Holesovice",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = AccessDefaults.TextMuted,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(
                        space = 12.dp,
                        alignment = Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = event.formattedPrice,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccessDefaults.TextPrimary,
                            fontSize = 16.sp,
                            letterSpacing = -(0.1).sp
                        )
                    )

                    RoundedBadge(
                        text = "%${event.percentage} Full"
                    )
                }
            }

            BaseProgressBar(capacity = event.percentage)
        }
    }
}

@Preview
@Composable
fun EventRowPreview() {
    CrewTheme {
        DashboardEventCard(
            event = EventDashboardUiModel(
                id = "1",
                title = "Live bar games. You show up solo, leave with a story.",
                date = "FRI, 12 MAR",
                formattedPrice = "220 CZK",
                percentage = 42,
                isFeatured = false,
            ), onClicked = {}
        )
    }
}
