package com.kaanf.core.domain.repository

/**
 * Auth session lifecycle actions that act on the locally stored session. Lives in core (next to
 * [SessionStorage] and the refresh flow) so any feature can end the session without depending on
 * the auth feature module.
 */
interface AuthSessionRepository {
    /**
     * Revokes the refresh token server-side (best-effort) and clears the local session. The local
     * session is always cleared, even if the network call fails, so sign out can never get stuck.
     */
    suspend fun logout()
}
