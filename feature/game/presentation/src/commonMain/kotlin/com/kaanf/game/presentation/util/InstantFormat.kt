package com.kaanf.game.presentation.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/** "20:41" biçiminde yerel saat; history satırı ve pasaport damga detayı kullanır. */
internal fun Instant.toClockText(): String {
    val time = toLocalDateTime(TimeZone.currentSystemDefault()).time
    return "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}
