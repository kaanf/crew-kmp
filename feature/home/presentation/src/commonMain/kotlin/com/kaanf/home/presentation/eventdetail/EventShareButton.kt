package com.kaanf.home.presentation.eventdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessDefaults
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.event_share_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EventShareButton(enabled: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .clip(CircleShape)
            .background(AccessDefaults.SurfaceElevated)
            .border(1.dp, AccessDefaults.BorderSoft, CircleShape)
            .size(32.dp),
    ) {
        Icon(
            painter = painterResource(AccessIcons.Share),
            contentDescription = stringResource(Res.string.event_share_title),
            tint = if (enabled) AccessDefaults.TextPrimary else AccessDefaults.TextMuted,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
internal expect fun rememberEventShareLauncher(): (String) -> Unit
