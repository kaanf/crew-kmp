package com.kaanf.game.presentation.taskactive

sealed interface TaskActiveAction {
    data object OnBackClick : TaskActiveAction
}
