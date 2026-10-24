package com.kaanf.home.presentation.dashboard.component.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.dashboard_profile_photo_reminder_description
import crew.feature.home.presentation.generated.resources.dashboard_profile_photo_reminder_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DashboardProfilePhotoReminder(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = AccessDefaults.Surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "📸",
                fontSize = 24.sp,
                modifier = Modifier.clearAndSetSemantics { },
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = stringResource(Res.string.dashboard_profile_photo_reminder_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = AccessDefaults.TextPrimary,
                )
                Text(
                    text = stringResource(Res.string.dashboard_profile_photo_reminder_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = AccessDefaults.TextSecondary,
                )
            }
            Icon(
                painter = painterResource(AccessIcons.RightChevron),
                contentDescription = null,
                tint = AccessDefaults.TextMuted,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun DashboardProfilePhotoReminderPreview() {
    CrewTheme {
        DashboardProfilePhotoReminder(onClick = {})
    }
}
