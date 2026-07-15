package com.kaanf.game.presentation.leaderboard

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.repository.UserRepository
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.game.domain.model.LeaderboardEntry
import com.kaanf.game.domain.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class LeaderboardState(
    val isLoading: Boolean = true,
    val entries: List<LeaderboardEntry> = emptyList(),
    /** Listede "benim" satırımı bulup ortalamak/vurgulamak için. */
    val currentUserId: String? = null,
)

class LeaderboardViewModel(
    private val matchRepository: MatchRepository,
    private val userRepository: UserRepository,
    private val snackbarController: SnackbarController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId: String = savedStateHandle.get<String>("eventId").orEmpty()

    private val _state = MutableStateFlow(LeaderboardState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    init {
        loadLeaderboard()
        userRepository.observeCurrentUser()
            .onEach { user -> _state.update { it.copy(currentUserId = user?.id) } }
            .launchIn(viewModelScope)
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            matchRepository.getLeaderboard(eventId)
                .onSuccess { entries ->
                    _state.update { it.copy(isLoading = false, entries = entries) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    snackbarController.show(error.toSnackbarMessage())
                }
        }
    }
}
