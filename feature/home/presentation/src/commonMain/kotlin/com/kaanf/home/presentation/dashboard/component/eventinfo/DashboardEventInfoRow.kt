package com.kaanf.home.presentation.dashboard.component.eventinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults

@Composable
fun DashboardEventInfoRow(
    leftText: String,
    description: String? = null,
    rightText: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = leftText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.sp
                ),
            )

            if (!description.isNullOrEmpty()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AccessDefaults.TextMuted,
                        fontSize = 12.sp,
                        letterSpacing = 0.sp,
                    ),
                )
            }
        }

        Text(
            text = rightText,
            style = MaterialTheme.typography.titleMedium.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 12.sp,
                letterSpacing = -(0.1).sp,
            ),
        )
    }
}
