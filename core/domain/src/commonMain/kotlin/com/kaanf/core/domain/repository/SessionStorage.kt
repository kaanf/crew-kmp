package com.kaanf.core.domain.repository

import com.kaanf.core.domain.model.auth.AuthInfo
import kotlinx.coroutines.flow.Flow

/** A versioned local session; a new login/logout invalidates outstanding requests. */
data class SessionSnapshot(val authInfo: AuthInfo?, val generation: Long)

interface SessionStorage {
    fun observeAuthInfo(): Flow<AuthInfo?>
    suspend fun snapshot(): SessionSnapshot
    suspend fun set(info: AuthInfo?)
    suspend fun update(transform: (AuthInfo?) -> AuthInfo?)

    /** Clears the same login, including any token rotation completed since the snapshot. */
    suspend fun clearSession(expected: SessionSnapshot): Boolean

    /** Changes only the session captured before an asynchronous operation started. */
    suspend fun compareAndSet(expected: SessionSnapshot, info: AuthInfo?): Boolean
}
