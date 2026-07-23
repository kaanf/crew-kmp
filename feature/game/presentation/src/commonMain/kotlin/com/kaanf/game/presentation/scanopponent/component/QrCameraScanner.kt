package com.kaanf.game.presentation.scanopponent.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * QR kamera önizlemesi. Kamera izni çağıran ekranın sorumluluğunda — bu composable yalnızca
 * izin verilmişken çağrılır. Overlay dışarıdan üstüne çizilir.
 *
 * Android tarafı kendi CameraX binding'ini kullanır (qr-kit'in Android scanner'ı odak
 * kontrolü sunmadığı için Samsung cihazlarda önizleme sürekli bulanık kalıyordu);
 * iOS tarafı qr-kit'e delege eder.
 */
@Composable
expect fun QrCameraScanner(
    modifier: Modifier = Modifier,
    onResult: (String) -> Unit,
)
