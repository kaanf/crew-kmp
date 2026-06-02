package com.kaanf.game.presentation.whowon.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.game.presentation.model.GameResultOptionUi
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WhoWonRow(
    optionUi: GameResultOptionUi,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AccessDefaults.Surface,
                shape = AccessShapes.Card,
            )
            .border(
                width = 1.dp,
                color = AccessDefaults.Border,
                shape = AccessShapes.Card,
            )
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = optionUi.pointText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 12.sp
                ),
            )

            Text(
                text = optionUi.title,
                style = MaterialTheme.typography.headlineLarge,
            )

            Text(
                text = optionUi.description,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextSecondary,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }

        Text(
            modifier = Modifier
                .background(
                    color = AccessDefaults.SurfaceElevated,
                    shape = AccessShapes.XLarge,
                )
                .border(
                    width = 1.dp,
                    color = AccessDefaults.Border,
                    shape = AccessShapes.XLarge,
                )
                .padding(16.dp),
            text = optionUi.emoji,
            fontSize = 32.sp,
        )
    }
}

@Composable
@Preview
fun WhoWonRowPreview() {
    CrewTheme {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            WhoWonRow(
                GameResultOptionUi(
                    title = "I won",
                    pointText = "+10 PTS",
                    description = "You pick a task for Mira",
                    emoji = "\uD83D\uDC51",
                ),
            )

            WhoWonRow(
                GameResultOptionUi(
                    title = "I lost",
                    pointText = "NO BONUS",
                    description = "Mira picks a task for you",
                    emoji = "\uD83D\uDE05",
                ),
            )
        }
    }
}
