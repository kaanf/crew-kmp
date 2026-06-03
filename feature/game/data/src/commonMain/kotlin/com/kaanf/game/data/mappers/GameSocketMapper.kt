package com.kaanf.game.data.mappers

import com.kaanf.game.data.dto.GameStartedPayloadDto
import com.kaanf.game.data.dto.MatchInviteReceivedPayloadDto
import com.kaanf.game.data.dto.MatchInviteResolvedPayloadDto
import com.kaanf.game.data.dto.MatchStartedPayloadDto
import com.kaanf.game.data.dto.SocketEnvelopeDto
import com.kaanf.game.domain.model.GameSocketMessage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Soket zarfını tipli domain mesajına dönüştürür. Bilinmeyen tipler ya da eksik/bozuk
 * payload'lar [GameSocketMessage.Unknown] olarak döner; böylece tek bir bozuk frame akışı
 * düşürmez.
 */
fun SocketEnvelopeDto.toDomain(json: Json): GameSocketMessage = when (type) {
    "CONNECTED" -> GameSocketMessage.Connected

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

    "MATCH_INVITE_DECLINED" -> json.decodePayloadOrNull<MatchInviteResolvedPayloadDto>(payload)
        ?.let {
            GameSocketMessage.MatchInviteDeclined(
                inviteId = it.inviteId,
                eventId = it.eventId,
            )
        }
        ?: GameSocketMessage.Unknown(type)

    else -> GameSocketMessage.Unknown(type)
}

private inline fun <reified T> Json.decodePayloadOrNull(payload: JsonElement?): T? {
    if (payload == null) return null
    return runCatching { decodeFromJsonElement<T>(payload) }.getOrNull()
}
