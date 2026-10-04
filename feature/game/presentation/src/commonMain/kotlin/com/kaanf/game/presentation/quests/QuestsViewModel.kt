package com.kaanf.game.presentation.quests

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.game.domain.model.Quest
import com.kaanf.game.domain.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class QuestsState(
    val isLoading: Boolean = true,
    val quests: List<Quest> = emptyList(),
    val claimingKey: String? = null,
)

class QuestsViewModel(
    private val matchRepository: MatchRepository,
    private val snackbarController: SnackbarController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId: String = savedStateHandle.get<String>("eventId").orEmpty()

    private val _state = MutableStateFlow(QuestsState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    init {
        loadQuests()
    }

    private fun loadQuests() {
        viewModelScope.launch {
            matchRepository.getQuests(eventId)
                .onSuccess { quests ->
                    _state.update { it.copy(isLoading = false, quests = quests) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    snackbarController.show(error.toSnackbarMessage())
                }
        }
    }

    fun claim(questKey: String) {
        if (_state.value.claimingKey != null) return
        _state.update { it.copy(claimingKey = questKey) }
        viewModelScope.launch {
            matchRepository.claimQuest(eventId, questKey)
                .onSuccess { claimed ->
                    _state.update { current ->
                        current.copy(
                            claimingKey = null,
                            quests = current.quests.map { if (it.key == claimed.key) claimed else it },
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(claimingKey = null) }
                    snackbarController.show(error.toSnackbarMessage())
                }
        }
    }
}
