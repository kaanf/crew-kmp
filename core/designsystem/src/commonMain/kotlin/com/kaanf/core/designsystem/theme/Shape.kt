package com.kaanf.core.designsystem.theme

import androidx.compose.ui.unit.Dp
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

@Suppress("FunctionName")
fun SquircleCornerShape(size: Dp) = SquircleShape(radius = size, smoothing = CornerSmoothing.Medium)
