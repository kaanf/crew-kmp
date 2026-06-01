package com.kaanf.game.presentation.scanopponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.permission.Permission
import com.kaanf.core.presentation.permission.rememberPermissionController
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.game.presentation.scanopponent.component.ScannerOverlay
import org.koin.compose.viewmodel.koinViewModel
import qrscanner.CameraLens
import qrscanner.OverlayShape
import qrscanner.QrScanner

@Composable
fun ScanOpponentRoot(
    viewModel: ScanOpponentViewModel = koinViewModel(),
    onCloseClicked: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ScanOpponentEvent.CloseScreen -> onCloseClicked()
        }
    }

    SnackbarScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.ScanOpponent,
                onBackClick = { viewModel.onAction(ScanOpponentAction.OnCloseClicked) },
            )
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        ScanOpponentScreen(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            state = state,
            onAction = viewModel::onAction,
            onQrScanned = {}
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScanOpponentScreen(
    modifier: Modifier = Modifier,
    state: ScanOpponentState,
    onAction: (ScanOpponentAction) -> Unit,
    onQrScanned: (String) -> Unit,
) {
    BackHandler {
        onAction(ScanOpponentAction.OnCloseClicked)
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
                    onQrScanned(result)
                }
            },
            imagePickerHandler = {},
            onFailure = {
            },
            overlayShape = OverlayShape.Rectangle,
            overlayColor = Color.Transparent,
            overlayBorderColor = Color.Transparent,
        )

        ScannerOverlay(
            onCloseClick = { onAction(ScanOpponentAction.OnCloseClicked) },
            onSimulateClick = {
                if (!handled) {
                    handled = true
                    onQrScanned("debug-match-qr")
                }
            },
        )
    }
}
