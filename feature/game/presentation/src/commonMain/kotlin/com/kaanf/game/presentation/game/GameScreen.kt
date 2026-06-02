package com.kaanf.game.presentation.game

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.component.qr.UserQrCard
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.permission.Permission
import com.kaanf.core.presentation.permission.PermissionState
import com.kaanf.core.presentation.permission.rememberPermissionController
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.game.presentation.component.OnboardingInfoCard
import com.kaanf.game.presentation.game.component.LostThrowInfoCard
import com.kaanf.game.presentation.gamelobby.component.dialog.LeaveEventDialog
import com.kaanf.game.presentation.scanopponent.component.sheet.GameRequestSheet
import com.kaanf.game.presentation.scanopponent.component.sheet.GameResponseSheet
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GameRoot(
    viewModel: GameViewModel = koinViewModel(),
    onNavigateToDashboard: () -> Unit,
    onNavigateScanOpponent: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollState: ScrollState = rememberScrollState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            GameEvent.NavigateToDashboard -> onNavigateToDashboard()
            GameEvent.NavigateToScanOpponent -> onNavigateScanOpponent()
        }
    }

    if (state.showMatchRequestSheet) {
        ContainerBottomSheet(
            dismissible = true,
            showDragHandle = false,
            onDismiss = {}
        ) {
            GameResponseSheet()
        }
    }

    SnackbarScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.GameLobby(""),
                onBackClick = { viewModel.onAction(GameAction.OnBackClick) },
            )
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        GameScreen(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            state = state,
            scrollState = scrollState,
            onAction = viewModel::onAction,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    state: GameState,
    scrollState: ScrollState,
    onAction: (GameAction) -> Unit,
) {
    BackHandler(enabled = !state.showExitConfirmDialog) {
        onAction(GameAction.OnBackClick)
    }

    val permissionController = rememberPermissionController()

    val scope = rememberCoroutineScope()

    if (state.showExitConfirmDialog) {
        BaseDialog(
            onDismissRequest = { onAction(GameAction.OnExitDismissed) },
        ) {
            LeaveEventDialog(
                onStay = { onAction(GameAction.OnExitDismissed) },
                onLeave = { onAction(GameAction.OnExitConfirmed) },
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = AccessDefaults.TextPrimary,
                    ),
                ) {
                    append(
                        "Go, make a ",
                    )
                }

                withStyle(
                    style = SpanStyle(
                        color = AccessDefaults.Accent,
                        fontWeight = FontWeight.Bold,
                    ),
                ) {
                    append(
                        "Crew \uD83E\uDEA9"
                    )
                }
            },
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Flash your code at a stranger, throw hands, and bank points before the lights come up.",
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            ),
        )

        Spacer(modifier = Modifier.height(1.dp))

        UserQrCard(
            modifier = Modifier.size(300.dp),
            inputText = "CR-7K8B-2M9X-04-CR",
        )

        Spacer(modifier = Modifier.height(1.dp))

        BaseButton(
            text = "Scan Someone Instead",
            backgroundColor = AccessDefaults.Surface,
            onClick = {
                scope.launch {
                    val result = permissionController.requestPermission(Permission.CAMERA)

                    if (result == PermissionState.GRANTED) {
                        onAction(GameAction.OnScanClicked)
                    } else {

                    }
                }
            },
            filled = false,
            contentColor = AccessDefaults.TextPrimary,
            leadingIcon = AccessIcons.QR,
        )


        Spacer(modifier = Modifier.height(1.dp))

        OnboardingInfoCard()

        LostThrowInfoCard()
    }
}

@Composable
@Preview
fun GameScreenPreview() {
    CrewTheme {
        GameScreen(
            state = GameState(),
            onAction = {},
            scrollState = rememberScrollState()
        )
    }
}
