package com.kaanf.core.designsystem.component.dialog

import androidx.compose.ui.window.DialogProperties

actual fun fullscreenDialogProperties(): DialogProperties = DialogProperties(
    usePlatformDefaultWidth = false,
    decorFitsSystemWindows = false,
)
