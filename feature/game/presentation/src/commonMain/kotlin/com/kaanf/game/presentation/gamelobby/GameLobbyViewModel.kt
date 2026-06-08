package com.kaanf.game.presentation.gamelobby

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.presentation.model.LobbyMember
import com.kaanf.core.presentation.model.UserAvatar
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Clock

class GameLobbyViewModel: ViewModel() {
    private val eventChannel = Channel<GameLobbyEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(
        GameLobbyState(
            targetEpochMillis = Clock.System.now().toEpochMilliseconds() + COUNTDOWN_DURATION_MILLIS,
            lobbyMembers = (0 until INITIAL_MEMBERS).map { newMember() },
        ).let { it.copy(lobbyTotalCount = it.lobbyMembers.size) },
    )
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    init {
        // TODO: replace with the lobby presence socket — feed joins/leaves into
        //  lobbyMembers and the real headcount into lobbyTotalCount. Until then this
        //  simulator drives the pop-in / pop-out / reflow animations with mock data.
        startPresenceSimulator()
    }

    fun onAction(action: GameLobbyAction) {
        when (action) {
            GameLobbyAction.OnBackClick -> _state.update { it.copy(showExitConfirmDialog = true) }
            GameLobbyAction.OnCountdownFinished -> _state.update { it.copy(showExitConfirmDialog = false, showGameStartSheet = true) }
            GameLobbyAction.OnExitDismissed -> _state.update { it.copy(showExitConfirmDialog = false) }
            GameLobbyAction.OnExitConfirmed -> onExitConfirmed()
            GameLobbyAction.OnEnterGameClick -> onEnterGameClick()
        }
    }

    private fun startPresenceSimulator() {
        viewModelScope.launch {
            while (isActive) {
                delay(Random.nextLong(SIM_MIN_DELAY_MS, SIM_MAX_DELAY_MS))
                _state.update { state ->
                    val members = state.lobbyMembers.toMutableList()
                    val shouldJoin = members.size < SIM_MIN_MEMBERS ||
                        (members.size < SIM_MAX_MEMBERS && Random.nextBoolean())
                    if (shouldJoin) {
                        members.add(Random.nextInt(members.size + 1), newMember())
                    } else if (members.isNotEmpty()) {
                        members.removeAt(Random.nextInt(members.size))
                    }
                    state.copy(lobbyMembers = members, lobbyTotalCount = members.size)
                }
            }
        }
    }

    private fun onEnterGameClick() {
        _state.update { it.copy(showGameStartSheet = false) }
        viewModelScope.launch {
            eventChannel.send(GameLobbyEvent.NavigateToGame)
        }
    }

    private fun onExitConfirmed() {
        _state.update { it.copy(showExitConfirmDialog = false, showGameStartSheet = false) }
        viewModelScope.launch {
            eventChannel.send(GameLobbyEvent.NavigateBack)
        }
    }

    private var nextMemberId = 0

    private fun newMember(): LobbyMember {
        val id = "sim-${nextMemberId++}"
        return LobbyMember(
            id = id,
            avatar = UserAvatar(
                label = MOCK_LABELS[Random.nextInt(MOCK_LABELS.length)].toString(),
                color = MOCK_COLORS.random(),
            ),
        )
    }

    private companion object {
        const val COUNTDOWN_DURATION_MILLIS = 30_000L

        const val INITIAL_MEMBERS = 6
        const val SIM_MIN_MEMBERS = 3
        const val SIM_MAX_MEMBERS = 16
        const val SIM_MIN_DELAY_MS = 900L
        const val SIM_MAX_DELAY_MS = 1_900L

        const val MOCK_LABELS = "EMAJKRLSTNOBCDFG"
        val MOCK_COLORS = listOf(
            Color(0xFFFF5A7A),
            Color(0xFFC8FF3D),
            Color(0xFF5BE0C5),
            Color(0xFF6FB7FF),
            Color(0xFFFF7A5C),
            Color(0xFFFFB341),
        )
    }
}
