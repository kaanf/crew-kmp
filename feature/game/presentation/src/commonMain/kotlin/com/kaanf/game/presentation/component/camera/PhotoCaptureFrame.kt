package com.kaanf.game.presentation.component.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Foto görevinin kare alanı: fotoğraf çekilene kadar burası kameradır, çekilince
 * çağıran ham JPEG baytlarını alır ve aynı kutuda önizlemeye geçer.
 *
 * Kamera izni çağıran ekranın sorumluluğunda — bu composable yalnızca izin verilmişken
 * çağrılır (QrCameraScanner ile aynı sözleşme).
 *
 * Android'de uygulama içi CameraX önizlemesi: deklanşör de bu kutunun içinde, dolayısıyla
 * çekim ile önizleme arasında hiç ekran değişimi olmaz. iOS'ta şimdilik sistem kamerası
 * açılır (AVFoundation yazılırsa yalnızca bu actual değişir, ortak kod aynı kalır).
 */
@Composable
expect fun PhotoCaptureFrame(
    modifier: Modifier = Modifier,
    onCaptured: (ByteArray) -> Unit,
)
