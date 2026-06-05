package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** `POST .../task/offer` gövdesi; kazananın seçtiği görevin kimliği. */
@Serializable
data class OfferTaskRequest(
    val taskId: String,
)
