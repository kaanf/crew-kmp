package com.kaanf.core.data

import com.kaanf.core.data.dto.BoardStickerDto
import com.kaanf.core.data.dto.ProfileBoardDto
import com.kaanf.core.data.dto.toDto
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProfileBoardDtoTest {
    @Test
    fun boardTransformsSurviveJsonAndDomainRoundTrip() {
        val input = """{"schemaVersion":1,"revision":7,"qrStyleId":"classic","stickers":[{"slot":2,"stickerId":"hiking","x":0.2,"y":0.1,"scale":0.8,"rotation":-9.0}]}"""
        val original = Json.decodeFromString<ProfileBoardDto>(input)
        val restored = Json.decodeFromString<ProfileBoardDto>(Json.encodeToString(original.toDomain().toDto()))
        assertEquals(original, restored)
    }

    @Test
    fun futureStickerIsOmittedFromRenderingAndCannotBeOverwrittenByOldApp() {
        val board = Json.decodeFromString<ProfileBoardDto>("""{"stickers":[{"slot":0,"stickerId":"future-sticker","x":0.2,"y":0.1}]}""").toDomain()
        assertEquals(emptyList(), board.stickers)
        assertFalse(board.canEdit)
    }

    @Test
    fun unknownQrAndSchemaAreReadOnly() {
        assertFalse(ProfileBoardDto(qrStyleId = "future-style").toDomain().canEdit)
        assertTrue(ProfileBoardDto(schemaVersion = 3, qrStyleId = "vampire_v9").toDomain().canEdit)
        val future = ProfileBoardDto(schemaVersion = 4,
            stickers = listOf(BoardStickerDto(0, "hiking", 100f, 100f))).toDomain()
        assertFalse(future.canEdit)
        assertEquals(emptyList(), future.stickers)
    }
    @Test
    fun customQrPlacementSurvivesVersionThreeRoundTrip() {
        val input = """{"schemaVersion":3,"revision":8,"qr":{"x":0.25,"y":1.2,"size":0.4}}"""
        val original = Json.decodeFromString<ProfileBoardDto>(input)
        assertEquals(original, original.toDomain().toDto())
    }

    @Test
    fun placementNearMeasuredHeaderDoesNotShiftWhenReloaded() {
        val original = Json.decodeFromString<ProfileBoardDto>("""{"schemaVersion":3,"qr":{"x":0.5,"y":0.75,"size":0.3}}""")
        assertEquals(original, original.toDomain().toDto())
    }

    @Test
    fun lovesSurviveNetworkAndCacheRoundTripAndLegacyDefaultsToEmpty() {
        val original = ProfileBoardDto(schemaVersion = 3, hobbies = listOf("Kahve", "🎮 Oyun"))
        assertEquals(original, Json.decodeFromString<ProfileBoardDto>(Json.encodeToString(original.toDomain().toDto())))
        assertEquals(emptyList(), Json.decodeFromString<ProfileBoardDto>("{}").toDomain().hobbies)
        assertEquals(emptyList(), original.copy(hobbies = emptyList()).toDomain().toDto().hobbies)
    }

    @Test
    fun clearingLovesExplicitlySendsEmptyArrayWithDefaultJsonSettings() {
        val body = Json.encodeToString(ProfileBoardDto(hobbies = emptyList()))
        val fields = Json.parseToJsonElement(body) as kotlinx.serialization.json.JsonObject
        assertEquals(kotlinx.serialization.json.JsonArray(emptyList()), fields["hobbies"])
    }

}
