package com.kaanf.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos

private const val SLOW_FRAME_MS = 33f
private const val WINDOW_MS = 1_000f

/**
 * Compose kare saatinin aralıklarını ölçer: 33 ms'i aşan her kareyi anında,
 * saniyelik özeti tek satır hâlinde basar. [tag] değiştiğinde pencere sıfırlanır,
 * böylece ekran geçişleri ayrı ayrı okunur.
 *
 * Jank ölçmek için `App`'te çağır, ölçüm bitince çağrıyı kaldır — her vsync'te
 * kare talep ettiği için sürümde açık bırakılmamalı:
 *
 * ```
 * FrameProbe(tag = currentDestination?.route?.substringAfterLast('.')?.substringBefore('/') ?: "boot")
 * ```
 *
 * iOS'ta çıktı: `xcrun simctl launch --console-pty booted com.kaanf.crew`
 */
@Composable
fun FrameProbe(tag: String) {
    LaunchedEffect(tag) {
        val deltas = ArrayList<Float>(128)
        var previous = 0L
        var windowStart = 0L

        while (true) {
            withFrameNanos { now ->
                if (windowStart == 0L) windowStart = now

                if (previous != 0L) {
                    val deltaMs = (now - previous) / 1_000_000f
                    deltas += deltaMs
                    if (deltaMs > SLOW_FRAME_MS) {
                        println("[frame] SLOW ${deltaMs.round1()}ms tag=$tag")
                    }
                }
                previous = now

                if ((now - windowStart) / 1_000_000f >= WINDOW_MS && deltas.isNotEmpty()) {
                    println(deltas.summary(tag))
                    deltas.clear()
                    windowStart = now
                }
            }
        }
    }
}

private fun List<Float>.summary(tag: String): String {
    val sorted = sorted()
    val total = sum()
    val janky = count { it > SLOW_FRAME_MS }
    return "[frame] tag=$tag n=$size fps=${(size * 1000f / total).round1()} " +
        "p50=${sorted.percentile(0.50f).round1()} p95=${sorted.percentile(0.95f).round1()} " +
        "max=${sorted.last().round1()} slow=$janky"
}

private fun List<Float>.percentile(fraction: Float): Float =
    this[((size - 1) * fraction).toInt()]

private fun Float.round1(): String {
    val scaled = (this * 10f).toInt()
    return "${scaled / 10}.${scaled % 10}"
}
