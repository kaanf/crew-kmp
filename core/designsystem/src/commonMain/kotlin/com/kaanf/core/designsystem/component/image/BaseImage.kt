package com.kaanf.core.designsystem.component.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest

@Composable
fun BaseImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    contentScale: ContentScale,
    /**
     * İmzalı (kısa ömürlü) URL'ler için sabit cache anahtarı. Verilmezse anahtar URL'in
     * kendisi olur; imza her tazelemede değiştiği için aynı görsel yeniden indirilir.
     */
    cacheKey: String? = null,
) {
    val platformContext = LocalPlatformContext.current
    val model = if (cacheKey == null) {
        imageUrl
    } else {
        remember(imageUrl, cacheKey) {
            ImageRequest.Builder(platformContext)
                .data(imageUrl)
                .memoryCacheKey(cacheKey)
                .diskCacheKey(cacheKey)
                .build()
        }
    }
    AsyncImage(
        model = model,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier,
    )
}

@Composable
fun BaseImage(
    modifier: Modifier = Modifier,
    imageBytes: ByteArray,
    contentScale: ContentScale
) {
    AsyncImage(
        model = imageBytes,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier,
    )
}


