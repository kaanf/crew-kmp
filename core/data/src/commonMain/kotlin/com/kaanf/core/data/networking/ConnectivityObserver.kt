package com.kaanf.core.data.networking

import kotlinx.coroutines.flow.Flow

/**
 * Cihazın ağ durumu. Hem soket yaşam döngüsü hem de app geneli "internet yok" uyarısı bunu dinler.
 */
expect class ConnectivityObserver {
    val isConnected: Flow<Boolean>
}
