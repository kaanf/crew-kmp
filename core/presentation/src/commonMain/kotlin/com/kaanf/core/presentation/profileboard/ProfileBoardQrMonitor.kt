package com.kaanf.core.presentation.profileboard

import com.kaanf.core.domain.model.profileboard.BoardQrRenderStatus
import com.kaanf.core.domain.model.profileboard.BoardQrTheme
import com.kaanf.core.domain.repository.ProfileBoardRepository
import com.kaanf.core.domain.repository.UserStore
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarIcon
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import com.kaanf.core.presentation.snackbar.SnackbarVariant
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.core.presentation.util.UIText
import crew.core.presentation.generated.resources.Res
import crew.core.presentation.generated.resources.profile_qr_processing_title
import crew.core.presentation.generated.resources.profile_qr_processing_description
import crew.core.presentation.generated.resources.profile_qr_fallback_title
import crew.core.presentation.generated.resources.profile_qr_fallback_description
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/** Owned by the root ViewModel: leaving the board screen must not cancel a render. */
class ProfileBoardQrMonitor(
    private val repository: ProfileBoardRepository,
    private val userStore: UserStore,
    private val snackbar: SnackbarController,
) {
    fun start(scope: CoroutineScope): Job = scope.launch {
        userStore.observeCurrentUser().map { it?.id }.distinctUntilChanged().collectLatest { userId ->
            try {
                if (userId != null) repository.observeBoard().distinctUntilChanged().collectLatest boardChanged@ { board ->
                    try {
                        if (board == null || BoardQrTheme.find(board.qrStyleId) == null) return@boardChanged
                        coroutineScope {
                            val progress = SnackbarMessage(
                                title = UIText.Resource(Res.string.profile_qr_processing_title),
                                description = UIText.Resource(Res.string.profile_qr_processing_description),
                                variant = SnackbarVariant.Info,
                                icon = SnackbarIcon.Syncing,
                            )
                            val cached = repository.observeQrRender().first()
                            if (cached?.themeId != board.qrStyleId || cached.status == BoardQrRenderStatus.PENDING) {
                                snackbar.showPersistent(PROGRESS_ID, progress)
                            }
                            // syncQrRender caches intermediate PENDING responses during its polling batch.
                            val renderObserver = launch {
                                repository.observeQrRender().distinctUntilChanged().collect { render ->
                                    if (render?.themeId == board.qrStyleId) {
                                        if (render.status == BoardQrRenderStatus.PENDING) snackbar.showPersistent(PROGRESS_ID, progress)
                                        else snackbar.dismissPersistent(PROGRESS_ID)
                                    }
                                }
                            }
                            try {
                                var errorShown = false
                                while (true) {
                                    when (val result = repository.syncQrRender()) {
                                        is Result.Failure -> {
                                            // A temporary network failure must not strand an indefinite notification.
                                            if (!errorShown) snackbar.show(result.error.toSnackbarMessage())
                                            errorShown = true
                                        }
                                        is Result.Success -> {
                                            errorShown = false
                                            val render = result.data
                                            if (render.themeId == board.qrStyleId || render.status == BoardQrRenderStatus.NONE) {
                                                if (render.status != BoardQrRenderStatus.PENDING) {
                                                    snackbar.dismissPersistent(PROGRESS_ID)
                                                    if (render.status == BoardQrRenderStatus.FALLBACK) snackbar.show(SnackbarMessage(
                                                        title = UIText.Resource(Res.string.profile_qr_fallback_title),
                                                        description = UIText.Resource(Res.string.profile_qr_fallback_description),
                                                        variant = SnackbarVariant.Warn,
                                                    ))
                                                    break
                                                }
                                            }
                                        }
                                    }
                                    // Continue after the repository's bounded polling batch, until ready or cancelled.
                                    delay(3_000)
                                }
                            } finally {
                                renderObserver.cancel()
                            }
                        }
                    } finally {
                        snackbar.dismissPersistent(PROGRESS_ID)
                    }
                }
            } finally {
                snackbar.dismissPersistent(PROGRESS_ID)
            }
        }
    }

    private companion object {
        const val PROGRESS_ID = "profile-board-qr-processing"
    }
}
