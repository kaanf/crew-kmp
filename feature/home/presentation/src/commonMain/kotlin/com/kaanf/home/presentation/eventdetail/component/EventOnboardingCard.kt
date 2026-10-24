package com.kaanf.home.presentation.eventdetail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.designsystem.theme.CrewTheme
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.how_it_works_task_cards
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EventOnboardingCard() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(Res.drawable.how_it_works_task_cards),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(36.dp),
            )
            Text(
                text = "How it works",
                modifier = Modifier.weight(1f).semantics { heading() },
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = BricolageGrotesque,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                    letterSpacing = (-0.6).sp,
                    color = AccessDefaults.TextPrimary,
                ),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            eventOnboardingItems.forEachIndexed { index, item ->
                EventOnboardingRow(item = item, index = index)
                if (index != eventOnboardingItems.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp),
                        color = AccessDefaults.TextPrimary.copy(alpha = 0.12f),
                    )
                }
            }
        }
    }
}

@Composable
private fun EventOnboardingRow(item: EventOnboardingItem, index: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 3.dp)
                .rotate(if (index % 2 == 0) -8f else 7f)
                .size(40.dp)
                .background(item.color, RoundedCornerShape(12.dp))
                .clearAndSetSemantics {},
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "${index + 1}",
                fontFamily = BricolageGrotesque,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                lineHeight = 32.sp,
                color = AccessDefaults.OnAccent,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = BricolageGrotesque,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    color = AccessDefaults.TextPrimary,
                ),
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AccessDefaults.TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
            )
        }
    }
}

private data class EventOnboardingItem(
    val color: Color,
    val title: String,
    val description: String,
)

private val eventOnboardingItems = listOf(
    EventOnboardingItem(
        color = AccessDefaults.Accent,
        title = "Show up solo",
        description = "Tickets are one per person. Friends come too, but you play on your own.",
    ),
    EventOnboardingItem(
        color = AccessDefaults.Blush,
        title = "Scan to play",
        description = "Walk up to someone. Scan their match QR. Play physical rock-paper-scissors.",
    ),
    EventOnboardingItem(
        color = AccessDefaults.Sky,
        title = "Pick a task",
        description = "Winner picks from 3 tasks. Loser does it. Both earn points.",
    ),
    EventOnboardingItem(
        color = AccessDefaults.Gold,
        title = "Crown the leader",
        description = "The night ends at 23:00. Leaderboard reveals. Hugs, then drinks.",
    ),
)

@Composable
@Preview
fun EventOnboardingRowPreview() {
    CrewTheme {
        Column(Modifier.background(AccessDefaults.Background).padding(20.dp)) {
            EventOnboardingCard()
        }
    }
}
