package com.kaanf.game.presentation.quests

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Foto questinin kare alanı: fotoğraf çekilene kadar burası kameradır, çekilince
 * çağıran ham JPEG baytlarını alır ve aynı kutuda etiketlemeye devam eder.
 *
 * Kamera izni çağıran ekranın sorumluluğunda — bu composable yalnızca izin verilmişken
 * çağrılır (QrCameraScanner ile aynı sözleşme).
 *
 * Android'de uygulama içi CameraX önizlemesi: deklanşör de bu kutunun içinde, dolayısıyla
 * çekim ile etiketleme arasında hiç ekran değişimi olmaz. iOS'ta şimdilik sistem kamerası
 * açılır (AVFoundation yazılırsa yalnızca bu actual değişir, ortak kod aynı kalır).
 */
@Composable
expect fun PhotoCaptureFrame(
    modifier: Modifier = Modifier,
    onCaptured: (ByteArray) -> Unit,
)
