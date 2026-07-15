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
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
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
            text = "Maçtan ayrılmak\nistediğine emin misin?",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = AccessDefaults.TextPrimary,
                textAlign = TextAlign.Center,
            ),
        )

        Text(
            text = "Şimdi ayrılırsan bu maçı kaybetmiş sayılırsın. Etkinlikte kalır, yeni maç yapabilirsin.",
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
                .background(
                    color = AccessDefaults.SurfaceElevated,
                    shape = AccessShapes.Medium
                )
                .border(
                    width = 1.dp,
                    color = AccessDefaults.BorderSoft,
                    shape = AccessShapes.Medium
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MatchPenaltyRow(
                label = "Hükmen galibiyet",
                value = "+5 · ${opponentName.ifBlank { "Rakip" }}",
            )
            MatchPenaltyRow(
                label = "Senin puanın",
                value = "+0",
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        BaseButton(
            text = "Maça Dön",
            onClick = onStay,
            filled = true,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Maçtan Ayrıl",
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
