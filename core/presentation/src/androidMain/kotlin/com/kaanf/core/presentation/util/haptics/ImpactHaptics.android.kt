package com.kaanf.core.presentation.util.haptics

import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
actual fun rememberImpactHaptics(): ImpactHaptics {
    val view = LocalView.current
    return remember(view) {
        ImpactHaptics { view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS) }
    }
}
