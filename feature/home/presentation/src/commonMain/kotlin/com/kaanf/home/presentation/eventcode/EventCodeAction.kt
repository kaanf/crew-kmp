package com.kaanf.home.presentation.eventcode

sealed interface EventCodeAction {
    data class OnCodeChanged(val code: String) : EventCodeAction
}
