package com.kaanf.game.domain.model

/**
 * Sunucudaki maçın, çağıran kullanıcının perspektifinden ("ben"/"rakip") çözülmüş anlık
 * durumu. Soket reconnect sonrası faz, kopukken kaçırılan push'lara rağmen bu snapshot'tan
 * yeniden kurulur. Aktif maç yoksa repository null döner (sunucu 204).
 */
data class MatchSnapshot(
    val matchId: String,
    val eventId: String,
    val state: MatchSnapshotState,
    val me: SnapshotParticipant,
    val opponent: SnapshotParticipant,
    val isMeReady: Boolean,
    val isOpponentReady: Boolean,
    val myReportedWinnerUserId: String?,
    val opponentReportedWinnerUserId: String?,
    val winnerUserId: String?,
    /** Teklif/aktif görev; TaskOfferPending'den itibaren dolu (kaybeden katalogu görmez). */
    val task: GameTask?,
    val completed: Boolean,
)

data class SnapshotParticipant(
    val participantId: String,
    val userId: String,
    val fullName: String,
)

/** Sunucu [com.kaanf.crew] MatchState ile eşleşir; tanınmayan değer [Unknown]'a düşer. */
enum class MatchSnapshotState {
    ReadyWaiting,
    ResultPending,
    ResultConfirmation,
    Disputed,
    TaskPickPending,
    TaskOfferPending,
    TaskActive,
    TaskConfirmPending,
    Completed,
    Unknown,
}
