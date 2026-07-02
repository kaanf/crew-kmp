package com.kaanf.auth.presentation.profilepicture

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.EditableProfilePhoto
import com.kaanf.core.designsystem.component.avatar.ProfilePhoto
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.info.InfoCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.util.TestTags
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
            .testTag(TestTags.PROFILE_PICTURE_SCREEN)
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

        EditableProfilePhoto(
            photo = state.selectedImageBytes?.let { ProfilePhoto.Bytes(it) } ?: ProfilePhoto.None,
            onPhotoClick = {
                if (state.hasSelectedImage) {
                    onAction(ProfilePictureAction.OnReCropClick)
                } else {
                    onAction(ProfilePictureAction.OnUploadPictureClick)
                }
            },
            onChangeClick = { onAction(ProfilePictureAction.OnUploadPictureClick) },
            onRemoveClick = { onAction(ProfilePictureAction.OnRemoveClick) },
        )

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
                    )
                    .testTag(TestTags.PROFILE_PICTURE_SUBMIT),
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
