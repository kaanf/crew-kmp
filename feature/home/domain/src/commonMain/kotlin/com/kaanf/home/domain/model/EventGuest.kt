package com.kaanf.home.domain.model

data class EventGuest(
    val userId: String,
    val fullName: String,
    val profilePictureUrl: String?,
    val hasArrived: Boolean,
)
