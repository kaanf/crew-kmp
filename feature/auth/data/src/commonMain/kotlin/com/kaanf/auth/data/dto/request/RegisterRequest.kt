package com.kaanf.auth.data.dto.request

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val dateOfBirth: LocalDate,
    val gender: GenderDto,
)
