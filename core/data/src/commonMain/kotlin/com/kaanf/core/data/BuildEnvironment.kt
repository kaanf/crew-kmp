package com.kaanf.core.data

/**
 * Binary'nin debug olup olmadığı. HTTP tanı logları debug'da yalnız güvenli istek özeti
 * içerir; gövdeler, auth ve query URL'leri loglanmaz. Sürümde HTTP logları kapalıdır.
 * Değer platform DI modülünden gelir (Android: applicationInfo, iOS: Platform.isDebugBinary).
 */
class BuildEnvironment(val isDebug: Boolean)
