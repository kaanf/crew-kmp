package com.kaanf.crew.push

import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Swift'ten gelen FCM token'ını backend'e kaydeder: kullanıcı login olduğunda ve
 * Firebase token'ı yenilediğinde (Android'deki PushTokenSync'in iOS karşılığı).
 * Swift tarafı: AppDelegate'in MessagingDelegate'i onNewToken'ı çağırır.
 */
object PushTokenBridge : KoinComponent {
    private val userRepository: UserRepository by inject()
    private val sessionStorage: SessionStorage by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val token = MutableStateFlow<String?>(null)

    fun start() {
        scope.launch {
            combine(sessionStorage.observeAuthInfo(), token) { auth, t ->
                if (auth != null) t else null
            }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    // ponytail: kayıt başarısız olursa retry yok; sonraki login/açılış telafi eder.
                    userRepository.registerDeviceToken(it, platform = "ios")
                }
        }
    }

    fun onNewToken(newToken: String) {
        token.value = newToken
    }
}
