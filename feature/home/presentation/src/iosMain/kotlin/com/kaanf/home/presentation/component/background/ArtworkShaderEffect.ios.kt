package com.kaanf.home.presentation.component.background

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.asComposeShader
import coil3.request.ImageRequest
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

internal actual fun ImageRequest.Builder.disallowHardwareBitmap(): ImageRequest.Builder = this

/**
 * CMP zaten Skia -> Metal üzerinden çiziyor; shader'ı aynı boru hattında tutmak UIKit
 * interop'undan ve ayrı bir render yüzeyinden kaçınıyor.
 */
internal actual fun createArtworkShaderEffect(
    palette: FloatArray,
    baseColor: Color,
    fadeStart: Float,
    fadeEnd: Float,
): ArtworkShaderEffect? = runCatching {
    SkslArtworkShaderEffect(palette, baseColor, fadeStart, fadeEnd)
}.getOrNull()

private class SkslArtworkShaderEffect(
    palette: FloatArray,
    baseColor: Color,
    fadeStart: Float,
    fadeEnd: Float,
) : ArtworkShaderEffect {

    private val builder = RuntimeShaderBuilder(
        RuntimeEffect.makeForShader(ARTWORK_SHADER_SOURCE),
    ).apply {
        // Palet yalnız burada yazılır: etkinlik değişmedikçe bir daha dokunulmaz.
        for (slot in 0 until PALETTE_SIZE) {
            uniform(
                "c$slot",
                palette[slot * 3],
                palette[slot * 3 + 1],
                palette[slot * 3 + 2],
            )
        }
        uniform("grain", ARTWORK_GRAIN)
        uniform("saturation", ARTWORK_SATURATION)
        uniform("baseColor", baseColor.red, baseColor.green, baseColor.blue)
        uniform("fadeRange", fadeStart, fadeEnd)
    }

    // Skia uniform'ları shader'a kopyalar, yerinde güncellenemez: yeni bir uniform seti
    // yeni bir shader demek. Ama değişen tek uniform `size` ve o yalnız layout'ta değişiyor;
    // boyuta göre cache'leyince kare başına native tahsis sıfıra iniyor. (Eskisini close()
    // etmiyoruz: boyut değişimi nadir ve o an bir karenin çizimi sürüyor olabilir.)
    private var cachedSize: Size? = null
    private var cachedShader: Shader? = null

    override fun shader(size: Size): Shader {
        cachedShader?.takeIf { cachedSize == size }?.let { return it }

        builder.uniform("size", size.width, size.height)
        return builder.makeShader().asComposeShader().also {
            cachedShader = it
            cachedSize = size
        }
    }
}
