package com.kaanf.game.data.repository

import com.kaanf.core.data.networking.delete
import com.kaanf.core.data.networking.get
import com.kaanf.core.data.networking.getOrNull
import com.kaanf.core.data.networking.mapCatching
import com.kaanf.core.data.networking.post
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result
import com.kaanf.core.domain.util.asEmptyResult
import com.kaanf.core.domain.util.map
import com.kaanf.game.data.dto.ConfirmTaskRequest
import com.kaanf.game.data.dto.CreateMatchInviteRequest
import com.kaanf.game.data.dto.EventMemoryDto
import com.kaanf.game.data.dto.LeaderboardEntryDto
import com.kaanf.game.data.dto.MatchCancelDto
import com.kaanf.game.data.dto.MatchDto
import com.kaanf.game.data.dto.MatchFinishDto
import com.kaanf.game.data.dto.MatchHistoryEntryDto
import com.kaanf.game.data.dto.MatchInviteDto
import com.kaanf.game.data.dto.MatchReadyDto
import com.kaanf.game.data.dto.MatchResultDto
import com.kaanf.game.data.dto.MatchScoreboardDto
import com.kaanf.game.data.dto.MatchSnapshotDto
import com.kaanf.game.data.dto.MatchTaskOfferDto
import com.kaanf.game.data.dto.MatchTaskStateDto
import com.kaanf.game.data.dto.MyParticipantDto
import com.kaanf.game.data.dto.OfferTaskRequest
import com.kaanf.game.data.dto.QuestDto
import com.kaanf.game.data.dto.ReportResultRequest
import com.kaanf.game.data.dto.TaskDto
import com.kaanf.game.data.mappers.toDomain
import com.kaanf.game.domain.model.EventMemory
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.LeaderboardEntry
import com.kaanf.game.domain.model.MatchHistoryEntry
import com.kaanf.game.domain.model.MatchInvite
import com.kaanf.game.domain.model.MatchParticipant
import com.kaanf.game.domain.model.MatchScoreboard
import com.kaanf.game.domain.model.MatchSnapshot
import com.kaanf.game.domain.model.Quest
import com.kaanf.game.domain.repository.MatchRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

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

    override suspend fun getTasks(eventId: String): Result<List<GameTask>, DataError.Remote> {
        return httpClient.get<List<TaskDto>>(
            route = "/tasks",
            queryParams = mapOf("eventId" to eventId),
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

    override suspend fun getMatchSnapshot(
        eventId: String,
    ): Result<MatchSnapshot?, DataError.Remote> {
        // 204 (aktif maç yok) → null; ekran Idle'a uzlaşır.
        return httpClient.getOrNull<MatchSnapshotDto>(
            route = "/events/$eventId/matches/current",
        ).map { it?.toDomain() }
    }

    override suspend fun getLeaderboard(
        eventId: String,
    ): Result<List<LeaderboardEntry>, DataError.Remote> {
        return httpClient.get<List<LeaderboardEntryDto>>(
            route = "/events/$eventId/leaderboard",
        ).map { entries -> entries.map { it.toDomain() } }
    }

    override suspend fun getMatchHistory(
        eventId: String, page: Int, size: Int,
    ): Result<List<MatchHistoryEntry>, DataError.Remote> {
        return httpClient.get<List<MatchHistoryEntryDto>>(
            route = "/events/$eventId/matches/history",
            queryParams = mapOf("page" to page, "size" to size),
        ).map { entries -> entries.map { it.toDomain() } }
    }

    override suspend fun cancelMatch(
        eventId: String, matchId: String,
    ): EmptyResult<DataError.Remote> {
        // Çağırana giden push yok; rakibe MATCH_CANCELLED gider. Yanıt gövdesi okunup atılır.
        return httpClient.post<MatchCancelDto>(
            route = "/events/$eventId/matches/$matchId/cancel",
        ).asEmptyResult()
    }

    override suspend fun finishMatch(
        eventId: String, matchId: String,
    ): EmptyResult<DataError.Remote> {
        // Soket push'u yoktur; yanıt gövdesi okunup atılır, geçiş çağrı başarısıyla sürülür.
        return httpClient.post<MatchFinishDto>(
            route = "/events/$eventId/matches/$matchId/finish",
        ).asEmptyResult()
    }

    override suspend fun getMemories(
        eventId: String, page: Int, size: Int,
    ): Result<List<EventMemory>, DataError.Remote> {
        return httpClient.get<List<EventMemoryDto>>(
            route = "/events/$eventId/memories",
            queryParams = mapOf("page" to page, "size" to size),
        ).mapCatching { memories -> memories.map { it.toDomain() } }
    }

    override suspend fun uploadMemory(
        eventId: String, imageBytes: ByteArray, mimeType: String,
    ): Result<EventMemory, DataError.Remote> {
        // Ham kamera baytları olduğu gibi gönderilir; boyut optimizasyonunu sunucu yapar
        // (1600px'e küçültüp JPEG'e çevirir). setBody, OutgoingContent'i serialize etmeden geçirir.
        return httpClient.post<MultiPartFormDataContent, EventMemoryDto>(
            route = "/events/$eventId/memories",
            body = MultiPartFormDataContent(
                formData {
                    append(
                        key = "file",
                        value = imageBytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, mimeType)
                            append(HttpHeaders.ContentDisposition, "filename=\"memory.jpg\"")
                        },
                    )
                },
            ),
        ).mapCatching { it.toDomain() }
    }

    override suspend fun deleteMemory(
        eventId: String, memoryId: String,
    ): EmptyResult<DataError.Remote> {
        // Sunucu 204 (gövdesiz) döner.
        return httpClient.delete<Unit>(
            route = "/events/$eventId/memories/$memoryId",
        )
    }

    override suspend fun getQuests(
        eventId: String,
    ): Result<List<Quest>, DataError.Remote> {
        return httpClient.get<List<QuestDto>>(
            route = "/events/$eventId/quests",
        ).map { quests -> quests.map { it.toDomain() } }
    }

    override suspend fun claimQuest(
        eventId: String, questKey: String,
    ): Result<Quest, DataError.Remote> {
        return httpClient.post<QuestDto>(
            route = "/events/$eventId/quests/$questKey/claim",
        ).map { it.toDomain() }
    }
}
