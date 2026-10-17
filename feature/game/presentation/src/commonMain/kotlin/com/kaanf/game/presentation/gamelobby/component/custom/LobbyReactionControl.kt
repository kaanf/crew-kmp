package com.kaanf.game.presentation.gamelobby.component.custom

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.presentation.util.rememberIsResumed
import crew.feature.game.presentation.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LobbyReactionControl(onReaction: (String) -> Unit, modifier: Modifier = Modifier) {
    val emojis = listOf("👋", "🔥", "🪩", "❤️")
    val labels = listOf(
        stringResource(Res.string.lobby_reaction_wave), stringResource(Res.string.lobby_reaction_fire),
        stringResource(Res.string.lobby_reaction_disco), stringResource(Res.string.lobby_reaction_heart),
    )
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    var repeated by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(if (pressed) 0.91f else 1f, tween(110), label = "Reaction press")
    val currentOnReaction by rememberUpdatedState(onReaction)
    val haptic = LocalHapticFeedback.current
    val resumed by rememberIsResumed()
    LaunchedEffect(pressed, selected, resumed) {
        if (!pressed || !resumed) return@LaunchedEffect
        repeated = false
        delay(400)
        repeated = true
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        while (isActive) {
            currentOnReaction(emojis[selected])
            delay(180)
        }
    }
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.height(72.dp).clip(CircleShape).background(AccessDefaults.TextPrimary),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { selected = (selected + emojis.lastIndex) % emojis.size }) {
                Icon(painterResource(AccessIcons.LeftChevron),
                    contentDescription = stringResource(Res.string.lobby_reaction_previous),
                    tint = AccessDefaults.OnAccent.copy(alpha = 0.55f), modifier = Modifier.size(22.dp))
            }
            Box(
                modifier = Modifier.width(86.dp).fillMaxHeight()
                    .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
                    .clip(CircleShape)
                    .clickable(interactionSource = interaction, indication = LocalIndication.current, role = Role.Button) {
                        if (!repeated) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            currentOnReaction(emojis[selected])
                        }
                    }
                    .semantics { contentDescription = labels[selected] },
                contentAlignment = Alignment.Center,
            ) { Text(emojis[selected], fontSize = 42.sp) }
            IconButton(onClick = { selected = (selected + 1) % emojis.size }) {
                Icon(painterResource(AccessIcons.RightChevron),
                    contentDescription = stringResource(Res.string.lobby_reaction_next),
                    tint = AccessDefaults.OnAccent.copy(alpha = 0.55f), modifier = Modifier.size(22.dp))
            }
        }
        Text(
            stringResource(Res.string.lobby_reaction_hold_hint),
            modifier = Modifier.padding(top = 10.dp),
            style = MaterialTheme.typography.bodySmall,
            color = AccessDefaults.TextMuted,
        )
    }
}
