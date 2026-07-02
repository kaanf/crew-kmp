package com.kaanf.core.designsystem.component.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.button.BaseMiniButton
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.util.dottedBorder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * The image shown inside an [EditableProfilePhoto]. The source differs by caller:
 * the registration flow crops in-memory [Bytes] before upload, while screens that show an
 * already-saved photo render it from a [Url]. [None] falls back to the user placeholder icon.
 */
sealed interface ProfilePhoto {
    data object None : ProfilePhoto
    data class Url(val url: String) : ProfilePhoto
    class Bytes(val bytes: ByteArray) : ProfilePhoto {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Bytes) return false
            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int = bytes.contentHashCode()
    }
}

/**
 * Circular profile photo with the dotted accent ring plus the "Change/Add" and "Remove" actions
 * underneath. Shared by the auth profile-picture step and the profile screen.
 *
 * [onPhotoClick] (tapping the circle) is kept separate from [onChangeClick] (the button) on purpose:
 * the auth flow re-opens the cropper on tap but picks a new source from the button, whereas the
 * profile screen wires both to the same action.
 */
@Composable
fun EditableProfilePhoto(
    photo: ProfilePhoto,
    onPhotoClick: () -> Unit,
    onChangeClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
    isUploading: Boolean = false,
    isRemoving: Boolean = false,
    photoSize: Dp = 200.dp,
) {
    val hasPhoto = photo !is ProfilePhoto.None

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(photoSize)
                .dottedBorder(
                    color = AccessDefaults.AccentGlow,
                    shape = CircleShape,
                    strokeWidth = 2.dp,
                    dotLength = 4.dp,
                    gapLength = 4.dp,
                )
                .padding(2.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2C3A2A),
                            Color(0xFF1A241A),
                        ),
                    ),
                    shape = CircleShape,
                )
                .border(
                    width = 6.dp,
                    color = Color(0xFF252E22),
                    shape = CircleShape,
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onPhotoClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            val imageModifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
            when (photo) {
                is ProfilePhoto.Url -> BaseImage(
                    modifier = imageModifier,
                    imageUrl = photo.url,
                    contentScale = ContentScale.Crop,
                )

                is ProfilePhoto.Bytes -> BaseImage(
                    modifier = imageModifier,
                    imageBytes = photo.bytes,
                    contentScale = ContentScale.Crop,
                )

                ProfilePhoto.None -> Icon(
                    modifier = Modifier.size(56.dp),
                    painter = painterResource(AccessIcons.User),
                    tint = AccessDefaults.TextPrimary,
                    contentDescription = "User profile avatar",
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            BaseMiniButton(
                text = if (hasPhoto) "Change photo" else "Add a photo",
                onClick = onChangeClick,
                filled = false,
                isLoading = isUploading,
                leadingIcon = AccessIcons.Camera,
            )

            if (hasPhoto) {
                BaseMiniButton(
                    text = "Remove",
                    onClick = onRemoveClick,
                    filled = false,
                    isLoading = isRemoving,
                    loadingText = "Remove",
                    textColor = AccessDefaults.LeftArrowColor,
                    leadingIcon = AccessIcons.Close,
                )
            }
        }
    }
}

@Composable
@Preview
private fun EditableProfilePhotoPreview() {
    CrewTheme {
        EditableProfilePhoto(
            photo = ProfilePhoto.None,
            onPhotoClick = {},
            onChangeClick = {},
            onRemoveClick = {},
        )
    }
}
