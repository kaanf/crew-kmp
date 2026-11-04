package com.kaanf.core.domain.model.profileboard

data class ProfileBoard(
    val schemaVersion: Int = 3,
    val revision: Long = 0,
    val qrStyleId: String = "rounded",
    val stickers: List<BoardSticker> = emptyList(),
    val canEdit: Boolean = true,
    val qr: BoardQrPlacement = BoardQrPlacement(),
)

enum class BoardQrStyle(val id: String) {
    ROUNDED("rounded"), CLASSIC("classic"), DOTS("dots"),
}

/** Rendered by the server. minSize keeps the code itself scannable: the character fills the rest of the image. */
enum class BoardQrTheme(val id: String, val minSize: Float) {
    HAMSTER("hamster_v2", .42f),
    GINGERBREAD("gingerbread_v2", .41f),
    RED_PLUSH("red_plush_v5", .43f),
    VAMPIRE("vampire_v9", .58f),
    ;

    companion object {
        fun find(id: String) = entries.find { it.id == id }
    }
}

fun isKnownQrDesign(id: String) = BoardQrStyle.entries.any { it.id == id } || BoardQrTheme.find(id) != null

fun minQrSize(styleId: String) = BoardQrTheme.find(styleId)?.minSize ?: BoardQrPlacement.MIN_SIZE

enum class BoardQrRenderStatus { NONE, PENDING, READY, FALLBACK }

data class BoardQrRender(
    val themeId: String?,
    val status: BoardQrRenderStatus,
    val imageUrl: String? = null,
)

/** Centre and square size in board-width units, always entirely inside the canvas. */
data class BoardQrPlacement(
    val x: Float = .5f,
    val y: Float = DEFAULT_Y,
    val size: Float = BoardStickerPlacement.QR_SIZE,
) {
    fun bounds() = BoardRect(x - size / 2f, y - size / 2f, x + size / 2f, y + size / 2f)

    fun transform(
        dx: Float,
        dy: Float,
        zoom: Float,
        minTop: Float = BoardIdentityLayout.QR_TOP,
        minSize: Float = MIN_SIZE,
        maxBottom: Float = BoardStickerPlacement.HEIGHT,
    ): BoardQrPlacement {
        if (!dx.isFinite() || !dy.isFinite() || !zoom.isFinite() || zoom <= 0f) return this
        val bottom = maxBottom.coerceIn(MIN_SIZE, BoardStickerPlacement.HEIGHT)
        val top = minTop.coerceIn(0f, bottom - MIN_SIZE)
        val maxSize = minOf(MAX_SIZE, bottom - top)
        val nextSize = (size * zoom).coerceIn(minOf(minSize, maxSize), maxSize)
        val half = nextSize / 2f
        return BoardQrPlacement((x + dx).coerceIn(half, 1f - half),
            (y + dy).coerceIn(top + half, bottom - half), nextSize)
    }

    companion object {
        // The default QR begins immediately below the profile identity.
        const val DEFAULT_Y = 1.12f
        const val MIN_SIZE = .30f
        const val MAX_SIZE = .90f
    }
}

/** Profile identity is drawn over stickers; the QR has its own non-overlapping region below it. */
object BoardIdentityLayout {
    const val TOP = 0f
    const val QR_TOP = .82f
}
