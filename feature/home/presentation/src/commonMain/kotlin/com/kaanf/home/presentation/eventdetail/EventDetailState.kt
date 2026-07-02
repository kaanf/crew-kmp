package com.kaanf.home.presentation.eventdetail

import com.kaanf.home.presentation.model.EventDetailUiModel

data class EventDetailState(
    val isLoading: Boolean = false,
    val isCheckingOut: Boolean = false,
    val loadFailed: Boolean = false,
    val event: EventDetailUiModel? = null,
)
