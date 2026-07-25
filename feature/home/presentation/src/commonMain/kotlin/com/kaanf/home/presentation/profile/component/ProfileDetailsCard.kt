package com.kaanf.home.presentation.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileDetailsCard(
    fullName: String,
    email: String,
    language: String,
    onEditName: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AccessDefaults.Surface, AccessShapes.Large)
            .border(1.dp, AccessDefaults.Border, AccessShapes.Large)
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        // Full name is the only editable identity field — tap to rename, no lock badge.
        ProfileDetailRow(
            label = "Full name",
            value = fullName,
            valueColor = AccessDefaults.TextPrimary,
            onClick = onEditName,
        )

        RowDivider()

        ProfileDetailRow(label = "Email", value = email, locked = true)

        RowDivider()

        // Language selection is temporarily locked; re-enable by restoring the selection sheet.
        ProfileDetailRow(label = "Language", value = language, locked = true)

        RowDivider()

        DeleteAccountRow(onClick = onDeleteAccount)
    }
}

@Composable
private fun DeleteAccountRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "Delete my account",
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.LeftArrowColor,
                fontSize = 12.sp,
            ),
        )

        Icon(
            modifier = Modifier.size(14.dp),
            painter = painterResource(AccessIcons.RightChevron),
            tint = AccessDefaults.LeftArrowColor,
            contentDescription = null,
        )
    }
}

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(AccessDefaults.BorderSoft),
    )
}

@Composable
private fun ProfileDetailRow(
    label: String,
    value: String,
    locked: Boolean = false,
    valueColor: Color = AccessDefaults.TextSecondary,
    trailingIcon: DrawableResource? = if (locked) AccessIcons.Lock else null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 12.sp,
            ),
        )

        Text(
            modifier = Modifier.weight(1f),
            text = value.ifBlank { "—" },
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (locked) AccessDefaults.TextSecondary else valueColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = -(0.2).sp,
            ),
        )

        if (trailingIcon != null) {
            Icon(
                modifier = Modifier.size(14.dp),
                painter = painterResource(trailingIcon),
                tint = AccessDefaults.TextFaint,
                contentDescription = null,
            )
        }
    }
}

@Composable
@Preview
private fun ProfileDetailsCardPreview() {
    CrewTheme {
        Box(modifier = Modifier.background(AccessDefaults.Background).padding(16.dp)) {
            ProfileDetailsCard(
                fullName = "Kaan Fırat",
                email = "frtpkaan@gmail.com",
                language = "English",
                onEditName = {},
                onDeleteAccount = {},
            )
        }
    }
}
