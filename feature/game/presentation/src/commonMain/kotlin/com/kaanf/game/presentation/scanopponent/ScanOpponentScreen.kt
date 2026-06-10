package com.kaanf.game.presentation.scanopponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.permission.Permission
import com.kaanf.core.presentation.permission.rememberPermissionController
import com.kaanf.game.presentation.scanopponent.component.overlay.ScannerOverlay
import com.kaanf.game.presentation.component.sheet.GameRequestSheet
import com.kaanf.game.presentation.session.MatchPhase
import com.kaanf.game.presentation.session.MatchSessionAction
import com.kaanf.game.presentation.session.MatchSessionViewModel
import qrscanner.CameraLens
import qrscanner.OverlayShape
import qrscanner.QrScanner

/**
 * Scan, container'ın ÜSTÜNE açılan ayrı bir destination'dır ama socket'e DOKUNMAZ:
 * graph-scoped [MatchSessionViewModel] verilir, state oradan okunur, okunan token oraya delege edilir.
 * Maç başlayınca (MATCH_STARTED → session phase = RpsReady) kendini pop'lar; arkadaki container
 * zaten RpsReady'i gösterir. Davet reddinde (MATCH_INVITE_DECLINED) bekleme sheet'i kapanır, ekran açık kalır.
 */
@Composable
fun ScanOpponentRoot(
    viewModel: MatchSessionViewModel,
    onClose: () -> Unit,
) {
    val sessionState by viewModel.state.collectAsStateWithLifecycle()

    // Navigasyon event ile değil, phase ile sürülür (tek event-collector container'da kalsın).
    LaunchedEffect(sessionState.phase) {
        if (sessionState.phase is MatchPhase.RpsReady) onClose()
    }

    // Session state → bu ekranın ihtiyaç duyduğu dilime indir.
    val scanState = ScanOpponentState(
        isLoading = sessionState.isSendingInvite,
        errorMessage = sessionState.errorMessage,
        showGameRequestSheet = sessionState.showOutgoingInviteSheet,
        opponentName = sessionState.outgoingOpponentName,
        opponentPhotoUrl = sessionState.outgoingOpponentPhotoUrl,
        selfPhotoUrl = sessionState.currentUserPhotoUrl,
    )

    AppScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.ScanOpponent,
                onBackClick = onClose,
            )
        },
    ) { innerPadding ->
        ScanOpponentScreen(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            state = scanState,
            onAction = { action ->
                when (action) {
                    ScanOpponentAction.OnCloseClicked -> onClose()
                    is ScanOpponentAction.OnScanResult ->
                        viewModel.onAction(MatchSessionAction.OnScanResult(action.scannedMatchQrToken))
                }
            },
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScanOpponentScreen(
    modifier: Modifier = Modifier,
    state: ScanOpponentState,
    onAction: (ScanOpponentAction) -> Unit,
) {
    BackHandler {
        onAction(ScanOpponentAction.OnCloseClicked)
    }

    if (state.showGameRequestSheet) {
        ContainerBottomSheet(
            dismissible = false,
            showDragHandle = false,
            onDismiss = {}
        ) {
            GameRequestSheet(
                opponentName = state.opponentName.orEmpty(),
                opponentPhotoUrl = state.opponentPhotoUrl,
                selfPhotoUrl = state.selfPhotoUrl,
            )
        }
    }

    val permissionController = rememberPermissionController()
    LaunchedEffect(true) {
        permissionController.requestPermission(Permission.CAMERA)
    }

    var handled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        QrScanner(
            modifier = Modifier.fillMaxSize(),
            flashlightOn = false,
            cameraLens = CameraLens.Back,
            openImagePicker = false,
            onCompletion = { result ->
                if (!handled) {
                    handled = true
                    onAction(ScanOpponentAction.OnScanResult(scannedMatchQrToken = result))
                }
            },
            imagePickerHandler = {},
            onFailure = {},
            overlayShape = OverlayShape.Rectangle,
            overlayColor = Color.Transparent,
            overlayBorderColor = Color.Transparent,
        )

        ScannerOverlay()
    }
}
