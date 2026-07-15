package com.kaanf.core.designsystem.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BaseDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            DialogProperties(
                dismissOnBackPress = dismissOnBackPress,
                dismissOnClickOutside = dismissOnClickOutside,
                usePlatformDefaultWidth = false,
            ),
    ) {
        Column(
            modifier =
                modifier
                    .widthIn(max = 360.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .background(
                        color = AccessDefaults.Surface,
                        shape = AccessShapes.Large,
                    )
                    .border(
                        width = 1.dp,
                        color = AccessDefaults.Border,
                        shape = AccessShapes.Large,
                    )
                    .padding(24.dp),
            content = content,
        )
    }
}

@Preview
@Composable
private fun BaseDialogPreview() {
    CrewTheme(isDarkTheme = true) {
        BaseDialog(onDismissRequest = {}) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Leave the game?",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            color = AccessDefaults.TextPrimary,
                        ),
                )
                Text(
                    text = "If you leave now, you'll exit the lobby and need to rejoin.",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = AccessDefaults.TextSecondary,
                            textAlign = TextAlign.Center,
                        ),
                )
                BaseButton(
                    text = "Leave game",
                    onClick = {},
                    filled = true,
                    modifier = Modifier.padding(top = 8.dp),
                )
                BaseButton(
                    text = "Cancel",
                    onClick = {},
                )
            }
        }
    }
}
