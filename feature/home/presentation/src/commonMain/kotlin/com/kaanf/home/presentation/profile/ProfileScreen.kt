package com.kaanf.home.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.avatar.EditableProfilePhoto
import com.kaanf.core.designsystem.component.avatar.ProfilePhoto
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.mediapicker.CropImageContent
import com.kaanf.core.designsystem.component.mediapicker.ImageSourceBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.domain.model.settings.AppLanguage
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.mediapicker.PickedImageData
import com.kaanf.core.presentation.util.mediapicker.rememberCameraLauncher
import com.kaanf.core.presentation.util.mediapicker.rememberImagePickerLauncher
import com.kaanf.home.presentation.profile.component.DeleteAccountDialog
import com.kaanf.home.presentation.profile.component.EditNameDialog
import com.kaanf.home.presentation.profile.component.ProfileDetailsCard
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.profile_language_czech
import crew.feature.home.presentation.generated.resources.profile_language_english
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoot(
    onBack: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showSourceSheet by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Deletion is confirmed by the backend before this flips; leaving here wipes the home stack.
    LaunchedEffect(state.accountDeleted) {
        if (state.accountDeleted) onSignedOut()
    }

    val onPicked = { picked: PickedImageData ->
        viewModel.onAction(ProfileAction.OnPhotoPicked(picked.bytes, picked.mimeType))
    }
    val galleryLauncher = rememberImagePickerLauncher(onResult = onPicked)
    val cameraLauncher = rememberCameraLauncher(onResult = onPicked)

    if (showNameDialog) {
        EditNameDialog(
            initialName = state.displayedName,
            onConfirm = { name ->
                viewModel.onAction(ProfileAction.OnNameEdited(name))
                showNameDialog = false
            },
            onDismiss = { showNameDialog = false },
        )
    }

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

    if (showDeleteDialog) {
        DeleteAccountDialog(
            isDeleting = state.isDeletingAccount,
            onConfirm = { viewModel.onAction(ProfileAction.OnDeleteAccountConfirm) },
            onDismiss = { showDeleteDialog = false },
        )
    }

    ProfileScreen(
        state = state,
        onBack = onBack,
        onAction = { action ->
            viewModel.onAction(action)
            when (action) {
                is ProfileAction.OnChangePhotoClick -> showSourceSheet = true
                is ProfileAction.OnEditNameClick -> showNameDialog = true
                is ProfileAction.OnDeleteAccountClick -> showDeleteDialog = true
                // Session is cleared NonCancellable in the VM; navigating here wipes the home stack.
                is ProfileAction.OnSignOutClick -> onSignedOut()
                else -> Unit
            }
        },
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    onBack: () -> Unit,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(enabled = state.isCropping) {
        onAction(ProfileAction.OnCropCancelled)
    }

    AppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                state = AppTopBarState.Profile(
                    hasUnsavedChanges = state.hasUnsavedChanges,
                    isSaving = state.isSaving,
                ),
                onBackClick = {
                    when {
                        state.isCropping -> onAction(ProfileAction.OnCropCancelled)
                        state.hasUnsavedChanges -> onAction(ProfileAction.OnCancelEdit)
                        else -> onBack()
                    }
                },
                onRightClick = {
                    if (state.hasUnsavedChanges) {
                        onAction(ProfileAction.OnSaveChanges)
                    } else {
                        onAction(ProfileAction.OnSignOutClick)
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                EditableProfilePhoto(
                    photo = profilePhotoOf(state),
                    onPhotoClick = { onAction(ProfileAction.OnChangePhotoClick) },
                    onChangeClick = { onAction(ProfileAction.OnChangePhotoClick) },
                    onRemoveClick = { onAction(ProfileAction.OnRemovePhotoClick) },
                )

                Spacer(modifier = Modifier.height(24.dp))

                ProfileDetailsCard(
                    fullName = state.displayedName,
                    email = state.email,
                    gender = state.gender,
                    language = stringResource(state.language.labelRes()),
                    onEditName = { onAction(ProfileAction.OnEditNameClick) },
                    onDeleteAccount = { onAction(ProfileAction.OnDeleteAccountClick) },
                )
            }

            val cropBytes = state.pendingCropBytes
            if (cropBytes != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AccessDefaults.Background),
                ) {
                    CropImageContent(
                        imageBytes = cropBytes,
                        onConfirm = { onAction(ProfileAction.OnCropConfirmed(it)) },
                        onDecodeFailed = { onAction(ProfileAction.OnCropCancelled) },
                    )
                }
            }
        }
    }
}

private fun profilePhotoOf(state: ProfileState): ProfilePhoto = when {
    state.pendingPhotoBytes != null -> ProfilePhoto.Bytes(state.pendingPhotoBytes)
    state.pendingPhotoRemoval -> ProfilePhoto.None
    !state.profilePictureUrl.isNullOrBlank() -> ProfilePhoto.Url(state.profilePictureUrl)
    else -> ProfilePhoto.None
}

private fun AppLanguage.labelRes(): StringResource = when (this) {
    AppLanguage.ENGLISH -> Res.string.profile_language_english
    AppLanguage.CZECH -> Res.string.profile_language_czech
}

@Composable
@Preview
private fun ProfileScreenPreview() {
    CrewTheme {
        ProfileScreen(
            state = ProfileState(),
            onBack = {},
            onAction = {},
        )
    }
}
