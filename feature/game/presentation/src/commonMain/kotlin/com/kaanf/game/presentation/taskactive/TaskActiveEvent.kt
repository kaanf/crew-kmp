package com.kaanf.game.presentation.taskactive

sealed interface TaskActiveEvent {
    data object NavigateBack : TaskActiveEvent
}
