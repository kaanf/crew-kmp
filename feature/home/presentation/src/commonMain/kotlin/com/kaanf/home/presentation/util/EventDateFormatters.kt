package com.kaanf.home.presentation.util

import com.kaanf.core.domain.model.venue.Venue
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

internal fun Instant.toEventDateText(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String {
    val date = toLocalDateTime(timeZone).date

    val dayText = when (date.dayOfWeek.name) {
        "MONDAY" -> "MON"
        "TUESDAY" -> "TUE"
        "WEDNESDAY" -> "WED"
        "THURSDAY" -> "THU"
        "FRIDAY" -> "FRI"
        "SATURDAY" -> "SAT"
        "SUNDAY" -> "SUN"
        else -> ""
    }

    val monthText = when (date.month.number) {
        1 -> "JAN"
        2 -> "FEB"
        3 -> "MAR"
        4 -> "APR"
        5 -> "MAY"
        6 -> "JUN"
        7 -> "JUL"
        8 -> "AUG"
        9 -> "SEP"
        10 -> "OCT"
        11 -> "NOV"
        12 -> "DEC"
        else -> ""
    }

    return "$dayText · $monthText ${date.day}"
}

internal fun Instant.toEventDetailDateText(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String {
    val date = toLocalDateTime(timeZone).date
    val day = date.day.toString().padStart(2, '0')
    val month = date.month.number.toString().padStart(2, '0')
    val dayName = date.dayOfWeek.name
        .lowercase()
        .replaceFirstChar { it.uppercase() }
    return "$day.$month.${date.year}, $dayName"
}

internal fun Instant.toClockText(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String {
    val time = toLocalDateTime(timeZone).time
    val hour = time.hour.toString().padStart(2, '0')
    val minute = time.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}

fun Instant.toQrDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val dateTime = this.toLocalDateTime(timeZone)

    val dayOfWeek = dateTime.dayOfWeek.name
        .take(3)
        .lowercase()
        .replaceFirstChar { it.uppercase() }

    val month = dateTime.month.name
        .take(3)
        .lowercase()
        .replaceFirstChar { it.uppercase() }

    val day = dateTime.dayOfMonth

    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')

    return "$dayOfWeek $month $day · $hour:$minute"
}
