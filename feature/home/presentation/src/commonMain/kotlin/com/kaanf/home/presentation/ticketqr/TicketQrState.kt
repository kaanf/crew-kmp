package com.kaanf.home.presentation.ticketqr

import com.kaanf.home.presentation.model.TicketQrUiModel

data class TicketQrState(
    val isLoading: Boolean = false,
    val ticket: TicketQrUiModel? = null,
)
