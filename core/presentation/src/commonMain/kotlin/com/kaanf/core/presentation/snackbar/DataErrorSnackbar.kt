package com.kaanf.core.presentation.snackbar

import com.kaanf.core.domain.util.DataError
import com.kaanf.core.presentation.util.UIText
import com.kaanf.core.presentation.util.apiErrorUi
import crew.core.presentation.generated.resources.Res
import crew.core.presentation.generated.resources.error_bad_request
import crew.core.presentation.generated.resources.error_conflict
import crew.core.presentation.generated.resources.error_forbidden
import crew.core.presentation.generated.resources.error_no_internet
import crew.core.presentation.generated.resources.error_not_found
import crew.core.presentation.generated.resources.error_payload_too_large
import crew.core.presentation.generated.resources.error_request_timeout
import crew.core.presentation.generated.resources.error_serialization
import crew.core.presentation.generated.resources.error_server
import crew.core.presentation.generated.resources.error_service_unavailable
import crew.core.presentation.generated.resources.error_too_many_requests
import crew.core.presentation.generated.resources.error_unauthorized
import crew.core.presentation.generated.resources.error_unknown
import crew.core.presentation.generated.resources.snackbar_connection_title
import crew.core.presentation.generated.resources.snackbar_generic_error_title
import crew.core.presentation.generated.resources.snackbar_heads_up_title
import crew.core.presentation.generated.resources.snackbar_not_allowed_title
import org.jetbrains.compose.resources.StringResource

/**
 * DataError.Remote'u kullanıcıya gösterilebilir bir snackbar mesajına çevirir. Networking katmanı
 * yalnız HTTP status taşıdığı için ayrımı buradan yaparız. [title] override'ı, ekrana özel bir
 * başlık gerektiğinde (ör. "Couldn't load events") kullanılır; açıklama ve varyant paylaşılır.
 * [icon] override'ı, ekrana özel bir senaryo ikonu gerektiğinde (ör. foto yükleme) kullanılır.
 */
fun DataError.Remote.toSnackbarMessage(
    title: UIText = UIText.Resource(defaultTitleRes()),
    icon: SnackbarIcon? = null,
): SnackbarMessage {
    if (this is DataError.Remote.Business) {
        val ui = apiErrorUi(code)
        return if (ui != null) {
            SnackbarMessage(
                title = UIText.Resource(ui.title),
                description = UIText.Resource(ui.description),
                variant = ui.variant,
                icon = icon ?: ui.variant.defaultIcon,
            )
        } else {
            // Bilinmeyen code: en azından backend'in mesajını göster (boşsa generic'e düş).
            SnackbarMessage(
                title = UIText.Resource(Res.string.snackbar_generic_error_title),
                description = if (message.isNotBlank()) {
                    UIText.DynamicString(message)
                } else {
                    UIText.Resource(Res.string.error_unknown)
                },
                variant = SnackbarVariant.Error,
                icon = icon ?: SnackbarVariant.Error.defaultIcon,
            )
        }
    }
    val variant = variant()
    return SnackbarMessage(
        title = title,
        description = UIText.Resource(descriptionRes()),
        variant = variant,
        icon = icon ?: defaultIcon(variant),
    )
}

private fun DataError.Remote.defaultIcon(variant: SnackbarVariant): SnackbarIcon = when (this) {
    DataError.Remote.NO_INTERNET -> SnackbarIcon.Offline
    else -> variant.defaultIcon
}

private fun DataError.Remote.defaultTitleRes(): StringResource = when (this) {
    DataError.Remote.NO_INTERNET,
    DataError.Remote.REQUEST_TIMEOUT,
    DataError.Remote.SERVICE_UNAVAILABLE -> Res.string.snackbar_connection_title

    DataError.Remote.UNAUTHORIZED,
    DataError.Remote.FORBIDDEN -> Res.string.snackbar_not_allowed_title

    DataError.Remote.CONFLICT,
    DataError.Remote.TOO_MANY_REQUESTS -> Res.string.snackbar_heads_up_title

    else -> Res.string.snackbar_generic_error_title
}

private fun DataError.Remote.variant(): SnackbarVariant = when (this) {
    DataError.Remote.NO_INTERNET,
    DataError.Remote.REQUEST_TIMEOUT,
    DataError.Remote.SERVICE_UNAVAILABLE,
    DataError.Remote.CONFLICT,
    DataError.Remote.TOO_MANY_REQUESTS -> SnackbarVariant.Warn

    else -> SnackbarVariant.Error
}

private fun DataError.Remote.descriptionRes(): StringResource = when (this) {
    DataError.Remote.BAD_REQUEST -> Res.string.error_bad_request
    DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_request_timeout
    DataError.Remote.UNAUTHORIZED -> Res.string.error_unauthorized
    DataError.Remote.FORBIDDEN -> Res.string.error_forbidden
    DataError.Remote.NOT_FOUND -> Res.string.error_not_found
    DataError.Remote.CONFLICT -> Res.string.error_conflict
    DataError.Remote.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests
    DataError.Remote.NO_INTERNET -> Res.string.error_no_internet
    DataError.Remote.PAYLOAD_TOO_LARGE -> Res.string.error_payload_too_large
    DataError.Remote.SERVER_ERROR -> Res.string.error_server
    DataError.Remote.SERVICE_UNAVAILABLE -> Res.string.error_service_unavailable
    DataError.Remote.SERIALIZATION -> Res.string.error_serialization
    DataError.Remote.UNKNOWN -> Res.string.error_unknown
    is DataError.Remote.Business -> Res.string.error_unknown // Business toSnackbarMessage'ta erken döner
}
