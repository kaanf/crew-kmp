package com.kaanf.core.presentation.snackbar

import com.kaanf.core.presentation.util.UIText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * App geneli snackbar otoritesi. ViewModel'ler bu controller üzerinden mesaj yollar;
 * tek bir host (root'ta) bunları dinler. Host NavHost'tan daha uzun yaşadığı için
 * mesajlar ekran/navigasyon değişiminden etkilenmez.
 */
class SnackbarController {
    private val _messages = Channel<SnackbarMessage>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    suspend fun show(message: SnackbarMessage) {
        _messages.send(message)
    }
}

data class SnackbarMessage(
    val title: UIText,
    val description: UIText,
    val variant: SnackbarVariant,
    val icon: SnackbarIcon = variant.defaultIcon,
)

/**
 * Mesajın anlamsal türü; renk karşılığı design system tarafında belirlenir.
 */
enum class SnackbarVariant {
    Accent,
    Success,
    Info,
    Warn,
    Error,
    AccentALT,
}

/**
 * Mesajın senaryo başlığı; glif karşılığı design system tarafında belirlenir.
 * Renk her zaman varyanttan gelir, ikon yalnız "ne oldu"yu söyler.
 */
enum class SnackbarIcon {
    Success,
    Error,
    Warning,
    Info,
    Celebration,
    Person,
    Offline,
    Online,
    Pending,
    Match,
    Photo,
    Syncing,
}

val SnackbarVariant.defaultIcon: SnackbarIcon
    get() = when (this) {
        SnackbarVariant.Accent -> SnackbarIcon.Celebration
        SnackbarVariant.Success -> SnackbarIcon.Success
        SnackbarVariant.Info -> SnackbarIcon.Info
        SnackbarVariant.Warn -> SnackbarIcon.Warning
        SnackbarVariant.Error -> SnackbarIcon.Error
        SnackbarVariant.AccentALT -> SnackbarIcon.Person
    }
