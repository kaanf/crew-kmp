package com.kaanf.home.presentation.ticketqr.component.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.badge.RoundedBadge
import com.kaanf.core.designsystem.component.divider.SectionDivider
import com.kaanf.core.designsystem.component.qr.LogoQrScreen
import com.kaanf.core.designsystem.component.textfield.BaseSelectField
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TicketQrInfoCard(
    onEventCodeClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = AccessDefaults.Surface,
                shape = AccessShapes.Medium
            )
            .border(
                width = 1.dp,
                color = AccessDefaults.Border,
                shape = AccessShapes.Medium
            )
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "CREW · VOL. 14",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.TextMuted,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                )

                Text(
                    text = "Sat May 30 · 20:00",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = AccessDefaults.TextPrimary,
                    )
                )

                Text(
                    text = "Bar Skutek · Vinohrady",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = AccessDefaults.TextMuted,
                    )
                )
            }

            RoundedBadge(
                text = "Entry QR",
                backgroundColor = Color.Transparent,
                borderColor = AccessDefaults.Accent,
                textColor = AccessDefaults.Accent,
            )
        }

        Spacer(modifier = Modifier.height(1.dp))

        Box(
            modifier = Modifier
                .size(285.dp)
                .background(
                    AccessDefaults.TextPrimary,
                    shape = AccessShapes.Large
                ),
            contentAlignment = Alignment.Center
        ) {
            LogoQrScreen(
                modifier = Modifier
                    .size(260.dp),
                inputText = "CR-7K8B-2M9X-04asdasdasda",
            )
        }

        Text(
            text = "CR-7K8B-2M9X-04",
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 12.sp
            )
        )

        SectionDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = AccessDefaults.Border,
                    shape = AccessShapes.Large
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onEventCodeClicked() },
                )
                .padding(horizontal = 12.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter Event Code",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
@Preview
fun TicketQrInfoCardPreview() {
    CrewTheme {
        TicketQrInfoCard(
            onEventCodeClicked = {}
        )
    }
}
