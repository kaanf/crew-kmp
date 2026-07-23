package com.kaanf.game.data.mappers

import com.kaanf.game.data.dto.MatchParticipantRefDto
import com.kaanf.game.data.dto.MatchSnapshotDto
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.MatchSnapshot
import com.kaanf.game.domain.model.MatchSnapshotState
import com.kaanf.game.domain.model.SnapshotParticipant

fun MatchSnapshotDto.toDomain(): MatchSnapshot = MatchSnapshot(
    matchId = matchId,
    eventId = eventId,
    state = state.toMatchSnapshotState(),
    me = me.toDomain(),
    opponent = opponent.toDomain(),
    isMeReady = isMeReady,
    isOpponentReady = isOpponentReady,
    myReportedWinnerUserId = myReportedWinnerUserId,
    opponentReportedWinnerUserId = opponentReportedWinnerUserId,
    winnerUserId = winnerUserId,
    task = task?.let {
        GameTask(
            id = it.taskId,
            title = it.title,
            points = it.points,
            rejectPoints = it.rejectPoints,
            categories = it.categories.map { category -> category.toTaskCategory() },
        )
    },
    completed = completed,
)

private fun MatchParticipantRefDto.toDomain(): SnapshotParticipant = SnapshotParticipant(
    participantId = participantId,
    userId = userId,
    fullName = fullName,
)

private fun String.toMatchSnapshotState(): MatchSnapshotState = when (this) {
    "ReadyWaiting" -> MatchSnapshotState.ReadyWaiting
    "ResultPending" -> MatchSnapshotState.ResultPending
    "ResultConfirmation" -> MatchSnapshotState.ResultConfirmation
    "Disputed" -> MatchSnapshotState.Disputed
    "TaskPickPending" -> MatchSnapshotState.TaskPickPending
    "TaskOfferPending" -> MatchSnapshotState.TaskOfferPending
    "TaskActive" -> MatchSnapshotState.TaskActive
    "TaskConfirmPending" -> MatchSnapshotState.TaskConfirmPending
    "Completed" -> MatchSnapshotState.Completed
    else -> MatchSnapshotState.Unknown
}
