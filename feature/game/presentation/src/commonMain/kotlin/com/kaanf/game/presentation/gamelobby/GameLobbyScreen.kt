package com.kaanf.game.presentation.gamelobby

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.component.sheet.TwoOptionBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.game.presentation.component.EmojiStackCard
import com.kaanf.game.presentation.component.OnboardingInfoCard
import com.kaanf.game.presentation.gamelobby.component.custom.BeforeTheBell
import com.kaanf.game.presentation.gamelobby.component.custom.LobbyPresenceCluster
import com.kaanf.game.presentation.gamelobby.component.custom.MinuteSecondCountdownCard
import com.kaanf.game.presentation.gamelobby.component.custom.TonightFlowCard
import com.kaanf.game.presentation.gamelobby.component.custom.WhoIsInTonightCard
import com.kaanf.game.presentation.gamelobby.component.dialog.LeaveEventDialog
import com.kaanf.game.presentation.session.MatchSessionAction
import com.kaanf.game.presentation.session.MatchSessionEvent
import com.kaanf.game.presentation.session.MatchSessionViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Lobi, ScanOpponent gibi graph-scoped [MatchSessionViewModel]'in bir görünümüdür: ayrı bir
 * soket aboneliği YOKTUR. Etkinlik boyunca tek canlı bağlantı session VM'inde yaşar; lobi
 * üyeleri/geri sayım oradan okunur, aksiyonlar oraya delege edilir.
 */
@Composable
fun GameLobbyRoot(
    viewModel: MatchSessionViewModel,
    onBack: () -> Unit,
    onNavigateToGame: () -> Unit,
) {
    val sessionState by viewModel.state.collectAsStateWithLifecycle()

    val scrollState: ScrollState = rememberScrollState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            MatchSessionEvent.NavigateBack -> onBack()
            MatchSessionEvent.NavigateToGame -> onNavigateToGame()
            else -> Unit
        }
    }

    // Session state → bu ekranın ihtiyaç duyduğu dilime indir.
    val state = GameLobbyState(
        targetEpochMillis = sessionState.lobbyTargetEpochMillis,
        showGameStartSheet = sessionState.showGameStartSheet,
        showExitConfirmDialog = sessionState.showExitConfirmDialog,
        lobbyMembers = sessionState.lobbyMembers,
        lobbyTotalCount = sessionState.lobbyTotalCount,
    )

    AppScaffold(
        topBar = {
            AppTopBar(
                elevated = { scrollState.canScrollBackward },
                state = AppTopBarState.GameLobby("Lobby"),
                onBackClick = { viewModel.onAction(MatchSessionAction.OnBackClick) },
            )
        },
    ) { innerPadding ->
        GameLobbyScreen(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            state = state,
            scrollState = scrollState,
            onAction = { action ->
                when (action) {
                    GameLobbyAction.OnBackClick ->
                        viewModel.onAction(MatchSessionAction.OnBackClick)

                    GameLobbyAction.OnCountdownFinished ->
                        viewModel.onAction(MatchSessionAction.OnLobbyCountdownFinished)

                    GameLobbyAction.OnExitDismissed ->
                        viewModel.onAction(MatchSessionAction.OnExitDismissed)

                    GameLobbyAction.OnExitConfirmed ->
                        viewModel.onAction(MatchSessionAction.OnLobbyExitConfirmed)

                    GameLobbyAction.OnEnterGameClick ->
                        viewModel.onAction(MatchSessionAction.OnEnterGameClick)
                }
            },
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
        BackHandler(enabled = !state.showExitConfirmDialog) {
            onAction(GameLobbyAction.OnBackClick)
        }

        TwoOptionBottomSheet(
            iconContent = {
                EmojiStackCard(
                    size = 64.dp,
                    isWaving = true,
                )
            },
            title = "Doors open!",
            description = "Find someone, jump into the game, and start the\nnight’s first little bit of chaos.",
            confirmButtonText = "Enter the game",
            cancelButtonText = "Leave event",
            onConfirmClicked = { onAction(GameLobbyAction.OnEnterGameClick) },
            onCancelClicked = { onAction(GameLobbyAction.OnBackClick) },
            isDismissable = false,
            showDragHandle = false,
            onDismiss = {}
        )
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

        LobbyPresenceCluster(
            members = state.lobbyMembers,
            totalCount = state.lobbyTotalCount,
        )

        Spacer(modifier = Modifier.height(1.dp))

        MinuteSecondCountdownCard(
            targetEpochMillis = state.targetEpochMillis,
            onFinished = { onAction(GameLobbyAction.OnCountdownFinished) },
        )

        Spacer(modifier = Modifier.height(1.dp))

        OnboardingInfoCard()

        //WhoIsInTonightCard()

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
