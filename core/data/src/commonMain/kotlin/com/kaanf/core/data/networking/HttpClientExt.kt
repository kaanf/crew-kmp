package com.kaanf.core.data.networking

import com.kaanf.core.data.dto.ApiErrorDto
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

expect suspend fun <T> platformSafeCall(
    execute: suspend () -> HttpResponse,
    handleResponse: suspend (HttpResponse) -> Result<T, DataError.Remote>,
): Result<T, DataError.Remote>

suspend inline fun <reified Request, reified Response : Any> HttpClient.post(
    route: String,
    body: Request,
    queryParams: Map<String, Any> = mapOf(),
    crossinline builder: HttpRequestBuilder.() -> Unit = {},
): Result<Response, DataError.Remote> {
    return safeCall {
        post {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            setBody(body)
            builder()
        }
    }
}

suspend inline fun <reified Response : Any> HttpClient.post(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    crossinline builder: HttpRequestBuilder.() -> Unit = {},
): Result<Response, DataError.Remote> {
    return safeCall {
        post {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            builder()
        }
    }
}

suspend inline fun <reified Response : Any> HttpClient.get(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    crossinline builder: HttpRequestBuilder.() -> Unit = {},
): Result<Response, DataError.Remote> {
    return safeCall {
        get {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            builder()
        }
    }
}

/**
 * GET ki yanıt gövdesi opsiyonel: 204 No Content → `Result.Success(null)`, aksi halde
 * normal gövde çözümü. "Kayıt yok" durumunu hata değil, null olarak modellemek için.
 */
suspend inline fun <reified Response : Any> HttpClient.getOrNull(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    crossinline builder: HttpRequestBuilder.() -> Unit = {},
): Result<Response?, DataError.Remote> {
    return platformSafeCall(
        execute = {
            get {
                url(constructRoute(route))
                queryParams.forEach { (key, value) ->
                    parameter(key, value)
                }
                builder()
            }
        },
        handleResponse = { response ->
            if (response.status == HttpStatusCode.NoContent) {
                Result.Success(null)
            } else {
                responseToResult<Response>(response)
            }
        },
    )
}

suspend inline fun <reified Response : Any> HttpClient.delete(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    crossinline builder: HttpRequestBuilder.() -> Unit = {},
): Result<Response, DataError.Remote> {
    return safeCall {
        delete {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            builder()
        }
    }
}

suspend inline fun <reified Request, reified Response : Any> HttpClient.put(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    body: Request,
    crossinline builder: HttpRequestBuilder.() -> Unit = {},
): Result<Response, DataError.Remote> {
    return safeCall {
        put {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            setBody(body)
            builder()
        }
    }
}

suspend inline fun <reified Request, reified Response : Any> HttpClient.patch(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    body: Request,
    crossinline builder: HttpRequestBuilder.() -> Unit = {},
): Result<Response, DataError.Remote> {
    return safeCall {
        patch {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            setBody(body)
            builder()
        }
    }
}

suspend inline fun <reified T> safeCall(noinline execute: suspend () -> HttpResponse): Result<T, DataError.Remote> {
    return platformSafeCall(execute = execute) { response ->
        responseToResult(response)
    }
}

@Suppress("SwallowedException")
suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Remote> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Result.Failure(DataError.Remote.SERIALIZATION)
            }
        }
        400 -> businessOr(response, DataError.Remote.BAD_REQUEST)
        // 401 sabit kalır: SessionRefresher/login akışı UNAUTHORIZED kimliğine bağlı.
        401 -> Result.Failure(DataError.Remote.UNAUTHORIZED)
        403 -> businessOr(response, DataError.Remote.FORBIDDEN)
        404 -> businessOr(response, DataError.Remote.NOT_FOUND)
        408 -> Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
        409 -> businessOr(response, DataError.Remote.CONFLICT)
        413 -> Result.Failure(DataError.Remote.PAYLOAD_TOO_LARGE)
        429 -> businessOr(response, DataError.Remote.TOO_MANY_REQUESTS)
        500 -> Result.Failure(DataError.Remote.SERVER_ERROR)
        503 -> Result.Failure(DataError.Remote.SERVICE_UNAVAILABLE)
        else -> Result.Failure(DataError.Remote.UNKNOWN)
    }
}

/**
 * İş-kuralı statülerinde (400/403/404/409/429) backend'in {code, message} gövdesini
 * [DataError.Remote.Business]'a çevirir; gövde yoksa veya bu şekle uymuyorsa (ör. bean-validation
 * {errors:[...]}) status tabanlı [fallback]'e düşer.
 */
@Suppress("SwallowedException", "TooGenericExceptionCaught")
suspend fun businessOr(
    response: HttpResponse,
    fallback: DataError.Remote,
): Result<Nothing, DataError.Remote> {
    return try {
        val dto = response.body<ApiErrorDto>()
        Result.Failure(DataError.Remote.Business(dto.code, dto.message.orEmpty()))
    } catch (e: Exception) {
        Result.Failure(fallback)
    }
}

/**
 * Bearer plugin token'ı bir kez okuyup cache'ler ve session storage değişse de kendi
 * kendine yenilemez. Session her değiştiğinde (login/logout/refresh) cache'i düşürerek
 * bir sonraki isteğin storage'dan taze token okumasını sağlar; aksi halde logout sonrası
 * eski kullanıcının token'ı aynı process'te canlı kalır.
 */
fun HttpClient.clearBearerToken() {
    authProvider<BearerAuthProvider>()?.clearToken()
}

/**
 * [map] gibi, ama transform'daki beklenmedik hatayı (Instant.parse, enum valueOf vb.)
 * SERIALIZATION hatasına çevirir. safeCall yalnız HTTP çağrısını sarar; mapper'daki bir
 * exception aksi halde ViewModel scope'una fırlayıp uygulamayı çökertir.
 */
inline fun <T, R> Result<T, DataError.Remote>.mapCatching(transform: (T) -> R): Result<R, DataError.Remote> {
    return when (this) {
        is Result.Failure -> this
        is Result.Success -> try {
            Result.Success(transform(data))
        } catch (e: Exception) {
            Result.Failure(DataError.Remote.SERIALIZATION)
        }
    }
}

fun constructRoute(route: String): String {
    return when {
        route.contains(UrlConstants.BASE_URL_HTTP) -> route
        route.startsWith("/") -> "${UrlConstants.BASE_URL_HTTP}$route"
        else -> "${UrlConstants.BASE_URL_HTTP}/$route"
    }
}
