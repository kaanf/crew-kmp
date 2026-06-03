package com.kaanf.game.domain.model

sealed interface GameSocketMessage {
    data object Connected : GameSocketMessage

    data class GameStarted(
        val eventId: String,
        val gameStartsAt: String,
        val serverNow: String,
    ) : GameSocketMessage

    data class MatchInviteReceived(
        val inviteId: String,
        val eventId: String,
        val fromParticipantId: String,
        val fromUserId: String,
        val fromFullName: String,
        val expiresAt: String,
    ) : GameSocketMessage

    /**
     * Davet kabul edildiğinde maçın iki tarafına da gönderilir (type = MATCH_STARTED).
     * Hem kabul eden hem davet eden bu mesajla GameRpsReady ekranına geçer.
     */
    data class MatchStarted(
        val matchId: String,
        val eventId: String,
        val opponentParticipantId: String,
        val opponentUserId: String,
        val opponentFullName: String,
    ) : GameSocketMessage

    /**
     * Davet reddedildiğinde yalnızca daveti gönderene iletilir (type = MATCH_INVITE_DECLINED).
     */
    data class MatchInviteDeclined(
        val inviteId: String,
        val eventId: String,
    ) : GameSocketMessage

    /** Henüz bağlanmamış sunucu mesaj tipleri (sonra ele alınacak). */
    data class Unknown(val type: String) : GameSocketMessage
}
