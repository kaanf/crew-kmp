package com.kaanf.core.domain.model.profileboard

/** Bundled v1 catalogue; IDs are persisted independently of enum order. */
enum class BoardStickerDesign(val width: Float, val height: Float, val round: Boolean = false) {
    CREW(0.28f, 0.10f),
    GOOD_VIBES(0.30f, 0.10f),
    STAR(0.18f, 0.18f, round = true),
    LIGHTNING(0.18f, 0.18f, round = true),
    TROPHY(0.18f, 0.18f, round = true),
    SPARKLE(0.18f, 0.18f, round = true),
    HIKING(0.30f, 0.10f),
    GAMING(0.30f, 0.10f),
    PHOTOGRAPHY(0.34f, 0.10f),
    COOKING(0.30f, 0.10f),
    MUSIC(0.28f, 0.10f),
    DANCING(0.30f, 0.10f),
    CYCLING(0.30f, 0.10f),
    COFFEE(0.30f, 0.10f),
    BOARD_GAMES(0.34f, 0.10f),
    ;

    val id: String get() = name.lowercase()
    val category: BoardStickerCategory get() = when (this) {
        HIKING, GAMING, PHOTOGRAPHY, COOKING -> BoardStickerCategory.HOBBIES
        MUSIC, DANCING -> BoardStickerCategory.MUSIC
        CYCLING -> BoardStickerCategory.SPORTS
        COFFEE, BOARD_GAMES -> BoardStickerCategory.SOCIAL
        else -> BoardStickerCategory.CREW
    }

}

enum class BoardStickerCategory { HOBBIES, MUSIC, SPORTS, SOCIAL, CREW }

data class BoardPosition(val x: Float, val y: Float)

data class BoardSticker(
    val slot: Int,
    val design: BoardStickerDesign,
    val position: BoardPosition,
    val rotation: Float,
    val scale: Float = 1f,
)

/** The fixed board surface, measured in board-width units. */
data class BoardLayout(
    val height: Float = BoardStickerPlacement.HEIGHT,
    val qr: BoardRect = BoardQrPlacement().bounds(),
    val stickerScale: Float = 1f,
    val protectedAreas: List<BoardRect> = emptyList(),
    val contentTop: Float = 0f,
)

data class BoardRect(val left: Float, val top: Float, val right: Float, val bottom: Float)
