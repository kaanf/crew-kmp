package com.kaanf.game.presentation.gamelobby

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.presentation.model.LobbyMember
import com.kaanf.core.presentation.model.UserAvatar
import com.kaanf.game.domain.event.EventConnectionClient
import com.kaanf.game.domain.model.GameSocketMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlin.math.abs

class GameLobbyViewModel(
    private val eventConnectionClient: EventConnectionClient,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId: String = savedStateHandle.get<String>("eventId").orEmpty()

    private val eventChannel = Channel<GameLobbyEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(GameLobbyState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    init {
        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        eventConnectionClient.observeEvents(eventId)
            .onEach(::onSocketMessage)
            .catch { }
            .launchIn(viewModelScope)
    }

    private fun onSocketMessage(message: GameSocketMessage) {
        when (message) {
            is GameSocketMessage.Connected -> {
                val epochMillis = runCatching {
                    Instant.parse(message.doorsAt).toEpochMilliseconds()
                }.getOrDefault(0L)
                val members = message.members.take(MAX_AVATARS).map { it.toPresentation() }
                _state.update {
                    it.copy(
                        targetEpochMillis = epochMillis,
                        lobbyMembers = members,
                        lobbyTotalCount = message.totalCount,
                    )
                }
            }

            is GameSocketMessage.LobbyUserJoined -> {
                _state.update { state ->
                    val members = state.lobbyMembers.toMutableList()
                    if (message.fullName != null && members.size < MAX_AVATARS) {
                        members.add(message.toPresentation())
                    }
                    state.copy(lobbyMembers = members, lobbyTotalCount = message.totalCount)
                }
            }

            is GameSocketMessage.LobbyUserLeft -> {
                _state.update { state ->
                    val members = state.lobbyMembers.filter { it.id != message.userId }
                    state.copy(lobbyMembers = members, lobbyTotalCount = message.totalCount)
                }
            }

            else -> Unit
        }
    }

    fun onAction(action: GameLobbyAction) {
        when (action) {
            GameLobbyAction.OnBackClick -> _state.update { it.copy(showExitConfirmDialog = true) }
            GameLobbyAction.OnCountdownFinished -> _state.update { it.copy(showExitConfirmDialog = false, showGameStartSheet = false) }
            GameLobbyAction.OnExitDismissed -> _state.update { it.copy(showExitConfirmDialog = false) }
            GameLobbyAction.OnExitConfirmed -> onExitConfirmed()
            GameLobbyAction.OnEnterGameClick -> onEnterGameClick()
        }
    }

    private fun onEnterGameClick() {
        _state.update { it.copy(showGameStartSheet = false) }
        viewModelScope.launch { eventChannel.send(GameLobbyEvent.NavigateToGame) }
    }

    private fun onExitConfirmed() {
        _state.update { it.copy(showExitConfirmDialog = false, showGameStartSheet = false) }
        viewModelScope.launch { eventChannel.send(GameLobbyEvent.NavigateBack) }
    }

    private fun com.kaanf.game.domain.model.LobbyMember.toPresentation(): LobbyMember {
        return LobbyMember(
            id = userId,
            avatar = UserAvatar(
                label = fullName.take(1).uppercase(),
                color = avatarColor(userId),
                imageUrl = profilePictureUrl,
            ),
        )
    }

    private fun GameSocketMessage.LobbyUserJoined.toPresentation(): LobbyMember {
        val name = fullName.orEmpty()
        return LobbyMember(
            id = userId,
            avatar = UserAvatar(
                label = name.take(1).uppercase(),
                color = avatarColor(userId),
                imageUrl = profilePictureUrl,
            ),
        )
    }

    private fun avatarColor(userId: String): Color =
        AVATAR_COLORS[abs(userId.hashCode()) % AVATAR_COLORS.size]

    private companion object {
        const val MAX_AVATARS = 13

        val AVATAR_COLORS = listOf(
            Color(0xFFFF5A7A),
            Color(0xFFC8FF3D),
            Color(0xFF5BE0C5),
            Color(0xFF6FB7FF),
            Color(0xFFFF7A5C),
            Color(0xFFFFB341),
        )
    }
}
