package com.kaanf.game.domain.model

sealed interface GameSocketMessage {
    data class Connected(
        val eventId: String,
        val doorsAt: String,
        val totalCount: Int,
        val members: List<LobbyMember>
    ) : GameSocketMessage

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

    data class MatchInviteDeclined(
        val inviteId: String,
        val eventId: String,
    ) : GameSocketMessage

    data class MatchInviteExpired(
        val inviteId: String,
        val eventId: String,
    ) : GameSocketMessage

    data class MatchStarted(
        val matchId: String,
        val eventId: String,
        val opponentParticipantId: String,
        val opponentUserId: String,
        val opponentFullName: String,
    ) : GameSocketMessage

    data class MatchCancelled(
        val matchId: String,
        val eventId: String,
        val state: String,
        val cancelledByUserId: String,
    ) : GameSocketMessage

    data class MatchReadyCompleted(
        val matchId: String,
        val eventId: String,
        val state: String,
    ) : GameSocketMessage

    data class MatchResultReported(
        val matchId: String,
        val eventId: String,
        val state: String,
        val reporterUserId: String,
        val claimedWinnerUserId: String,
    ) : GameSocketMessage

    data class MatchResultConfirmed(
        val matchId: String,
        val eventId: String,
        val state: String,
        val winnerUserId: String,
    ) : GameSocketMessage

    data class TaskOffered(
        val matchId: String,
        val eventId: String,
        val state: String,
        val offeredByUserId: String,
        val taskId: String,
        val taskTitle: String,
        val taskPoints: Int,
        val taskCategories: List<TaskCategory>,
    ) : GameSocketMessage

    data class TaskStarted(
        val matchId: String,
        val eventId: String,
        val state: String,
        val taskId: String,
    ) : GameSocketMessage

    data class TaskRejected(
        val matchId: String,
        val eventId: String,
        val state: String,
        val rejectedByUserId: String,
    ) : GameSocketMessage

    data class TaskFinished(
        val matchId: String,
        val eventId: String,
        val state: String,
        val completed: Boolean,
        val winnerUserId: String,
        val loserUserId: String,
        val winnerPointsAwarded: Int,
        val loserPointsAwarded: Int,
        val winnerTotalScore: Int,
        val loserTotalScore: Int,
    ) : GameSocketMessage

    data class LobbyUserJoined(
        val userId: String,
        val totalCount: Int,
        val fullName: String?,
        val profilePictureUrl: String?,
    ) : GameSocketMessage

    data class LobbyUserLeft(
        val userId: String,
        val totalCount: Int,
    ) : GameSocketMessage

    data class Unknown(val type: String) : GameSocketMessage
}
