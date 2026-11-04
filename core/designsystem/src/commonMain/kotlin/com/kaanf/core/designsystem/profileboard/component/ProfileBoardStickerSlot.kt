package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.presentation.util.dottedBorder
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.profile_sticker_add
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Empty, tappable sticker placement guide. */
@Composable
internal fun ProfileBoardStickerSlot(
    shape: RoundedCornerShape,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(Res.string.profile_sticker_add)
    Box(
        modifier = modifier
            .semantics { contentDescription = description }
            .clip(shape)
            .clickable(role = Role.Button, onClick = onClick)
            .dottedBorder(
                color = AccessDefaults.TextFaint,
                shape = shape,
                strokeWidth = 1.5.dp,
                dotLength = 4.dp,
                gapLength = 5.dp,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(AccessIcons.Close),
            contentDescription = null,
            tint = AccessDefaults.TextFaint,
            modifier = Modifier.size(16.dp).rotate(45f),
        )
    }
}
