package com.kaanf.game.presentation.scanopponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.permission.Permission
import com.kaanf.core.presentation.permission.PermissionState
import com.kaanf.core.presentation.permission.rememberPermissionController
import com.kaanf.game.presentation.scanopponent.component.QrCameraScanner
import com.kaanf.game.presentation.scanopponent.component.overlay.ScannerOverlay
import com.kaanf.game.presentation.component.sheet.GameRequestSheet
import com.kaanf.game.presentation.session.MatchPhase
import com.kaanf.game.presentation.session.MatchSessionAction
import com.kaanf.game.presentation.session.MatchSessionViewModel
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_camera_permission_description
import crew.feature.game.presentation.generated.resources.match_camera_permission_retry_action
import crew.feature.game.presentation.generated.resources.match_camera_permission_settings_action
import crew.feature.game.presentation.generated.resources.match_camera_permission_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

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
    val permissionScope = rememberCoroutineScope()
    var cameraPermission by remember { mutableStateOf(PermissionState.NOT_DETERMINED) }
    // İlk girişte iste; sistem dialogu veya Ayarlar'dan dönüşte (ON_RESUME) sessizce yeniden
    // kontrol et — Ayarlar'da izni açıp dönen kullanıcıda tarayıcı kendiliğinden açılır.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        permissionScope.launch {
            val current = permissionController.checkPermission(Permission.CAMERA)
            cameraPermission = if (current == PermissionState.NOT_DETERMINED) {
                permissionController.requestPermission(Permission.CAMERA)
            } else {
                current
            }
        }
    }

    // QrScanner her karede onCompletion çağırır; mandal davet spam'ini önler. Ama davet akışı
    // (gönderim + bekleme sheet'i) bitince yeniden kurulmalı: başarıda ekran RpsReady'e pop'lanır,
    // hata/redde idle'a döner → mandal sıfırlanır, kamera tekrar taramaya hazır.
    val inviteInFlight = state.isLoading || state.showGameRequestSheet
    var handled by remember { mutableStateOf(false) }
    LaunchedEffect(inviteInFlight) {
        if (!inviteInFlight) handled = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (cameraPermission) {
            PermissionState.GRANTED -> {
                QrCameraScanner(
                    modifier = Modifier.fillMaxSize(),
                    onResult = { result ->
                        if (!handled) {
                            handled = true
                            onAction(ScanOpponentAction.OnScanResult(scannedMatchQrToken = result))
                        }
                    },
                )

                ScannerOverlay()
            }

            PermissionState.NOT_DETERMINED -> Unit // sistem izin dialogu ekranda

            else -> CameraPermissionContent(
                permanentlyDenied = cameraPermission == PermissionState.PERMANENTLY_DENIED,
                onOpenSettings = { permissionController.openAppSettings() },
                onRequest = {
                    permissionScope.launch {
                        cameraPermission = permissionController.requestPermission(Permission.CAMERA)
                    }
                },
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun CameraPermissionContent(
    permanentlyDenied: Boolean,
    onOpenSettings: () -> Unit,
    onRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.match_camera_permission_title),
            style = MaterialTheme.typography.titleMedium.copy(
                color = AccessDefaults.TextPrimary,
                textAlign = TextAlign.Center,
            ),
        )

        Text(
            text = stringResource(Res.string.match_camera_permission_description),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        BaseButton(
            text = stringResource(
                if (permanentlyDenied) {
                    Res.string.match_camera_permission_settings_action
                } else {
                    Res.string.match_camera_permission_retry_action
                },
            ),
            onClick = if (permanentlyDenied) onOpenSettings else onRequest,
            filled = true,
        )
    }
}
