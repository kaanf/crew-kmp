package com.kaanf.core.designsystem.component.textfield

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.input.PlatformImeOptions
import platform.UIKit.UITextAutocapitalizationType
import platform.UIKit.UITextAutocorrectionType

@OptIn(ExperimentalComposeUiApi::class)
internal actual fun suppressionPlatformImeOptions(isSecure: Boolean): PlatformImeOptions? =
    PlatformImeOptions {
        textContentType("")
        autocapitalizationType(UITextAutocapitalizationType.UITextAutocapitalizationTypeNone)
        autocorrectionType(UITextAutocorrectionType.UITextAutocorrectionTypeNo)
        if (isSecure) {
            isSecureTextEntry(true)
        }
    }
