package com.kaanf.game.data.repository

import com.kaanf.core.data.networking.get
import com.kaanf.core.data.networking.post
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result
import com.kaanf.core.domain.util.map
import com.kaanf.game.data.dto.CreateMatchInviteRequest
import com.kaanf.game.data.dto.MatchDto
import com.kaanf.game.data.dto.MatchInviteDto
import com.kaanf.game.data.dto.MyParticipantDto
import com.kaanf.game.data.mappers.toDomain
import com.kaanf.game.domain.model.Match
import com.kaanf.game.domain.model.MatchInvite
import com.kaanf.game.domain.repository.MatchRepository
import io.ktor.client.HttpClient

class MatchRepositoryImpl(
    private val httpClient: HttpClient,
) : MatchRepository {
    override suspend fun getMyMatchQrToken(eventId: String): Result<String, DataError.Remote> {
        return httpClient.get<MyParticipantDto>(
            route = "/events/$eventId/my-participant",
        ).map { it.matchQrToken }
    }

    override suspend fun sendInvite(
        eventId: String, scannedMatchQrToken: String,
    ): Result<MatchInvite, DataError.Remote> {
        return httpClient.post<CreateMatchInviteRequest, MatchInviteDto>(
            route = "/events/$eventId/invite",
            body = CreateMatchInviteRequest(scannedMatchQrToken = scannedMatchQrToken),
        ).map { it.toDomain() }
    }

    override suspend fun acceptInvite(
        eventId: String, inviteId: String,
    ): Result<Match, DataError.Remote> {
        return httpClient.post<MatchDto>(
            route = "/events/$eventId/invite/$inviteId/accept",
        ).map { it.toDomain() }
    }

    override suspend fun declineInvite(
        eventId: String, inviteId: String,
    ): EmptyResult<DataError.Remote> {
        // Sunucu 204 (gövdesiz) döner; Unit body Ktor'un default transformer'ı ile çözülür.
        return httpClient.post<Unit>(
            route = "/events/$eventId/invite/$inviteId/decline",
        )
    }
}
