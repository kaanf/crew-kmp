package com.kaanf.core.domain.model.profileboard

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/** Pure geometry shared by placement and dragging, including the rotated sticker bounds. */
object BoardStickerPlacement {
    const val ASPECT_RATIO = 0.50f
    const val HEIGHT = 1f / ASPECT_RATIO
    const val QR_SIZE = 0.60f
    const val MIN_SCALE = 0.65f
    const val MAX_SCALE = 1.60f
    private const val EPSILON = 0.00001f
    private const val QR_GAP = 0.012f
    fun create(slot: Int, design: BoardStickerDesign, layout: BoardLayout = BoardLayout()): BoardSticker {
        require(slot in 0..3)
        val rotation = listOf(-9f, 10f, -11f, 12f)[slot]
        val scale = layout.stickerScale
        val initial = when (slot) {
            0 -> BoardPosition(0.24f * scale, layout.contentTop + 0.12f * scale)
            1 -> BoardPosition(0.87f * scale, layout.contentTop + 0.11f * scale)
            2 -> BoardPosition(0.14f * scale, layout.height - 0.11f * scale)
            else -> BoardPosition(0.87f * scale, layout.height - 0.12f * scale)
        }
        val sticker = BoardSticker(slot, design, initial, rotation)
        return constrain(sticker, layout)
    }

    /** Reconcile layout changes against every protected area, choosing the nearest safe position. */
    fun constrain(sticker: BoardSticker, layout: BoardLayout): BoardSticker {
        val limited = sticker.copy(scale = sticker.scale.coerceIn(MIN_SCALE, MAX_SCALE))
        val (halfWidth, halfHeight) = halfExtents(limited, layout)
        val bounded = bounded(limited.position, halfHeight, layout)
        val current = limited.copy(position = bounded)
        if (isAllowed(current, layout)) return current
        val areas = protectedAreas(layout)
        val xs = listOf(bounded.x, 0f, 1f) + areas.flatMap { listOf(it.left - halfWidth, it.right + halfWidth) }
        val ys = listOf(bounded.y, layout.contentTop + halfHeight, layout.height - halfHeight) +
            areas.flatMap { listOf(it.top - halfHeight, it.bottom + halfHeight) }
        val candidate = xs.flatMap { x -> ys.map { y -> BoardPosition(x, y) } }
            .filter { isAllowed(limited.copy(position = it), layout) }
            .minByOrNull { distanceSquared(bounded, it) }
        if (candidate != null) return limited.copy(position = candidate)
        // Compact layouts can require a smaller sticker to leave protected content readable.
        if (limited.scale > MIN_SCALE) return constrain(limited.copy(scale = MIN_SCALE), layout)
        return current
    }

    /** Sweep both axes against every obstacle, preserving sliding along protected edges. */
    fun move(sticker: BoardSticker, dx: Float, dy: Float, layout: BoardLayout = BoardLayout()): BoardSticker {
        if (!dx.isFinite() || !dy.isFinite()) return sticker
        val (halfWidth, halfHeight) = halfExtents(sticker, layout)
        val areas = protectedAreas(layout)
        val start = sticker.position
        val target = bounded(BoardPosition(start.x + dx, start.y + dy), halfHeight, layout)
        var x = target.x
        for (area in areas) {
            if (start.y + halfHeight > area.top && start.y - halfHeight < area.bottom) {
                if (start.x + halfWidth <= area.left + EPSILON && x + halfWidth > area.left) x = area.left - halfWidth
                if (start.x - halfWidth >= area.right - EPSILON && x - halfWidth < area.right) x = area.right + halfWidth
            }
        }
        var y = target.y
        for (area in areas) {
            if (x + halfWidth > area.left && x - halfWidth < area.right) {
                if (start.y + halfHeight <= area.top + EPSILON && y + halfHeight > area.top) y = area.top - halfHeight
                if (start.y - halfHeight >= area.bottom - EPSILON && y - halfHeight < area.bottom) y = area.bottom + halfHeight
            }
        }
        val moved = sticker.copy(position = BoardPosition(x, y))
        return if (isAllowed(moved, layout)) moved else sticker
    }

    /** Follow the fingers across protected areas; settle with constrain when the gesture ends. */
    fun previewTransform(
        sticker: BoardSticker, dx: Float, dy: Float, zoom: Float, rotation: Float, layout: BoardLayout,
    ): BoardSticker {
        if (!dx.isFinite() || !dy.isFinite() || !zoom.isFinite() || zoom <= 0f || !rotation.isFinite()) return sticker
        val transformed = sticker.copy(
            scale = (sticker.scale * zoom).coerceIn(MIN_SCALE, MAX_SCALE),
            rotation = normalizeAngle(sticker.rotation + rotation),
        )
        val (_, halfHeight) = halfExtents(transformed, layout)
        return transformed.copy(position = bounded(BoardPosition(sticker.position.x + dx, sticker.position.y + dy), halfHeight, layout))
    }

    fun transform(
        sticker: BoardSticker,
        dx: Float,
        dy: Float,
        zoom: Float,
        rotation: Float,
        layout: BoardLayout,
    ): BoardSticker {
        if (!zoom.isFinite() || zoom <= 0f || !rotation.isFinite()) return sticker
        var result = move(sticker, dx, dy, layout)
        val targetScale = (result.scale * zoom).coerceIn(MIN_SCALE, MAX_SCALE)
        val angle = rotation.coerceIn(-180f, 180f)
        val scaleChange = targetScale - result.scale
        val steps = max(1, ceil(max(abs(angle) / 3f, abs(scaleChange) / 0.03f)).toInt())
        val origin = result
        for (step in 1..steps) {
            val fraction = step.toFloat() / steps
            val candidate = origin.copy(
                scale = origin.scale + scaleChange * fraction,
                rotation = normalizeAngle(origin.rotation + angle * fraction),
            )
            if (!isAllowed(candidate, layout)) break
            result = candidate
        }
        return result
    }

    fun isAllowed(sticker: BoardSticker, layout: BoardLayout = BoardLayout()): Boolean {
        if (sticker.scale !in MIN_SCALE..MAX_SCALE || !sticker.rotation.isFinite()) return false
        val (halfWidth, halfHeight) = halfExtents(sticker, layout)
        val (x, y) = sticker.position
        val inside = x >= -EPSILON && x <= 1f + EPSILON &&
            y - halfHeight >= layout.contentTop - EPSILON && y + halfHeight <= layout.height + EPSILON
        return inside && protectedAreas(layout).all { area ->
            x + halfWidth <= area.left + EPSILON || x - halfWidth >= area.right - EPSILON ||
                y + halfHeight <= area.top + EPSILON || y - halfHeight >= area.bottom - EPSILON
        }
    }

    /** Rotated footprint, also used by hit testing and the in-bounds removal badge. */
    fun footprint(sticker: BoardSticker, layout: BoardLayout): Pair<Float, Float> {
        val (halfWidth, halfHeight) = halfExtents(sticker, layout)
        return halfWidth * 2f to halfHeight * 2f
    }

    private fun halfExtents(sticker: BoardSticker, layout: BoardLayout): Pair<Float, Float> {
        val design = sticker.design
        val scale = layout.stickerScale * sticker.scale
        if (design.round) return design.width * scale / 2f to design.height * scale / 2f
        val radians = sticker.rotation * (kotlin.math.PI.toFloat() / 180f)
        val cosine = abs(cos(radians))
        val sine = abs(sin(radians))
        return (design.width * cosine + design.height * sine) * scale / 2f to
            (design.width * sine + design.height * cosine) * scale / 2f
    }

    private fun bounded(position: BoardPosition, halfHeight: Float, layout: BoardLayout) = BoardPosition(
        position.x.coerceIn(0f, 1f),
        position.y.coerceIn(layout.contentTop + halfHeight, (layout.height - halfHeight).coerceAtLeast(layout.contentTop + halfHeight)),
    )

    private fun protectedAreas(layout: BoardLayout): List<BoardRect> =
        listOf(layout.qr.inflate(QR_GAP)) + layout.protectedAreas.map { it.inflate(0.012f) }

    private fun BoardRect.inflate(gap: Float) = BoardRect(left - gap, top - gap, right + gap, bottom + gap)

    private fun normalizeAngle(angle: Float): Float = ((angle + 180f) % 360f + 360f) % 360f - 180f

    private fun distanceSquared(a: BoardPosition, b: BoardPosition): Float =
        (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)
}
