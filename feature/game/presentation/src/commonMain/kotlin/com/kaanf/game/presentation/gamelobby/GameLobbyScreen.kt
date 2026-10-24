package com.kaanf.game.presentation.gamelobby

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import com.kaanf.game.presentation.gamelobby.component.custom.LobbyWave
import com.kaanf.game.presentation.gamelobby.component.custom.LobbyCountdown
import crew.feature.game.presentation.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import com.kaanf.core.presentation.model.LobbyMember
import com.kaanf.core.presentation.model.UserAvatar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import com.kaanf.core.designsystem.component.info.InfoRow
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.FullScreenLoader
import com.kaanf.core.designsystem.component.sheet.TwoOptionBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.game.presentation.component.EmojiStackCard
import com.kaanf.game.presentation.gamelobby.component.dialog.LeaveEventDialog
import com.kaanf.game.presentation.session.MatchSessionAction
import com.kaanf.game.presentation.session.MatchSessionEvent
import com.kaanf.game.presentation.session.MatchSessionViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

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

    val state = GameLobbyState(
        isLoading = sessionState.lobbyTargetEpochMillis == 0L,
        targetEpochMillis = sessionState.lobbyTargetEpochMillis,
        showGameStartSheet = sessionState.showGameStartSheet,
        showExitConfirmDialog = sessionState.showExitConfirmDialog,
        lobbyMembers = sessionState.lobbyMembers,
        lobbyTotalCount = sessionState.lobbyTotalCount,
        currentUserId = sessionState.currentUserId,
        currentUserName = sessionState.currentUserName,
        currentUserPhotoUrl = sessionState.currentUserPhotoUrl,
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

private const val SHOW_GAME_START_SHEET = false

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameLobbyScreen(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    state: GameLobbyState,
    onAction: (GameLobbyAction) -> Unit,
    incomingWaves: Flow<LobbyWave> = emptyFlow(),
    onSayHi: (() -> Unit)? = null,
) {
    BackHandler(enabled = !state.showExitConfirmDialog) {
        onAction(GameLobbyAction.OnBackClick)
    }

    // ponytail: sheet yerine lobide LIVE + "Enter the game" butonu gösteriliyor;
    // sheet'e dönmek istenirse bu bayrağı true yapmak yeterli.
    if (SHOW_GAME_START_SHEET && state.showGameStartSheet) {
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

    Crossfade(
        targetState = state.isLoading,
        modifier = modifier.fillMaxSize(),
        animationSpec = tween(durationMillis = 250),
    ) { isLoading ->
        if (isLoading) {
            FullScreenLoader()
        } else {
            GameLobbyContent(
                scrollState = scrollState,
                state = state,
                onAction = onAction,
                incomingWaves = incomingWaves,
                onSayHi = onSayHi,
            )
        }
    }
}

@Composable
private fun GameLobbyContent(
    scrollState: ScrollState,
    state: GameLobbyState,
    onAction: (GameLobbyAction) -> Unit,
    incomingWaves: Flow<LobbyWave> = emptyFlow(),
    onSayHi: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 22.dp)
            .padding(bottom = 20.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 32.dp),
                contentAlignment = Alignment.Center,
            ) {
                LobbyCountdown(
                    targetEpochMillis = state.targetEpochMillis,
                    isLive = state.showGameStartSheet,
                    onFinished = { onAction(GameLobbyAction.OnCountdownFinished) },
                    compact = true,
                )
            }
            InfoRow(
                title = stringResource(Res.string.lobby_before_game_tip_title),
                text = AnnotatedString(stringResource(Res.string.lobby_before_game_tip_description)),
                modifier = Modifier.padding(bottom = 20.dp),
            )
        }
        BaseButton(
            text = stringResource(Res.string.lobby_crowd_enter),
            modifier = Modifier.padding(top = 16.dp),
            enabled = state.showGameStartSheet,
            filled = true,
            pill = true,
            onClick = { onAction(GameLobbyAction.OnEnterGameClick) },
        )
    }
}

@Composable
@Preview(name = "Lobby · compact", widthDp = 320, heightDp = 640)
@Preview(name = "Lobby · phone", widthDp = 390, heightDp = 780)
fun GameLobbyScreenPreview() {
    CrewTheme {
        GameLobbyScreen(
            scrollState = rememberScrollState(),
            state = GameLobbyState(
                isLoading = false,
                targetEpochMillis = kotlin.time.Clock.System.now().toEpochMilliseconds() + 183_000,
                lobbyTotalCount = 70,
                currentUserId = "self",
                currentUserName = "Kaan",
                lobbyMembers = List(70) { index ->
                    LobbyMember(
                        id = if (index == 0) "self" else "guest-$index",
                        avatar = UserAvatar(
                            label = ('A' + index % 26).toString(),
                            color = listOf(AccessDefaults.Blush, AccessDefaults.Sky,
                                AccessDefaults.Accent, AccessDefaults.Gold)[index % 4],
                        ),
                    )
                },
            ),
            onAction = {},
        )
    }
}
