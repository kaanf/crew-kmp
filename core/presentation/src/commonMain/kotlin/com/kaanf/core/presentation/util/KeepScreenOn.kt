package com.kaanf.core.presentation.util

import androidx.compose.runtime.Composable

/**
 * Keeps the screen awake while the calling composable is in composition and restores the
 * normal idle timeout on dispose. Use on screens shown passively at arm's length (door
 * code / ticket), where the system dimming the display mid-queue breaks the flow.
 */
@Composable
expect fun KeepScreenOn()
