package com.kaanf.core.data.repository

import com.kaanf.core.domain.model.user.User
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.repository.UserStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserStoreImpl(
    private val sessionStorage: SessionStorage,
) : UserStore {
    override fun observeCurrentUser(): Flow<User?> {
        return sessionStorage.observeAuthInfo().map { it?.user }
    }

    override suspend fun updateCurrentUser(user: User) {
        val current = sessionStorage.observeAuthInfo().first() ?: return
        sessionStorage.set(current.copy(user = user))
    }
}
