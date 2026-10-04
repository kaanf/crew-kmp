package com.kaanf.crew

data class MainState(
    val isLoggedIn: Boolean = false,
    val isCheckingAuth: Boolean = true,
    /** Doluysa kurulu sürüm eski: force update sheet bu linkle gösterilir. */
    val updateStoreUrl: String? = null,
)
