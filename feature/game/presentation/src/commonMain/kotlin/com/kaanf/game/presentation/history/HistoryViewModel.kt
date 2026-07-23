package com.kaanf.game.presentation.history

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.game.domain.model.MatchHistoryEntry
import com.kaanf.game.domain.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

@Immutable
data class HistoryState(
    val isLoading: Boolean = true,
    val entries: List<MatchHistoryEntry> = emptyList(),
    val endReached: Boolean = false,
)

class HistoryViewModel(
    private val matchRepository: MatchRepository,
    private val snackbarController: SnackbarController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId: String = savedStateHandle.get<String>("eventId").orEmpty()

    private var nextPage = 0
    private var isLoadingPage = false

    private val _state = MutableStateFlow(HistoryState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        if (isLoadingPage || _state.value.endReached) return
        isLoadingPage = true
        viewModelScope.launch {
            matchRepository.getMatchHistory(eventId, page = nextPage, size = PAGE_SIZE)
                .onSuccess { page ->
                    nextPage++
                    _state.update { current ->
                        // Sayfalar arası kayma bir kaydı iki sayfada gösterebilir; matchId'ye göre tekilleştir.
                        val merged = (current.entries + page).distinctBy { it.matchId }
                        current.copy(
                            isLoading = false,
                            entries = merged,
                            endReached = page.size < PAGE_SIZE,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    snackbarController.show(error.toSnackbarMessage())
                }
            isLoadingPage = false
        }
    }
}
