package com.kaanf.core.data

/**
 * Binary'nin debug olup olmadığı. Ktor'un gövde logu (LogLevel.ALL) her yanıtı String'e
 * çevirip StringBuilder'da biriktiriyor ve bearer token'ları da yazıyor; sürümde kapalı olmalı.
 * Değer platform DI modülünden gelir (Android: applicationInfo, iOS: Platform.isDebugBinary).
 */
class BuildEnvironment(val isDebug: Boolean)
