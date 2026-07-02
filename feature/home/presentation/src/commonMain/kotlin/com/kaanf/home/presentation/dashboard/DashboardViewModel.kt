package com.kaanf.home.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.repository.UserStore
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.core.presentation.util.UIText
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
import kotlin.time.Clock

class DashboardViewModel(
    private val getEventsUseCase: GetEventsUseCase,
    private val snackbarController: SnackbarController,
    userStorage: UserStore
) : ViewModel() {
    private val eventChannel = Channel<DashboardEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false
    private var isFirstResume = true

    private val _state = MutableStateFlow(DashboardState())
    val state = combine(
        _state,
        userStorage.observeCurrentUser()
    ) { currentState, currentUser ->
        if (currentUser != null) {
            currentState.copy(
                profilePictureUrl = currentUser.profilePictureUrl,
                userName = currentUser.fullName
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

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.OnEventClicked -> navigateToEventDetail(action.id)
            is DashboardAction.OnRefresh -> loadEvents(isRefresh = true)
            // First resume coincides with onStart's initial load; skip it, then
            // silently re-sync on every return to the screen (e.g. after buying a ticket).
            is DashboardAction.OnResume -> if (isFirstResume) isFirstResume = false else loadEvents(silent = true)
        }
    }

    private fun navigateToEventDetail(eventId: EventId) = viewModelScope.launch {
        eventChannel.send(DashboardEvent.NavigateToEventDetail(eventId))
    }

    private fun loadEvents(isRefresh: Boolean = false, silent: Boolean = false) = viewModelScope.launch {
        if (!silent) {
            _state.update {
                if (isRefresh) it.copy(isRefreshing = true) else it.copy(isLoading = true)
            }
        }

        when (val result = getEventsUseCase()) {
            is Result.Success -> {
                val now = Clock.System.now()
                val myEvents = result.data.filter { it.hasMyTicket }
                val (doorsOpen, upcoming) = result.data
                    .filter { !it.hasMyTicket }
                    .partition { it.doorsAt <= now }

                _state.update {
                    it.copy(
                        myEvents = myEvents.map { event -> event.toUiModel() },
                        doorsOpenEvents = doorsOpen.map { event -> event.toUiModel() },
                        upcomingEvents = upcoming.map { event -> event.toUiModel() },
                        isLoading = false,
                        isRefreshing = false,
                    )
                }
            }

            is Result.Failure -> {
                if (silent) return@launch // keep stale data, don't nag on background sync
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                    )
                }
                snackbarController.show(
                    result.error.toSnackbarMessage(
                        title = UIText.Resource(Res.string.dashboard_snackbar_load_failed_title),
                    ),
                )
            }
        }
    }
}
