package com.kaanf.game.presentation.taskactive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskActiveViewModel : ViewModel() {
    private val eventChannel = Channel<TaskActiveEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(TaskActiveState())
    val state =
        _state.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    fun onAction(action: TaskActiveAction) {
        when (action) {
            TaskActiveAction.OnBackClick -> onBackClick()
        }
    }

    private fun onBackClick() {
        viewModelScope.launch {
            eventChannel.send(TaskActiveEvent.NavigateBack)
        }
    }
}
