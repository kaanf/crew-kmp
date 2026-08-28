package com.kaanf.home.presentation.component.background

import org.jetbrains.skia.RuntimeEffect
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Shader kaynağındaki bir yazım hatası sessizce yedeğe düşürür; derlenip derlenmediğini
 * burada yakalıyoruz. (AGSL tarafı aynı kaynağı kullanır ama cihaz ister.)
 */
class ArtworkShaderSourceTest {

    @Test
    fun artwork_shader_source_compiles() {
        assertNotNull(RuntimeEffect.makeForShader(ARTWORK_SHADER_SOURCE))
    }
}
