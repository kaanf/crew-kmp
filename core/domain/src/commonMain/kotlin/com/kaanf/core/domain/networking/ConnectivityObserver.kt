package com.kaanf.core.domain.networking

import kotlinx.coroutines.flow.Flow

/**
 * Cihazın ağ durumu. Hem soket yaşam döngüsü hem de app geneli "internet yok" uyarısı bunu dinler.
 */
interface ConnectivityObserver {
    val isConnected: Flow<Boolean>
}
