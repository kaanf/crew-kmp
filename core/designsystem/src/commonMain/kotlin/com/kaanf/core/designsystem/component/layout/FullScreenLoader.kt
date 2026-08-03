package com.kaanf.core.designsystem.component.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kaanf.core.designsystem.theme.AccessDefaults
import kotlinx.coroutines.delay

/**
 * Ekranın tamamını kaplayan, ortalanmış yükleme göstergesi.
 *
 * Gösterge [delayMillis] kadar geciktirilir: veri bu süreden önce gelirse spinner hiç
 * çizilmez, ekran doğrudan içerikle açılır. Yarım saniyeden kısa görünen bir spinner
 * kullanıcıya hızlı değil titrek gelir (androidx ContentLoadingProgressBar ile aynı
 * yaklaşım, aynı varsayılan süre).
 *
 * Kutu ilk andan itibaren yerindedir, yalnız gösterge gecikir; bekleme sırasında layout
 * kaymaz.
 */
@Composable
fun FullScreenLoader(
    modifier: Modifier = Modifier,
    color: Color = AccessDefaults.Accent,
    delayMillis: Long = 500L,
) {
    var isIndicatorVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMillis)
        isIndicatorVisible = true
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (isIndicatorVisible) {
            CircularProgressIndicator(color = color)
        }
    }
}
