package com.kaanf.game.domain.model

/**
 * Bir davet kabul edildiğinde ([com.kaanf.game.domain.repository.MatchRepository.acceptInvite])
 * sunucudan dönen başlamış maç. Aynı anda her iki oyuncuya soket üzerinden MATCH_STARTED
 * push'lanır (o tip henüz bağlı değil).
 */
data class Match(
    val matchId: String,
    val eventId: String,
    val opponentParticipantId: String,
    val opponentUserId: String,
    val opponentFullName: String,
)
