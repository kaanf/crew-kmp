package com.kaanf.game.presentation.gamelobby.component.custom

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.AvatarContent
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.presentation.model.LobbyMember
import com.kaanf.core.presentation.util.rememberIsResumed
import crew.feature.game.presentation.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.TimeSource

/** Local-only preview unless [onSayHi] and [incomingWaves] are connected by a socket adapter. */
@Composable
fun LobbyCrowd(
    members: List<LobbyMember>,
    totalCount: Int,
    self: LobbyMember,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    incomingWaves: Flow<LobbyWave> = emptyFlow(),
    onSayHi: (() -> Unit)? = null,
) {
    var showMembers by rememberSaveable { mutableStateOf(false) }
    val queue = remember { mutableStateListOf<LobbyWave>() }
    val recentIds = remember { linkedSetOf<String>() }
    val recentSenders = remember { linkedMapOf<String, TimeSource.Monotonic.ValueTimeMark>() }
    var activeWave by remember { mutableStateOf<LobbyWave?>(null) }
    var nextLocalId by remember { mutableLongStateOf(0L) }
    var nextHelloAt by rememberSaveable { mutableLongStateOf(0L) }
    var cooldown by remember { mutableIntStateOf(0) }
    val resumed by rememberIsResumed()
    val currentOnSayHi by rememberUpdatedState(onSayHi)
    val enqueue: (LobbyWave) -> Unit = { wave ->
        val lastFromSender = recentSenders[wave.sender.id]
        if (wave.id !in recentIds && queue.size < 6 &&
            (lastFromSender == null || lastFromSender.elapsedNow().inWholeSeconds >= 8)) {
            recentIds.add(wave.id)
            if (recentIds.size > 128) recentIds.remove(recentIds.first())
            recentSenders.remove(wave.sender.id)
            recentSenders[wave.sender.id] = TimeSource.Monotonic.markNow()
            if (recentSenders.size > 128) recentSenders.remove(recentSenders.keys.first())
            queue.add(wave)
        }
    }
    val currentEnqueue by rememberUpdatedState(enqueue)
    LaunchedEffect(incomingWaves, resumed) {
        if (resumed) incomingWaves.collect { currentEnqueue(it) }
    }
    LaunchedEffect(resumed, queue.firstOrNull()?.id) {
        activeWave = null
        if (!resumed) {
            queue.clear()
            return@LaunchedEffect
        }
        val next = queue.firstOrNull() ?: return@LaunchedEffect
        activeWave = next
        delay(2600)
        activeWave = null
        delay(200)
        queue.removeAll { it.id == next.id }
    }
    LaunchedEffect(nextHelloAt, resumed) {
        if (!resumed) return@LaunchedEffect
        do {
            val remaining = nextHelloAt - Clock.System.now().toEpochMilliseconds()
            cooldown = ((remaining.coerceAtLeast(0) + 999) / 1000).toInt()
            if (cooldown > 0) delay(250)
        } while (cooldown > 0)
    }
    if (showMembers) {
        LobbyMembersSheet(members = members, self = self, totalCount = totalCount,
            onDismiss = { showMembers = false })
    }
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val photoHeight = ((maxHeight - 296.dp) / 2).coerceIn(90.dp, 190.dp)
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(Res.string.lobby_people_title),
                        modifier = Modifier.weight(1f).semantics { heading() },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = BricolageGrotesque, fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.6).sp), color = AccessDefaults.TextPrimary)
                    Row(Modifier.clip(CircleShape).clickable(role = Role.Button) { showMembers = true }
                        .heightIn(min = 48.dp).padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(stringResource(Res.string.lobby_deck_count, totalCount),
                            style = MaterialTheme.typography.bodySmall, color = AccessDefaults.TextSecondary)
                        Icon(painterResource(AccessIcons.ArrowUpRight), null, Modifier.size(16.dp), tint = AccessDefaults.TextSecondary)
                    }
                }
                LobbyPeopleGrid(members, self, photoHeight, activeWave)
            }
            Spacer(Modifier.height(16.dp))
            LobbyHello(activeWave, cooldown, isPreview = onSayHi == null, onSayHi = {
                val now = Clock.System.now().toEpochMilliseconds()
                if (resumed && now >= nextHelloAt) {
                    nextHelloAt = now + 8000
                    cooldown = 8
                    if (currentOnSayHi != null) {
                        // Let the real adapter echo/acknowledge this wave; do not imply successful delivery.
                        currentOnSayHi?.invoke()
                    } else {
                        currentEnqueue(LobbyWave("local-${nextLocalId++}", self))
                    }
                }
            })
        }
    }
}

@Composable
private fun LobbyMembersSheet(
    members: List<LobbyMember>,
    self: LobbyMember,
    totalCount: Int,
    onDismiss: () -> Unit,
) {
    val available = (listOf(self) + members).distinctBy { it.id }
    ContainerBottomSheet(onDismiss = onDismiss) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.lobby_crowd_count, totalCount),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = BricolageGrotesque, fontWeight = FontWeight.ExtraBold),
                color = AccessDefaults.TextPrimary,
            )
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.lobby_crowd_close), color = AccessDefaults.TextSecondary)
            }
        }
        if (totalCount > available.size) {
            Text(stringResource(Res.string.lobby_crowd_available, available.size, totalCount),
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodySmall, color = AccessDefaults.TextMuted)
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = 440.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            items(available, key = { it.id }) { member ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AvatarCircle(
                        content = member.avatar.imageUrl?.let { AvatarContent.Image(it) }
                            ?: AvatarContent.Initials(member.avatar.label, member.avatar.color),
                        avatarSize = 44,
                        borderSize = 0,
                    )
                    Text(
                        text = if (member.id == self.id) stringResource(Res.string.lobby_crowd_you)
                            else member.fullName ?: member.avatar.label,
                        style = MaterialTheme.typography.bodyMedium, color = AccessDefaults.TextPrimary,
                    )
                }
            }
        }
    }
}
