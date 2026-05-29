package com.kaanf.core.presentation.base

import com.kaanf.core.presentation.model.SnackbarMessage

interface BaseEvent {
    data class ShowSnackbar(val snackbarMessage: SnackbarMessage) : BaseEvent
}
