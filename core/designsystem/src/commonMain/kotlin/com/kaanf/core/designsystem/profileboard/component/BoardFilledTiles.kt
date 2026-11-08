package com.kaanf.core.designsystem.profileboard.component

import com.kaanf.core.domain.model.movie.Movie

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.profileboard.BoardTileLimits
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.designsystem.theme.Inter
import com.kaanf.core.domain.model.music.MusicTrack
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.profile_board_ask_label
import crew.core.designsystem.generated.resources.profile_board_loves_add_more
import crew.core.designsystem.generated.resources.profile_board_song_label
import crew.core.designsystem.generated.resources.profile_board_tile_remove
import org.jetbrains.compose.resources.stringResource

private val SongAccent = Color(0xFFC7A3F7)
private val FilmAccent = Color(0xFFF79B87)
private val AskAccent = Color(0xFF7EE0B5)
private val ChipColors = listOf(Color(0xFF7CC8F7), Color(0xFFF2C14E), Color(0xFFF79BC7), Color(0xFF3BE256), Color(0xFFC7A3F7))
private val ChipTilts = listOf(-4f, 3f, -2f, 5f, -3f)

@Composable
private fun tileLabelStyle() = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 9.sp, letterSpacing = 1.2.sp)

/** A tilted pixel sticker with an optional pixel × badge in its top-end corner. */
@Composable
private fun PixelTile(
    accent: Color,
    tilt: Float,
    onClick: () -> Unit,
    onRemove: (() -> Unit)?,
    removeLabel: String,
    modifier: Modifier = Modifier,
    shape: Shape = PixelTileShape,
    onLongClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.rotate(tilt)) {
        PixelStickerFrame(accent, Modifier.fillMaxSize().clip(shape).boardTileClickable(onLongClick = onLongClick, onClick = onClick),
            shape = shape, content = content)
        if (onRemove != null) PixelRemoveBadge(removeLabel, onRemove, Modifier.align(Alignment.TopEnd).offset(x = 7.dp, y = (-9).dp))
    }
}

@Composable
private fun PixelRemoveBadge(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, size: Dp = 26.dp) {
    Box(
        modifier = modifier.size(size).clip(PixelBadgeShape).background(PixelInk)
            .semantics(mergeDescendants = true) { contentDescription = label }
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        PixelIcon(pixelCross(AccessDefaults.TextPrimary), columns = 5, rows = 5, size = size * 10 / 26)
    }
}

@Composable
internal fun BoardSongTile(
    track: MusicTrack,
    onClick: () -> Unit,
    onRemove: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
) {
    val line = "${track.title} · ${track.artists.joinToString()}"
    PixelTile(SongAccent, -1.2f, onClick, onRemove, stringResource(Res.string.profile_board_tile_remove, track.title), modifier, onLongClick = onLongClick) {
        Row(Modifier.fillMaxSize().padding(start = 8.dp, end = 2.dp), horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    PixelIcon(PixelSpotify, columns = 14, rows = 14, size = 12.dp)
                    Text(stringResource(Res.string.profile_board_song_label), style = tileLabelStyle(), color = SongAccent)
                }
                Text(line, style = TextStyle(fontFamily = BricolageGrotesque, fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp, lineHeight = 17.sp), color = AccessDefaults.TextPrimary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

        }
    }
}

@Composable
internal fun BoardAskTile(
    text: String,
    onClick: () -> Unit,
    onRemove: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
) {
    PixelTile(AskAccent, 2f, onClick, onRemove, stringResource(Res.string.profile_board_tile_remove, text),
        modifier, onLongClick = onLongClick) {
        Column(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(Res.string.profile_board_ask_label), style = tileLabelStyle(), color = AskAccent)
            Text(text, style = TextStyle(fontFamily = BricolageGrotesque, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp,
                lineHeight = 16.sp), color = AccessDefaults.TextPrimary, maxLines = 5, overflow = TextOverflow.Ellipsis)
        }
    }
}

/** Pixel hobby chips; in edit mode each chip has a pixel × and a dashed "+" opens the sheet. */
@Composable
internal fun BoardLovesRow(
    hobbies: List<String>,
    editing: Boolean,
    onEdit: () -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(14.dp),
            itemVerticalAlignment = Alignment.CenterVertically) {
            hobbies.forEachIndexed { index, hobby ->
                val color = ChipColors[index % ChipColors.size]
                Box(Modifier.rotate(ChipTilts[index % ChipTilts.size])) {
                    // Chips use a single 3 dp ink frame inside the white sticker outline.
                    PixelStickerFrame(color, Modifier.clip(PixelChipShape).boardTileClickable(onLongClick = onLongClick, onClick = onEdit),
                        shape = PixelChipShape, outline = 2.5.dp, frame = 0.dp) {
                        Text(
                            text = hobby,
                            modifier = Modifier.padding(3.dp).background(color, PixelChipShape)
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            style = TextStyle(fontFamily = BricolageGrotesque, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp),
                            color = PixelInk,
                            maxLines = 1,
                        )
                    }
                    if (editing) PixelRemoveBadge(stringResource(Res.string.profile_board_tile_remove, hobby), { onRemove(hobby) },
                        Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-8).dp), size = 20.dp)
                }
            }
            if (editing && hobbies.size < BoardTileLimits.MAX_HOBBIES) {
                val addMore = stringResource(Res.string.profile_board_loves_add_more)
                Box(
                    modifier = Modifier.size(34.dp).clip(RoundedCornerShape(10.dp))
                        .semantics(mergeDescendants = true) { contentDescription = addMore }
                        .dashedBorder(AccessDefaults.TextPrimary, cornerRadius = 10.dp, width = 2.dp)
                        .clickable(role = Role.Button, onClick = onEdit),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("+", style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 18.sp),
                        color = AccessDefaults.TextPrimary, modifier = Modifier.clearAndSetSemantics {})
                }
            }
        }
    }
}

@Composable
internal fun BoardMovieTile(
    movie: Movie,
    onClick: () -> Unit,
    onRemove: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
) {
    PixelTile(
        accent = FilmAccent,
        tilt = -2f,
        onClick = onClick,
        onRemove = onRemove,
        removeLabel = stringResource(Res.string.profile_board_tile_remove, movie.title),
        modifier = modifier,
        onLongClick = onLongClick,
    ) {
        Box(Modifier.fillMaxSize().clip(PixelTileShape).background(PixelInk)) {
            PixelIcon(PixelFilm, columns = 12, rows = 12, size = 32.dp,
                modifier = Modifier.align(Alignment.Center))
            coil3.compose.AsyncImage(
                model = movie.posterUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth()
                    .background(PixelInk.copy(alpha = .94f))
                    .padding(horizontal = 8.dp, vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(movie.title, color = AccessDefaults.TextPrimary,
                    style = TextStyle(fontFamily = BricolageGrotesque, fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp, lineHeight = 15.sp),
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                movie.releaseYear?.let { year ->
                    Text(year, color = FilmAccent, style = TextStyle(fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold, fontSize = 10.sp))
                }
            }
        }
    }
}
