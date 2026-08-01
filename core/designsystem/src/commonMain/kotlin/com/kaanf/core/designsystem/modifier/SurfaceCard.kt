package com.kaanf.core.designsystem.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes

/**
 * Uygulamanın standart kart yüzeyi: dolgu + 1dp ince kenarlık, ikisi de aynı shape'te.
 * İçeriği kırpmaz — resim gibi taşan içerik için çağıran ayrıca [Modifier.clip] uygular.
 */
fun Modifier.surfaceCard(
    shape: Shape = AccessShapes.Medium,
    backgroundColor: Color = AccessDefaults.Surface,
    borderColor: Color = AccessDefaults.BorderSoft,
    borderWidth: Dp = 1.dp,
): Modifier = this
    .background(color = backgroundColor, shape = shape)
    .border(width = borderWidth, color = borderColor, shape = shape)
