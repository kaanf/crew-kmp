package com.kaanf.game.data.repository

import com.kaanf.core.data.networking.get
import com.kaanf.core.data.networking.post
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result
import com.kaanf.core.domain.util.asEmptyResult
import com.kaanf.core.domain.util.map
import com.kaanf.game.data.dto.ConfirmTaskRequest
import com.kaanf.game.data.dto.CreateMatchInviteRequest
import com.kaanf.game.data.dto.MatchDto
import com.kaanf.game.data.dto.MatchFinishDto
import com.kaanf.game.data.dto.MatchInviteDto
import com.kaanf.game.data.dto.MatchReadyDto
import com.kaanf.game.data.dto.MatchResultDto
import com.kaanf.game.data.dto.MatchScoreboardDto
import com.kaanf.game.data.dto.MatchTaskOfferDto
import com.kaanf.game.data.dto.MatchTaskStateDto
import com.kaanf.game.data.dto.MyParticipantDto
import com.kaanf.game.data.dto.OfferTaskRequest
import com.kaanf.game.data.dto.ReportResultRequest
import com.kaanf.game.data.dto.TaskDto
import com.kaanf.game.data.mappers.toDomain
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.MatchInvite
import com.kaanf.game.domain.model.MatchParticipant
import com.kaanf.game.domain.model.MatchScoreboard
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

    override suspend fun getMyParticipant(
        eventId: String,
    ): Result<MatchParticipant, DataError.Remote> {
        return httpClient.get<MyParticipantDto>(
            route = "/events/$eventId/my-participant",
        ).map { it.toDomain() }
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
    ): EmptyResult<DataError.Remote> {
        // Yanıt gövdesi (Match) okunur ama geçiş (MATCH_STARTED) soketle sürüldüğü için atılır.
        return httpClient.post<MatchDto>(
            route = "/events/$eventId/invite/$inviteId/accept",
        ).asEmptyResult()
    }

    override suspend fun declineInvite(
        eventId: String, inviteId: String,
    ): EmptyResult<DataError.Remote> {
        // Sunucu 204 (gövdesiz) döner; Unit body Ktor'un default transformer'ı ile çözülür.
        return httpClient.post<Unit>(
            route = "/events/$eventId/invite/$inviteId/decline",
        )
    }

    override suspend fun markReady(
        eventId: String, matchId: String,
    ): EmptyResult<DataError.Remote> {
        // Yanıt gövdesi (bothReady vb.) okunur ama geçiş soketle sürüldüğü için atılır.
        return httpClient.post<MatchReadyDto>(
            route = "/events/$eventId/matches/$matchId/ready",
        ).asEmptyResult()
    }

    override suspend fun reportResult(
        eventId: String, matchId: String, won: Boolean,
    ): EmptyResult<DataError.Remote> {
        // Yanıt gövdesi (winnerUserId vb.) okunur ama geçiş soketle sürüldüğü için atılır.
        return httpClient.post<ReportResultRequest, MatchResultDto>(
            route = "/events/$eventId/matches/$matchId/result",
            body = ReportResultRequest(won = won),
        ).asEmptyResult()
    }

    override suspend fun getTasks(): Result<List<GameTask>, DataError.Remote> {
        return httpClient.get<List<TaskDto>>(
            route = "/tasks",
        ).map { tasks -> tasks.map { it.toDomain() } }
    }

    override suspend fun offerTask(
        eventId: String, matchId: String, taskId: String,
    ): EmptyResult<DataError.Remote> {
        // Yanıt gövdesi (taskId vb.) okunur ama kaybedenin geçişi soketle sürüldüğü için atılır.
        return httpClient.post<OfferTaskRequest, MatchTaskOfferDto>(
            route = "/events/$eventId/matches/$matchId/task/offer",
            body = OfferTaskRequest(taskId = taskId),
        ).asEmptyResult()
    }

    override suspend fun acceptTask(
        eventId: String, matchId: String,
    ): EmptyResult<DataError.Remote> {
        // Geçiş (TASK_STARTED) soketle sürüldüğü için yanıt gövdesi okunup atılır.
        return httpClient.post<MatchTaskStateDto>(
            route = "/events/$eventId/matches/$matchId/task/accept",
        ).asEmptyResult()
    }

    override suspend fun rejectTask(
        eventId: String, matchId: String,
    ): EmptyResult<DataError.Remote> {
        return httpClient.post<MatchTaskStateDto>(
            route = "/events/$eventId/matches/$matchId/task/reject",
        ).asEmptyResult()
    }

    override suspend fun confirmTask(
        eventId: String, matchId: String, completed: Boolean,
    ): EmptyResult<DataError.Remote> {
        // Geçiş (TASK_FINISHED) soketle sürüldüğü için yanıt gövdesi okunup atılır.
        return httpClient.post<ConfirmTaskRequest, MatchTaskStateDto>(
            route = "/events/$eventId/matches/$matchId/task/confirm",
            body = ConfirmTaskRequest(completed = completed),
        ).asEmptyResult()
    }

    override suspend fun getScoreboard(
        eventId: String, matchId: String,
    ): Result<MatchScoreboard, DataError.Remote> {
        return httpClient.get<MatchScoreboardDto>(
            route = "/events/$eventId/matches/$matchId/scoreboard",
        ).map { it.toDomain() }
    }

    override suspend fun finishMatch(
        eventId: String, matchId: String,
    ): EmptyResult<DataError.Remote> {
        // Soket push'u yoktur; yanıt gövdesi okunup atılır, geçiş çağrı başarısıyla sürülür.
        return httpClient.post<MatchFinishDto>(
            route = "/events/$eventId/matches/$matchId/finish",
        ).asEmptyResult()
    }
}
