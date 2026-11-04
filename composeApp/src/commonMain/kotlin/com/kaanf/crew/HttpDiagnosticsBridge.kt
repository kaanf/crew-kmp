package com.kaanf.crew

import com.kaanf.core.data.networking.HttpDiagnostics

/** Native hosts provide their SDK callback; shared networking stays platform independent. */
fun configureHttpDiagnostics(onResponse: (String, String, Int) -> Unit) {
    HttpDiagnostics.onResponse = onResponse
}

/** Invoked only by developer entry points; also checks the actual binary's debug flag. */
suspend fun verifyLocalSentry(baseUrl: String): String =
    com.kaanf.core.data.networking.verifyLocalSentry(
        baseUrl,
        org.koin.mp.KoinPlatform.getKoin().get<com.kaanf.core.data.BuildEnvironment>(),
    )
