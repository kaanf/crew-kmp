package com.kaanf.core.designsystem.component.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kaanf.core.designsystem.theme.AccessDefaults

/** Ekranın tamamını kaplayan, ortalanmış yükleme göstergesi. */
@Composable
fun FullScreenLoader(
    modifier: Modifier = Modifier,
    color: Color = AccessDefaults.Accent,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = color)
    }
}
