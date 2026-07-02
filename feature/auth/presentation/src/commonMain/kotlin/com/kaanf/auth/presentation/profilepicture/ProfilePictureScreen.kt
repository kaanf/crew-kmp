package com.kaanf.auth.presentation.profilepicture

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.mediapicker.CropImageContent
import com.kaanf.core.designsystem.component.mediapicker.ImageSourceBottomSheet
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.core.presentation.util.mediapicker.PickedImageData
import com.kaanf.core.presentation.util.mediapicker.rememberCameraLauncher
import com.kaanf.core.presentation.util.mediapicker.rememberImagePickerLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfilePictureRoot(
    viewModel: ProfilePictureViewModel = koinViewModel(),
    onUploadSuccess: () -> Unit,
    onSkip: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ProfilePictureEvent.UploadSuccess -> onUploadSuccess()
            ProfilePictureEvent.UploadError -> Unit
            ProfilePictureEvent.SkipSuccess -> onSkip()
        }
    }

    val onPicked = { pickedImageData: PickedImageData ->
        viewModel.onAction(
            ProfilePictureAction.OnPictureSelected(
                pickedImageData.bytes,
                pickedImageData.mimeType,
            ),
        )
    }
    val galleryLauncher = rememberImagePickerLauncher(onResult = onPicked)
    val cameraLauncher = rememberCameraLauncher(onResult = onPicked)

    var showSourceSheet by remember { mutableStateOf(false) }

    if (showSourceSheet) {
        ImageSourceBottomSheet(
            onTakePhoto = {
                showSourceSheet = false
                cameraLauncher.launch()
            },
            onChooseFromGallery = {
                showSourceSheet = false
                galleryLauncher.launch()
            },
            onDismiss = { showSourceSheet = false },
        )
    }

    ProfilePictureScreen(
        state = state,
        onAction = { action ->
            if (action is ProfilePictureAction.OnUploadPictureClick) {
                showSourceSheet = true
            }
            viewModel.onAction(action)
        },
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfilePictureScreen(
    state: ProfilePictureState,
    onAction: (ProfilePictureAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(enabled = state.phase == ProfilePicturePhase.Crop) {
        onAction(ProfilePictureAction.OnCropCancelled)
    }

    AppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                state = when (state.phase) {
                    ProfilePicturePhase.Picker -> AppTopBarState.ProfilePicture
                    ProfilePicturePhase.Crop -> AppTopBarState.ImageCrop
                },
                onBackClick = {
                    when (state.phase) {
                        ProfilePicturePhase.Crop -> onAction(ProfilePictureAction.OnCropCancelled)
                        else -> Unit
                    }
                },
                onRightClick = {
                    if (state.phase == ProfilePicturePhase.Picker) {
                        onAction(ProfilePictureAction.OnSkipClick)
                    }
                },
            )
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = state.phase,
            contentKey = { it },
            contentAlignment = Alignment.Center,
            transitionSpec = {
                val forward = targetState.ordinal > initialState.ordinal
                val towards = if (forward) {
                    AnimatedContentTransitionScope.SlideDirection.Left
                } else {
                    AnimatedContentTransitionScope.SlideDirection.Right
                }
                slideIntoContainer(towards, tween(300)) togetherWith
                    slideOutOfContainer(towards, tween(300)) using
                    null
            },
            label = "profile_picture_phase",
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) { phase ->
            when (phase) {
                ProfilePicturePhase.Picker -> ProfilePictureContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.fillMaxSize(),
                )

                ProfilePicturePhase.Crop -> {
                    val cropBytes = remember { state.pendingCropBytes }
                    if (cropBytes != null) {
                        CropImageContent(
                            imageBytes = cropBytes,
                            onConfirm = {
                                onAction(ProfilePictureAction.OnCropConfirmed(it))
                            },
                            onDecodeFailed = {
                                onAction(ProfilePictureAction.OnCropCancelled)
                            },
                        )
                    }
                }
            }
        }
    }
}
