package com.kaanf.core.designsystem.profileboard.component

import com.kaanf.core.domain.model.movie.Movie

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.button.BaseMiniButton
import com.kaanf.core.designsystem.component.qr.UserQrCard
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.component.textfield.BaseTextField
import com.kaanf.core.designsystem.profileboard.BoardTileLimits
import com.kaanf.core.designsystem.profileboard.BoardTool
import com.kaanf.core.designsystem.profileboard.ProfileBoardAction
import com.kaanf.core.designsystem.profileboard.ProfileBoardState
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.designsystem.theme.Inter
import com.kaanf.core.domain.model.music.MusicTrack
import com.kaanf.core.domain.model.profileboard.BoardQrStyle
import com.kaanf.core.domain.model.profileboard.BoardQrTheme
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.profile_board_apply
import crew.core.designsystem.generated.resources.profile_board_ask_counter
import crew.core.designsystem.generated.resources.profile_board_ask_ideas
import crew.core.designsystem.generated.resources.profile_board_ask_placeholder
import crew.core.designsystem.generated.resources.profile_board_ask_sheet_sub
import crew.core.designsystem.generated.resources.profile_board_ask_title
import crew.core.designsystem.generated.resources.profile_board_ideas
import crew.core.designsystem.generated.resources.profile_board_loves_add
import crew.core.designsystem.generated.resources.profile_board_loves_custom
import crew.core.designsystem.generated.resources.profile_board_loves_options
import crew.core.designsystem.generated.resources.profile_board_loves_picked
import crew.core.designsystem.generated.resources.profile_board_loves_sheet_sub
import crew.core.designsystem.generated.resources.profile_board_loves_title
import crew.core.designsystem.generated.resources.profile_board_qr_sheet_apply
import crew.core.designsystem.generated.resources.profile_board_qr_sheet_sub
import crew.core.designsystem.generated.resources.profile_board_sheet_close
import crew.core.designsystem.generated.resources.profile_board_song_load_more
import crew.core.designsystem.generated.resources.profile_board_song_no_results
import crew.core.designsystem.generated.resources.profile_board_song_results
import crew.core.designsystem.generated.resources.profile_board_song_search
import crew.core.designsystem.generated.resources.profile_board_song_search_error
import crew.core.designsystem.generated.resources.profile_board_song_search_hint
import crew.core.designsystem.generated.resources.profile_board_song_sheet_sub
import crew.core.designsystem.generated.resources.profile_board_song_sheet_title
import crew.core.designsystem.generated.resources.profile_board_movie_sheet_title
import crew.core.designsystem.generated.resources.profile_board_movie_sheet_sub
import crew.core.designsystem.generated.resources.profile_board_movie_search
import crew.core.designsystem.generated.resources.profile_board_movie_search_hint
import crew.core.designsystem.generated.resources.profile_board_movie_search_error
import crew.core.designsystem.generated.resources.profile_board_movie_no_results
import crew.core.designsystem.generated.resources.profile_board_movie_results
import crew.core.designsystem.generated.resources.profile_board_retry
import crew.core.designsystem.generated.resources.profile_board_movie_load_more
import crew.core.designsystem.generated.resources.profile_board_tool_qr_skin
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

@Composable
private fun sectionLabelStyle() = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 10.sp,
    letterSpacing = 1.2.sp)

/** The sheet for [ProfileBoardState.openTool]; dismissing it keeps the board in edit mode. */
@Composable
internal fun BoardToolSheet(state: ProfileBoardState, onAction: (ProfileBoardAction) -> Unit) {
    val tool = state.openTool ?: return
    val close = { onAction(ProfileBoardAction.CloseBoardTool) }
    ContainerBottomSheet(onDismiss = close, dismissible = !state.movieSearch.isSaving) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            when (tool) {
                BoardTool.QR_SKIN -> QrSkinSheet(state, onAction, close)
                BoardTool.SONG -> SongSheet(state, onAction, close)
                BoardTool.ASK -> AskSheet(state.askMe, onAction, close)
                BoardTool.LOVES -> LovesSheet(state.hobbies, onAction, close)
                BoardTool.FILM -> MovieSheet(state, onAction, close)
            }
        }
    }
}

@Composable
private fun SheetHeader(title: String, subtitle: String, onClose: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = TextStyle(fontFamily = BricolageGrotesque, fontWeight = FontWeight.Bold,
                fontSize = 22.sp, lineHeight = 24.sp), color = AccessDefaults.TextPrimary)
            Text(subtitle, style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 13.sp),
                color = AccessDefaults.TextSecondary)
        }
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(AccessDefaults.SurfaceHigh)
                .clickable(role = Role.Button, onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(AccessIcons.Close), stringResource(Res.string.profile_board_sheet_close),
                tint = AccessDefaults.TextPrimary, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = sectionLabelStyle(), color = AccessDefaults.TextMuted)
}

@Composable
private fun QrSkinSheet(state: ProfileBoardState, onAction: (ProfileBoardAction) -> Unit, onClose: () -> Unit) {
    SheetHeader(stringResource(Res.string.profile_board_tool_qr_skin), stringResource(Res.string.profile_board_qr_sheet_sub), onClose)
    // Basic styles first, then premium themes; selecting previews the skin on the board draft.
    val skinIds = BoardQrStyle.entries.map { it.id } + BoardQrTheme.entries.map { it.id }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        skinIds.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { id ->
                    Box(Modifier.weight(1f)) {
                        SkinOption(id, state.publicCode, chosen = state.qrStyleId == id) {
                            onAction(ProfileBoardAction.SelectQrStyle(id))
                        }
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
    BaseButton(text = stringResource(Res.string.profile_board_qr_sheet_apply), onClick = onClose, filled = true)
}

@Composable
private fun SkinOption(id: String, publicCode: String, chosen: Boolean, onClick: () -> Unit) {
    val theme = BoardQrTheme.find(id)
    val label = theme?.label() ?: BoardQrStyle.entries.first { it.id == id }.label()
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        PaletteTile(label, chosen, onClick) {
            if (theme != null) Image(painterResource(theme.preview()), null, Modifier.padding(8.dp).fillMaxSize())
            else UserQrCard(inputText = publicCode.ifBlank { "CREW123456" }, styleId = id, qrContentDescription = label,
                modifier = Modifier.padding(8.dp).fillMaxSize())
        }
        Text(label, style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
            color = AccessDefaults.TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clearAndSetSemantics {})
    }
}

@Composable
private fun SongSheet(state: ProfileBoardState, onAction: (ProfileBoardAction) -> Unit, onClose: () -> Unit) {
    val search = state.songSearch
    val field = rememberTextFieldState(search.query)
    val latestQuery by rememberUpdatedState(search.query)
    LaunchedEffect(field) {
        snapshotFlow { field.text.toString() }.collect { if (it != latestQuery) onAction(ProfileBoardAction.SearchSongs(it)) }
    }
    var picked by remember { mutableStateOf(state.favoriteTrack) }
    Column(Modifier.fillMaxHeight(.9f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SheetHeader(stringResource(Res.string.profile_board_song_sheet_title),
            stringResource(Res.string.profile_board_song_sheet_sub), onClose)
        BaseTextField(state = field, placeholder = stringResource(Res.string.profile_board_song_search))
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val query = search.query.trim()
            when {
                query.length < 2 -> item { SheetMessage(stringResource(Res.string.profile_board_song_search_hint)) }
                search.hasError && search.tracks.isEmpty() -> item { SheetMessage(stringResource(Res.string.profile_board_song_search_error)) }
                !search.isLoading && search.tracks.isEmpty() ->
                    item { SheetMessage(stringResource(Res.string.profile_board_song_no_results, query)) }
                search.tracks.isNotEmpty() -> item {
                    Box(Modifier.padding(top = 2.dp, bottom = 6.dp)) { SectionLabel(stringResource(Res.string.profile_board_song_results)) }
                }
            }
            items(search.tracks, key = { it.id }) { track ->
                SongRow(track, selected = picked?.id == track.id) { picked = track }
            }
            if (search.isLoading) item {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(22.dp), color = AccessDefaults.TextPrimary, strokeWidth = 2.dp)
                }
            } else if (search.nextOffset != null && search.tracks.isNotEmpty()) item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TextButton(onClick = { onAction(ProfileBoardAction.LoadMoreSongs) }) {
                        Text(stringResource(Res.string.profile_board_song_load_more), color = AccessDefaults.TextPrimary)
                    }
                }
            }
        }
        val track = picked
        BaseButton(
            text = stringResource(Res.string.profile_board_apply),
            onClick = { if (track != null) onAction(ProfileBoardAction.ApplySong(track)) },
            enabled = track != null && track.id != state.favoriteTrack?.id,
            isLoading = search.isSaving,
            loadingText = stringResource(Res.string.profile_board_apply),
            filled = true,
        )
    }
}

@Composable
private fun SheetMessage(text: String) {
    Text(text, style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 14.sp),
        color = AccessDefaults.TextSecondary, textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp, horizontal = 8.dp))
}

@Composable
private fun SongRow(track: MusicTrack, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(if (selected) AccessDefaults.SurfaceHigh else AccessDefaults.Surface)
            .semantics(mergeDescendants = true) { this.selected = selected }
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(52.dp).clip(RoundedCornerShape(8.dp)).background(AccessDefaults.SurfaceHigh)
            .clearAndSetSemantics {}, contentAlignment = Alignment.Center) {
            Text(BoardTool.SONG.emoji, fontSize = 20.sp)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(track.title, style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                color = AccessDefaults.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artists.joinToString(), style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 13.sp),
                color = AccessDefaults.TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        SelectionDot(selected)
    }
}

@Composable
private fun SelectionDot(selected: Boolean) {
    Box(
        modifier = Modifier.size(24.dp).clip(CircleShape)
            .background(if (selected) AccessDefaults.Accent else AccessDefaults.Surface)
            .then(if (selected) Modifier else Modifier.border(2.dp, AccessDefaults.Border, CircleShape)),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) Icon(painterResource(AccessIcons.Check), null, tint = AccessDefaults.OnAccent, modifier = Modifier.size(14.dp))
    }
}

@Composable
private fun AskSheet(askMe: String?, onAction: (ProfileBoardAction) -> Unit, onClose: () -> Unit) {
    SheetHeader(stringResource(Res.string.profile_board_ask_title), stringResource(Res.string.profile_board_ask_sheet_sub), onClose)
    val field = rememberTextFieldState(askMe.orEmpty())
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BaseTextField(
            state = field,
            placeholder = stringResource(Res.string.profile_board_ask_placeholder),
            inputTransformation = InputTransformation.maxLength(BoardTileLimits.ASK_ME_LENGTH),
        )
        Text(stringResource(Res.string.profile_board_ask_counter, field.text.length, BoardTileLimits.ASK_ME_LENGTH),
            style = sectionLabelStyle(), color = AccessDefaults.TextMuted, modifier = Modifier.align(Alignment.End))
    }
    SectionLabel(stringResource(Res.string.profile_board_ideas))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        stringArrayResource(Res.array.profile_board_ask_ideas).forEach { idea ->
            BoardChip(idea, selected = false) { field.setTextAndPlaceCursorAtEnd(idea) }
        }
    }
    BaseButton(
        text = stringResource(Res.string.profile_board_apply),
        onClick = { onAction(ProfileBoardAction.ApplyAskMe(field.text.toString())) },
        enabled = field.text.isNotBlank(),
        filled = true,
    )
}

@Composable
private fun LovesSheet(hobbies: List<String>, onAction: (ProfileBoardAction) -> Unit, onClose: () -> Unit) {
    SheetHeader(stringResource(Res.string.profile_board_loves_title), stringResource(Res.string.profile_board_loves_sheet_sub), onClose)
    val picked = remember { mutableStateListOf(*hobbies.toTypedArray()) }
    val custom = rememberTextFieldState()
    val customText = custom.text.trim().toString()
    val full = picked.size >= BoardTileLimits.MAX_HOBBIES
    val canAdd = customText.isNotEmpty() && !full && picked.none { it.equals(customText, ignoreCase = true) }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        BaseTextField(
            state = custom,
            placeholder = stringResource(Res.string.profile_board_loves_custom),
            inputTransformation = InputTransformation.maxLength(BoardTileLimits.HOBBY_LENGTH),
            modifier = Modifier.weight(1f),
        )
        BaseMiniButton(
            text = stringResource(Res.string.profile_board_loves_add),
            onClick = {
                picked += customText
                custom.setTextAndPlaceCursorAtEnd("")
            },
            enabled = canAdd,
            filled = true,
        )
    }
    SectionLabel(stringResource(Res.string.profile_board_loves_picked, picked.size, BoardTileLimits.MAX_HOBBIES))
    val options = stringArrayResource(Res.array.profile_board_loves_options)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        (options + picked.filterNot { it in options }).forEach { option ->
            val selected = option in picked
            BoardChip(option, selected, enabled = selected || !full) {
                if (selected) picked.remove(option) else picked += option
            }
        }
    }
    BaseButton(
        text = stringResource(Res.string.profile_board_apply),
        onClick = { onAction(ProfileBoardAction.ApplyHobbies(picked.toList())) },
        enabled = picked.toList() != hobbies,
        filled = true,
    )
}

@Composable
private fun BoardChip(text: String, selected: Boolean, enabled: Boolean = true, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier.clip(CircleShape)
            .background(if (selected) AccessDefaults.Accent else AccessDefaults.SurfaceHigh)
            .then(if (selected) Modifier else Modifier.border(1.dp, AccessDefaults.Border, CircleShape))
            .semantics { this.selected = selected }
            .clickable(enabled = enabled, role = Role.Checkbox, onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 9.dp),
        style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
        color = when {
            selected -> AccessDefaults.OnAccent
            enabled -> AccessDefaults.TextPrimary
            else -> AccessDefaults.TextFaint
        },
    )
}

@Composable
private fun MovieSheet(state: ProfileBoardState, onAction: (ProfileBoardAction) -> Unit, onClose: () -> Unit) {
    val search = state.movieSearch
    val field = rememberTextFieldState(search.query)
    val latestQuery by rememberUpdatedState(search.query)
    LaunchedEffect(field) {
        snapshotFlow { field.text.toString() }.collect { if (it != latestQuery) onAction(ProfileBoardAction.SearchMovies(it)) }
    }
    var picked by remember { mutableStateOf(state.favoriteMovie) }
    Column(Modifier.fillMaxHeight(.9f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SheetHeader(stringResource(Res.string.profile_board_movie_sheet_title),
            stringResource(Res.string.profile_board_movie_sheet_sub), onClose)
        BaseTextField(state = field, placeholder = stringResource(Res.string.profile_board_movie_search))
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val query = search.query.trim()
            when {
                query.length < 2 -> item { SheetMessage(stringResource(Res.string.profile_board_movie_search_hint)) }
                !search.hasError && !search.isLoading && search.movies.isEmpty() ->
                    item { SheetMessage(stringResource(Res.string.profile_board_movie_no_results, query)) }
                search.movies.isNotEmpty() -> item {
                    Box(Modifier.padding(top = 2.dp, bottom = 6.dp)) { SectionLabel(stringResource(Res.string.profile_board_movie_results)) }
                }
            }
            items(search.movies, key = { it.id }) { movie ->
                MovieRow(movie, selected = picked?.id == movie.id) { picked = movie }
            }
            if (search.hasError) item {
                SheetMessage(stringResource(Res.string.profile_board_movie_search_error))
                TextButton(onClick = { onAction(if (search.movies.isEmpty()) ProfileBoardAction.SearchMovies(search.query)
                    else ProfileBoardAction.LoadMoreMovies) }) {
                    Text(stringResource(Res.string.profile_board_retry), color = AccessDefaults.TextPrimary)
                }
            }
            if (search.isLoading) item {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(22.dp), color = AccessDefaults.TextPrimary, strokeWidth = 2.dp)
                }
            } else if (!search.hasError && search.nextPage != null && search.movies.isNotEmpty()) item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TextButton(onClick = { onAction(ProfileBoardAction.LoadMoreMovies) }) {
                        Text(stringResource(Res.string.profile_board_movie_load_more), color = AccessDefaults.TextPrimary)
                    }
                }
            }
        }
        val movie = picked
        BaseButton(
            text = stringResource(Res.string.profile_board_apply),
            onClick = { if (movie != null) onAction(ProfileBoardAction.ApplyMovie(movie)) },
            enabled = movie != null && movie.id != state.favoriteMovie?.id,
            isLoading = search.isSaving,
            loadingText = stringResource(Res.string.profile_board_apply),
            filled = true,
        )
    }
}


@Composable
private fun MovieRow(movie: Movie, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(if (selected) AccessDefaults.SurfaceHigh else AccessDefaults.Surface)
            .semantics(mergeDescendants = true) { this.selected = selected }
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(52.dp).clip(RoundedCornerShape(8.dp)).background(AccessDefaults.SurfaceHigh)
            .clearAndSetSemantics {}, contentAlignment = Alignment.Center) {
            coil3.compose.AsyncImage(model = movie.posterUrl, contentDescription = null,
                modifier = Modifier.fillMaxSize(), contentScale = androidx.compose.ui.layout.ContentScale.Crop)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(movie.title, style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                color = AccessDefaults.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(movie.releaseYear.orEmpty(), style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 13.sp),
                color = AccessDefaults.TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        SelectionDot(selected)
    }
}

