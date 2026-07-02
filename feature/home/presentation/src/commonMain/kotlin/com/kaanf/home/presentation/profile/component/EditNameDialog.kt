package com.kaanf.home.presentation.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.designsystem.component.textfield.BaseTextField
import com.kaanf.core.designsystem.theme.AccessDefaults

@Composable
fun EditNameDialog(
    initialName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val textState = rememberTextFieldState(initialName)
    val canSave by remember {
        derivedStateOf { textState.text.isNotBlank() }
    }

    BaseDialog(onDismissRequest = onDismiss) {
        Text(
            text = "Edit name",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = AccessDefaults.TextPrimary,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        BaseTextField(
            state = textState,
            placeholder = "Full name",
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BaseButton(
                text = "Cancel",
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                filled = false,
            )

            BaseButton(
                text = "Done",
                onClick = { onConfirm(textState.text.toString()) },
                modifier = Modifier.weight(1f),
                enabled = canSave,
                filled = true,
            )
        }
    }
}
