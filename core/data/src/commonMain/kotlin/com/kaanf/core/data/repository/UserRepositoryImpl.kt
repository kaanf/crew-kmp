package com.kaanf.core.data.repository

import com.kaanf.core.data.dto.GenericBooleanResponse
import com.kaanf.core.data.dto.UserSerializable
import com.kaanf.core.data.mappers.toDomain
import com.kaanf.core.data.mappers.toSerializable
import com.kaanf.core.data.networking.get
import com.kaanf.core.data.networking.put
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
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val httpClient: HttpClient,
    private val userStore: UserStore,
) : UserRepository {
    override fun observeCurrentUser(): Flow<User?> = userStore.observeCurrentUser()

    override suspend fun getUser(): Result<User, DataError.Remote> {
        return httpClient.get<UserSerializable>(route = "/user")
            .map { it.toDomain() }
            .onSuccess { userStore.updateCurrentUser(it) }
    }

    override suspend fun updateUser(user: User): EmptyResult<DataError.Remote> {
        return httpClient.put<UserSerializable, GenericBooleanResponse>(
            route = "/user",
            body = user.toSerializable()
        ).onSuccess {
            userStore.updateCurrentUser(user)
        }.asEmptyResult()
    }
}
