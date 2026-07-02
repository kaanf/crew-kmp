package com.kaanf.home.presentation.ticketqr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay
import kotlin.time.Clock

/**
 * Live "doors haven't opened yet" flag aligned to server time.
 *
 * Server-aligned now = device now + [serverClockOffsetMillis] (serverNow - deviceNow captured at
 * fetch), so a skewed device clock can't unlock the screen early or late. The producer publishes
 * `true` while locked and flips to `false` once doors open — it must set the value itself (not just
 * rely on [produceState]'s initialValue), because the ticket loads async: on first composition the
 * args are still 0/0 and a key change does not reset the retained state.
 */
@Composable
fun rememberDoorsLocked(
    doorsOpenAtMillis: Long,
    serverClockOffsetMillis: Long,
): Boolean {
    val locked by produceState(
        initialValue = serverNowMillis(serverClockOffsetMillis) < doorsOpenAtMillis,
        key1 = doorsOpenAtMillis,
        key2 = serverClockOffsetMillis,
    ) {
        while (serverNowMillis(serverClockOffsetMillis) < doorsOpenAtMillis) {
            value = true
            delay(1000)
        }
        value = false
    }
    return locked
}

private fun serverNowMillis(offsetMillis: Long): Long =
    Clock.System.now().toEpochMilliseconds() + offsetMillis
