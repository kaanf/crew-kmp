package com.kaanf.game.presentation.memories

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.game.domain.model.EventMemory
import com.kaanf.game.domain.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

@Immutable
data class MemoriesState(
    val isLoading: Boolean = true,
    /** Oyun sürerken yalnız yüklediklerim + etiketlendiklerim; bitince tüm rulo (sunucu karar verir). */
    val memories: List<EventMemory> = emptyList(),
    val endReached: Boolean = false,
)

/**
 * Etkinliğin foto quest karelerinin galerisi (leaderboard'daki "roll developed" reveal'ı).
 * Liste sunucu tarafında faza göre daralır/genişler. İmzalı URL'ler kısa ömürlü olduğundan
 * her açılışta [refresh].
 */
class MemoriesViewModel(
    private val matchRepository: MatchRepository,
    private val snackbarController: SnackbarController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId: String = savedStateHandle.get<String>("eventId").orEmpty()

    private var nextPage = 0
    private var isLoadingPage = false

    private val _state = MutableStateFlow(MemoriesState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    init {
        refresh()
    }

    /** İlk sayfayı çekip listeyi baştan kurar (imzalı URL'ler bayatlayabildiği için her açılışta). */
    fun refresh() {
        if (isLoadingPage) return
        isLoadingPage = true
        viewModelScope.launch {
            matchRepository.getMemories(eventId, page = 0, size = PAGE_SIZE)
                .onSuccess { firstPage ->
                    nextPage = 1
                    _state.update {
                        it.copy(
                            isLoading = false,
                            memories = firstPage,
                            endReached = firstPage.size < PAGE_SIZE,
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

    fun loadNextPage() {
        if (isLoadingPage || _state.value.endReached) return
        isLoadingPage = true
        viewModelScope.launch {
            matchRepository.getMemories(eventId, page = nextPage, size = PAGE_SIZE)
                .onSuccess { page ->
                    nextPage++
                    _state.update { current ->
                        // Yeni upload sayfaları kaydırabilir; aynı kayıt iki sayfada gelebilir.
                        val merged = (current.memories + page).distinctBy { it.id }
                        current.copy(memories = merged, endReached = page.size < PAGE_SIZE)
                    }
                }
                .onFailure { error ->
                    snackbarController.show(error.toSnackbarMessage())
                }
            isLoadingPage = false
        }
    }

}
