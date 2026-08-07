package com.kaanf.auth.domain.repository

import com.kaanf.auth.domain.model.LinkIdentityParams
import com.kaanf.auth.domain.model.RegisterParams
import com.kaanf.auth.domain.model.SignInMethods
import com.kaanf.auth.domain.model.SocialLoginParams
import com.kaanf.auth.domain.model.SocialProvider
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

    suspend fun getSignInMethods(): Result<SignInMethods, DataError.Remote>

    suspend fun linkIdentity(params: LinkIdentityParams): EmptyResult<DataError.Remote>

    suspend fun unlinkIdentity(provider: SocialProvider): EmptyResult<DataError.Remote>

    /**
     * Şifreyi değiştirir; [currentPassword] null ise sosyal girişle açılmış hesaba ilk şifreyi
     * belirler. Backend mevcut tüm refresh token'ları iptal ettiği için yerine yeni oturum döner.
     */
    suspend fun changePassword(
        currentPassword: String?,
        newPassword: String,
    ): Result<AuthInfo, DataError.Remote>

    suspend fun resendVerificationMail(email: String): EmptyResult<DataError.Remote>

    suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote>

    suspend fun forgotPassword(email: String): EmptyResult<DataError.Remote>
}
