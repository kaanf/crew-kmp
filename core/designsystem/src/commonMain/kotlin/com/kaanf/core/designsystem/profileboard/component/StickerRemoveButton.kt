package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.profile_sticker_remove
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun StickerRemoveButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val description = stringResource(Res.string.profile_sticker_remove)
    Box(
        modifier = modifier.size(24.dp).clip(CircleShape)
            .background(Color.Black)
            .border(1.dp, Color.White, CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(10.dp)) {
            val inset = 2.dp.toPx()
            val stroke = 1.dp.toPx()
            drawLine(Color.White, Offset(inset, inset), Offset(size.width - inset, size.height - inset),
                strokeWidth = stroke, cap = StrokeCap.Round)
            drawLine(Color.White, Offset(size.width - inset, inset), Offset(inset, size.height - inset),
                strokeWidth = stroke, cap = StrokeCap.Round)
        }
    }
}
