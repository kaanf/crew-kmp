package com.kaanf.home.presentation.gamelobby.component

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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Immutable
data class ChecklistItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val accent: Color,
)

object BeforeTheBellDefaults {
    // Per-item accents used in the mockup.
    val Amber = Color(0xFFF2A83C)
    val Blue = Color(0xFF5B9BD5)
    val Lime = Color(0xFFA6E03A)

    // Turns on only when the list is fully complete.
    val ReadyGreen = Color(0xFF8FE34A)

    // Muted secondary text / header / footer.
    val MutedText = Color(0xFF8A8A82)

    // Dark glyph drawn inside a filled (bright) checkbox.
    val OnAccent = Color(0xFF14140F)

    val ContainerShape = RoundedCornerShape(24.dp)
    val RowShape = RoundedCornerShape(16.dp)
}

/**
 * Stateless. The caller owns [checkedIds] and reacts to [onToggle]; the component
 * renders only what it is given. This keeps it testable and lets the same instance
 * back any number of items without internal coupling to "0/3 / 2/3 / 3/3".
 */
@Composable
fun BeforeTheBell(
    items: List<ChecklistItem>,
    checkedIds: Set<String>,
    onToggle: (id: String) -> Unit,
    modifier: Modifier = Modifier,
    surface: Color = AccessDefaults.Surface,
    borderColor: Color = AccessDefaults.Border,
) {
    val total = items.size
    val readyCount = items.count { it.id in checkedIds }
    val allReady = total > 0 && readyCount == total

    Column(
        modifier = modifier
            .clip(BeforeTheBellDefaults.ContainerShape)
            .background(surface)
            .border(1.dp, borderColor, BeforeTheBellDefaults.ContainerShape)
            .padding(20.dp),
    ) {
        Header(readyCount = readyCount, total = total, allReady = allReady)

        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEach { item ->
                ChecklistRow(
                    item = item,
                    checked = item.id in checkedIds,
                    onToggle = { onToggle(item.id) },
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
        targetValue = if (allReady) BeforeTheBellDefaults.ReadyGreen
        else BeforeTheBellDefaults.MutedText,
        label = "counterColor",
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "BEFORE THE BELL",
            color = BeforeTheBellDefaults.MutedText,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            letterSpacing = 3.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "$readyCount/$total ready",
            color = counterColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            letterSpacing = 1.sp,
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
        targetValue = if (checked) accent.copy(alpha = 0.07f)
        else Color.White.copy(alpha = 0.03f),
        label = "rowBg",
    )
    val rowBorder by animateColorAsState(
        targetValue = if (checked) accent.copy(alpha = 0.55f) else Color.Transparent,
        label = "rowBorder",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(BeforeTheBellDefaults.RowShape)
            // Whole row is the touch target (>48dp tall) and carries the
            // Checkbox role, so the small visual box is never the hit area.
            .toggleable(
                value = checked,
                role = Role.Checkbox,
                onValueChange = { onToggle() },
            )
            .background(rowBg)
            .border(1.dp, rowBorder, BeforeTheBellDefaults.RowShape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckBox(checked = checked, accent = accent)
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                color = BeforeTheBellDefaults.MutedText,
                fontSize = 14.sp,
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
        targetValue = if (checked) accent else BeforeTheBellDefaults.MutedText.copy(alpha = 0.6f),
        label = "boxBorder",
    )
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(shape)
            .background(boxBg)
            .border(1.5.dp, boxBorder, shape),
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
                    contentDescription = null, // state announced by Role.Checkbox
                    tint = BeforeTheBellDefaults.OnAccent,
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
    AnimatedContent(
        targetState = allReady,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "footer",
    ) { ready ->
        if (ready) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    color = BeforeTheBellDefaults.ReadyGreen,
                    fontSize = 13.sp,
                    modifier = Modifier.rotate(angle),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "You're game-ready. Doors any minute.",
                    color = BeforeTheBellDefaults.ReadyGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                )
            }
        } else {
            Text(
                text = "Tick these off while the room fills.",
                color = BeforeTheBellDefaults.MutedText,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                textAlign = TextAlign.Start,
            )
        }
    }
}

val sampleItems = listOf(
    ChecklistItem("drink", "Grab a drink", "Steady the nerves", BeforeTheBellDefaults.Amber),
    ChecklistItem("sound", "Sound on", "You'll hear the bell ring", BeforeTheBellDefaults.Blue),
    ChecklistItem("name", "Learn one name", "Anyone standing near you", BeforeTheBellDefaults.Lime),
)

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun BeforeTheBellPreview() {
    var checked by remember { mutableStateOf(setOf("drink", "sound")) }
    Box(Modifier.padding(16.dp)) {
        BeforeTheBell(
            items = sampleItems,
            checkedIds = checked,
            onToggle = { id ->
                checked = if (id in checked) checked - id else checked + id
            },
        )
    }
}
