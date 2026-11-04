package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.AvatarContent
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.BricolageGrotesque

@Composable
fun ProfileBoardIdentity(
    fullName: String,
    profilePictureUrl: String?,
    modifier: Modifier = Modifier,
    avatarSize: Int = 96,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val avatar = profilePictureUrl?.takeIf { it.isNotBlank() }?.let(AvatarContent::Image)
            ?: AvatarContent.Initials(
                label = fullName.trim().take(1).uppercase(),
                color = AccessDefaults.Rose,
            )
        AvatarCircle(
            content = avatar,
            avatarSize = avatarSize,
            borderSize = 3,
            borderColor = AccessDefaults.TextPrimary,
        )
        if (fullName.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = fullName,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = BricolageGrotesque,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                ),
                color = AccessDefaults.TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
