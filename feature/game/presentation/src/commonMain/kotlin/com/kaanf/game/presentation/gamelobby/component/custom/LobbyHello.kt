package com.kaanf.game.presentation.gamelobby.component.custom

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.AvatarContent
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.model.LobbyMember
import crew.feature.game.presentation.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Presentation event. A future socket adapter supplies a unique event ID and sender snapshot. */
data class LobbyWave(val id: String, val sender: LobbyMember)

@Composable
internal fun LobbyHello(
    wave: LobbyWave?, cooldownSeconds: Int, isPreview: Boolean, onSayHi: () -> Unit,
) {
    val noticeHeight = with(LocalDensity.current) { 44.sp.toDp() } + 16.dp
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = onSayHi, enabled = cooldownSeconds == 0,
            shape = CircleShape, modifier = Modifier.heightIn(min = 48.dp),
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccessDefaults.TextPrimary, contentColor = AccessDefaults.OnAccent,
                disabledContainerColor = AccessDefaults.SurfaceHigh, disabledContentColor = AccessDefaults.TextSecondary,
            )) {
            Text("👋", fontSize = 24.sp)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(Res.string.lobby_hello_action), style = MaterialTheme.typography.titleMedium)
        }
        // Reserved space prevents each incoming wave from shifting the photo grid or button.
        Box(Modifier.fillMaxWidth().heightIn(min = noticeHeight).semantics { liveRegion = LiveRegionMode.Polite },
            contentAlignment = Alignment.Center) {
            AnimatedContent(targetState = wave, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "Lobby hello") { current ->
                if (current == null) {
                    Text(
                        text = if (cooldownSeconds > 0) stringResource(Res.string.lobby_hello_cooldown, cooldownSeconds)
                            else stringResource(if (isPreview) Res.string.lobby_hello_preview_hint else Res.string.lobby_hello_hint),
                        color = AccessDefaults.TextSecondary, style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center,
                    )
                } else {
                    Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val avatar = current.sender.avatar
                        AvatarCircle(content = avatar.imageUrl?.takeIf { it.isNotBlank() }?.let { AvatarContent.Image(it) }
                            ?: AvatarContent.Initials(avatar.label, avatar.color), avatarSize = 28, borderSize = 0)
                        Text(stringResource(Res.string.lobby_hello_from,
                            current.sender.fullName?.takeIf { it.isNotBlank() } ?: avatar.label),
                            color = AccessDefaults.TextPrimary, style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false))
                        key(current.id) { WavingHand() }
                    }
                }
            }
        }
    }
}

@Composable
internal fun WavingHand(modifier: Modifier = Modifier) {
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        rotation.animateTo(0f, keyframes {
            durationMillis = 850
            0f at 0; -18f at 160; 16f at 320; -12f at 470; 10f at 620; 0f at 850
        })
    }
    Text("👋", fontSize = 30.sp, modifier = modifier.graphicsLayer { rotationZ = rotation.value })
}
