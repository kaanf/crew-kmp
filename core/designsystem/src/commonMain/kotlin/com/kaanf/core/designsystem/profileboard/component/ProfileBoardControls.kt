package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import crew.core.designsystem.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileBoardControls(
    onClose: () -> Unit,
    onEditBoard: () -> Unit,
    modifier: Modifier = Modifier,
    isEditing: Boolean = false,
    isSaving: Boolean = false,
    canEdit: Boolean = true,
    onCancel: () -> Unit = {},
    onSettings: () -> Unit = {},
) {
    val pillColors = ButtonDefaults.buttonColors(
        containerColor = AccessDefaults.SurfaceElevated, contentColor = AccessDefaults.TextPrimary,
    )
    val pillText = MaterialTheme.typography.titleSmall.copy(
        fontFamily = BricolageGrotesque, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp,
    )
    Row(
        modifier = modifier.fillMaxWidth().height(44.dp).padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isEditing) {
            Button(onClick = onCancel, enabled = !isSaving, modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(50), contentPadding = PaddingValues(horizontal = 12.dp), colors = pillColors) {
                Text(stringResource(Res.string.profile_board_cancel), style = pillText)
            }
        } else {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape)
                    .background(AccessDefaults.SurfaceElevated).border(1.dp, AccessDefaults.BorderSoft, CircleShape)
                    .clickable(enabled = !isSaving, role = Role.Button, onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Icon(painterResource(AccessIcons.LeftChevron), stringResource(Res.string.accessibility_navigate_back),
                    tint = AccessDefaults.TextPrimary, modifier = Modifier.size(18.dp))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            key(isEditing) {
            Button(onClick = onEditBoard, enabled = canEdit && !isSaving,
                modifier = Modifier.height(32.dp), shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp), colors = pillColors) {
                if (!isEditing) {
                    Icon(painterResource(Res.drawable.ic_board_edit), null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                }
                Text(stringResource(if (isSaving) Res.string.profile_board_saving
                    else if (isEditing) Res.string.profile_board_done else Res.string.profile_board_edit), style = pillText)
            }
            }
            if (!isEditing) {
                IconButton(onClick = onSettings, enabled = !isSaving, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.Default.Settings, stringResource(Res.string.profile_board_settings),
                        tint = AccessDefaults.TextPrimary, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}
