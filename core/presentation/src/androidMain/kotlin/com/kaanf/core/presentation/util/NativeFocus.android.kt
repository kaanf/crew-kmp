package com.kaanf.core.presentation.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
actual fun rememberNativeFocusClearer(): () -> Unit {
    val view = LocalView.current
    return remember(view) {
        {
            // findFocus() returns the focused interop EditText (if any); blur it and hide the IME.
            view.findFocus()?.clearFocus()
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
