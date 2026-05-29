package com.kaanf.core.data.mappers

import com.kaanf.core.data.dto.AuthInfoSerializable
import com.kaanf.core.data.dto.UserSerializable
import com.kaanf.core.domain.model.auth.AuthInfo
import com.kaanf.core.domain.model.user.User
import kotlin.String

fun AuthInfoSerializable.toDomain(): AuthInfo {
    return AuthInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user?.toDomain(),
    )
}

fun UserSerializable.toDomain(): User {
    return User(
        id = id,
        email = email,
        fullName = fullName,
        dateOfBirth = dateOfBirth,
        gender = gender,
        role = role,
        status = status,
        hasVerifiedEmail = hasVerifiedEmail,
    )
}

fun User.toSerializable(): UserSerializable {
    return UserSerializable(
        id = id,
        email = email,
        fullName = fullName,
        dateOfBirth = dateOfBirth,
        gender = gender,
        role = role,
        status = status,
        hasVerifiedEmail = hasVerifiedEmail,
    )
}

fun AuthInfo.toSerializable(): AuthInfoSerializable {
    return AuthInfoSerializable(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user?.toSerializable(),
    )
}
