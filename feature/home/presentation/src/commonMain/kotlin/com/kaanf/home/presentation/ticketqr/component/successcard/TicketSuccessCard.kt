package com.kaanf.home.presentation.ticketqr.component.successcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.home.presentation.ticketqr.component.countdown.CountdownCard
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.ticket_qr_success_on_list
import crew.feature.home.presentation.generated.resources.ticket_qr_success_see_you
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

@Composable
fun TicketSuccessCard(
    doorsAtEpochMillis: Long,
    serverClockOffsetMillis: Long,
) {
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
                        stringResource(Res.string.ticket_qr_success_on_list),
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
                        stringResource(Res.string.ticket_qr_success_see_you),
                    )
                }
            },
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        CountdownCard(
            targetEpochMillis = doorsAtEpochMillis,
            serverClockOffsetMillis = serverClockOffsetMillis,
        )
    }
}

@Composable
@Preview
fun TicketSuccessCardPreview() {
    CrewTheme {
        TicketSuccessCard(
            doorsAtEpochMillis = Clock.System.now().toEpochMilliseconds() + 81342605,
            serverClockOffsetMillis = 0L,
        )
    }
}
