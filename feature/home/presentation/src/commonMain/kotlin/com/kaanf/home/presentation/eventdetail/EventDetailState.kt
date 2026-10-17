package com.kaanf.home.presentation.eventdetail

import com.kaanf.home.domain.model.EventGuest
import com.kaanf.home.presentation.model.EventDetailUiModel

data class EventDetailState(
    val isLoading: Boolean = false,
    val isCheckingOut: Boolean = false,
    val loadFailed: Boolean = false,
    val event: EventDetailUiModel? = null,
    val guests: List<EventGuest> = emptyList(),
    val isGuestsLoading: Boolean = false,
)
