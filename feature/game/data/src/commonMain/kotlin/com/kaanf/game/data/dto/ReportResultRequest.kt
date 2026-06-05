package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** `POST .../result` gövdesi; çağıran kullanıcının kazanıp kazanmadığını bildirir. */
@Serializable
data class ReportResultRequest(
    val won: Boolean,
)
