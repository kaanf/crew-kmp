package com.kaanf.core.presentation.util.mediapicker

/** Largest power-of-two sample that preserves at least the requested longest edge. */
internal fun calculateDecodeSampleSize(width: Int, height: Int, maxSize: Int): Int {
    require(width > 0 && height > 0 && maxSize > 0)
    var longest = maxOf(width, height)
    var sample = 1
    while (longest / 2 >= maxSize) {
        longest /= 2
        sample *= 2
    }
    return sample
}
