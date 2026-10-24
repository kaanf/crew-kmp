package com.kaanf.core.palette

import android.os.Build

internal actual val isRenderEffectBlurSupported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
