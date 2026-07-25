package com.kaanf.auth.presentation.social

/**
 * Custom Tab'daki Apple akışı, backend'in crew://auth/apple redirect'iyle MainActivity'ye
 * döner; MainActivity URI'yi buraya iletir, bekleyen launcher callback'i tüketir.
 * ExternalUriHandler ile aynı desen: process-tekil, tek bekleyen akış.
 */
object AppleSignInBridge {
    var pendingCallback: ((String) -> Unit)? = null

    fun onCallback(uri: String) {
        pendingCallback?.invoke(uri)
        pendingCallback = null
    }
}
