package com.kaanf.auth.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire representation of gender. Kept separate from the domain [com.kaanf.auth.domain.model.Gender]
 * so the backend's exact string values are pinned here via [SerialName] and can evolve
 * independently of the domain naming.
 */
@Serializable
enum class GenderDto {
    @SerialName("Female")
    FEMALE,

    @SerialName("Male")
    MALE,

    @SerialName("NonBinary")
    NON_BINARY,

    @SerialName("Other")
    OTHER,

    @SerialName("PreferNotToSay")
    PREFER_NOT_TO_SAY,
}
