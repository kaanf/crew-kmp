package com.kaanf.core.domain.repository

import com.kaanf.core.domain.model.ProfilePictureUploadUrls
import com.kaanf.core.domain.model.user.User
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeCurrentUser(): Flow<User?>

    suspend fun getUser(): Result<User, DataError.Remote>

    suspend fun updateUser(user: User): EmptyResult<DataError.Remote>

    suspend fun uploadProfilePicture(
        imageBytes: ByteArray, mimeType: String
    ): EmptyResult<DataError.Remote>

    suspend fun deleteProfilePicture(): EmptyResult<DataError.Remote>

    suspend fun registerDeviceToken(token: String, platform: String): EmptyResult<DataError.Remote>
}
