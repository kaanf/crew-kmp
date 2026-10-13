package com.kaanf.home.presentation.util

import com.kaanf.core.domain.model.venue.Venue
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/** "Wednesday, 9 July" */
internal fun Instant.toEventDateText(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String {
    val date = toLocalDateTime(timeZone).date
    val dayName = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    val monthName = date.month.name.lowercase().replaceFirstChar { it.uppercase() }

    return "$dayName, ${date.day} $monthName"
}

private val MonthShortNames = listOf(
    "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
    "JUL", "AUG", "SEP", "OCT", "NOV", "DEC",
)

internal fun Instant.toMonthShortText(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String = MonthShortNames[toLocalDateTime(timeZone).date.month.number - 1]

internal fun Instant.toDayOfMonthText(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String = toLocalDateTime(timeZone).date.day.toString()

/** "Tuesday, 27 Oct 2026" */
internal fun Instant.toEventDetailDateText(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String {
    val date = toLocalDateTime(timeZone).date
    val dayName = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    val monthName = date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    return "$dayName, ${date.day} $monthName ${date.year}"
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
