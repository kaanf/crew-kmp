package com.kaanf.core.designsystem.component.textfield

import androidx.compose.ui.text.input.PlatformImeOptions

internal expect fun suppressionPlatformImeOptions(isSecure: Boolean): PlatformImeOptions?
