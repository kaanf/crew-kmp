package com.kaanf.core.designsystem.component.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.kaanf.core.designsystem.theme.AccessDefaults

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
    /**
     * Görsel inene kadar ortada küçük bir gösterge çizer. Yalnızca [modifier]'ın ölçüsü
     * belli olduğu yerlerde kullanılmalı: gösterge kutuyu ölçen çocuk değildir, dolayısıyla
     * boyutunu görselden alan yerleşimlerde (ör. sadece fillMaxWidth) kutu yüksekliği sıfır kalır.
     */
    showLoadingIndicator: Boolean = false,
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

    if (!showLoadingIndicator) {
        AsyncImage(
            model = model,
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier,
        )
        return
    }

    var isLoading by remember(model) { mutableStateOf(true) }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AsyncImage(
            model = model,
            contentDescription = null,
            contentScale = contentScale,
            onState = { state -> isLoading = state is AsyncImagePainter.State.Loading },
            modifier = Modifier.matchParentSize(),
        )
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                color = AccessDefaults.Accent,
                strokeWidth = 1.5.dp,
            )
        }
    }
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


