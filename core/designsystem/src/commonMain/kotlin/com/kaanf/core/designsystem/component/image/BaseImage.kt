package com.kaanf.core.designsystem.component.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun BaseImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    contentScale: ContentScale
) {
    AsyncImage(
        model = imageUrl,
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


