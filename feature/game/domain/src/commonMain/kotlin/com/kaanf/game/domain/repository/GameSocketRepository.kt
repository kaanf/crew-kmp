package com.kaanf.game.domain.repository

import com.kaanf.game.domain.model.GameSocketMessage
import kotlinx.coroutines.flow.Flow

interface GameSocketRepository {
    /**
     * Verilen [eventId] için event soketine bağlanır ve gelen mesajları akış olarak verir.
     * Akışın toplanması durduğunda (scope iptali) soket otomatik kapanır.
     */
    fun observeEvents(eventId: String): Flow<GameSocketMessage>
}
