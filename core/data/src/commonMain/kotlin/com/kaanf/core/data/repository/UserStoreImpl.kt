package com.kaanf.core.data.repository

import com.kaanf.core.domain.model.user.User
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.repository.UserStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserStoreImpl(
    private val sessionStorage: SessionStorage,
) : UserStore {
    override fun observeCurrentUser(): Flow<User?> {
        return sessionStorage.observeAuthInfo().map { it?.user }
    }

    override suspend fun updateCurrentUser(user: User) {
        sessionStorage.update { current ->
            current?.copy(user = user)
        }
    }
}
