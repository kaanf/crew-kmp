package com.kaanf.auth.presentation.social

import com.kaanf.auth.domain.model.SocialProvider

data class SocialLoginState(
    // Onay sheet'inin gösterildiği provider; null = sheet kapalı.
    val consentProvider: SocialProvider? = null,
    // Provider akışı veya backend çağrısı süren provider.
    val pendingProvider: SocialProvider? = null,
    val isSubmitting: Boolean = false,
)
