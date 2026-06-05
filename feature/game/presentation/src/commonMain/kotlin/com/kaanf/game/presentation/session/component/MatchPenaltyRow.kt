package com.kaanf.game.presentation.session.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults

@Composable
fun MatchPenaltyRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "RPS Bonus",
            style = MaterialTheme.typography.titleMedium.copy(
                color = AccessDefaults.TextSecondary,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp
            )
        )

        Text(
            text = "+3 to Mira",
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.LeftArrowColor,
                fontSize = 12.sp
            )
        )
    }
}
