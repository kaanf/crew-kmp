package com.kaanf.core.designsystem.component.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.button.BaseMiniButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.ic_tmdb
import crew.core.designsystem.generated.resources.profile_board_credits
import crew.core.designsystem.generated.resources.profile_board_sheet_close
import crew.core.designsystem.generated.resources.profile_board_tmdb_notice
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreditsDialog(onDismiss: () -> Unit) {
    BaseDialog(onDismissRequest = onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(stringResource(Res.string.profile_board_credits),
                style = MaterialTheme.typography.titleLarge, color = AccessDefaults.TextPrimary)
            Image(painterResource(Res.drawable.ic_tmdb), contentDescription = "TMDB",
                modifier = Modifier.size(width = 140.dp, height = 19.dp))
            Text(stringResource(Res.string.profile_board_tmdb_notice),
                style = MaterialTheme.typography.bodyMedium, color = AccessDefaults.TextSecondary)
            BaseMiniButton(text = stringResource(Res.string.profile_board_sheet_close), onClick = onDismiss)
        }
    }
}
