package com.kaanf.game.presentation.session

import androidx.compose.runtime.Immutable
import com.kaanf.game.domain.model.GameConnectionState
import com.kaanf.game.domain.model.GameSocketMessage
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.MatchScoreboardEntry

@Immutable
data class MatchSessionState(
    val phase: MatchPhase = MatchPhase.Idle,
    val connectionState: GameConnectionState = GameConnectionState.Connecting,
    val matchQrToken: String? = null,
    val currentUserId: String? = null,
    val matchId: String? = null,
    val opponentFullName: String? = null,
    val amIWinner: Boolean? = null,
    val activeTask: GameTask? = null,
    val incomingInvite: GameSocketMessage.MatchInviteReceived? = null,
    val isRespondingToInvite: Boolean = false,
    val showMatchRequestSheet: Boolean = false,
    val isSendingInvite: Boolean = false,
    val showOutgoingInviteSheet: Boolean = false,
    val outgoingOpponentName: String? = null,
    val showExitConfirmDialog: Boolean = false,
    val errorMessage: String? = null,
) {
    val isConnected: Boolean get() = connectionState is GameConnectionState.Connected

    val formattedOpponentName: String
        get() = opponentFullName?.trim()?.substringBefore(" ").orEmpty()
}

sealed interface MatchPhase {
    val key: String

    data object Idle : MatchPhase {
        override val key = "idle"
    }

    data class RpsReady(
        val isMarkingReady: Boolean = false,
    ) : MatchPhase {
        override val key = "rps_ready"
    }

    data class WhoWon(
        val isReporting: Boolean = false,
        val myResultClaimWon: Boolean? = null,
        val opponentClaimedWinnerUserId: String? = null,
    ) : MatchPhase {
        override val key = "who_won"

        fun opponentClaimedMeWon(currentUserId: String?): Boolean? =
            opponentClaimedWinnerUserId?.let { it == currentUserId }
    }

    data class WinnerPicks(
        val isLoading: Boolean = true,
        val tasks: List<GameTask> = emptyList(),
        val selectedTaskId: String? = null,
        val isOffering: Boolean = false,
    ) : MatchPhase {
        override val key = "winner_picks"
    }

    data object LoserWaits : MatchPhase {
        override val key = "loser_waits"
    }

    data class LoserAccepts(
        val task: GameTask,
        val isResponding: Boolean = false,
    ) : MatchPhase {
        override val key = "loser_accepts"
    }

    data object TaskActive : MatchPhase {
        override val key = "task_active"
    }

    data class WinnerConfirms(
        val isConfirming: Boolean = false,
    ) : MatchPhase {
        override val key = "winner_confirms"
    }

    data class Scoreboard(
        val completed: Boolean,
        val isLoading: Boolean = true,
        val entries: List<MatchScoreboardEntry> = emptyList(),
        val isFinishing: Boolean = false,
    ) : MatchPhase {
        override val key = "scoreboard"
    }
}
