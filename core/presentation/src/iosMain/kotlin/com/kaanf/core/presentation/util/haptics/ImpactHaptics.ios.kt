package com.kaanf.core.presentation.util.haptics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

@Composable
actual fun rememberImpactHaptics(): ImpactHaptics = remember {
    val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
    generator.prepare()
    ImpactHaptics {
        generator.impactOccurredWithIntensity(1.0)
        generator.prepare()
    }
}
