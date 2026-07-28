package com.kaanf.core.data.repository

import com.kaanf.core.data.dto.ConfirmProfilePictureRequest
import com.kaanf.core.data.dto.ProfilePictureUploadUrlsResponse
import com.kaanf.core.data.dto.RegisterDeviceTokenRequest
import com.kaanf.core.data.dto.UpdateUserRequest
import com.kaanf.core.data.dto.UserSerializable
import com.kaanf.core.data.mappers.toDomain
import com.kaanf.core.data.mappers.toUpdateUserRequest
import com.kaanf.core.data.networking.delete
import com.kaanf.core.data.networking.get
import com.kaanf.core.data.networking.patch
import com.kaanf.core.data.networking.post
import com.kaanf.core.data.networking.put
import com.kaanf.core.data.networking.safeCall
import com.kaanf.core.domain.model.ProfilePictureUploadUrls
import com.kaanf.core.domain.model.user.User
import com.kaanf.core.domain.repository.UserRepository
import com.kaanf.core.domain.repository.UserStore
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result
import com.kaanf.core.domain.util.asEmptyResult
import com.kaanf.core.domain.util.map
import com.kaanf.core.domain.util.onSuccess
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepositoryImpl(
    private val httpClient: HttpClient,
    private val userStore: UserStore,
    ) : UserRepository {
    override fun observeCurrentUser(): Flow<User?> = userStore.observeCurrentUser()

    override suspend fun getUser(): Result<User, DataError.Remote> {
        return httpClient.get<UserSerializable>(route = "/profile")
            .map { it.toDomain() }
            .onSuccess { userStore.updateCurrentUser(it) }
    }

    override suspend fun updateUser(user: User): EmptyResult<DataError.Remote> {
        // PATCH /profile is a partial update: only the non-null fields are applied server-side.
        return httpClient.patch<UpdateUserRequest, UserSerializable>(
            route = "/profile",
            body = user.toUpdateUserRequest()
        ).onSuccess { updated ->
            userStore.updateCurrentUser(updated.toDomain())
        }.asEmptyResult()
    }

    override suspend fun deleteProfilePicture(): EmptyResult<DataError.Remote> {
        return httpClient.delete<Unit>(route = "/profile/profile-picture")
            .onSuccess {
                val currentUser = userStore.observeCurrentUser().first() ?: return@onSuccess
                userStore.updateCurrentUser(
                    currentUser.copy(profilePictureUrl = null)
                )
            }
            .asEmptyResult()
    }

    override suspend fun uploadProfilePicture(
        imageBytes: ByteArray,
        mimeType: String,
    ): EmptyResult<DataError.Remote> {
        val result = getProfilePictureUploadUrl(mimeType)

        if(result is Result.Failure) {
            return result
        }

        val uploadUrls = (result as Result.Success).data
        val uploadResult = uploadImageToSignedUrl(
            uploadUrl = uploadUrls.uploadUrl,
            imageBytes = imageBytes,
            headers = uploadUrls.headers
        )

        if(uploadResult is Result.Failure) {
            return uploadResult
        }

        return confirmProfilePictureUpload(uploadUrls.publicUrl)
            .onSuccess {
                val currentUser = userStore.observeCurrentUser().first() ?: return@onSuccess
                userStore.updateCurrentUser(
                    currentUser.copy(profilePictureUrl = uploadUrls.publicUrl)
                )
            }
    }

    override suspend fun registerDeviceToken(token: String, platform: String): EmptyResult<DataError.Remote> {
        return httpClient.put<RegisterDeviceTokenRequest, Unit>(
            route = "/profile/device-token",
            body = RegisterDeviceTokenRequest(token = token, platform = platform)
        )
    }

    private suspend fun getProfilePictureUploadUrl(mimeType: String): Result<ProfilePictureUploadUrls, DataError.Remote> {
        return httpClient.post<Unit, ProfilePictureUploadUrlsResponse>(
            route = "/profile/profile-picture-upload",
            queryParams = mapOf(
                "mimeType" to mimeType
            ),
            body = Unit
        ).map { it.toDomain() }
    }

    private suspend fun uploadImageToSignedUrl(
        uploadUrl: String,
        imageBytes: ByteArray,
        headers: Map<String, String>
    ): EmptyResult<DataError.Remote> {
        return safeCall {
            httpClient.put {
                url(uploadUrl)
                headers.forEach { (key, value) ->
                    header(key, value)
                }
                setBody(imageBytes)
            }
        }
    }

    private suspend fun confirmProfilePictureUpload(publicUrl: String): EmptyResult<DataError.Remote> {
        return httpClient.post<ConfirmProfilePictureRequest, Unit>(
            route = "/profile/confirm-profile-picture",
            body = ConfirmProfilePictureRequest(publicUrl)
        )
    }
}
