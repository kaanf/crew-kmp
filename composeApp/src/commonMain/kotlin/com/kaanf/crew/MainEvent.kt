package com.kaanf.crew

sealed interface MainEvent {
    /** Oturum bitti: kullanıcı çıkış yaptı, hesabını sildi ya da refresh token düştü. */
    data object OnSessionEnded: MainEvent
}
