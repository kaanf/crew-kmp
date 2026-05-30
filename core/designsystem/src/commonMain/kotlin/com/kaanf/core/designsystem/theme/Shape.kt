package com.kaanf.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

@Suppress("FunctionName")
fun SquircleCornerShape(size: Dp) = SquircleShape(radius = size, smoothing = CornerSmoothing.Medium)

object AccessShapes {
    val None = RoundedCornerShape(0.dp)

    val XSmall = RoundedCornerShape(4.dp)
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(12.dp)
    val Large = RoundedCornerShape(16.dp)
    val XLarge = RoundedCornerShape(20.dp)
    val XXLarge = RoundedCornerShape(24.dp)

    val Card = RoundedCornerShape(16.dp)
    val LargeCard = RoundedCornerShape(20.dp)
    val BottomSheet = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp,
    )

    val Pill = RoundedCornerShape(percent = 50)
}
