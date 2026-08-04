package com.kaanf.game.presentation.session

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.coachmark.CoachmarkHost
import com.kaanf.core.designsystem.component.coachmark.coachmarkTarget
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.game.presentation.component.sheet.GameResponseSheet
import com.kaanf.game.presentation.gamelobby.component.dialog.LeaveEventDialog
import com.kaanf.game.presentation.history.HistoryTab
import com.kaanf.game.presentation.session.coachmark.GameCoachmarkKey
import com.kaanf.game.presentation.session.coachmark.rememberGameCoachmarkSteps
import com.kaanf.game.presentation.leaderboard.LeaderboardTab
import com.kaanf.game.presentation.session.component.GameBottomBar
import com.kaanf.game.presentation.session.component.GameBottomTab
import com.kaanf.game.presentation.session.component.LeaveMatchSheet
import com.kaanf.game.presentation.session.phase.LoserAcceptsPhase
import com.kaanf.game.presentation.session.phase.LoserActiveTaskPhase
import com.kaanf.game.presentation.session.phase.LoserWaitsPhase
import com.kaanf.game.presentation.session.phase.MatchScoreboardPhase
import com.kaanf.game.presentation.session.phase.QrHomePhase
import com.kaanf.game.presentation.session.phase.RpsReadyPhase
import com.kaanf.game.presentation.session.phase.WhoWonPhase
import com.kaanf.game.presentation.session.phase.WinnerConfirmsPhase
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.history_top_bar_title
import crew.feature.game.presentation.generated.resources.leaderboard_top_bar_title
import org.jetbrains.compose.resources.stringResource
import com.kaanf.game.presentation.session.phase.WinnerPicksPhase

@Composable
fun MatchContainerRoot(
    viewModel: MatchSessionViewModel,
    onNavigateToScanOpponent: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToQuests: () -> Unit,
    onNavigateToPassport: () -> Unit,
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

    // Quests/scan gibi başka destination'lardan dönünce bu ekran yeniden compose olur;
    // graph-scoped VM ölmediği için skor init'te bir kez çekildiği hâliyle kalıyordu.
    LaunchedEffect(Unit) { viewModel.onAction(MatchSessionAction.OnStatsRefreshRequested) }

    // Tab'lar saf UI durumu; navigasyon yok, hepsi bu container içinde yaşar.
    var selectedTab by rememberSaveable { mutableStateOf(GameBottomTab.Play) }

    // ponytail: kalıcılık yok, tur her app açılışında bir kez gösterilir.
    // Kalıcı istenirse LanguageStore pattern'i (DataStore boolean) kopyalanır.
    var showCoachmark by rememberSaveable { mutableStateOf(true) }

    // Etkinlik bitince Play kilitlenir, leaderboard açılır; maç aktivitesi başlarsa
    // (davet sheet'i ya da faz Idle'dan çıktıysa) Play'e dön — davet/faz UI'ının
    // tek sahibi Play içeriği.
    LaunchedEffect(state.isGameEnded, state.phase, state.showMatchRequestSheet) {
        selectedTab = when {
            state.isGameEnded -> GameBottomTab.Leaderboard
            state.phase != MatchPhase.Idle || state.showMatchRequestSheet -> GameBottomTab.Play
            else -> selectedTab
        }
    }

    MatchContainerScreen(
        state = state,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        onAction = viewModel::onAction,
        onQuestsClick = onNavigateToQuests,
        onPassportClick = onNavigateToPassport,
        showCoachmark = showCoachmark,
        onCoachmarkFinish = { showCoachmark = false },
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MatchContainerScreen(
    state: MatchSessionState,
    selectedTab: GameBottomTab,
    onTabSelected: (GameBottomTab) -> Unit,
    onAction: (MatchSessionAction) -> Unit,
    modifier: Modifier = Modifier,
    onQuestsClick: () -> Unit = {},
    onPassportClick: () -> Unit = {},
    showCoachmark: Boolean = false,
    onCoachmarkFinish: () -> Unit = {},
) {
    val isIdle = state.phase == MatchPhase.Idle

    BackHandler(enabled = !state.showExitConfirmDialog) {
        if (isIdle && !state.isGameEnded && selectedTab != GameBottomTab.Play) {
            onTabSelected(GameBottomTab.Play)
        } else {
            onAction(MatchSessionAction.OnBackClick)
        }
    }

    if (state.showExitConfirmDialog) {
        // Maç ortasındaysa çıkış = forfeit (etkinlikte kalırsın); Idle/Scoreboard'da = etkinlikten ayrıl.
        val isMidMatch = state.phase != MatchPhase.Idle && state.phase !is MatchPhase.Scoreboard
        if (isMidMatch) {
            ContainerBottomSheet(
                onDismiss = { onAction(MatchSessionAction.OnExitDismissed) },
            ) {
                LeaveMatchSheet(
                    opponentName = state.formattedOpponentName,
                    onStay = { onAction(MatchSessionAction.OnExitDismissed) },
                    onLeave = { onAction(MatchSessionAction.OnExitConfirmed) },
                )
            }
        } else {
            BaseDialog(onDismissRequest = { onAction(MatchSessionAction.OnExitDismissed) }) {
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

    // Host scaffold'ın tamamını sarar: tur top bar'ı da karartır ve son adım
    // scaffold içindeki bottom bar'ı hedefleyebilir.
    CoachmarkHost(
        steps = rememberGameCoachmarkSteps(),
        visible = showCoachmark &&
            isIdle &&
            selectedTab == GameBottomTab.Play &&
            !state.showMatchRequestSheet &&
            !state.showExitConfirmDialog,
        onFinish = onCoachmarkFinish,
    ) {
        AppScaffold(
            topBar = {
                when {
                    !isIdle -> AppTopBar(
                        state = topBarStateFor(state.phase),
                        onBackClick = { onAction(MatchSessionAction.OnBackClick) },
                    )

                    selectedTab == GameBottomTab.Play -> AppTopBar(
                        state = AppTopBarState.Game(
                            showQuestsAction = true,
                            showPassportAction = true,
                        ),
                        onLeftClick = onQuestsClick,
                        onPassportClick = onPassportClick,
                        onRightClick = { onAction(MatchSessionAction.OnBackClick) },
                    )

                    else -> AppTopBar(
                        state = AppTopBarState.Game(
                            stringResource(
                                if (selectedTab == GameBottomTab.Leaderboard) {
                                    Res.string.leaderboard_top_bar_title
                                } else {
                                    Res.string.history_top_bar_title
                                },
                            ),
                        ),
                        onRightClick = { onAction(MatchSessionAction.OnBackClick) },
                    )
                }
            },
        ) { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            ) {
                if (isIdle && selectedTab != GameBottomTab.Play) {
                    when (selectedTab) {
                        GameBottomTab.Leaderboard -> LeaderboardTab(
                            modifier = Modifier.fillMaxSize(),
                        )

                        else -> HistoryTab(modifier = Modifier.fillMaxSize())
                    }
                } else {
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
                        modifier = Modifier.fillMaxSize(),
                    ) { phase ->
                        MatchPhaseContent(
                            phase = phase,
                            state = state,
                            onAction = onAction,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }

                // Tek bottom bar; yalnız ana (Idle) ekranlarda, faz ekranlarında gösterilmez.
                if (isIdle) {
                    GameBottomBar(
                        activeTab = selectedTab,
                        isGameEnded = state.isGameEnded,
                        onTabClick = onTabSelected,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .coachmarkTarget(GameCoachmarkKey.Tabs),
                    )
                }
            }
        }
    }
}

private fun topBarStateFor(phase: MatchPhase): AppTopBarState = when (phase) {
    MatchPhase.Idle -> AppTopBarState.Game()
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
            currentUserPhotoUrl = state.currentUserPhotoUrl,
            opponentPhotoUrl = state.opponentProfilePictureUrl,
        )
    }
}

