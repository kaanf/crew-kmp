package com.kaanf.crew

sealed interface MainEvent {
    data object OnSessionExpired: MainEvent
}
