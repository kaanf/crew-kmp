package com.kaanf.crew

import com.kaanf.core.data.analytics.ProductAnalytics

fun configureProductAnalytics(capture: (String, Map<String, String>) -> Unit) {
    ProductAnalytics.capture = capture
}
