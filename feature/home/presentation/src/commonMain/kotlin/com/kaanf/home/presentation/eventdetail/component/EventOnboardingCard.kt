package com.kaanf.home.presentation.eventdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EventOnboardingCard() {
    Column(
        modifier = Modifier
            .padding(4.dp)
    ) {
        Text(
            text = "HOW IT WORKS",
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        eventOnboardingItems.forEachIndexed { index, item ->
            EventOnboardingRow(
                count = index + 1,
                title = item.title,
                description = item.description
            )

            if (index != eventOnboardingItems.lastIndex) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun EventOnboardingRow(
    count: Int,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EventOnboardingCountSquare(count)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextPrimary,
                    fontSize = 14.sp
                )
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

@Composable
private fun EventOnboardingCountSquare(count: Int) {
    Box(
        modifier = Modifier
            .background(
                color = AccessDefaults.SurfaceElevated,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(
                horizontal = 6.dp, vertical = 4.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
            )
        )
    }
}

private data class EventOnboardingItem(
    val title: String,
    val description: String
)

private val eventOnboardingItems = listOf(
    EventOnboardingItem(
        title = "Show up solo",
        description = "Tickets are one per person. Friends come too — but you play on your own."
    ),
    EventOnboardingItem(
        title = "Scan to play",
        description = "Walk up to someone. Scan their match QR. Play physical rock-paper-scissors."
    ),
    EventOnboardingItem(
        title = "Pick a task",
        description = "Winner picks from 3 tasks. Loser does it. Both earn points."
    ),
    EventOnboardingItem(
        title = "Crown the leader",
        description = "The night ends at 23:00. Leaderboard reveals. Hugs, then drinks."
    )
)

@Composable
@Preview
fun EventOnboardingRowPreview() {
    CrewTheme {
        EventOnboardingCard()
    }
}
