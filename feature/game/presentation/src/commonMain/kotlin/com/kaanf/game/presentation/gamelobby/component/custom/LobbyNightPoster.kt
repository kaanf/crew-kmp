package com.kaanf.game.presentation.gamelobby.component.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.presentation.model.LobbyMember
import com.kaanf.core.presentation.util.mediapicker.encodeJpeg
import crew.feature.game.presentation.generated.resources.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LobbyNightPoster(members: List<LobbyMember>, self: LobbyMember, totalCount: Int) {
    var style by rememberSaveable { mutableIntStateOf(0) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    // Stable positions; no timer or animation loop, even in a crowded lobby.
    val featured = remember(members, self) {
        members.filterNot { it.id == self.id }.distinctBy { it.id }.take(4) + self
    }
    val openLabel = stringResource(Res.string.lobby_poster_open)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NightPosterArtwork(featured, self.id, totalCount, style,
            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .clickable(role = Role.Button, onClickLabel = openLabel) { expanded = true })
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            PosterStyles(style, { style = it }, Modifier.weight(1f))
            TextButton(onClick = { expanded = true }) {
                Text(openLabel, color = AccessDefaults.TextSecondary,
                    style = MaterialTheme.typography.labelMedium)
                Icon(painterResource(AccessIcons.ArrowUpRight), null,
                    Modifier.padding(start = 4.dp).size(16.dp), tint = AccessDefaults.TextSecondary)
            }
        }
    }
    if (expanded) {
        // Freeze the cast while editing/exporting; socket updates cannot change the saved composition.
        val snapshot = remember { featured.toList() }
        val count = remember { totalCount }
        PosterViewer(snapshot, self.id, count, style, { style = it }, { expanded = false })
    }
}

@Composable
private fun PosterStyles(selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val names = listOf(Res.string.lobby_poster_paper, Res.string.lobby_poster_pop, Res.string.lobby_poster_acid)
    Row(modifier.selectableGroup()) {
        names.forEachIndexed { index, name ->
            val label = stringResource(name)
            Box(Modifier.size(48.dp).clip(CircleShape)
                .selectable(selected == index, role = Role.RadioButton, onClick = { onSelect(index) })
                .semantics { contentDescription = label }, contentAlignment = Alignment.Center) {
                Box(Modifier.size(if (selected == index) 30.dp else 22.dp)
                    .background(posterColor(index), CircleShape), contentAlignment = Alignment.Center) {
                    if (selected == index) Box(Modifier.size(8.dp).background(AccessDefaults.OnAccent, CircleShape))
                }
            }
        }
    }
}

private fun posterColor(style: Int): Color = when (style) {
    1 -> AccessDefaults.Blush
    2 -> AccessDefaults.Accent
    else -> AccessDefaults.TextPrimary
}

private data class PhotoPlacement(val x: Float, val y: Float, val width: Float, val height: Float, val angle: Float)
private val posterLayouts = listOf(
    listOf(PhotoPlacement(.05f, .32f, .32f, .33f, -12f), PhotoPlacement(.66f, .29f, .29f, .32f, 10f),
        PhotoPlacement(.04f, .63f, .29f, .28f, 6f), PhotoPlacement(.69f, .62f, .28f, .29f, -8f)),
    listOf(PhotoPlacement(.06f, .31f, .30f, .30f, 9f), PhotoPlacement(.64f, .37f, .31f, .32f, -12f),
        PhotoPlacement(.06f, .66f, .31f, .26f, -8f), PhotoPlacement(.67f, .69f, .27f, .24f, 8f)),
    listOf(PhotoPlacement(.04f, .35f, .31f, .32f, -6f), PhotoPlacement(.65f, .31f, .31f, .32f, 7f),
        PhotoPlacement(.08f, .68f, .27f, .25f, 10f), PhotoPlacement(.67f, .65f, .28f, .27f, -10f)),
)

/** Shared artwork for the lobby, full-screen view and exported image. */
@Composable
private fun NightPosterArtwork(
    members: List<LobbyMember>, selfId: String, count: Int, style: Int, modifier: Modifier = Modifier,
) {
    val ink = AccessDefaults.OnAccent
    BoxWithConstraints(modifier.aspectRatio(.88f).background(posterColor(style))) {
        val w = maxWidth
        val h = maxHeight
        val headline = stringResource(Res.string.lobby_poster_headline)
        Text(headline, modifier = Modifier.fillMaxWidth().padding(horizontal = w * .055f, vertical = h * .025f),
            fontFamily = BricolageGrotesque, fontWeight = FontWeight.ExtraBold,
            fontSize = (w.value * .17f).sp, lineHeight = (w.value * .15f).sp,
            letterSpacing = (-1.5).sp, color = ink, maxLines = 2,
            autoSize = TextAutoSize.StepBased(minFontSize = 22.sp, maxFontSize = (w.value * .17f).sp))
        val others = members.filterNot { it.id == selfId }
        others.forEachIndexed { index, member ->
            val p = posterLayouts[style][index]
            PosterPortrait(member, false, ink,
                Modifier.offset(x = w * p.x, y = h * p.y).size(w * p.width, h * p.height).rotate(p.angle))
        }
        members.firstOrNull { it.id == selfId }?.let { self ->
            val solo = others.isEmpty()
            PosterPortrait(self, true, ink,
                Modifier.offset(x = w * if (solo) .24f else .29f, y = h * .40f)
                    .size(w * if (solo) .52f else .43f, h * .44f)
                    .rotate(if (style == 1) 5f else -4f))
        }
        Row(Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            .padding(horizontal = w * .055f, vertical = h * .02f),
            verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.lobby_deck_count, count), Modifier.weight(1f),
                color = ink, style = MaterialTheme.typography.labelSmall, maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Text("crew", color = ink, fontFamily = BricolageGrotesque,
                fontWeight = FontWeight.ExtraBold, fontSize = (w.value * .065f).sp)
        }
    }
}

@Composable
private fun PosterPortrait(member: LobbyMember, isSelf: Boolean, ink: Color, modifier: Modifier) {
    val name = if (isSelf) stringResource(Res.string.lobby_crowd_you)
        else member.fullName?.substringBefore(" ")?.takeIf { it.isNotBlank() } ?: member.avatar.label
    Column(modifier.background(AccessDefaults.TextPrimary).padding(3.dp)
        .semantics(mergeDescendants = true) { contentDescription = name }) {
        Box(Modifier.fillMaxWidth().weight(1f).background(member.avatar.color), contentAlignment = Alignment.Center) {
            Text(member.avatar.label.ifBlank { "?" }, color = ink,
                fontFamily = BricolageGrotesque, fontWeight = FontWeight.ExtraBold,
                fontSize = if (isSelf) 64.sp else 36.sp, maxLines = 1,
                autoSize = TextAutoSize.StepBased(minFontSize = 16.sp, maxFontSize = if (isSelf) 64.sp else 36.sp))
            member.avatar.imageUrl?.takeIf { it.isNotBlank() }?.let {
                BaseImage(imageUrl = it, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
            }
        }
        Text(name, Modifier.fillMaxWidth().background(if (isSelf) AccessDefaults.Accent else AccessDefaults.TextPrimary)
            .padding(horizontal = 4.dp, vertical = 3.dp),
            textAlign = TextAlign.Center, color = ink, fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun PosterViewer(
    members: List<LobbyMember>, selfId: String, totalCount: Int, style: Int,
    onStyle: (Int) -> Unit, onDismiss: () -> Unit,
) {
    val layer = rememberGraphicsLayer()
    val share = rememberPosterShareLauncher()
    val scope = rememberCoroutineScope()
    var exporting by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize(), color = AccessDefaults.Background) {
            Column(Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(Res.string.lobby_poster_title), Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge, color = AccessDefaults.TextPrimary,
                        fontFamily = BricolageGrotesque, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(Res.string.lobby_crowd_close), color = AccessDefaults.TextSecondary)
                    }
                }
                Spacer(Modifier.height(20.dp))
                NightPosterArtwork(members, selfId, totalCount, style,
                    Modifier.widthIn(max = 440.dp).fillMaxWidth().drawWithContent {
                        layer.record { this@drawWithContent.drawContent() }
                        drawLayer(layer)
                    })
                Spacer(Modifier.height(16.dp))
                // Keep the composition fixed while the layer is captured.
                if (!exporting) PosterStyles(style, onStyle)
                else CircularProgressIndicator(Modifier.padding(12.dp).size(24.dp), color = AccessDefaults.Accent)
                Button(enabled = !exporting, onClick = {
                    scope.launch {
                        exporting = true
                        failed = false
                        try {
                            val bytes = encodeJpeg(layer.toImageBitmap(), quality = 95)
                            check(bytes.isNotEmpty())
                            share(bytes)
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (_: Exception) {
                            failed = true
                        } finally {
                            exporting = false
                        }
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = AccessDefaults.Accent, contentColor = AccessDefaults.OnAccent),
                    modifier = Modifier.padding(top = 12.dp).heightIn(min = 48.dp)) {
                    Icon(painterResource(AccessIcons.Share), null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.lobby_poster_share))
                }
                if (failed) Text(stringResource(Res.string.lobby_poster_export_error),
                    Modifier.padding(top = 12.dp), color = AccessDefaults.Error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
internal expect fun rememberPosterShareLauncher(): suspend (ByteArray) -> Unit
