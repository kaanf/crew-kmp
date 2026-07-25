package com.kaanf.auth.domain.repository

import com.kaanf.auth.domain.model.RegisterParams
import com.kaanf.auth.domain.model.SocialLoginParams
import com.kaanf.core.domain.model.auth.AuthInfo
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result

interface AuthRepository {
    suspend fun register(params: RegisterParams): EmptyResult<DataError.Remote>

    suspend fun login(
        email: String,
        password: String,
    ): Result<AuthInfo, DataError.Remote>

    suspend fun socialLogin(params: SocialLoginParams): Result<AuthInfo, DataError.Remote>

    suspend fun resendVerificationMail(email: String): EmptyResult<DataError.Remote>

    suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote>

    suspend fun forgotPassword(email: String): EmptyResult<DataError.Remote>
}
