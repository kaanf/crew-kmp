package com.kaanf.core.presentation.util

import androidx.compose.runtime.Composable

/**
 * Returns an action that resigns focus from the currently focused native (interop) text input —
 * `UITextField` on iOS, `EditText` on Android — and hides the soft keyboard.
 *
 * Compose's `FocusManager.clearFocus()` does not reach views embedded via `UIKitView`/`AndroidView`,
 * so tap-to-dismiss flows must call this in addition to `clearFocus()` to blur native fields.
 */
@Composable
expect fun rememberNativeFocusClearer(): () -> Unit
