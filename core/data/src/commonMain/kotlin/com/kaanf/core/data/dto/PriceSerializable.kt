package com.kaanf.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PriceSerializable(
    val amount: Double,
    val currency: String
)
