package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Soketten gelen her frame'in ortak zarfı: `{ "type": ..., "payload": ... }`.
 * [payload] tipe bağlı olarak ayrı bir DTO'ya decode edilir.
 */
@Serializable
data class SocketEnvelopeDto(
    val type: String,
    val payload: JsonElement? = null,
)
