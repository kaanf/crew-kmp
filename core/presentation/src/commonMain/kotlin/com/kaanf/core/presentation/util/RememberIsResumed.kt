package com.kaanf.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.awaitCancellation

@Composable
fun rememberIsResumed(): State<Boolean> {
    val owner = LocalLifecycleOwner.current
    return produceState(initialValue = false, owner) {
        owner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            value = true
            try {
                awaitCancellation()
            } finally {
                value = false
            }
        }
    }
}
