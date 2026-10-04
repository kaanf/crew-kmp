package com.kaanf.core.designsystem.component.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.force_update_button
import crew.core.designsystem.generated.resources.force_update_description
import crew.core.designsystem.generated.resources.force_update_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Kapatılamaz: kullanıcı mağazadan dönünce sheet hâlâ açık, güncellemeden devam edemez. */
@Composable
fun ForceUpdateBottomSheet(onUpdateClick: () -> Unit) {
    ContainerBottomSheet(
        onDismiss = {},
        dismissible = false,
    ) {
        ForceUpdateContent(onUpdateClick = onUpdateClick)
    }
}

@Composable
private fun ForceUpdateContent(onUpdateClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .background(color = AccessDefaults.SurfaceElevated, shape = CircleShape)
                .border(width = 1.dp, color = AccessDefaults.Border, shape = CircleShape)
                .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(AccessIcons.Refresh),
                contentDescription = null,
                tint = AccessDefaults.TextMuted,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(Res.string.force_update_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = AccessDefaults.TextPrimary,
                textAlign = TextAlign.Center,
            ),
        )

        Text(
            text = stringResource(Res.string.force_update_description),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        BaseButton(
            text = stringResource(Res.string.force_update_button),
            onClick = onUpdateClick,
            filled = true,
        )
    }
}

@Composable
@Preview
fun ForceUpdateContentPreview() {
    CrewTheme {
        ForceUpdateContent(onUpdateClick = {})
    }
}
