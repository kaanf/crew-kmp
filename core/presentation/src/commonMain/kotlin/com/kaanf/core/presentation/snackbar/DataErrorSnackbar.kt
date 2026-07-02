package com.kaanf.core.presentation.snackbar

import com.kaanf.core.domain.util.DataError
import com.kaanf.core.presentation.util.UIText
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
 */
fun DataError.Remote.toSnackbarMessage(
    title: UIText = UIText.Resource(defaultTitleRes()),
): SnackbarMessage = SnackbarMessage(
    title = title,
    description = UIText.Resource(descriptionRes()),
    variant = variant(),
)

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
}
