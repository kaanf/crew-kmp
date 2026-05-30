package com.kaanf.core.designsystem.component.avatar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.kaanf.core.presentation.model.UserAvatar

@Composable
fun AvatarStack(
    avatars: List<UserAvatar>,
    avatarSize: Int = 46,
    extraCount: Int,
    modifier: Modifier = Modifier,
) {
    val step = (avatarSize/1.5).dp

    Box(
        modifier = modifier
            .wrapContentSize()
    ) {
        avatars.forEachIndexed { index, avatar ->
            AvatarCircle(
                label = avatar.label,
                color = avatar.color,
                avatarSize = avatarSize,
                modifier = Modifier.offset(x = step * index)
            )
        }

        ExtraAvatarCircle(
            count = extraCount,
            avatarSize = avatarSize,
            modifier = Modifier.offset(x = step * avatars.size)
        )
    }
}
