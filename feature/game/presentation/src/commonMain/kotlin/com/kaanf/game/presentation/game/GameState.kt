package com.kaanf.game.presentation.game

import com.kaanf.game.domain.model.GameSocketMessage

data class GameState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showExitConfirmDialog: Boolean = false,
    val showMatchRequestSheet: Boolean = false,
    // Ekranda QR olarak gösterilen kendi maç token'ımız (yüklenene kadar null).
    val matchQrToken: String? = null,
    // Oyun başladı bildirimi (GAME_STARTED) geldi mi.
    val gameStarted: Boolean = false,
    // Karşı taraftan gelen maç daveti (MATCH_INVITE_RECEIVED); sheet bununla doldurulacak.
    val incomingInvite: GameSocketMessage.MatchInviteReceived? = null,
    // Gelen davete accept/decline yanıtı gönderiliyor (çift tıklama / yeniden giriş engeli).
    val isRespondingToInvite: Boolean = false,
)
