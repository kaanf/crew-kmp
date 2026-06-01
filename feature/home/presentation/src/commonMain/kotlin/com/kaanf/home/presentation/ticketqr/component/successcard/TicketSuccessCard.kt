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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(
                all = 4.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                AccessDefaults.Accent,
                                AccessDefaults.Coral,
                                AccessDefaults.Sky,
                                AccessDefaults.Amber,
                                AccessDefaults.Teal,
                            )
                        ),
                        fontSize = 24.sp
                    ),
                ) {
                    append(
                        "You're on the list.",
                    )
                }

                append("\n")

                withStyle(
                    style = SpanStyle(
                        color = AccessDefaults.TextPrimary,
                        fontWeight = FontWeight.Bold,
                    ),
                ) {
                    append(
                        "See you saturday!"
                    )
                }
            },
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
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
