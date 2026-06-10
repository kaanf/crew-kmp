package com.kaanf.game.data.mappers

import com.kaanf.game.data.dto.MatchDto
import com.kaanf.game.data.dto.MatchInviteDto
import com.kaanf.game.data.dto.MatchScoreboardDto
import com.kaanf.game.data.dto.MyParticipantDto
import com.kaanf.game.domain.model.Match
import com.kaanf.game.domain.model.MatchInvite
import com.kaanf.game.domain.model.MatchParticipant
import com.kaanf.game.domain.model.MatchScoreboard
import com.kaanf.game.domain.model.MatchScoreboardEntry

fun MatchInviteDto.toDomain(): MatchInvite = MatchInvite(
    inviteId = inviteId,
    eventId = eventId,
    toParticipantId = toParticipantId,
    toFullName = toFullName,
    toProfilePictureUrl = toProfilePictureUrl,
    expiresAt = expiresAt,
)

fun MyParticipantDto.toDomain(): MatchParticipant = MatchParticipant(
    id = id,
    userId = userId,
    matchQrToken = matchQrToken,
    score = score,
)

fun MatchDto.toDomain(): Match = Match(
    matchId = matchId,
    eventId = eventId,
    opponentParticipantId = opponentParticipantId,
    opponentUserId = opponentUserId,
    opponentFullName = opponentFullName,
)

fun MatchScoreboardDto.toDomain(): MatchScoreboard = MatchScoreboard(
    matchId = matchId,
    eventId = eventId,
    entries = entries.map { entry ->
        MatchScoreboardEntry(
            participantId = entry.participantId,
            userId = entry.userId,
            fullName = entry.fullName,
            isWinner = entry.role == "WINNER",
            points = entry.points,
        )
    },
)
