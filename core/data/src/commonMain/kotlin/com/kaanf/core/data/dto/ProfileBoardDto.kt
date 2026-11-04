package com.kaanf.core.data.dto

import com.kaanf.core.domain.model.profileboard.*
import kotlinx.serialization.Serializable

@Serializable
data class BoardStickerDto(
    val slot: Int,
    val stickerId: String,
    val x: Float,
    val y: Float,
    val scale: Float = 1f,
    val rotation: Float = 0f,
)

@Serializable
data class BoardQrPlacementDto(
    val x: Float = .5f,
    val y: Float = BoardQrPlacement.DEFAULT_Y,
    val size: Float = BoardStickerPlacement.QR_SIZE,
)

@Serializable
data class BoardQrRenderDto(
    val themeId: String? = null,
    val status: String = "NONE",
    val imageUrl: String? = null,
) {
    fun toDomain() = BoardQrRender(
        themeId = themeId,
        status = BoardQrRenderStatus.entries.find { it.name == status } ?: BoardQrRenderStatus.NONE,
        imageUrl = imageUrl,
    )
}

@Serializable
data class ProfileBoardDto(
    val schemaVersion: Int = 1,
    val revision: Long = 0,
    val qrStyleId: String = "rounded",
    val stickers: List<BoardStickerDto> = emptyList(),
    val qr: BoardQrPlacementDto = BoardQrPlacementDto(),
) {
    fun toDomain(): ProfileBoard {
        if (schemaVersion !in 1..3) return ProfileBoard(schemaVersion = schemaVersion, revision = revision,
            qrStyleId = qrStyleId, canEdit = false)
        val knownStickers = stickers.mapNotNull { sticker ->
            val design = BoardStickerDesign.entries.find { it.id == sticker.stickerId } ?: return@mapNotNull null
            BoardSticker(sticker.slot, design, BoardPosition(sticker.x, sticker.y), sticker.rotation, sticker.scale)
        }
        return ProfileBoard(schemaVersion, revision, qrStyleId, knownStickers,
            canEdit = schemaVersion in 1..3 && knownStickers.size == stickers.size && isKnownQrDesign(qrStyleId),
            qr = BoardQrPlacement(qr.x, qr.y, qr.size).transform(0f, 0f, 1f, minTop = 0f))
    }
}

fun ProfileBoard.toDto() = ProfileBoardDto(schemaVersion, revision, qrStyleId, stickers.map {
    BoardStickerDto(it.slot, it.design.id, it.position.x, it.position.y, it.scale, it.rotation)
}, BoardQrPlacementDto(qr.x, qr.y, qr.size))
