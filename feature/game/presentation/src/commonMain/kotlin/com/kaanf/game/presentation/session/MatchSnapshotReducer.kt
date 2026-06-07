package com.kaanf.game.presentation.session

import com.kaanf.game.domain.model.MatchSnapshot
import com.kaanf.game.domain.model.MatchSnapshotState

/**
 * Sunucu snapshot'ını ekran fazına çevirir. Snapshot zaten "ben"/"rakip" perspektifinde
 * çözülmüş olduğundan kazanan/kaybeden ayrımı [MatchSnapshot.me] üzerinden yapılır;
 * ViewModel'e ayrı bir `currentUserId` gerekmez.
 *
 * WinnerPicks ve Scoreboard fazları `isLoading = true` döner: kazananın görev listesi
 * ve puan tablosu snapshot'ta taşınmaz, ViewModel reconcile sonrası ilgili load'u tetikler.
 */
internal fun MatchSnapshot.toMatchPhase(): MatchPhase {
    val amIWinner = winnerUserId != null && winnerUserId == me.userId
    return when (state) {
        MatchSnapshotState.ReadyWaiting -> MatchPhase.RpsReady()

        MatchSnapshotState.ResultPending,
        MatchSnapshotState.ResultConfirmation,
        -> MatchPhase.WhoWon(
            myResultClaimWon = myReportedWinnerUserId?.let { it == me.userId },
            opponentClaimedWinnerUserId = opponentReportedWinnerUserId,
        )

        MatchSnapshotState.TaskPickPending ->
            if (amIWinner) MatchPhase.WinnerPicks(isLoading = true) else MatchPhase.LoserWaits

        MatchSnapshotState.TaskOfferPending ->
            if (amIWinner) {
                MatchPhase.WinnerPicks(isLoading = true, isOffering = true, selectedTaskId = task?.id)
            } else {
                task?.let { MatchPhase.LoserAccepts(task = it) } ?: MatchPhase.LoserWaits
            }

        MatchSnapshotState.TaskActive,
        MatchSnapshotState.TaskConfirmPending,
        -> if (amIWinner) MatchPhase.WinnerConfirms() else MatchPhase.TaskActive

        MatchSnapshotState.Completed -> MatchPhase.Scoreboard(completed = completed)

        // Tanınmayan/terminal durum: maç yok say, Idle'a düş.
        MatchSnapshotState.Unknown -> MatchPhase.Idle
    }
}

/** [MatchSnapshot.winnerUserId] çözülmüşse "ben kazandım mı" bilgisi, değilse null. */
internal fun MatchSnapshot.amIWinnerOrNull(): Boolean? =
    winnerUserId?.let { it == me.userId }
