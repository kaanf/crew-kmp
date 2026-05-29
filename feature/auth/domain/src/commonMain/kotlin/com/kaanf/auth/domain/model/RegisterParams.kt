package com.kaanf.auth.domain.model

import kotlinx.datetime.LocalDate

data class RegisterParams(
    val email: String,
    val password: String,
    val fullName: String,
    val dateOfBirth: LocalDate,
    val gender: Gender
)
