package com.kaanf.auth.presentation.profilepicture

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.auth.presentation.util.mediapicker.rememberImagePickerLauncher
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.button.BaseMiniButton
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.component.info.InfoCard
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.core.presentation.util.dottedBorder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfilePictureRoot(
    viewModel: ProfilePictureViewModel = koinViewModel(),
    onBack: () -> Unit,
    onUploadSuccess: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ProfilePictureEvent.UploadSuccess -> onUploadSuccess()
            ProfilePictureEvent.UploadError -> Unit
        }
    }

    val launcher = rememberImagePickerLauncher { pickedImageData ->
        viewModel.onAction(
            ProfilePictureAction.OnPictureSelected(
                pickedImageData.bytes,
                pickedImageData.mimeType,
            ),
        )
    }

    SnackbarScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.ProfilePicture,
                onBackClick = onBack,
            )
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        ProfilePictureScreen(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            state = state,
            onAction = { action ->
                when (action) {
                    is ProfilePictureAction.OnUploadPictureClick -> {
                        launcher.launch()
                    }

                    else -> Unit
                }
                viewModel.onAction(action)
            },
        )
    }
}

@Composable
fun ProfilePictureScreen(
    modifier: Modifier = Modifier,
    state: ProfilePictureState,
    onAction: (ProfilePictureAction) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
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
                lineHeight = 22.sp,
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
                    onClick = { onAction(ProfilePictureAction.OnUploadPictureClick) },
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

        Spacer(modifier = Modifier.height(16.dp))

        BaseMiniButton(
            text = if (state.hasSelectedImage) "Change photo" else "Add a photo",
            onClick = {
                onAction(ProfilePictureAction.OnUploadPictureClick)
            },
            filled = false,
            leadingIcon = AccessIcons.Camera,
        )

        Spacer(modifier = Modifier.height(32.dp))

        InfoCard(
            text = buildAnnotatedString {
                append("Only players at the events you join can see your photo. You can change it anytime in settings.")
            },
            backgroundColor = AccessDefaults.SurfaceElevated,
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
                    .padding(20.dp),
            isLoading = state.isUploadingImage,
            enabled = state.hasSelectedImage && !state.isUploadingImage,
            filled = true,
        )
    }
}

@Composable
@Preview
private fun Preview() {
    CrewTheme {
        ProfilePictureScreen(
            state = ProfilePictureState(),
            onAction = {},
        )
    }
}
