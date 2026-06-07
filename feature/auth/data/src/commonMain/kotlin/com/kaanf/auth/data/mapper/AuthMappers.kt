package com.kaanf.auth.data.mapper

import com.kaanf.auth.data.dto.request.GenderDto
import com.kaanf.auth.data.dto.request.RegisterRequest
import com.kaanf.auth.domain.model.Gender
import com.kaanf.auth.domain.model.RegisterParams

fun RegisterParams.toDto(): RegisterRequest =
    RegisterRequest(
        email = email,
        password = password,
        fullName = fullName,
        dateOfBirth = dateOfBirth,
        profilePictureUrl = profilePictureUrl,
        gender = gender.toDto(),
    )

private fun Gender.toDto(): GenderDto =
    when (this) {
        Gender.Female -> GenderDto.FEMALE
        Gender.Male -> GenderDto.MALE
        Gender.NonBinary -> GenderDto.NON_BINARY
        Gender.Other -> GenderDto.OTHER
        Gender.PreferNotToSay -> GenderDto.PREFER_NOT_TO_SAY
    }
