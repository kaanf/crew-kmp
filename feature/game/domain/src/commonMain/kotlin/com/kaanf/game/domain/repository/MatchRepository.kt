package com.kaanf.game.domain.repository

import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result
import com.kaanf.game.domain.model.Match
import com.kaanf.game.domain.model.MatchInvite

interface MatchRepository {
    suspend fun getMyMatchQrToken(eventId: String): Result<String, DataError.Remote>

    suspend fun sendInvite(
        eventId: String, scannedMatchQrToken: String,
    ): Result<MatchInvite, DataError.Remote>

    /**
     * Gelen daveti kabul eder; başlamış maçı döner. Sunucu ayrıca iki tarafa da
     * MATCH_STARTED push'lar.
     */
    suspend fun acceptInvite(
        eventId: String, inviteId: String,
    ): Result<Match, DataError.Remote>

    /**
     * Gelen daveti reddeder. Sunucu daveti gönderene MATCH_INVITE_DECLINED push'lar.
     */
    suspend fun declineInvite(
        eventId: String, inviteId: String,
    ): EmptyResult<DataError.Remote>
}
