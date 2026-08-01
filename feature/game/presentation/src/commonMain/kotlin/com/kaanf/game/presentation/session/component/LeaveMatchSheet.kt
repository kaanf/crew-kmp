package com.kaanf.game.presentation.session.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.modifier.surfaceCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LeaveMatchSheet(
    opponentName: String,
    onStay: () -> Unit,
    onLeave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = AccessDefaults.LeftArrowColor.copy(alpha = 0.05f),
                    shape = CircleShape,
                )
                .border(
                    width = 1.dp,
                    color = AccessDefaults.LeftArrowColor.copy(alpha = 0.2f),
                    shape = CircleShape,
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(AccessIcons.LeftArrow),
                contentDescription = null,
                tint = AccessDefaults.LeftArrowColor,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Sure you want to\nleave the match?",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = AccessDefaults.TextPrimary,
                textAlign = TextAlign.Center,
            ),
        )

        Text(
            text = "If you leave now, this match counts as a loss. You stay in the event and can start a new match.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(6.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .surfaceCard(backgroundColor = AccessDefaults.SurfaceElevated)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MatchPenaltyRow(
                label = "Win by forfeit",
                value = "+5 · ${opponentName.ifBlank { "Opponent" }}",
            )
            MatchPenaltyRow(
                label = "Your points",
                value = "+0",
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        BaseButton(
            text = "Back to the match",
            onClick = onStay,
            filled = true,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Leave match",
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onLeave,
            ),
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.LeftArrowColor,
                fontSize = 12.sp,
            ),
        )
    }
}

@Composable
@Preview
fun LeaveMatchSheetPreview() {
    CrewTheme {
        LeaveMatchSheet(
            opponentName = "Mira",
            onStay = {},
            onLeave = {},
        )
    }
}
