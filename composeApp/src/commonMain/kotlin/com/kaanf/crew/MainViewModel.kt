@file:OptIn(FlowPreview::class)

package com.kaanf.crew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.data.networking.ConnectivityObserver
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.seconds

class MainViewModel(
    private val sessionStorage: SessionStorage,
    private val snackbarController: SnackbarController,
    private val connectivityObserver: ConnectivityObserver,
) : ViewModel() {
    private val eventChannel = Channel<MainEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(MainState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MainState(),
        )

    private var previousRefreshToken: String? = null

    init {
        sessionStorage
            .observeAuthInfo()
            .onEach { authInfo ->
                val currentRefreshToken = authInfo?.refreshToken
                val isSessionEnded = previousRefreshToken != null && currentRefreshToken == null

                if (isSessionEnded) {
                    eventChannel.send(MainEvent.OnSessionEnded)
                }

                _state.update {
                    it.copy(
                        isCheckingAuth = false,
                        isLoggedIn = authInfo != null,
                    )
                }

                previousRefreshToken = currentRefreshToken
            }
            .launchIn(viewModelScope)

        observeConnectivity()
    }

    /** Game grafiği görünürken bu bayrak açık; oradaki soket zaten kendi uyarısını gösteriyor. */
    private val isGameGraphVisible = MutableStateFlow(false)

    fun onGameGraphVisibilityChanged(visible: Boolean) {
        isGameGraphVisible.value = visible
    }

    /**
     * App geneli "internet yok" uyarısı. Game ekranları soket durumundan kendi snackbar'ını
     * gösterdiği için orada susarız; burası diğer tüm ekranları kapsar.
     */
    private fun observeConnectivity() {
        connectivityObserver.isConnected
            // Kısa flap'leri (hücre↔wifi geçişi) yut; ilk emisyon açılış durumu, uyarma.
            .debounce(1.seconds)
            .distinctUntilChanged()
            .drop(1)
            .filter { isConnected -> !isConnected && !isGameGraphVisible.value }
            .onEach { snackbarController.show(DataError.Remote.NO_INTERNET.toSnackbarMessage()) }
            .launchIn(viewModelScope)
    }
}
