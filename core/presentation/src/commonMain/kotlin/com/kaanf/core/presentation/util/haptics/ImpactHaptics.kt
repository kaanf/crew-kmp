package com.kaanf.core.presentation.util.haptics

import androidx.compose.runtime.Composable

fun interface ImpactHaptics {
    fun perform()
}

/** Deliberate impact feedback for direct manipulation, stronger than text-selection ticks. */
@Composable
expect fun rememberImpactHaptics(): ImpactHaptics
