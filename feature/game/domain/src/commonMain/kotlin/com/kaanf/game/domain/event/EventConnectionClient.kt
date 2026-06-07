package com.kaanf.game.domain.event

import com.kaanf.game.domain.model.GameConnectionState
import com.kaanf.game.domain.model.GameSocketMessage
import kotlinx.coroutines.flow.Flow

interface EventConnectionClient {
    fun observeEvents(eventId: String): Flow<GameSocketMessage>

    fun observeConnectionState(eventId: String): Flow<GameConnectionState>
}
