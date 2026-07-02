package com.kaanf.home.presentation.dashboard.component.featuredevent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarStack
import com.kaanf.core.designsystem.component.badge.RoundedBadge
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.button.BaseMiniButton
import com.kaanf.core.designsystem.component.progressbar.BaseProgressBar
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.UserAvatar
import com.kaanf.core.presentation.util.TestTags
import com.kaanf.home.presentation.component.eventHeroBackground
import com.kaanf.home.presentation.model.EventDashboardUiModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DashboardFeaturedEventCard(
    modifier: Modifier = Modifier,
    onClicked: (eventId: String) -> Unit = { },
    event: EventDashboardUiModel,
) {
    val avatars: List<UserAvatar> = listOf(
        UserAvatar("M", Color(0xFFC8FF3D)),
        UserAvatar("J", Color(0xFF6FB7FF)),
        UserAvatar("K", Color(0xFFFF7A5C)),
        UserAvatar("R", Color(0xFF5BE0C5)),
        UserAvatar("A", Color(0xFFFF5A7A)),
        UserAvatar("L", Color(0xFFFFB341)),
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .eventHeroBackground(
                shape = AccessShapes.Large,
            )
            .border(
                width = 2.dp,
                color = AccessDefaults.BorderSoft,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClicked.invoke("") },
            )
            .padding(all = 16.dp),
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = event.date,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccessDefaults.Accent,
                        fontSize = 11.sp,
                    ),
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = AccessDefaults.TextPrimary,
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        painter = painterResource(AccessIcons.Pin),
                        tint = AccessDefaults.TextMuted,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )

                    Text(
                        text = "Bar Skutek - Vinohrady",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = AccessDefaults.TextMuted,
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarStack(
                        avatars = avatars,
                        avatarSize = 32,
                        extraCount = 36,
                    )

                    BaseMiniButton(
                        text = "490 Kc",
                        filled = true,
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun DashboardFeaturedEventCardPreview() {
    CrewTheme {
        DashboardFeaturedEventCard(
            event = EventDashboardUiModel(
                id = "1",
                title = "Crew - Vol 14.",
                date = "FRI, 12 MAR - DOORS 20:00",
                formattedPrice = "220 CZK",
                percentage = 42,
                isFeatured = false,
                hasMyTicket = false,
            ),
            onClicked = {},
        )
    }
}
