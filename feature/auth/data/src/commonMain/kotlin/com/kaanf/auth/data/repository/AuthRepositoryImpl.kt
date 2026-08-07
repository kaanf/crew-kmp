package com.kaanf.auth.data.repository

import com.kaanf.auth.data.dto.SignInMethodsResponse
import com.kaanf.auth.data.dto.request.ChangePasswordRequest
import com.kaanf.auth.data.dto.request.EmailRequest
import com.kaanf.core.data.dto.AuthInfoSerializable
import com.kaanf.auth.data.dto.request.LinkIdentityRequest
import com.kaanf.auth.data.dto.request.LoginRequest
import com.kaanf.auth.data.dto.request.SocialLoginRequest
import com.kaanf.auth.data.mapper.toDomain
import com.kaanf.auth.data.mapper.toDto
import com.kaanf.auth.domain.model.LinkIdentityParams
import com.kaanf.auth.domain.model.RegisterParams
import com.kaanf.auth.domain.model.SignInMethods
import com.kaanf.auth.domain.model.SocialLoginParams
import com.kaanf.auth.domain.model.SocialProvider
import com.kaanf.core.data.mappers.toDomain
import com.kaanf.core.data.networking.delete
import com.kaanf.core.data.networking.get
import com.kaanf.core.data.networking.post
import com.kaanf.core.domain.model.auth.AuthInfo
import com.kaanf.core.domain.provider.DeviceIdProvider
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result
import com.kaanf.core.domain.util.map
import com.kaanf.auth.domain.repository.AuthRepository
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val deviceIdProvider: DeviceIdProvider,
) : AuthRepository {
    override suspend fun register(params: RegisterParams): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/register",
            body = params.toDto(),
        ) {
            headers.append(
                "deviceId",
                deviceIdProvider.getDeviceId(),
            )
        }
    }

    override suspend fun login(
        email: String,
        password: String,
    ): Result<AuthInfo, DataError.Remote> {
        return httpClient.post<LoginRequest, AuthInfoSerializable>(
            route = "/auth/login",
            body =
                LoginRequest(
                    email = email,
                    password = password,
                ),
        ).map { authInfoSerializable ->
            authInfoSerializable.toDomain()
        }
    }

    override suspend fun socialLogin(params: SocialLoginParams): Result<AuthInfo, DataError.Remote> {
        return httpClient.post<SocialLoginRequest, AuthInfoSerializable>(
            route = "/auth/social",
            body =
                SocialLoginRequest(
                    provider = params.provider.name,
                    idToken = params.idToken,
                    nonce = params.nonce,
                    ageConfirmed = params.ageConfirmed,
                    privacyAccepted = params.privacyAccepted,
                    fullName = params.fullName,
                ),
        ) {
            headers.append(
                "deviceId",
                deviceIdProvider.getDeviceId(),
            )
        }.map { authInfoSerializable ->
            authInfoSerializable.toDomain()
        }
    }

    override suspend fun getSignInMethods(): Result<SignInMethods, DataError.Remote> {
        return httpClient.get<SignInMethodsResponse>(route = "/auth/identities")
            .map { response -> response.toDomain() }
    }

    override suspend fun linkIdentity(params: LinkIdentityParams): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/link",
            body =
                LinkIdentityRequest(
                    provider = params.provider.name,
                    idToken = params.idToken,
                    nonce = params.nonce,
                ),
        )
    }

    override suspend fun unlinkIdentity(provider: SocialProvider): EmptyResult<DataError.Remote> {
        return httpClient.delete(route = "/auth/link/${provider.name}")
    }

    override suspend fun changePassword(
        currentPassword: String?,
        newPassword: String,
    ): Result<AuthInfo, DataError.Remote> {
        return httpClient.post<ChangePasswordRequest, AuthInfoSerializable>(
            route = "/auth/change-password",
            body =
                ChangePasswordRequest(
                    oldPassword = currentPassword,
                    newPassword = newPassword,
                ),
        ).map { authInfoSerializable ->
            authInfoSerializable.toDomain()
        }
    }

    override suspend fun resendVerificationMail(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/resend-verification",
            body = EmailRequest(email = email),
        )
    }

    override suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote> {
        return httpClient.get(
            route = "/notification/activate-user",
            queryParams = mapOf("token" to token),
        )
    }

    override suspend fun forgotPassword(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/notification/forget-password",
            body = EmailRequest(email = email),
        )
    }
}
