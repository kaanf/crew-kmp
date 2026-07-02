package com.kaanf.game.presentation.session

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.game.domain.model.GameConnectionState
import com.kaanf.game.presentation.component.sheet.GameResponseSheet
import com.kaanf.game.presentation.gamelobby.component.dialog.LeaveEventDialog
import com.kaanf.game.presentation.session.component.GameHomeTopBar
import com.kaanf.game.presentation.session.phase.LoserAcceptsPhase
import com.kaanf.game.presentation.session.phase.LoserActiveTaskPhase
import com.kaanf.game.presentation.session.phase.LoserWaitsPhase
import com.kaanf.game.presentation.session.phase.MatchScoreboardPhase
import com.kaanf.game.presentation.session.phase.QrHomePhase
import com.kaanf.game.presentation.session.phase.RpsReadyPhase
import com.kaanf.game.presentation.session.phase.WhoWonPhase
import com.kaanf.game.presentation.session.phase.WinnerConfirmsPhase
import com.kaanf.game.presentation.session.phase.WinnerPicksPhase

@Composable
fun MatchContainerRoot(
    viewModel: MatchSessionViewModel,
    onNavigateToScanOpponent: () -> Unit,
    onNavigateToDashboard: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            MatchSessionEvent.NavigateToScanOpponent -> onNavigateToScanOpponent()
            MatchSessionEvent.NavigateToDashboard -> onNavigateToDashboard()
            // Lobi event'leri; bu ekranda tüketilmez.
            MatchSessionEvent.NavigateToGame,
            MatchSessionEvent.NavigateBack -> Unit
        }
    }

    MatchContainerScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MatchContainerScreen(
    state: MatchSessionState,
    onAction: (MatchSessionAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(enabled = !state.showExitConfirmDialog) {
        onAction(MatchSessionAction.OnBackClick)
    }

    if (state.showExitConfirmDialog) {
        // Maç ortasındaysa çıkış = forfeit (etkinlikte kalırsın); Idle/Scoreboard'da = etkinlikten ayrıl.
        val isMidMatch = state.phase != MatchPhase.Idle && state.phase !is MatchPhase.Scoreboard
        BaseDialog(onDismissRequest = { onAction(MatchSessionAction.OnExitDismissed) }) {
            if (isMidMatch) {
                LeaveEventDialog(
                    onStay = { onAction(MatchSessionAction.OnExitDismissed) },
                    onLeave = { onAction(MatchSessionAction.OnExitConfirmed) },
                    title = "Maçtan ayrılmak\nistediğine emin misin?",
                    subtitle = "Şimdi ayrılırsan bu maçı kaybetmiş sayılırsın. Etkinlikte kalır, yeni maç yapabilirsin.",
                    stayLabel = "Maça Dön",
                    leaveLabel = "Maçtan Ayrıl",
                )
            } else {
                LeaveEventDialog(
                    onStay = { onAction(MatchSessionAction.OnExitDismissed) },
                    onLeave = { onAction(MatchSessionAction.OnExitConfirmed) },
                )
            }
        }
    }

    if (state.showMatchRequestSheet) {
        ContainerBottomSheet(
            dismissible = true,
            showDragHandle = false,
            onDismiss = { onAction(MatchSessionAction.OnInviteDeclined) },
        ) {
            state.incomingInvite?.let { invite ->
                GameResponseSheet(
                    isResponding = state.isRespondingToInvite,
                    message = invite,
                    selfPhotoUrl = state.currentUserPhotoUrl,
                    onAccept = { onAction(MatchSessionAction.OnInviteAccepted) },
                    onDecline = { onAction(MatchSessionAction.OnInviteDeclined) },
                )
            }
        }
    }

    AppScaffold(
        topBar = {
            if (state.phase == MatchPhase.Idle) {
                GameHomeTopBar(
                    userName = state.currentUserName.orEmpty(),
                    photoUrl = state.currentUserPhotoUrl,
                    score = state.currentUserScore,
                    winCount = state.currentUserWinCount,
                    matchesCount = state.currentUserMatchesCount,
                    recentResults = state.currentUserRecentResults,
                    onCloseClick = { onAction(MatchSessionAction.OnBackClick) },
                )
            } else {
                AppTopBar(
                    state = topBarStateFor(state.phase),
                    onBackClick = { onAction(MatchSessionAction.OnBackClick) },
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            ConnectionBanner(
                connectionState = state.connectionState,
                modifier = Modifier.fillMaxWidth(),
            )

            AnimatedContent(
                targetState = state.phase,
                contentKey = { it.key },
                transitionSpec = {
                    (slideInHorizontally { it / 4 } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it / 4 } + fadeOut()) using
                        SizeTransform(clip = false)
                },
                contentAlignment = Alignment.Center,
                label = "match_phase",
                modifier = Modifier.weight(1f),
            ) { phase ->
                MatchPhaseContent(
                    phase = phase,
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

private fun topBarStateFor(phase: MatchPhase): AppTopBarState = when (phase) {
    MatchPhase.Idle -> AppTopBarState.Game
    is MatchPhase.RpsReady -> AppTopBarState.RpsReady
    is MatchPhase.WhoWon -> AppTopBarState.RpsConfirmation
    is MatchPhase.WinnerPicks -> AppTopBarState.WinnerPicks
    MatchPhase.LoserWaits -> AppTopBarState.LoserWaits
    is MatchPhase.LoserAccepts -> AppTopBarState.LoserAccepts
    MatchPhase.TaskActive -> AppTopBarState.LoserActiveTask
    is MatchPhase.WinnerConfirms -> AppTopBarState.WinnerConfirms
    is MatchPhase.Scoreboard -> AppTopBarState.GameLobby("")
}

@Composable
private fun MatchPhaseContent(
    phase: MatchPhase,
    state: MatchSessionState,
    onAction: (MatchSessionAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (phase) {
        MatchPhase.Idle -> QrHomePhase(
            state = state,
            onAction = onAction,
            modifier = modifier
        )

        is MatchPhase.RpsReady -> RpsReadyPhase(
            opponentFullName = state.formattedOpponentName,
            isWaiting = phase.isMarkingReady,
            onReadyClick = { onAction(MatchSessionAction.OnReadyClick) },
            modifier = modifier,
            opponentImageUrl = state.opponentProfilePictureUrl,
            myImageUrl = state.currentUserPhotoUrl,
        )

        is MatchPhase.WhoWon -> WhoWonPhase(
            opponentFullName = state.formattedOpponentName,
            isReporting = phase.isReporting,
            onResult = { won -> onAction(MatchSessionAction.OnReportResult(won)) },
            modifier = modifier,
            myClaimWon = phase.myResultClaimWon,
            opponentClaimedMeWon = phase.opponentClaimedMeWon(state.currentUserId),
            opponentImageUrl = state.opponentProfilePictureUrl,
            myImageUrl = state.currentUserPhotoUrl,
        )

        is MatchPhase.WinnerPicks -> WinnerPicksPhase(
            opponentName = state.formattedOpponentName,
            isLoading = phase.isLoading,
            tasks = phase.tasks,
            selectedTaskId = phase.selectedTaskId,
            isOffering = phase.isOffering,
            onTaskSelected = { onAction(MatchSessionAction.OnTaskSelected(it)) },
            onSendClick = { onAction(MatchSessionAction.OnSendTaskClick) },
            modifier = modifier,
        )

        MatchPhase.LoserWaits -> LoserWaitsPhase(
            opponentName = state.formattedOpponentName,
            modifier = modifier,
        )

        is MatchPhase.LoserAccepts -> LoserAcceptsPhase(
            opponentName = state.formattedOpponentName,
            task = phase.task,
            isResponding = phase.isResponding,
            onAccept = { onAction(MatchSessionAction.OnAcceptTask) },
            onReject = { onAction(MatchSessionAction.OnRejectTask) },
            modifier = modifier,
        )

        MatchPhase.TaskActive -> LoserActiveTaskPhase(
            opponentName = state.formattedOpponentName,
            task = state.activeTask,
            modifier = modifier,
            opponentImageUrl = state.opponentProfilePictureUrl,
        )

        is MatchPhase.WinnerConfirms -> WinnerConfirmsPhase(
            opponentName = state.formattedOpponentName,
            task = state.activeTask,
            isConfirming = phase.isConfirming,
            onConfirm = { completed -> onAction(MatchSessionAction.OnConfirmTask(completed)) },
            modifier = modifier,
        )

        is MatchPhase.Scoreboard -> MatchScoreboardPhase(
            entries = phase.entries,
            currentUserId = state.currentUserId,
            isLoading = phase.isLoading,
            completed = phase.completed,
            forfeit = phase.forfeit,
            isFinishing = phase.isFinishing,
            onFinish = { onAction(MatchSessionAction.OnFinishMatch) },
            modifier = modifier,
        )
    }
}

@Composable
private fun ConnectionBanner(
    connectionState: GameConnectionState,
    modifier: Modifier = Modifier,
) {
    val text = when (connectionState) {
        GameConnectionState.Connecting -> "Bağlanılıyor…"
        GameConnectionState.Reconnecting -> "Bağlantı koptu, yeniden bağlanılıyor…"
        is GameConnectionState.Disconnected ->
            // Gerçek terminal hata snackbar'la gösterilir; banner gizli. Beklenen kopuşlar
            // (arka plan/ağ/oturum) kendiliğinden toparlanır → "yeniden bağlanılıyor".
            if (connectionState.isError) return else "Bağlantı koptu, yeniden bağlanılıyor…"
        GameConnectionState.Connected -> return
    }

    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium.copy(
            color = AccessDefaults.TextSecondary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        ),
    )
}
