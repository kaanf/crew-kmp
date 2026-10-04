package com.kaanf.core.domain.review

import platform.StoreKit.SKStoreReviewController
import platform.UIKit.UIApplication
import platform.UIKit.UIWindowScene

actual fun requestAppReview() {
    // Aktif pencere sahnesi yoksa (uygulama arka planda) istek yapılamaz; sessizce geç.
    val scene = UIApplication.sharedApplication.connectedScenes
        .filterIsInstance<UIWindowScene>()
        .firstOrNull() ?: return

    SKStoreReviewController.requestReviewInScene(scene)
}

// action=write-review App Store'u doğrudan "Değerlendirme Yaz" ekranında açar.
actual val appReviewUrl = "https://apps.apple.com/app/id6778055243?action=write-review"
