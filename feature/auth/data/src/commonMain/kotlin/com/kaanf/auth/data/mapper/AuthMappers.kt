package com.kaanf.auth.data.mapper

import com.kaanf.auth.data.dto.SignInMethodsResponse
import com.kaanf.auth.data.dto.request.RegisterRequest
import com.kaanf.auth.domain.model.LinkedIdentity
import com.kaanf.auth.domain.model.RegisterParams
import com.kaanf.auth.domain.model.SignInMethods
import com.kaanf.auth.domain.model.SocialProvider

fun RegisterParams.toDto(): RegisterRequest =
    RegisterRequest(
        email = email,
        password = password,
        fullName = fullName,
        profilePictureUrl = profilePictureUrl,
        ageConfirmed = ageConfirmed,
        privacyAccepted = privacyAccepted,
    )

fun SignInMethodsResponse.toDomain(): SignInMethods =
    SignInMethods(
        accountEmail = email,
        hasPassword = hasPassword,
        signUpProvider = socialProviderOrNull(signUpMethod),
        identities =
            identities.mapNotNull { identity ->
                socialProviderOrNull(identity.provider)?.let { provider ->
                    LinkedIdentity(provider = provider, email = identity.email)
                }
            },
    )

private fun socialProviderOrNull(name: String): SocialProvider? =
    SocialProvider.entries.firstOrNull { it.name == name }
