package com.kaanf.home.presentation.profileboard

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.profileboard.ProfileBoardAction
import com.kaanf.core.designsystem.profileboard.ProfileBoardTilesContent
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileBoardRoot(
    onClose: () -> Unit,
    onEditProfile: () -> Unit,
    viewModel: ProfileBoardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }
    val close: () -> Unit = {
        if (!state.isSaving && !state.movieSearch.isSaving) {
            if (state.isEditingBoard) viewModel.onAction(ProfileBoardAction.CancelEditing)
            onClose()
        }
    }
    BackHandler(enabled = state.isEditingBoard || state.isSaving) {
        when {
            state.isSaving || state.movieSearch.isSaving -> Unit
            state.openTool != null -> viewModel.onAction(ProfileBoardAction.CloseBoardTool)
            else -> viewModel.onAction(ProfileBoardAction.CancelEditing)
        }
    }
    AppScaffold { padding ->
        ProfileBoardTilesContent(
            state = state,
            modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
                .consumeWindowInsets(PaddingValues(bottom = padding.calculateBottomPadding())),
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            onBack = close,
            onEditProfile = onEditProfile,
            onRetry = viewModel::refresh,
            onQrImageError = viewModel::onQrImageError,
            onAction = viewModel::onAction,
        )
    }
}
