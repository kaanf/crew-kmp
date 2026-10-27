package com.kaanf.core.presentation.util

import androidx.compose.runtime.Composable

// UIKit'te karşılığı yok: başlangıç süresi ölçümü Android tarafına özgü.
@Composable
actual fun ReportFullyDrawnWhen(isFullyDrawn: () -> Boolean) = Unit
