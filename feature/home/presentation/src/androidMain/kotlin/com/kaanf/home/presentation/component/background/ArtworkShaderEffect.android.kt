package com.kaanf.home.presentation.component.background

import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import coil3.request.ImageRequest
import coil3.request.allowHardware

internal actual fun ImageRequest.Builder.disallowHardwareBitmap(): ImageRequest.Builder =
    allowHardware(false)

internal actual fun createArtworkShaderEffect(
    palette: FloatArray,
    baseColor: Color,
    fadeStart: Float,
    fadeEnd: Float,
): ArtworkShaderEffect? {
    // minSdk 26; AGSL API 33 ile geldi, altında statik palet alanına düşüyoruz.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return null
    return runCatching {
        AgslArtworkShaderEffect(palette, baseColor, fadeStart, fadeEnd)
    }.getOrNull()
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class AgslArtworkShaderEffect(
    palette: FloatArray,
    baseColor: Color,
    fadeStart: Float,
    fadeEnd: Float,
) : ArtworkShaderEffect {

    private val runtimeShader = RuntimeShader(ARTWORK_SHADER_SOURCE).apply {
        // Palet yalnız burada yazılır: etkinlik değişmedikçe bir daha dokunulmaz.
        for (slot in 0 until PALETTE_SIZE) {
            setFloatUniform(
                "c$slot",
                palette[slot * 3],
                palette[slot * 3 + 1],
                palette[slot * 3 + 2],
            )
        }
        setFloatUniform("grain", ARTWORK_GRAIN)
        setFloatUniform("saturation", ARTWORK_SATURATION)
        setFloatUniform("baseColor", baseColor.red, baseColor.green, baseColor.blue)
        setFloatUniform("fadeRange", fadeStart, fadeEnd)
    }

    override fun shader(size: Size): Shader {
        // Aynı shader nesnesi güncelleniyor: kare başına tahsis yok.
        runtimeShader.setFloatUniform("size", size.width, size.height)
        return runtimeShader
    }
}
