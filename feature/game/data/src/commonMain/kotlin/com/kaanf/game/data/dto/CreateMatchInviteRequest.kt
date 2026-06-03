package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateMatchInviteRequest(
    val scannedMatchQrToken: String,
)
