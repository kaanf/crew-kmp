package com.kaanf.home.presentation.dashboard.component.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kaanf.core.designsystem.component.info.ReminderCard
import com.kaanf.core.designsystem.theme.CrewTheme
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.dashboard_profile_photo_reminder_description
import crew.feature.home.presentation.generated.resources.dashboard_profile_photo_reminder_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DashboardProfilePhotoReminder(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ReminderCard(
        emoji = "📸",
        title = stringResource(Res.string.dashboard_profile_photo_reminder_title),
        description = stringResource(Res.string.dashboard_profile_photo_reminder_description),
        onClick = onClick,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun DashboardProfilePhotoReminderPreview() {
    CrewTheme {
        DashboardProfilePhotoReminder(onClick = {})
    }
}
