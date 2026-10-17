package com.kaanf.game.presentation.gamelobby

import com.kaanf.core.presentation.model.LobbyMember

data class GameLobbyState(
    val isLoading: Boolean = true,
    val targetEpochMillis: Long = 0L,
    val showGameStartSheet: Boolean = false,
    val showExitConfirmDialog: Boolean = false,
    val lobbyMembers: List<LobbyMember> = emptyList(),
    val lobbyTotalCount: Int = 0,
    val currentUserId: String? = null,
    val currentUserName: String? = null,
    val currentUserPhotoUrl: String? = null,
)
