package com.kaanf.core.domain.review

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory
import java.lang.ref.WeakReference

private var reviewActivity: WeakReference<Activity>? = null

fun attachAppReviewActivity(activity: Activity) {
    reviewActivity = WeakReference(activity)
}

actual fun requestAppReview() {
    val activity = reviewActivity?.get()?.takeUnless { it.isFinishing } ?: return
    val manager = ReviewManagerFactory.create(activity)
    manager.requestReviewFlow().addOnCompleteListener { request ->
        if (request.isSuccessful) manager.launchReviewFlow(activity, request.result)
    }
}

actual val appReviewUrl = "https://play.google.com/store/apps/details?id=com.kaanf.crew"
