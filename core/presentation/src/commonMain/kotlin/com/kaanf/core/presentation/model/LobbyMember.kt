package com.kaanf.core.presentation.model

/**
 * A single person present in the lobby. [id] is stable for the lifetime of the
 * person's presence so the cluster can key avatars across joins/leaves and run
 * reflow + pop animations correctly. [avatar] carries the visual content
 * (profile image when available, otherwise initials + color).
 */
data class LobbyMember(
    val id: String,
    val avatar: UserAvatar,
)
