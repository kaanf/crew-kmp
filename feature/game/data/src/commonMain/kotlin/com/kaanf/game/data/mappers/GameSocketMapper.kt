package com.kaanf.game.data.mappers

import com.kaanf.game.data.dto.ConnectedPayloadDto
import com.kaanf.game.data.dto.LobbyUserJoinedDto
import com.kaanf.game.data.dto.LobbyUserLeftDto
import com.kaanf.game.data.dto.GameStartedPayloadDto
import com.kaanf.game.data.dto.LobbyMemberDto
import com.kaanf.game.data.dto.MatchCancelledPayloadDto
import com.kaanf.game.data.dto.MatchDisputedPayloadDto
import com.kaanf.game.data.dto.MatchInviteReceivedPayloadDto
import com.kaanf.game.data.dto.MatchInviteResolvedPayloadDto
import com.kaanf.game.data.dto.MatchReadyCompletedPayloadDto
import com.kaanf.game.data.dto.MatchResultConfirmedPayloadDto
import com.kaanf.game.data.dto.MatchResultReportedPayloadDto
import com.kaanf.game.data.dto.MatchStartedPayloadDto
import com.kaanf.game.data.dto.SocketEnvelopeDto
import com.kaanf.game.data.dto.TaskFinishedPayloadDto
import com.kaanf.game.data.dto.TaskOfferedPayloadDto
import com.kaanf.game.data.dto.TaskRejectedPayloadDto
import com.kaanf.game.data.dto.TaskStartedPayloadDto
import com.kaanf.game.data.dto.ViewerStatsDto
import com.kaanf.game.domain.model.CurrentUserStats
import com.kaanf.game.domain.model.GameSocketMessage
import com.kaanf.game.domain.model.LobbyMember
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

private fun LobbyMemberDto.toDomain(): LobbyMember {
    return LobbyMember(
        userId = userId,
        fullName = fullName,
        profilePictureUrl = profilePictureUrl
    )
}

private fun ViewerStatsDto.toDomain(): CurrentUserStats {
    return CurrentUserStats(
        score = score,
        winCount = winCount,
        matchesCount = matchesCount,
    )
}

fun SocketEnvelopeDto.toDomain(json: Json): GameSocketMessage = when (type) {
    "CONNECTED" -> json.decodePayloadOrNull<ConnectedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.Connected(
                eventId = it.eventId,
                gameStartsAt = it.gameStartsAt,
                gameEndsAt = it.gameEndsAt,
                boldStartsAt = it.boldStartsAt,
                finalStartsAt = it.finalStartsAt,
                serverNow = it.serverNow,
                totalCount = it.totalCount,
                members = it.members.map { member ->
                    member.toDomain()
                },
                me = it.me?.toDomain(),
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "GAME_STARTED" -> json.decodePayloadOrNull<GameStartedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.GameStarted(
                eventId = it.eventId,
                gameStartsAt = it.gameStartsAt,
                serverNow = it.serverNow,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "MATCH_INVITE_RECEIVED" -> json.decodePayloadOrNull<MatchInviteReceivedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.MatchInviteReceived(
                inviteId = it.inviteId,
                eventId = it.eventId,
                fromParticipantId = it.fromParticipantId,
                fromUserId = it.fromUserId,
                fromFullName = it.fromFullName,
                fromProfilePictureUrl = it.fromProfilePictureUrl,
                expiresAt = it.expiresAt,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "MATCH_STARTED" -> json.decodePayloadOrNull<MatchStartedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.MatchStarted(
                matchId = it.matchId,
                eventId = it.eventId,
                opponentParticipantId = it.opponentParticipantId,
                opponentUserId = it.opponentUserId,
                opponentFullName = it.opponentFullName,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "MATCH_CANCELLED" -> json.decodePayloadOrNull<MatchCancelledPayloadDto>(payload)
        ?.let {
            GameSocketMessage.MatchCancelled(
                matchId = it.matchId,
                eventId = it.eventId,
                state = it.state,
                cancelledByUserId = it.cancelledByUserId,
                winnerUserId = it.winnerUserId,
                winnerTotalScore = it.winnerTotalScore,
                winnerPointsAwarded = it.winnerPointsAwarded,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "MATCH_READY_COMPLETED" -> json.decodePayloadOrNull<MatchReadyCompletedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.MatchReadyCompleted(
                matchId = it.matchId,
                eventId = it.eventId,
                state = it.state,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "MATCH_RESULT_REPORTED" -> json.decodePayloadOrNull<MatchResultReportedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.MatchResultReported(
                matchId = it.matchId,
                eventId = it.eventId,
                state = it.state,
                reporterUserId = it.reporterUserId,
                claimedWinnerUserId = it.claimedWinnerUserId,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "MATCH_RESULT_CONFIRMED" -> json.decodePayloadOrNull<MatchResultConfirmedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.MatchResultConfirmed(
                matchId = it.matchId,
                eventId = it.eventId,
                state = it.state,
                winnerUserId = it.winnerUserId,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "MATCH_DISPUTED" -> json.decodePayloadOrNull<MatchDisputedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.MatchDisputed(
                matchId = it.matchId,
                eventId = it.eventId,
                state = it.state,
                disputedByUserId = it.disputedByUserId,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "TASK_OFFERED" -> json.decodePayloadOrNull<TaskOfferedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.TaskOffered(
                matchId = it.matchId,
                eventId = it.eventId,
                state = it.state,
                offeredByUserId = it.offeredByUserId,
                taskId = it.taskId,
                taskTitle = it.taskTitle,
                taskPoints = it.taskPoints,
                taskRejectPoints = it.taskRejectPoints,
                taskCategories = it.taskCategories.map { category -> category.toTaskCategory() },
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "TASK_STARTED" -> json.decodePayloadOrNull<TaskStartedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.TaskStarted(
                matchId = it.matchId,
                eventId = it.eventId,
                state = it.state,
                taskId = it.taskId,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "TASK_FINISHED" -> json.decodePayloadOrNull<TaskFinishedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.TaskFinished(
                matchId = it.matchId,
                eventId = it.eventId,
                state = it.state,
                completed = it.completed,
                winnerUserId = it.winnerUserId,
                loserUserId = it.loserUserId,
                winnerPointsAwarded = it.winnerPointsAwarded,
                loserPointsAwarded = it.loserPointsAwarded,
                winnerTotalScore = it.winnerTotalScore,
                loserTotalScore = it.loserTotalScore,
                winnerWinCount = it.winnerWinCount,
                winnerMatchesCount = it.winnerMatchesCount,
                loserWinCount = it.loserWinCount,
                loserMatchesCount = it.loserMatchesCount,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "TASK_REJECTED" -> json.decodePayloadOrNull<TaskRejectedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.TaskRejected(
                matchId = it.matchId,
                eventId = it.eventId,
                state = it.state,
                rejectedByUserId = it.rejectedByUserId,
                rejectPoints = it.rejectPoints,
                rejectedByTotalScore = it.rejectedByTotalScore,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "MATCH_INVITE_DECLINED" -> json.decodePayloadOrNull<MatchInviteResolvedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.MatchInviteDeclined(
                inviteId = it.inviteId,
                eventId = it.eventId,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "MATCH_INVITE_EXPIRED" -> json.decodePayloadOrNull<MatchInviteResolvedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.MatchInviteExpired(
                inviteId = it.inviteId,
                eventId = it.eventId,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "LOBBY_USER_JOINED" -> json.decodePayloadOrNull<LobbyUserJoinedDto>(payload)
        ?.let {
            GameSocketMessage.LobbyUserJoined(
                userId = it.userId,
                totalCount = it.totalCount,
                fullName = it.fullName,
                profilePictureUrl = it.profilePictureUrl,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    "LOBBY_USER_LEFT" -> json.decodePayloadOrNull<LobbyUserLeftDto>(payload)
        ?.let {
            GameSocketMessage.LobbyUserLeft(
                userId = it.userId,
                totalCount = it.totalCount,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    else -> GameSocketMessage.Unknown(type)
}

private inline fun <reified T> Json.decodePayloadOrNull(payload: JsonElement?): T? {
    if (payload == null) return null
    return runCatching { decodeFromJsonElement<T>(payload) }.getOrNull()
}
