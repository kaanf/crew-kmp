package com.kaanf.auth.data.mapper

import com.kaanf.auth.data.dto.request.RegisterRequest
import com.kaanf.auth.domain.model.RegisterParams

fun RegisterParams.toDto(): RegisterRequest =
    RegisterRequest(
        email = email,
        password = password,
        fullName = fullName,
        profilePictureUrl = profilePictureUrl,
        ageConfirmed = ageConfirmed,
        privacyAccepted = privacyAccepted,
    )
