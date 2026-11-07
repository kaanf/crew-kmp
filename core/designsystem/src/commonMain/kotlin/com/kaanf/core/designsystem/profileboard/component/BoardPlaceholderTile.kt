package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.designsystem.theme.Inter

private val TileShape = RoundedCornerShape(18.dp)

/** An empty board tile inviting the owner to add content, laid out as a row or a tall card. */
@Composable
internal fun BoardPlaceholderTile(
    emoji: String,
    title: String,
    hint: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tall: Boolean = false,
) {
    val tileModifier = modifier
        .clip(TileShape)
        .background(AccessDefaults.Background.copy(alpha = .32f))
        .dashedBorder(AccessDefaults.TextPrimary.copy(alpha = .6f), cornerRadius = 18.dp)
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
    if (tall) {
        Column(
            modifier = tileModifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            TileEmoji(emoji)
            TileText(title, hint)
        }
    } else {
        Row(
            modifier = tileModifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TileEmoji(emoji)
            TileText(title, hint, Modifier.weight(1f))
            Text("+", style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
                color = AccessDefaults.TextPrimary)
        }
    }
}

@Composable
private fun TileEmoji(emoji: String) {
    Box(
        modifier = Modifier.size(40.dp).background(AccessDefaults.TextPrimary.copy(alpha = .14f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(emoji, fontSize = 16.sp)
    }
}

@Composable
private fun TileText(title: String, hint: String, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = TextStyle(fontFamily = BricolageGrotesque, fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp, lineHeight = 17.sp),
            color = AccessDefaults.TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = hint,
            style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
            color = AccessDefaults.TextPrimary.copy(alpha = .75f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
