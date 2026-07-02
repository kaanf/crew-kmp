package com.kaanf.game.presentation.gamelobby.component.dialog

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
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LeaveEventDialog(
    onStay: () -> Unit,
    onLeave: () -> Unit,
    title: String = "Etkinlikten ayrılmak\nistediğine emin misin?",
    subtitle: String = "İstediğin zaman geri dönebilirsin; yerin korunur.",
    stayLabel: String = "Oyunda Kal",
    leaveLabel: String = "Etkinlikten Ayrıl",
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
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
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                color = AccessDefaults.TextPrimary,
                textAlign = TextAlign.Center,
            ),
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        BaseButton(
            text = stayLabel,
            onClick = onStay,
            filled = true,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = leaveLabel,
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
fun LeaveEventDialogPreview() {
    CrewTheme {
        LeaveEventDialog(
            onStay = {},
            onLeave = {},
        )
    }
}
