package com.kaanf.game.presentation.gamelobby.component.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessIcons
import org.jetbrains.compose.resources.painterResource

@Immutable
private data class ChecklistItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val accent: Color,
)

@Composable
fun BeforeTheBell(
    modifier: Modifier = Modifier,
) {
    var checkedIds by rememberSaveable(
        stateSaver = listSaver(
            save = { it.toList() },
            restore = { it.toSet() },
        ),
    ) { mutableStateOf(emptySet<String>()) }

    val total = checklistItems.size
    val readyCount = checklistItems.count { it.id in checkedIds }
    val allReady = total > 0 && readyCount == total

    Column(
        modifier = modifier
            .clip(AccessShapes.XXLarge)
            .background(AccessDefaults.Surface)
            .border(1.dp, AccessDefaults.Border, AccessShapes.XXLarge)
            .padding(20.dp),
    ) {
        Header(readyCount = readyCount, total = total, allReady = allReady)

        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            checklistItems.forEach { item ->
                ChecklistRow(
                    item = item,
                    checked = item.id in checkedIds,
                    onToggle = {
                        checkedIds = if (item.id in checkedIds) checkedIds - item.id
                        else checkedIds + item.id
                    },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Footer(allReady = allReady)
    }
}

@Composable
private fun Header(readyCount: Int, total: Int, allReady: Boolean) {
    val counterColor by animateColorAsState(
        targetValue = if (allReady) {
            AccessDefaults.Accent
        } else {
            AccessDefaults.TextMuted
        },
        label = "counterColor",
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "BEFORE THE BELL",
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 2.sp
            ),
        )
        Text(
            text = "$readyCount/$total ready",
            style = MaterialTheme.typography.labelSmall.copy(
                color = counterColor,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            ),
        )
    }
}

@Composable
private fun ChecklistRow(
    item: ChecklistItem,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    val accent = item.accent

    val rowBg by animateColorAsState(
        targetValue = if (checked) {
            accent.copy(alpha = 0.1f).compositeOver(AccessDefaults.SurfaceElevated)
        } else {
            AccessDefaults.SurfaceElevated
        },
        label = "rowBg",
    )
    val rowBorder by animateColorAsState(
        targetValue = if (checked) accent.copy(alpha = 0.55f) else accent.copy(alpha = 0f),
        label = "rowBorder",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AccessShapes.Large)
            .background(rowBg)
            .border(1.dp, rowBorder, AccessShapes.Large)
            .toggleable(
                value = checked,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Checkbox,
                onValueChange = { onToggle() },
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckBox(checked = checked, accent = accent)
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextPrimary,
                    fontSize = 13.sp
                )
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = AccessDefaults.TextMuted,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
private fun CheckBox(checked: Boolean, accent: Color) {
    val boxBg by animateColorAsState(
        targetValue = if (checked) accent else Color.Transparent,
        label = "boxBg",
    )
    val boxBorder by animateColorAsState(
        targetValue = if (checked) accent else AccessDefaults.TextMuted.copy(alpha = 0.6f),
        label = "boxBorder",
    )

    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(AccessShapes.XSmall)
            .background(boxBg)
            .border(1.5.dp, boxBorder, AccessShapes.XSmall),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = checked,
            transitionSpec = {
                (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut())
            },
            label = "check",
        ) { isChecked ->
            if (isChecked) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = AccessDefaults.OnAccent,
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Spacer(Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun Footer(allReady: Boolean) {

        if (allReady) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                /*
                val transition = rememberInfiniteTransition(label = "spark")
                val angle by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart,
                    ),
                    label = "angle",
                )
                Text(
                    text = "\u273B",
                    color = AccessDefaults.Accent,
                    fontSize = 10.sp,
                    modifier = Modifier.rotate(angle),
                )
                 */

                Icon(
                    modifier = Modifier.size(11.dp),
                    tint = AccessDefaults.Accent,
                    painter = painterResource(AccessIcons.Sparkle),
                    contentDescription = null,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "You're game-ready. Doors any minute.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.AccentGlow,
                        fontSize = 12.sp,
                        letterSpacing = -(0.1).sp
                    )
                )
            }
        } else {
            Text(
                text = "Tick these off while the room fills.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 12.sp,
                    letterSpacing = -(0.1).sp
                )
            )
        }
}

private val checklistItems = listOf(
    ChecklistItem("drink", "Grab a drink", "Steady the nerves", AccessDefaults.Amber),
    ChecklistItem("sound", "Sound on", "You'll hear the bell ring", AccessDefaults.Sky),
    ChecklistItem("name", "Learn one name", "Anyone standing near you", AccessDefaults.Teal),
)

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun BeforeTheBellPreview() {
    CrewTheme {
        Box(Modifier.padding(16.dp)) {
            BeforeTheBell()
        }
    }
}
