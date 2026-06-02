package com.kaanf.core.designsystem.component.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun TwoOptionBottomSheet(
    modifier: Modifier = Modifier,
    iconContent: (@Composable () -> Unit)? = null,
    title: String,
    description: String,
    confirmButtonText: String,
    cancelButtonText: String,
    onConfirmClicked: () -> Unit,
    onCancelClicked: () -> Unit,
    onDismiss: () -> Unit,
    isDismissable: Boolean = false,
    showDragHandle: Boolean = false
) {
    ContainerBottomSheet(
        showDragHandle = showDragHandle,
        dismissible = isDismissable,
        onDismiss = onDismiss,
        content = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        all = 24.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 6.dp,
                    alignment = Alignment.CenterVertically,
                ),
            ) {
                if (iconContent != null) {
                    iconContent()

                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = AccessDefaults.TextPrimary,
                        textAlign = TextAlign.Center
                    ),
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AccessDefaults.TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                    ),
                )

                Spacer(modifier = Modifier.height(12.dp))

                BaseButton(
                    text = confirmButtonText,
                    onClick = onConfirmClicked,
                    filled = true,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = cancelButtonText,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onCancelClicked,
                    ),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.LeftArrowColor,
                        fontSize = 12.sp,
                    ),
                )
            }
        }
    )
}
