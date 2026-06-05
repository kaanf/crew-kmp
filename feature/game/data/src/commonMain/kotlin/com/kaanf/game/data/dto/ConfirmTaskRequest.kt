package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** `POST .../task/confirm` gövdesi; kazananın görevi tamamlanmış sayıp saymadığını bildirir. */
@Serializable
data class ConfirmTaskRequest(
    val completed: Boolean,
)
