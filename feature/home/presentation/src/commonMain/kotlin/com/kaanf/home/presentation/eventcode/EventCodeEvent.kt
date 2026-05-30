package com.kaanf.home.presentation.eventcode

sealed interface EventCodeEvent {
    data object CodeSuccess: EventCodeEvent
}
