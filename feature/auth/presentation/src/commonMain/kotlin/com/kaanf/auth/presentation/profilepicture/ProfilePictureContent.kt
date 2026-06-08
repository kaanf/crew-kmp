package com.kaanf.auth.presentation.profilepicture

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.button.BaseMiniButton
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.component.info.InfoCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.util.dottedBorder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfilePictureContent(
    state: ProfilePictureState,
    onAction: (ProfilePictureAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Put a face to the name.",
            style = MaterialTheme.typography.displaySmall.copy(
                color = AccessDefaults.TextPrimary,
                textAlign = TextAlign.Start,
            ),
        )

        Text(
            text = "Crew plays out face-to-face. Your photo helps the stranger you matched with find you across a loud bar — and it sits next to your name on the leaderboard.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            ),
            modifier = Modifier.padding(top = 8.dp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(200.dp)
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
                    onClick = {
                        if (state.hasSelectedImage) {
                            onAction(ProfilePictureAction.OnReCropClick)
                        } else {
                            onAction(ProfilePictureAction.OnUploadPictureClick)
                        }
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            val selectedImage = state.selectedImageBytes
            if (selectedImage != null) {
                BaseImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    imageBytes = selectedImage,
                    contentScale = ContentScale.Crop,
                )
            } else {
                Icon(
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
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BaseMiniButton(
                text = if (state.hasSelectedImage) "Change photo" else "Add a photo",
                onClick = {
                    onAction(ProfilePictureAction.OnUploadPictureClick)
                },
                filled = false,
                leadingIcon = AccessIcons.Camera,
            )

            if (state.hasSelectedImage) {
                BaseMiniButton(
                    text = "Remove",
                    onClick = {
                        onAction(ProfilePictureAction.OnRemoveClick)
                    },
                    filled = false,
                    textColor = AccessDefaults.LeftArrowColor,
                    leadingIcon = AccessIcons.Close,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        InfoCard(
            text = buildAnnotatedString {
                append("Only players at the events you join can see your photo. You can change it anytime in settings.")
            },
            backgroundColor = AccessDefaults.Surface,
            icon = AccessIcons.Shield,
            iconTint = AccessDefaults.TextPrimary,
        )

        Spacer(modifier = Modifier.weight(1f))

        BaseButton(
            text = if (state.hasSelectedImage) "Continue" else "Add a photo to continue",
            onClick = {
                onAction(ProfilePictureAction.OnConfirmClick)
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 4.dp,
                        vertical = 20.dp
                    ),
            isLoading = state.isUploadingImage,
            enabled = state.hasSelectedImage && !state.isUploadingImage,
            filled = true,
        )
    }
}

@Composable
@Preview
private fun ProfilePictureContentPreview() {
    CrewTheme {
        ProfilePictureContent(
            state = ProfilePictureState(),
            onAction = {},
        )
    }
}
