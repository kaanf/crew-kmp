package com.kaanf.core.presentation.util

import androidx.compose.runtime.Composable

/**
 * Reports "time to full display" to the platform once [isFullyDrawn] turns true — the moment the
 * screen shows real content rather than a splash or a spinner.
 *
 * The platform's own startup measurement stops at the first drawn frame, which for this app is
 * still the splash, so without this the reported cold start is shorter than what the user waits.
 * Reporting it feeds Play Console Vitals, Macrobenchmark's time-to-full-display metric and the
 * boundary the runtime uses when collecting a startup profile.
 *
 * Pass a predicate that is true for *every* terminal state, failure included: the report fires
 * once and a predicate that never turns true drops the metric silently.
 *
 * No-op outside Android.
 */
@Composable
expect fun ReportFullyDrawnWhen(isFullyDrawn: () -> Boolean)
