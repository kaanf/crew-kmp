package com.kaanf.core.domain.repository

import com.kaanf.core.domain.model.auth.AuthInfo
import kotlinx.coroutines.flow.Flow

interface SessionStorage {
    fun observeAuthInfo(): Flow<AuthInfo?>
    suspend fun set(info: AuthInfo?)
}
