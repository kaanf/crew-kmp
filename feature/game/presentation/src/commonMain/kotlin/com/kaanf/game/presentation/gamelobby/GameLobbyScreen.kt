package com.kaanf.game.presentation.gamelobby

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.badge.RoundedBadge
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.designsystem.component.info.InfoCard
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.game.presentation.component.OnboardingInfoCard
import com.kaanf.game.presentation.gamelobby.component.custom.BeforeTheBell
import com.kaanf.game.presentation.gamelobby.component.custom.MinuteSecondCountdownCard
import com.kaanf.game.presentation.gamelobby.component.custom.TonightFlowCard
import com.kaanf.game.presentation.gamelobby.component.custom.WhoIsInTonightCard
import com.kaanf.game.presentation.gamelobby.component.dialog.LeaveEventDialog
import com.kaanf.game.presentation.gamelobby.component.sheet.GameStartSheet
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GameLobbyRoot(
    viewModel: GameLobbyViewModel = koinViewModel(),
    onBack: () -> Unit,
    onNavigateToGame: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollState: ScrollState = rememberScrollState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            GameLobbyEvent.NavigateBack -> onBack()
            GameLobbyEvent.NavigateToGame -> onNavigateToGame()
        }
    }

    SnackbarScaffold(
        topBar = {
            AppTopBar(
                elevated = { scrollState.canScrollBackward },
                state = AppTopBarState.GameLobby("Lobby"),
                onBackClick = { viewModel.onAction(GameLobbyAction.OnBackClick) },
            )
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        GameLobbyScreen(
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
fun GameLobbyScreen(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    state: GameLobbyState,
    onAction: (GameLobbyAction) -> Unit,
) {
    BackHandler(enabled = !state.showExitConfirmDialog) {
        onAction(GameLobbyAction.OnBackClick)
    }

    if (state.showGameStartSheet) {
        ContainerBottomSheet(
            dismissible = false,
            showDragHandle = false,
            onDismiss = {}
        ) {
            BackHandler(enabled = !state.showExitConfirmDialog) {
                onAction(GameLobbyAction.OnBackClick)
            }
            GameStartSheet(
                onEnterGame = { onAction(GameLobbyAction.OnEnterGameClick) },
                onLeaveEvent = { onAction(GameLobbyAction.OnBackClick) },
            )
        }
    }

    if (state.showExitConfirmDialog) {
        BaseDialog(
            onDismissRequest = { onAction(GameLobbyAction.OnExitDismissed) },
        ) {
            LeaveEventDialog(
                onStay = { onAction(GameLobbyAction.OnExitDismissed) },
                onLeave = { onAction(GameLobbyAction.OnExitConfirmed) },
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
        RoundedBadge(
            text = "Lobby - Doors Open",
            backgroundColor = AccessDefaults.OnAccent,
            borderColor = AccessDefaults.AccentGlow,
            textColor = AccessDefaults.Accent,
        )

        Spacer(modifier = Modifier.height(1.dp))

        MinuteSecondCountdownCard(
            targetEpochMillis = state.targetEpochMillis,
            onFinished = { onAction(GameLobbyAction.OnCountdownFinished) },
        )

        Spacer(modifier = Modifier.height(1.dp))

        OnboardingInfoCard()

        WhoIsInTonightCard()

        TonightFlowCard()

        BeforeTheBell()

        InfoCard(
            icon = AccessIcons.Sparkle,
            iconTint = AccessDefaults.Accent,
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = AccessDefaults.TextPrimary,
                        fontWeight = FontWeight.Bold,
                    ),
                ) {
                    append(
                        "Warm up",
                    )
                }

                append("\n")

                withStyle(
                    style = SpanStyle(
                        color = AccessDefaults.TextMuted,
                        fontWeight = FontWeight.Normal,
                    ),
                ) {
                    append(
                        "Order a drink, find one person you've never met, ask them what their last good Saturday was.",
                    )
                }
            },
        )
    }
}

@Composable
@Preview
fun GameLobbyScreenPreview() {
    CrewTheme {
        GameLobbyScreen(
            scrollState = rememberScrollState(),
            state = GameLobbyState(),
            onAction = {},
        )
    }
}
