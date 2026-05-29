package com.kaanf.core.domain.repository

import com.kaanf.core.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserStore {
    fun observeCurrentUser(): Flow<User?>
    suspend fun updateCurrentUser(user: User)
}
