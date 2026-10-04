package com.kaanf.game.data.mappers

import com.kaanf.game.data.dto.SocketEnvelopeDto
import com.kaanf.game.domain.model.GameSocketMessage
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class GameSocketMapperTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `match cancelled before ready decodes with explicit null winner fields`() {
        // Ready'de vazgeçmede (Rejected) backend null alanları açıkça yollar.
        val raw = """
            {"type":"MATCH_CANCELLED","payload":{"matchId":"m","eventId":"e","state":"Rejected",
            "cancelledByUserId":"u","winnerUserId":null,"winnerTotalScore":null,"winnerPointsAwarded":null}}
        """.trimIndent()

        val message = json.decodeFromString<SocketEnvelopeDto>(raw).toDomain(json)

        val cancelled = assertIs<GameSocketMessage.MatchCancelled>(message)
        assertNull(cancelled.winnerUserId)
        assertEquals(0, cancelled.winnerPointsAwarded)
    }
}
