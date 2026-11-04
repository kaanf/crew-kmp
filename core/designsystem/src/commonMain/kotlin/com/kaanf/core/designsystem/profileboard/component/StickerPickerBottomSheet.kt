package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.designsystem.theme.Inter
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.qr.UserQrCard
import com.kaanf.core.designsystem.profileboard.*
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.domain.model.profileboard.BoardQrStyle
import com.kaanf.core.domain.model.profileboard.BoardQrTheme
import com.kaanf.core.domain.model.profileboard.BoardStickerDesign
import crew.core.designsystem.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Non-modal palette: the board above remains visible and receives all editing gestures. */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun BoardEditorPanel(state: ProfileBoardState, onAction: (ProfileBoardAction) -> Unit, isActive: Boolean = true) {
    BackHandler(enabled = isActive) { onAction(ProfileBoardAction.CancelEditing) }
    var query by remember { mutableStateOf("") }
    var showAll by remember { mutableStateOf(true) }
    val qrTab = state.editorCategory == BoardEditorCategory.QR
    val categories = BoardEditorCategory.entries.filter { it != BoardEditorCategory.QR }
    val stickerLabels = BoardStickerDesign.entries.associateWith { it.label() }
    val designs = BoardStickerDesign.entries.filter {
        (showAll || it.category == state.editorCategory.stickerCategory) &&
            stickerLabels.getValue(it).contains(query.trim(), ignoreCase = true)
    }
    val panelColor = Color(0xFF100E0B)
    val tileColor = Color(0xFF181510)
    val outline = Color(0xFF332E27)
    val text = AccessDefaults.TextPrimary
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        Surface(color = panelColor, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, outline),
            modifier = Modifier.fillMaxWidth().height((maxHeight * .58f).coerceAtMost(440.dp))) {
            Column(Modifier.padding(horizontal = 16.dp).padding(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.align(Alignment.CenterHorizontally).size(36.dp, 4.dp)
                    .background(Color(0xFF4B4740), CircleShape))
                Text(stringResource(Res.string.profile_board_edit),
                    fontFamily = BricolageGrotesque, fontWeight = FontWeight.Bold,
                    fontSize = 23.sp, color = text, modifier = Modifier.padding(vertical = 4.dp))
                Row(Modifier.fillMaxWidth().height(44.dp)
                    .background(tileColor, CircleShape).border(1.dp, outline, CircleShape).padding(3.dp)) {
                    PanelTab(stringResource(Res.string.profile_board_stickers), !qrTab, Modifier.weight(1f)) {
                        showAll = true
                        onAction(ProfileBoardAction.SelectEditorCategory(BoardEditorCategory.CREW))
                    }
                    PanelTab(stringResource(Res.string.profile_board_qr_design), qrTab, Modifier.weight(1f)) {
                        query = ""
                        onAction(ProfileBoardAction.SelectEditorCategory(BoardEditorCategory.QR))
                    }
                }
                if (!qrTab) {
                    OutlinedTextField(value = query, onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth().height(50.dp), singleLine = true,
                        shape = CircleShape,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = Inter, color = text),
                        placeholder = { Text(stringResource(Res.string.profile_board_search_stickers), fontFamily = Inter) },
                        leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(20.dp)) },
                        trailingIcon = {
                            if (query.isNotEmpty()) IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, stringResource(Res.string.profile_board_clear_search), Modifier.size(18.dp))
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = tileColor, unfocusedContainerColor = tileColor,
                            focusedBorderColor = text, unfocusedBorderColor = outline,
                            focusedTextColor = text, unfocusedTextColor = text,
                            cursorColor = text, focusedLeadingIconColor = text, unfocusedLeadingIconColor = text,
                            focusedPlaceholderColor = AccessDefaults.TextSecondary,
                            unfocusedPlaceholderColor = AccessDefaults.TextSecondary,
                            focusedTrailingIconColor = text, unfocusedTrailingIconColor = text))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            CategoryPill(stringResource(Res.string.profile_board_category_all), showAll) { showAll = true }
                        }
                        items(categories, key = { it.name }) { category ->
                            CategoryPill(category.label(), !showAll && category == state.editorCategory) {
                                showAll = false
                                onAction(ProfileBoardAction.SelectEditorCategory(category))
                            }
                        }
                    }
                }
                LazyVerticalGrid(columns = GridCells.Fixed(if (qrTab) 3 else 4),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (qrTab) {
                        items(BoardQrStyle.entries, key = { it.id }) { style ->
                            PaletteTile(style.label(), state.qrStyleId == style.id,
                                onClick = { onAction(ProfileBoardAction.SelectQrStyle(style.id)) }) {
                                UserQrCard(inputText = state.publicCode.ifBlank { "CREW123456" }, styleId = style.id,
                                    qrContentDescription = style.label(), modifier = Modifier.padding(8.dp).fillMaxSize())
                            }
                        }
                        items(BoardQrTheme.entries, key = { it.id }) { theme ->
                            PaletteTile(theme.label(), state.qrStyleId == theme.id,
                                onClick = { onAction(ProfileBoardAction.SelectQrStyle(theme.id)) }) {
                                Image(painterResource(theme.preview()), null, Modifier.padding(8.dp).fillMaxSize())
                            }
                        }
                    } else {
                        items(designs, key = { it.id }) { design ->
                            val chosen = state.stickers.any { it.slot == state.focusedStickerSlot && it.design == design }
                            PaletteTile(stickerLabels.getValue(design), chosen,
                                onClick = { onAction(ProfileBoardAction.SelectSticker(design)) }) {
                                BoardStickerArtwork(design, if (design.round) Modifier.fillMaxSize(.65f)
                                    else Modifier.fillMaxWidth(.86f).aspectRatio(design.width / design.height))
                            }
                        }
                        if (designs.isEmpty()) item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                            Text(stringResource(Res.string.profile_board_no_stickers), color = AccessDefaults.TextSecondary,
                                fontFamily = Inter, modifier = Modifier.padding(vertical = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PanelTab(label: String, chosen: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(modifier.fillMaxHeight().clip(CircleShape)
        .background(if (chosen) AccessDefaults.TextPrimary else Color.Transparent)
        .semantics { selected = chosen }.clickable(role = Role.Tab, onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 14.sp,
            color = if (chosen) Color.Black else AccessDefaults.TextSecondary)
    }
}

@Composable
private fun CategoryPill(label: String, chosen: Boolean, onClick: () -> Unit) {
    Box(Modifier.height(34.dp).clip(CircleShape)
        .background(if (chosen) AccessDefaults.TextPrimary else Color.Transparent)
        .border(1.dp, Color(0xFF332E27), CircleShape)
        .semantics { selected = chosen }.clickable(role = Role.RadioButton, onClick = onClick)
        .padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
        Text(label, fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 12.sp,
            color = if (chosen) Color.Black else AccessDefaults.TextSecondary)
    }
}

@Composable
private fun PaletteTile(label: String, chosen: Boolean, onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Box(Modifier.fillMaxWidth().aspectRatio(1f).clip(shape)
        .background(Color(0xFF181510))
        .border(if (chosen) 2.dp else 1.dp, if (chosen) AccessDefaults.Accent else Color(0xFF332E27), shape)
        .semantics { contentDescription = label; selected = chosen }
        .clickable(role = Role.RadioButton, onClick = onClick), contentAlignment = Alignment.Center) {
        content()
        if (chosen) Box(Modifier.align(Alignment.TopEnd).padding(5.dp).size(18.dp)
            .background(AccessDefaults.Accent, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Check, null, tint = Color.Black, modifier = Modifier.size(13.dp))
        }
    }
}

@Composable
private fun BoardEditorCategory.label(): String = stringResource(when (this) {
    BoardEditorCategory.QR -> Res.string.profile_board_qr_design
    BoardEditorCategory.HOBBIES -> Res.string.profile_board_category_hobbies
    BoardEditorCategory.MUSIC -> Res.string.profile_board_category_music
    BoardEditorCategory.SPORTS -> Res.string.profile_board_category_sports
    BoardEditorCategory.SOCIAL -> Res.string.profile_board_category_social
    BoardEditorCategory.CREW -> Res.string.profile_board_category_crew
})

@Composable
private fun BoardQrTheme.label(): String = stringResource(when (this) {
    BoardQrTheme.HAMSTER -> Res.string.profile_board_qr_theme_hamster
    BoardQrTheme.GINGERBREAD -> Res.string.profile_board_qr_theme_gingerbread
    BoardQrTheme.RED_PLUSH -> Res.string.profile_board_qr_theme_red_plush
    BoardQrTheme.VAMPIRE -> Res.string.profile_board_qr_theme_vampire
})

/** Sample artwork for choosing a theme; it never carries the user's code, so it is not scannable. */
internal fun BoardQrTheme.preview(): DrawableResource = when (this) {
    BoardQrTheme.HAMSTER -> Res.drawable.qr_theme_hamster
    BoardQrTheme.GINGERBREAD -> Res.drawable.qr_theme_gingerbread
    BoardQrTheme.RED_PLUSH -> Res.drawable.qr_theme_red_plush
    BoardQrTheme.VAMPIRE -> Res.drawable.qr_theme_vampire
}

@Composable
private fun BoardQrStyle.label(): String = stringResource(when (this) {
    BoardQrStyle.ROUNDED -> Res.string.profile_board_qr_rounded
    BoardQrStyle.CLASSIC -> Res.string.profile_board_qr_classic
    BoardQrStyle.DOTS -> Res.string.profile_board_qr_dots
})
