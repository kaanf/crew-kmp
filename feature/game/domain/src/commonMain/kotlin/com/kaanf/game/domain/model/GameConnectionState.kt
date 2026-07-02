package com.kaanf.game.domain.model

sealed interface GameConnectionState {
    data object Connecting : GameConnectionState
    data object Connected : GameConnectionState
    data object Reconnecting : GameConnectionState
    data class Disconnected(val code: Int?, val reason: String?) : GameConnectionState {
        /**
         * Beklenen/kullanıcı kaynaklı kopuşlar (oturum kapandı, arka plan, ağ yok) hata değildir:
         * kendiliğinden toparlanır ve banner yeterlidir. Yalnızca gerçek terminal hatalar
         * (giriş yapılmamış, etkinlik yok, sunucu pes etti vb.) kullanıcıya snackbar olarak gösterilir.
         */
        val isError: Boolean
            get() = reason !in EXPECTED_REASONS
    }

    companion object {
        /** Soket yaşam döngüsü kapılarından gelen "beklenen" kopuş sebepleri. Data katmanı bu sabitleri kullanır. */
        const val REASON_UNAUTHENTICATED = "unauthenticated"
        const val REASON_BACKGROUND = "background"
        const val REASON_NO_NETWORK = "no_network"

        private val EXPECTED_REASONS = setOf(REASON_UNAUTHENTICATED, REASON_BACKGROUND, REASON_NO_NETWORK)
    }
}
