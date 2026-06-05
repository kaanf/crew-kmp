package com.kaanf.game.domain.repository

import com.kaanf.game.domain.model.GameConnectionState
import com.kaanf.game.domain.model.GameSocketMessage
import kotlinx.coroutines.flow.Flow

interface GameSocketRepository {
    fun observeEvents(eventId: String): Flow<GameSocketMessage>

    fun observeConnectionState(eventId: String): Flow<GameConnectionState>
}
