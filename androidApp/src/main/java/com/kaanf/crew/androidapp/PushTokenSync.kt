package com.kaanf.crew.androidapp

import com.google.firebase.messaging.FirebaseMessaging
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * FCM token'ını backend'e kaydeder: kullanıcı login olduğunda ve
 * Firebase token'ı rotate ettiğinde (onNewToken).
 */
class PushTokenSync(
    private val userRepository: UserRepository,
    private val sessionStorage: SessionStorage,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start() {
        scope.launch {
            sessionStorage.observeAuthInfo()
                .map { it != null }
                .distinctUntilChanged()
                .filter { it }
                .collect {
                    // ponytail: kayıt başarısız olursa retry yok; bir sonraki login/app açılışı telafi eder.
                    val token = currentToken() ?: return@collect
                    userRepository.registerDeviceToken(token, platform = "android")
                }
        }
    }

    fun onNewToken(token: String) {
        scope.launch {
            if (sessionStorage.observeAuthInfo().first() != null) {
                userRepository.registerDeviceToken(token, platform = "android")
            }
        }
    }

    private suspend fun currentToken(): String? = suspendCancellableCoroutine { cont ->
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { cont.resume(it) }
            .addOnFailureListener { cont.resume(null) }
    }
}
