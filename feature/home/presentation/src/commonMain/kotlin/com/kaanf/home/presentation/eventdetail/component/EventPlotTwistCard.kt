package com.kaanf.home.presentation.eventdetail.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.plot_twist_title
import crew.feature.home.presentation.generated.resources.plot_twist_question
import crew.feature.home.presentation.generated.resources.plot_twist_reveal
import crew.feature.home.presentation.generated.resources.plot_twist_another
import crew.feature.home.presentation.generated.resources.plot_twist_1
import crew.feature.home.presentation.generated.resources.plot_twist_2
import crew.feature.home.presentation.generated.resources.plot_twist_3
import crew.feature.home.presentation.generated.resources.plot_twist_4
import crew.feature.home.presentation.generated.resources.plot_twist_5
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

private val plotTwists = listOf(
    Res.string.plot_twist_1,
    Res.string.plot_twist_2,
    Res.string.plot_twist_3,
    Res.string.plot_twist_4,
    Res.string.plot_twist_5,
)

@Composable
fun EventPlotTwistCard(eventId: String, modifier: Modifier = Modifier) {
    var revealed by rememberSaveable(eventId) { mutableStateOf(false) }
    var scenario by rememberSaveable(eventId) { mutableIntStateOf(Random.nextInt(plotTwists.size)) }
    var flipping by remember(eventId) { mutableStateOf(false) }
    val rotation = remember(eventId) { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val revealLabel = stringResource(Res.string.plot_twist_reveal)
    val flip: () -> Unit = {
        if (!flipping) {
            flipping = true
            scope.launch {
                try {
                    rotation.animateTo(90f, tween(150))
                    if (revealed) {
                        // Draw a different possibility every time, without a network request.
                        scenario = (scenario + Random.nextInt(1, plotTwists.size)) % plotTwists.size
                    }
                    revealed = true
                    rotation.snapTo(-90f)
                    rotation.animateTo(0f, tween(200))
                } finally {
                    flipping = false
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.plot_twist_title),
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = BricolageGrotesque,
                fontWeight = FontWeight.Bold,
                color = AccessDefaults.TextPrimary,
            ),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 12f * density
                }
                .clip(RoundedCornerShape(16.dp))
                .background(if (revealed) AccessDefaults.Violet else AccessDefaults.Accent)
                .then(
                    if (!revealed) Modifier.clickable(
                        enabled = !flipping,
                        role = Role.Button,
                        onClickLabel = revealLabel,
                        onClick = flip,
                    ) else Modifier,
                )
                .heightIn(min = 272.dp)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Icon(
                    painter = painterResource(AccessIcons.Sparkle),
                    contentDescription = null,
                    tint = AccessDefaults.OnAccent,
                    modifier = Modifier.size(36.dp).graphicsLayer { rotationZ = -12f },
                )
            }
            Text(
                text = stringResource(if (revealed) plotTwists[scenario] else Res.string.plot_twist_question),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = BricolageGrotesque,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (revealed) 24.sp else 32.sp,
                    lineHeight = if (revealed) 30.sp else 36.sp,
                    color = AccessDefaults.OnAccent,
                ),
            )
            if (revealed) {
                TextButton(onClick = flip, enabled = !flipping) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(Res.string.plot_twist_another), color = AccessDefaults.OnAccent)
                        Icon(painterResource(AccessIcons.Refresh), null, tint = AccessDefaults.OnAccent, modifier = Modifier.size(18.dp))
                    }
                }
            } else {
                Row(Modifier.heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(revealLabel, color = AccessDefaults.OnAccent, style = MaterialTheme.typography.labelLarge)
                    Icon(painterResource(AccessIcons.ArrowUpRight), null, tint = AccessDefaults.OnAccent, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
