package com.kaanf.game.presentation.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.game.domain.repository.GameSocketRepository
import kotlinx.coroutines.Job
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

class GameViewModel(
    private val gameSocketRepository: GameSocketRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId = savedStateHandle.get<String>("eventId").orEmpty()

    private val eventChannel = Channel<GameEvent>()
    val events = eventChannel.receiveAsFlow()

    private var socketJob: Job? = null

    private val _state = MutableStateFlow(GameState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    init {
        // Kullanıcı oyuna girince sokete subscribe ol.
        subscribeToEvents()
    }

    fun onAction(action: GameAction) {
        when (action) {
            GameAction.OnBackClick -> _state.update { it.copy(showExitConfirmDialog = true) }
            GameAction.OnExitDismissed -> _state.update { it.copy(showExitConfirmDialog = false) }
            GameAction.OnExitConfirmed -> onExitConfirmed()
            GameAction.OnScanClicked -> onScanClicked()
        }
    }

    private fun subscribeToEvents() {
        if (socketJob?.isActive == true) return

        socketJob = gameSocketRepository.observeEvents(eventId)
            .onEach { message ->
                // TODO: gelen mesaja göre state'i güncelle (message.type / message.raw)
            }
            .catch { error ->
                _state.update { it.copy(errorMessage = error.message) }
            }
            .launchIn(viewModelScope)
    }

    private fun unsubscribeFromEvents() {
        socketJob?.cancel()
        socketJob = null
    }

    private fun onExitConfirmed() {
        _state.update { it.copy(showExitConfirmDialog = false) }
        unsubscribeFromEvents()
        viewModelScope.launch {
            eventChannel.send(GameEvent.NavigateToDashboard)
        }
    }

    private fun onScanClicked() {
        viewModelScope.launch {
            eventChannel.send(GameEvent.NavigateToScanOpponent)
        }
    }

    override fun onCleared() {
        // Ekran back-stack'ten çıkınca (oyundan ayrılınca) soketi kapat.
        unsubscribeFromEvents()
        super.onCleared()
    }
}
