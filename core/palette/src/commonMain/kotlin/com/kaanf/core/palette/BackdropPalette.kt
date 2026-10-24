package com.kaanf.core.palette

/**
 * Cover backdrop colours by role, as opaque ARGB ints. The backend extracts them when a cover is
 * uploaded (crew-api `CoverColors.kt`); a missing circle role means "don't draw that circle".
 */
data class BackdropPalette(
    /** Vertical base gradient behind everything (reference `colors[0]`). */
    val base: Int,
    /** Largest circle, bottom-start (reference `colors[1]`). */
    val bottom: Int? = null,
    /** Middle circle, start edge (reference `colors[2]`). */
    val middle: Int? = null,
    /** Smallest circle, top-end (reference `colors[3]`). */
    val topEnd: Int? = null,
) {
    companion object {
        /** [colors] in role order (base, bottom, middle, top-end); null when empty. */
        fun fromRoleOrder(colors: List<Int>): BackdropPalette? {
            val base = colors.firstOrNull() ?: return null
            return BackdropPalette(base, colors.getOrNull(1), colors.getOrNull(2), colors.getOrNull(3))
        }
    }
}
