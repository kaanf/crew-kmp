package com.kaanf.core.domain.model.participant

import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.model.user.UserId

data class CheckInResult(
    val participantId: EventParticipantId,
    val eventId: EventId,
    val userId: UserId,
    val fullName: String,
    val attendanceState: String,
)
