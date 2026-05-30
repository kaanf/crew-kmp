package com.kaanf.home.presentation.eventcode

import com.kaanf.home.presentation.eventcode.component.CodeFieldStatus

data class EventCodeState(
    val eventCode: String = "",
    val status: CodeFieldStatus = CodeFieldStatus.Editing,
    val isLoading: Boolean = false,
    val error: String? = null,
)
