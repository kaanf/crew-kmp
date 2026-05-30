package com.kaanf.home.presentation.ticketqr.component.successcard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.home.presentation.component.eventHeroBackground
import com.kaanf.home.presentation.ticketqr.component.countdown.CountdownCard
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

@Composable
fun TicketSuccessCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .eventHeroBackground(
                shape = AccessShapes.Card,
            )
            .border(
                width = 1.dp,
                color = AccessDefaults.AccentGlow,
                shape = AccessShapes.Card,
            )
            .padding(
                all = 24.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "See you Saturday.",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = AccessDefaults.TextPrimary,
            )
        )

        CountdownCard(
            targetEpochMillis = Clock.System.now().toEpochMilliseconds() + 81342605
        )
    }
}

@Composable
@Preview
fun TicketSuccessCardPreview() {
    CrewTheme {
        TicketSuccessCard()
    }
}
