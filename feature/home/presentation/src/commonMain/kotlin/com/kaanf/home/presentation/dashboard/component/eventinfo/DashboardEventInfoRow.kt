package com.kaanf.home.presentation.dashboard.component.eventinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.kaanf.core.designsystem.theme.AccessDefaults

@Composable
fun DashboardEventInfoRow(
    leftText: String,
    description: String? = null,
    rightText: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = leftText,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineSmall,
                color = AccessDefaults.TextPrimary,
            )

            if (!description.isNullOrEmpty()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AccessDefaults.TextMuted,
                        fontSize = 13.sp,
                        letterSpacing = 0.sp,
                    ),
                )
            }
        }

        if (rightText.isNotEmpty()) {
            Text(
                text = rightText,
                style = MaterialTheme.typography.bodySmall,
                color = AccessDefaults.TextMuted,
            )
        }
    }
}
