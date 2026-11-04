package com.kaanf.crew

import com.kaanf.core.data.networking.HttpDiagnostics

/** Native hosts provide their SDK callback; shared networking stays platform independent. */
fun configureHttpDiagnostics(onResponse: (String, String, Int) -> Unit) {
    HttpDiagnostics.onResponse = onResponse
}
