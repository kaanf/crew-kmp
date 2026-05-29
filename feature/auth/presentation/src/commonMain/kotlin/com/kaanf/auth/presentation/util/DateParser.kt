package com.kaanf.auth.presentation.util

import kotlinx.datetime.LocalDate

fun String.toLocalDate(): LocalDate? {
    val digits = this.filter { it.isDigit() }
    if (digits.length != DATE_OF_BIRTH_DIGIT_COUNT) return null

    val day = digits.substring(0, 2).toIntOrNull() ?: return null
    val month = digits.substring(2, 4).toIntOrNull() ?: return null
    val year = digits.substring(4, 8).toIntOrNull() ?: return null

    return runCatching { LocalDate(year, month, day) }.getOrNull()
}

private const val DATE_OF_BIRTH_DIGIT_COUNT = 8
