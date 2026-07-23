package com.kaanf.home.presentation.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
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
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DeleteAccountDialog(
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseDialog(onDismissRequest = { if (!isDeleting) onDismiss() }) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
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
                painter = painterResource(AccessIcons.Close),
                contentDescription = null,
                tint = AccessDefaults.LeftArrowColor,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Delete your account?",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = AccessDefaults.TextPrimary,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "This permanently deletes your account and\ncannot be undone.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(18.dp))

        BaseButton(
            text = "Keep my account",
            onClick = onDismiss,
            enabled = !isDeleting,
            filled = true,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !isDeleting,
                    onClick = onConfirm,
                ),
            text = if (isDeleting) "Deleting…" else "Delete my account",
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.LeftArrowColor,
                fontSize = 12.sp,
            ),
        )
    }
}

@Composable
@Preview
private fun DeleteAccountDialogPreview() {
    CrewTheme {
        DeleteAccountDialog(
            isDeleting = false,
            onConfirm = {},
            onDismiss = {},
        )
    }
}
