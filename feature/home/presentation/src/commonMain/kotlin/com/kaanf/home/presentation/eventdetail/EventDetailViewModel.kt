package com.kaanf.home.presentation.eventdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

class EventDetailViewModel: ViewModel() {
    private val eventChannel = Channel<EventDetailEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(EventDetailState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EventDetailState(),
        )
}
