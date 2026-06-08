package com.kaanf.home.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.repository.UserStore
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.base.BaseEvent
import com.kaanf.core.presentation.model.SnackbarMessage
import com.kaanf.core.presentation.model.SnackbarVariant
import com.kaanf.core.presentation.util.UIText
import com.kaanf.core.presentation.util.toUiText
import com.kaanf.home.domain.usecase.GetEventsUseCase
import com.kaanf.home.presentation.mapper.toUiModel
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.dashboard_snackbar_load_failed_title
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getEventsUseCase: GetEventsUseCase,
    userStorage: UserStore
) : ViewModel() {
    private val eventChannel = Channel<DashboardEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(DashboardState())
    val state = combine(
        _state,
        userStorage.observeCurrentUser()
    ) { currentState, currentUser ->
        if (currentUser != null) {
            currentState.copy(
                profilePictureUrl = currentUser.profilePictureUrl
            )
        } else currentState
    }.onStart {
        if (!hasLoadedInitialData) {
            loadEvents()
            hasLoadedInitialData = true
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DashboardState(),
    )

    init {
        loadEvents()
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.OnEventClicked -> navigateToEventDetail(action.id)
        }
    }

    private fun navigateToEventDetail(eventId: EventId) = viewModelScope.launch {
        eventChannel.send(DashboardEvent.NavigateToEventDetail(eventId))
    }

    private fun loadEvents() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val result = getEventsUseCase()) {
            is Result.Success -> {
                val uiModels = result.data.map { it.toUiModel() }

                _state.update {
                    it.copy(
                        events = uiModels,
                        isLoading = false,
                    )
                }
            }

            is Result.Failure -> {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
