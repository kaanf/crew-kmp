package com.kaanf.game.presentation.gamelobby.component.custom

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.AvatarContent
import com.kaanf.core.designsystem.component.avatar.ExtraAvatarCircle
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.LobbyMember
import com.kaanf.core.presentation.model.UserAvatar
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.sin

/**
 * Live "who's here" cluster shown in the lobby. Renders up to [MAX_CELLS] cells in a
 * centered, slightly-overlapping 4 / 5 / 4 grid (a tightly-seated friend group):
 *
 *  - `total <= MAX_CELLS` → every member gets an avatar, no overflow badge.
 *  - `total >= MAX_CELLS + 1` → first [MAX_AVATARS] members get avatars and the last
 *    cell becomes a `+N` badge where `N = total - MAX_AVATARS`.
 *
 * Members are keyed by [LobbyMember.id], so joins pop in, leaves pop out, and the
 * remaining avatars reflow to close the gap. Every cell also rides a faint, continuous
 * Mexican wave that ripples left → right.
 *
 * Reserves a constant 3-row height so the surrounding screen never jumps as people
 * come and go.
 */
@Composable
fun LobbyPresenceCluster(
    members: List<LobbyMember>,
    totalCount: Int,
    modifier: Modifier = Modifier,
    avatarSize: Int = 44,
) {
    val step = avatarSize * (1f - OVERLAP)

    // How many real avatars to draw, and whether the last cell is a +N badge.
    val avatarCount = if (totalCount <= MAX_CELLS) totalCount else MAX_AVATARS
    val badgeCount = if (totalCount > MAX_CELLS) totalCount - MAX_AVATARS else 0
    val occupiedCells = if (totalCount <= MAX_CELLS) totalCount else MAX_CELLS

    val cells = remember(occupiedCells, step) { buildCells(occupiedCells, step) }
    val visibleMembers = remember(members, avatarCount) { members.take(avatarCount) }

    // Render list keeps members that are animating out until their pop-out finishes,
    // so a leave can play even though the member is already gone from [members].
    val rendered = remember { mutableStateListOf<LobbyMember>() }

    LaunchedEffect(visibleMembers) {
        visibleMembers.forEach { member ->
            if (rendered.none { it.id == member.id }) rendered.add(member)
        }
    }

    val wavePhase = rememberWavePhase()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height((2f * step + avatarSize).dp),
        contentAlignment = Alignment.Center,
    ) {
        rendered.forEach { member ->
            key(member.id) {
                val presentIndex = visibleMembers.indexOfFirst { it.id == member.id }
                val present = presentIndex >= 0
                PresenceAvatar(
                    member = member,
                    present = present,
                    targetCell = if (present) cells.getOrNull(presentIndex) else null,
                    avatarSize = avatarSize,
                    step = step,
                    wavePhase = wavePhase,
                    onExited = { rendered.removeAll { it.id == member.id } },
                )
            }
        }

        if (badgeCount > 0) {
            cells.lastOrNull()?.let { cell ->
                ClusterCell(
                    cell = cell,
                    step = step,
                    wavePhase = wavePhase,
                    popIn = true,
                ) {
                    ExtraAvatarCircle(count = badgeCount, avatarSize = avatarSize)
                }
            }
        }
    }
}

@Composable
private fun PresenceAvatar(
    member: LobbyMember,
    present: Boolean,
    targetCell: Cell?,
    avatarSize: Int,
    step: Float,
    wavePhase: Float,
    onExited: () -> Unit,
) {
    val scale = remember { Animatable(0f) }

    // Freeze the last slot while leaving so the avatar pops out in place instead of
    // sliding to (0, 0).
    var cell by remember { mutableStateOf(targetCell ?: Cell(0f, 0f)) }
    if (targetCell != null) cell = targetCell

    LaunchedEffect(present) {
        if (present) {
            scale.animateTo(1f, popInSpring())
        } else {
            scale.animateTo(0f, tween(durationMillis = POP_OUT_MS))
            onExited()
        }
    }

    AnimatedClusterCell(
        cell = cell,
        step = step,
        wavePhase = wavePhase,
        scale = scale.value,
    ) {
        AvatarCircle(
            content = member.avatar.imageUrl
                ?.let { AvatarContent.Image(it) }
                ?: AvatarContent.Initials(label = member.avatar.label, color = member.avatar.color),
            avatarSize = avatarSize,
        )
    }
}

/** A cell that pops in once on first composition (used for the static overflow badge). */
@Composable
private fun ClusterCell(
    cell: Cell,
    step: Float,
    wavePhase: Float,
    popIn: Boolean,
    content: @Composable () -> Unit,
) {
    val scale = remember { Animatable(if (popIn) 0f else 1f) }
    LaunchedEffect(Unit) { if (popIn) scale.animateTo(1f, popInSpring()) }
    AnimatedClusterCell(cell = cell, step = step, wavePhase = wavePhase, scale = scale.value, content = content)
}

/** Places [content] at [cell] with spring-animated reflow, the shared wave, and a scale. */
@Composable
private fun AnimatedClusterCell(
    cell: Cell,
    step: Float,
    wavePhase: Float,
    scale: Float,
    content: @Composable () -> Unit,
) {
    val x by animateFloatAsState(cell.x, reflowSpring(), label = "cellX")
    val y by animateFloatAsState(cell.y, reflowSpring(), label = "cellY")
    val waveY = waveLiftFor(wavePhase, x, step) * WAVE_AMPLITUDE

    Box(
        modifier = Modifier
            .offset { IntOffset(x.dp.roundToPx(), (y - waveY).dp.roundToPx()) }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = scale
            },
    ) {
        content()
    }
}

/** Cell center offset, in dp, relative to the cluster center. */
private data class Cell(val x: Float, val y: Float)

/** Distributes [occupied] cells top-down into the [ROW_CAPS] rows, each row centered. */
private fun buildCells(occupied: Int, step: Float): List<Cell> {
    val rows = mutableListOf<Int>()
    var remaining = occupied
    for (cap in ROW_CAPS) {
        if (remaining <= 0) break
        val n = minOf(cap, remaining)
        rows.add(n)
        remaining -= n
    }
    val totalHeight = (rows.size - 1).coerceAtLeast(0) * step
    val cells = mutableListOf<Cell>()
    rows.forEachIndexed { row, count ->
        val y = row * step - totalHeight / 2f
        for (col in 0 until count) {
            val x = (col - (count - 1) / 2f) * step
            cells.add(Cell(x, y))
        }
    }
    return cells
}

@Composable
private fun rememberWavePhase(): Float {
    val transition = rememberInfiniteTransition(label = "lobbyWave")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = WAVE_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "lobbyWavePhase",
    )
    return phase
}

/**
 * Faint sine bump that ripples left → right. The horizontal position [x] is normalized
 * into a phase shift so cells further right crest slightly later, giving the wave.
 */
private fun waveLiftFor(phase: Float, x: Float, step: Float): Float {
    val halfSpan = (ROW_CAPS.max() - 1) / 2f * step
    val normalized = if (halfSpan == 0f) 0.5f else (x + halfSpan) / (2f * halfSpan)
    val local = phase - normalized * WAVE_SPREAD
    return if (local in 0f..WAVE_BUMP_WIDTH) {
        sin(local / WAVE_BUMP_WIDTH * PI).toFloat()
    } else {
        0f
    }
}

private fun popInSpring() = spring<Float>(
    dampingRatio = 0.55f,
    stiffness = Spring.StiffnessMediumLow,
)

private fun reflowSpring() = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessLow,
)

private val ROW_CAPS = intArrayOf(4, 5, 4)
private const val MAX_CELLS = 13
private const val MAX_AVATARS = MAX_CELLS - 1

private const val OVERLAP = 0.16f
private const val WAVE_AMPLITUDE = 3.5f
private const val WAVE_DURATION_MS = 2600
private const val WAVE_BUMP_WIDTH = 0.5f
private const val WAVE_SPREAD = 0.5f
private const val POP_OUT_MS = 220

@Composable
@Preview
private fun LobbyPresenceClusterPreview() {
    val members = listOf(
        LobbyMember("1", UserAvatar("E", Color(0xFFFF5A7A))),
        LobbyMember("2", UserAvatar("M", Color(0xFFC8FF3D))),
        LobbyMember("3", UserAvatar("A", Color(0xFF5BE0C5))),
        LobbyMember("4", UserAvatar("J", Color(0xFF6FB7FF))),
        LobbyMember("5", UserAvatar("K", Color(0xFFFF7A5C))),
        LobbyMember("6", UserAvatar("R", Color(0xFF5BE0C5))),
        LobbyMember("7", UserAvatar("A", Color(0xFFFF5A7A))),
        LobbyMember("8", UserAvatar("L", Color(0xFFFFB341))),
        LobbyMember("9", UserAvatar("S", Color(0xFF6FB7FF))),
        LobbyMember("10", UserAvatar("T", Color(0xFFC8FF3D))),
        LobbyMember("11", UserAvatar("N", Color(0xFFFF7A5C))),
        LobbyMember("12", UserAvatar("O", Color(0xFF5BE0C5))),
    )
    CrewTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            LobbyPresenceCluster(members = members, totalCount = 47)
        }
    }
}
