package com.kaanf.home.presentation.mapper

import com.kaanf.core.domain.model.event.EventDashboard
import com.kaanf.home.presentation.model.EventDashboardUiModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun EventDashboard.toUiModel(): EventDashboardUiModel {
    return EventDashboardUiModel(
        id = id,
        title = title,
        isFeatured = isFeatured,
        date = startsAt.toEventDateText(),
        percentage = percentage,
        formattedPrice = price.format(),
    )
}

private fun Instant.toEventDateText(
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

    val monthText = when (date.monthNumber) {
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

    return "$dayText · $monthText ${date.dayOfMonth}"
}
