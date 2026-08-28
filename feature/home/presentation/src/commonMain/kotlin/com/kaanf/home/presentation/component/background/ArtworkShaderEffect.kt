package com.kaanf.home.presentation.component.background

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shader
import coil3.request.ImageRequest

/**
 * Paletten üretilen renk alanını tek geçişte boyayan platform shader'ı.
 * Android'de AGSL [android.graphics.RuntimeShader], iOS'ta Skia [org.jetbrains.skia.RuntimeEffect].
 *
 * Palet uniform'ları yalnızca nesne kurulurken yazılır; karede değişen tek şey zamandır.
 */
internal interface ArtworkShaderEffect {
    fun shader(size: Size): Shader
}

/** null dönerse platform runtime shader desteklemiyor (Android < 33) ya da derleme başarısız. */
/**
 * @param baseColor renk alanının aşağı doğru döndüğü zemin rengi
 * @param fadeStart renk alanının tam güçte olduğu son nokta (alan yüksekliğinin oranı)
 * @param fadeEnd tamamen [baseColor]'a döndüğü nokta (alan yüksekliğinin oranı)
 */
internal expect fun createArtworkShaderEffect(
    palette: FloatArray,
    baseColor: Color,
    fadeStart: Float,
    fadeEnd: Float,
): ArtworkShaderEffect?

/**
 * Android'de Coil, API 26+ üzerinde HARDWARE bitmap döndürür; bunlar yazılım canvas'ına
 * çizilemez ve palet için piksel okurken çökerler. iOS'ta karşılığı yok.
 */
internal expect fun ImageRequest.Builder.disallowHardwareBitmap(): ImageRequest.Builder

/**
 * Palet çıkarımı için indirilecek kare boyutu. Histogram için yeterli, decode maliyeti
 * ihmal edilebilir; tam boy kapak arka plan yüzünden asla decode edilmez.
 */
internal const val PALETTE_SAMPLE_SIZE = 48

internal const val ARTWORK_GRAIN = 0.09f

/** Karışımın ortalamaya kaçıp grileşmesini geri alır. */
internal const val ARTWORK_SATURATION = 1.45f

/**
 * AGSL ve SkSL aynı dil ailesinden; tek kaynak iki platformda da derleniyor.
 *
 * Beş çok geniş Gauss lekesi. Ağırlıklar normalize edildiği için ekranda ölü bölge yok,
 * her piksel palet renklerinin bir karışımı. Alan durgun: zaman uniform'u yok, dolayısıyla
 * çizim yalnız katman geçersiz kılındığında çalışıyor, kare başına değil.
 */
internal val ARTWORK_SHADER_SOURCE = """
uniform float2 size;
uniform float grain;
uniform float saturation;
uniform float3 baseColor;
uniform float2 fadeRange;
uniform float3 c0;
uniform float3 c1;
uniform float3 c2;
uniform float3 c3;
uniform float3 c4;

/**
 * Grain tanesinin piksel cinsinden boyutu. 1 px tane 3x ekranda gözle görülmeden
 * ortalanıp pus gibi kalıyor; taneyi büyütmek "pürüzlü" hissi veren şey.
 */
const float GRAIN_CELL = 2.0;

/**
 * sin() tabanlı hash büyük fragCoord değerlerinde float hassasiyetini tüketip yönlü
 * (dikey) desen bırakıyor. Bu hash yalnız küçük çarpımlar ve fract kullanır.
 */
float hash(float2 p) {
    float3 v = fract(float3(p.x, p.y, p.x) * float3(0.1031, 0.1030, 0.0973));
    v += dot(v, v.yzx + 33.33);
    return fract((v.x + v.y) * v.z);
}

/** Gauss sönümü: yarıçapın ötesinde de yumuşakça devam eder, kenar bırakmaz. */
float blob(float2 p, float2 center, float radius) {
    float2 d = (p - center) / radius;
    return exp(-dot(d, d));
}

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / size;
    float aspect = size.y / size.x;

    // Genişlik 1 birim, yükseklik aspect birim: bütün merkez ve yarıçaplar bu uzayda.
    float2 p = float2(uv.x, uv.y * aspect);

    // Düşük frekanslı iki basamaklı alan bükümü: lekelerin sınırları geometrik değil
    // organik okunsun diye. Zamana bağlı değil, alan durgun.
    float2 w = p;
    w += 0.10 * float2(sin(p.y * 1.9), cos(p.x * 2.2));
    w += 0.06 * float2(sin(w.y * 3.3), cos(w.x * 3.0));

    float2 b0 = float2(0.22, aspect * 0.18);
    float2 b1 = float2(0.86, aspect * 0.30);
    float2 b2 = float2(0.50, aspect * 0.58);
    float2 b3 = float2(0.12, aspect * 0.74);
    float2 b4 = float2(0.90, aspect * 0.90);

    // Dar yarıçap = her bölgede tek renk baskın. Genişledikçe beş renk ortalanıyor ve
    // alan griye kaçıyor. Epsilon yalnız sıfıra bölmeyi engelleyecek kadar küçük.
    //
    // Yarıçaplar palet sırasına göre: c0 baskın renk, en geniş alanı o tutuyor. Eskiden en
    // geniş leke c2'ydi (canlı vurgu) ve alan görselin baskın renginden çok vurgusuyla
    // okunuyordu. Vurgular daraldı: kendi bölgelerinde daha saf, aradaki geçiş yine yumuşak.
    float w0 = blob(w, b0, 0.78);
    float w1 = blob(w, b1, 0.58);
    float w2 = blob(w, b2, 0.56);
    float w3 = blob(w, b3, 0.50);
    float w4 = blob(w, b4, 0.56);

    float total = w0 + w1 + w2 + w3 + w4 + 1e-8;
    half3 color = half3((c0 * w0 + c1 * w1 + c2 * w2 + c3 * w3 + c4 * w4) / total);

    // Karışım ne kadar örtüşürse o kadar ortalamaya gider; doygunluğu geri veriyoruz.
    half luma = dot(color, half3(0.2126, 0.7152, 0.0722));
    color = mix(half3(luma), color, half(saturation));

    // Hero kartı üst bantta duruyor: oranın zemini kartın fotoğrafından koyu olmazsa kart
    // öne çıkmıyor. Koyu zeminde siyah gölge okunmadığı için derinliği kontrast veriyor.
    color *= half(0.30 + 0.26 * smoothstep(0.02, 0.30, uv.y));

    // Renk alanı yalnız kartın çevresinde; aşağı doğru uygulamanın kendi koyu zeminine
    // dönüyor, böylece gövde metni sabit bir zeminde okunuyor. Sönüm çizim alanının
    // oranı cinsinden: alan artık hero bloğunun kendisi, dolayısıyla renk her zaman
    // tam o bloğun altında bitiyor.
    color = mix(color, half3(baseColor), half(smoothstep(fadeRange.x, fadeRange.y, uv.y)));

    // Kanal başına ayrı gürültü: tek kanallı gürültü parlaklık pusu gibi okunuyor,
    // film grain'inin dokusu renk oynamasından geliyor.
    float2 cell = floor(fragCoord / GRAIN_CELL);
    half3 noise = half3(half(hash(cell)), half(hash(cell + 17.31)), half(hash(cell + 43.77)));
    color += (noise - half3(0.5)) * half(grain);

    return half4(clamp(color, half3(0.0), half3(1.0)), 1.0);
}
""".trimIndent()
