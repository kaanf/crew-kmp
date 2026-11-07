package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.profileboard.BoardTool
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.Inter
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.profile_board_tool_ask
import crew.core.designsystem.generated.resources.profile_board_tool_film
import crew.core.designsystem.generated.resources.profile_board_tool_loves
import crew.core.designsystem.generated.resources.profile_board_tool_qr_skin
import crew.core.designsystem.generated.resources.profile_board_tool_song
import crew.core.designsystem.generated.resources.profile_board_tools
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal val BoardTool.emoji: String get() = when (this) {
    BoardTool.QR_SKIN -> "🎨"
    BoardTool.FILM -> "🎬"
    BoardTool.SONG -> "🎵"
    BoardTool.ASK -> "💬"
    BoardTool.LOVES -> "❤️"
}

private val BoardTool.color: Color get() = when (this) {
    BoardTool.QR_SKIN -> AccessDefaults.Violet
    BoardTool.FILM -> AccessDefaults.Amber
    BoardTool.SONG -> AccessDefaults.Teal
    BoardTool.ASK -> AccessDefaults.Mint
    BoardTool.LOVES -> AccessDefaults.Rose
}

private val BoardTool.label: StringResource get() = when (this) {
    BoardTool.QR_SKIN -> Res.string.profile_board_tool_qr_skin
    BoardTool.FILM -> Res.string.profile_board_tool_film
    BoardTool.SONG -> Res.string.profile_board_tool_song
    BoardTool.ASK -> Res.string.profile_board_tool_ask
    BoardTool.LOVES -> Res.string.profile_board_tool_loves
}

/** Inline editing tools that scroll with the board. */
@Composable
internal fun BoardToolBar(
    onToolClick: (BoardTool) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val title = stringResource(Res.string.profile_board_tools)
    Row(
        modifier = modifier.fillMaxWidth().semantics { contentDescription = title },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        BoardTool.entries.forEach { tool ->
            BoardToolItem(tool, enabled, { onToolClick(tool) }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun BoardToolItem(tool: BoardTool, enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier.size(50.dp).background(tool.color, RoundedCornerShape(18.dp))
                .clearAndSetSemantics {},
            contentAlignment = Alignment.Center,
        ) {
            Text(tool.emoji, fontSize = 18.sp)
        }
        Text(
            text = stringResource(tool.label),
            style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 12.sp),
            color = AccessDefaults.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
