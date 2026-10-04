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

    // init'te değil tab her açıldığında çağrılır (HistoryTab): VM Game entry'sine scope'lu, init tek sefer koşar.
    fun refresh() = loadPage(page = 0)

    fun loadNextPage() {
        if (_state.value.endReached) return
        loadPage(page = nextPage)
    }

    private fun loadPage(page: Int) {
        if (isLoadingPage) return
        isLoadingPage = true
        viewModelScope.launch {
            matchRepository.getMatchHistory(eventId, page = page, size = PAGE_SIZE)
                .onSuccess { entries ->
                    nextPage = page + 1
                    _state.update { current ->
                        // Sayfalar arası kayma bir kaydı iki sayfada gösterebilir; matchId'ye göre tekilleştir.
                        // İlk sayfa listeyi baştan kurar: yeni biten maçlar en üste gelir.
                        val base = if (page == 0) emptyList() else current.entries
                        current.copy(
                            isLoading = false,
                            entries = (base + entries).distinctBy { it.matchId },
                            endReached = entries.size < PAGE_SIZE,
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
