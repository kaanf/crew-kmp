package com.kaanf.home.presentation.eventdetail

import coil3.Image
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull

internal data class EventShareContent(
    val title: String,
    val text: String,
    val url: String,
    val imageUrl: String?,
)

/** Reuse the event image cache; sharing still works offline or without a cover. */
internal suspend fun loadEventShareImage(context: PlatformContext, imageUrl: String?): Image? {
    if (imageUrl.isNullOrBlank()) return null
    return try {
        withTimeoutOrNull(1_000L) {
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .size(320, 320)
                .build()
            (SingletonImageLoader.get(context).execute(request) as? SuccessResult)?.image
        }
    } catch (exception: CancellationException) {
        throw exception
    } catch (_: Exception) {
        null
    }
}
