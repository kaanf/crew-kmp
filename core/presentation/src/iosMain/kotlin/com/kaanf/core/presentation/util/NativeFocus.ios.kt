package com.kaanf.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIApplication

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberNativeFocusClearer(): () -> Unit =
    remember {
        {
            // Sending resignFirstResponder to a nil target routes it to the current first responder
            // (the focused UITextField), which dismisses the keyboard.
            UIApplication.sharedApplication.sendAction(
                NSSelectorFromString("resignFirstResponder"),
                to = null,
                from = null,
                forEvent = null,
            )
        }
    }
