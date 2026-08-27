package com.kaanf.game.domain.model

sealed interface GameSocketMessage {
    data class Connected(
        val eventId: String,
        val gameStartsAt: String,
        val gameEndsAt: String,
        /** Oyun içi phase sınırları; serverNow ile offset düzeltilerek lokal takip edilir. */
        val boldStartsAt: String?,
        val finalStartsAt: String?,
        val serverNow: String?,
        val totalCount: Int,
        val members: List<LobbyMember>,
        /** Bağlanan kullanıcının kendi app bar istatistikleri; katılımcı değilse null. */
        val me: CurrentUserStats?,
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
        val fromProfilePictureUrl: String?,
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
        val winnerUserId: String?,
        val winnerTotalScore: Int?,
        val winnerPointsAwarded: Int,
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

    data class MatchDisputed(
        val matchId: String,
        val eventId: String,
        val state: String,
        val disputedByUserId: String,
    ) : GameSocketMessage

    data class TaskOffered(
        val matchId: String,
        val eventId: String,
        val state: String,
        val offeredByUserId: String,
        val taskId: String,
        val taskTitle: String,
        val taskPoints: Int,
        val taskRejectPoints: Int,
        val taskCategory: TaskCategory,
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
        val rejectPoints: Int,
        val rejectedByTotalScore: Int?,
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
        val winnerWinCount: Int,
        val winnerMatchesCount: Int,
        val loserWinCount: Int,
        val loserMatchesCount: Int,
    ) : GameSocketMessage

    /**
     * Adres defterinde yeni bir kişi açıldı. Puan burada verilmez; [pointsAwarded]
     * pasaporttan claim edilebilecek tutardır (host daha değerli).
     */
    data class FirstMeeting(
        val eventId: String,
        val userId: String,
        val fullName: String,
        val profilePictureUrl: String?,
        /** Sunucu lakabı (şimdilik yalnız host 👑). Null = sıradan katılımcı. */
        val title: AddressBookTitle?,
        /** Otomatik verilmez: pasaporttan claim edilebilecek tutar (host daha değerli). */
        val pointsAwarded: Int,
        val totalScore: Int,
    ) : GameSocketMessage

    data class LobbyUserJoined(
        val userId: String,
        val totalCount: Int,
        val fullName: String?,
        val profilePictureUrl: String?,
    ) : GameSocketMessage

    /**
     * Mekân duyurusu (ör. barda indirimli içki). [durationSeconds] doluysa duyuru o süre
     * boyunca aktiftir; backend süresi dolmamış duyuruyu her (yeniden) bağlanışta kalan
     * süreyle tekrar yollar, bu yüzden client'ın kalıcı saklamasına gerek yok.
     */
    data class Announcement(
        val eventId: String,
        val title: String,
        val body: String,
        val durationSeconds: Int?,
        /** Doluysa chip tıklanabilir olur ve kokteyl sheet'ini açar. */
        val cocktail: AnnouncementCocktail?,
    ) : GameSocketMessage

    data class LobbyUserLeft(
        val userId: String,
        val totalCount: Int,
    ) : GameSocketMessage

    data class Unknown(val type: String) : GameSocketMessage
}
