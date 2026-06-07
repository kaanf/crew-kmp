package com.kaanf.home.presentation.ticketqr.component.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.badge.RoundedBadge
import com.kaanf.core.designsystem.component.button.BaseMiniButton
import com.kaanf.core.designsystem.component.divider.SectionDivider
import com.kaanf.core.designsystem.component.qr.UserQrCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.ticket_qr_enter_event_code_action
import crew.feature.home.presentation.generated.resources.ticket_qr_entry_badge
import crew.feature.home.presentation.generated.resources.ticket_qr_event_date
import crew.feature.home.presentation.generated.resources.ticket_qr_event_series
import crew.feature.home.presentation.generated.resources.ticket_qr_event_venue
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun TicketQrInfoCard(
    eventTitle: String,
    entryCode: String,
    formattedVenueAddress: String,
    formattedDoorTime: String,
    onEventCodeClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = AccessDefaults.Surface,
                shape = AccessShapes.Medium,
            )
            .border(
                width = 1.dp,
                color = AccessDefaults.Border,
                shape = AccessShapes.Medium,
            )
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = eventTitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.TextMuted,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                    ),
                )

                Text(
                    text = formattedDoorTime,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = AccessDefaults.TextPrimary,
                    ),
                )

                Text(
                    text = formattedVenueAddress,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = AccessDefaults.TextMuted,
                    ),
                )

                Spacer(modifier = Modifier.height(6.dp))
            }

            RoundedBadge(
                text = stringResource(Res.string.ticket_qr_entry_badge),
                backgroundColor = Color.Transparent,
                borderColor = AccessDefaults.Accent,
                textColor = AccessDefaults.Accent,
            )
        }

        BaseMiniButton(
            text = stringResource(Res.string.ticket_qr_enter_event_code_action),
            filled = false,
            onClick = { onEventCodeClicked() },
        )

        SectionDivider()

        UserQrCard(
            inputText = entryCode + Uuid.random(),
        )

        Text(
            text = entryCode,
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 12.sp,
            ),
        )
    }
}
