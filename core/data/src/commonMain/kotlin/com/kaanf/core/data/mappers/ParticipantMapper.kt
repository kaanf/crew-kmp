package com.kaanf.core.data.mappers

import com.kaanf.core.data.dto.CheckInResultDto
import com.kaanf.core.domain.model.participant.CheckInResult

fun CheckInResultDto.toDomain(): CheckInResult {
    return CheckInResult(
        participantId = participantId,
        eventId = eventId,
        userId = userId,
        fullName = fullName,
        attendanceState = attendanceState,
    )
}
