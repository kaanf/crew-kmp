package com.kaanf.game.presentation.scanopponent

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import com.kaanf.game.domain.model.GameSocketMessage
import com.kaanf.game.domain.repository.GameSocketRepository
import com.kaanf.game.domain.repository.MatchRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScanOpponentViewModel(
    private val matchRepository: MatchRepository,
    private val gameSocketRepository: GameSocketRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId = savedStateHandle.get<String>("eventId").orEmpty()

    private val eventChannel = Channel<ScanOpponentEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(ScanOpponentState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    init {
        observeInviteResult()
    }

    private fun observeInviteResult() {
        gameSocketRepository.observeEvents(eventId)
            .onEach { message ->
                when (message) {
                    is GameSocketMessage.MatchStarted ->
                        eventChannel.send(ScanOpponentEvent.NavigateToGameRpsReady)

                    is GameSocketMessage.MatchInviteDeclined -> {
                        _state.update { it.copy(showGameRequestSheet = false) }
                        eventChannel.send(ScanOpponentEvent.CloseScreen)
                    }

                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: ScanOpponentAction) {
        when (action) {
            ScanOpponentAction.OnCloseClicked -> onBackClick()
            is ScanOpponentAction.OnScanResult -> onScanResult(action.scannedMatchQrToken)
        }
    }

    private fun onScanResult(scannedMatchQrToken: String) {
        if (_state.value.isLoading) return
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            matchRepository.sendInvite(eventId = eventId, scannedMatchQrToken = scannedMatchQrToken)
                .onSuccess { result ->
                    _state.update { it.copy(
                        isLoading = false,
                        opponentName = result.toFullName,
                        showGameRequestSheet = true
                    ) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toString()) }
                }
        }
    }

    private fun onBackClick() {
        viewModelScope.launch {
            eventChannel.send(ScanOpponentEvent.CloseScreen)
        }
    }
}
