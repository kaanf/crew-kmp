package com.kaanf.crew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.repository.SessionStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MainViewModel(
    private val sessionStorage: SessionStorage,
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
                val isSessionExpired = previousRefreshToken != null && currentRefreshToken == null

                if (isSessionExpired) {
                    eventChannel.send(MainEvent.OnSessionExpired)
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
    }
}
