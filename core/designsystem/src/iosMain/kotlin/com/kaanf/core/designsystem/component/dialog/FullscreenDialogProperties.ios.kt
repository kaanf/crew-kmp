package com.kaanf.core.designsystem.component.dialog

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalComposeUiApi::class)
actual fun fullscreenDialogProperties(): DialogProperties = DialogProperties(
    usePlatformDefaultWidth = false,
    usePlatformInsets = false,
)
