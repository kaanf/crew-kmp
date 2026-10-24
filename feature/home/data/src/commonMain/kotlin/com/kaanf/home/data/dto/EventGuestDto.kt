package com.kaanf.home.data.dto

import com.kaanf.home.domain.model.EventGuest
import kotlinx.serialization.Serializable

/** Backend'in `/events/{id}/participants` cevabı; yalnız guest list'in kullandığı alanlar. */
@Serializable
data class EventGuestDto(
    val userId: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val attendanceState: String,
)

fun EventGuestDto.toDomain(): EventGuest = EventGuest(
    userId = userId,
    fullName = fullName,
    profilePictureUrl = profilePictureUrl,
    hasArrived = attendanceState == "CHECKED_IN",
)
