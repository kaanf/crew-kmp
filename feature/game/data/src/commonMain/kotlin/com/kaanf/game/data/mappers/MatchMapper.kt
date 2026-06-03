package com.kaanf.game.data.mappers

import com.kaanf.game.data.dto.MatchDto
import com.kaanf.game.data.dto.MatchInviteDto
import com.kaanf.game.domain.model.Match
import com.kaanf.game.domain.model.MatchInvite

fun MatchInviteDto.toDomain(): MatchInvite = MatchInvite(
    inviteId = inviteId,
    eventId = eventId,
    toParticipantId = toParticipantId,
    toFullName = toFullName,
    expiresAt = expiresAt,
)

fun MatchDto.toDomain(): Match = Match(
    matchId = matchId,
    eventId = eventId,
    opponentParticipantId = opponentParticipantId,
    opponentUserId = opponentUserId,
    opponentFullName = opponentFullName,
)
