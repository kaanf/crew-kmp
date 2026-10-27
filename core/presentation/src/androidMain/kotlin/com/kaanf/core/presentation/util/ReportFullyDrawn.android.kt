package com.kaanf.core.presentation.util

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.runtime.Composable

@Composable
actual fun ReportFullyDrawnWhen(isFullyDrawn: () -> Boolean) {
    ReportDrawnWhen(isFullyDrawn)
}
