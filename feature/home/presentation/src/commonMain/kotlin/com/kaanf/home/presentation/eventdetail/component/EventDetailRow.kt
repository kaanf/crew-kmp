package com.kaanf.home.presentation.eventdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EventDetailInformationCard(
    doorsTime: String,
    gameTime: String,
    crew: String,
    price: String,
) {
    val items = listOf(
        EventDetailInfoItem("Doors", doorsTime),
        EventDetailInfoItem("Game", gameTime),
        EventDetailInfoItem("Crew", crew),
        EventDetailInfoItem("Price", price),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEachIndexed { index, item ->
            EventDetailRow(
                title = item.title,
                value = item.value
            )

            if (index != items.lastIndex) {
                EventDetailDivider()
            }
        }
    }
}

@Composable
private fun EventDetailDivider() {
    Box(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(AccessDefaults.BorderSoft)
    )
}

@Composable
fun EventDetailRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 4.dp,
                horizontal = 4.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        )

        Text(
            textAlign = TextAlign.Start,
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = -(0.2).sp
            )
        )
    }
}

private data class EventDetailInfoItem(
    val title: String,
    val value: String,
)

@Composable
@Preview
fun EventDetailRowPreview() {
    EventDetailInformationCard(
        doorsTime = "20:00",
        gameTime = "20:30 - 23:00",
        crew = "42 / 80 in",
        price = "490 CZK",
    )
}
