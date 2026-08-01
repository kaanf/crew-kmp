package com.kaanf.game.presentation.memories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * Rulonun iki girişinin ortak kabuğu: coral tonlu kart, sol ikon karesi, başlık +
 * [subtitle], sağda [trailing]. Oyun sırasında "Tonight's roll" (kamera + kare sayısı),
 * etkinlik bitince "The roll developed" (kilit açık + chevron) olarak kullanılır.
 */
@Composable
internal fun MemoriesEntryCard(
    icon: DrawableResource,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: @Composable ColumnScope.() -> Unit,
    trailing: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(AccessShapes.Card)
            .background(AccessDefaults.Surface)
            .border(
                width = 1.dp,
                color = AccessDefaults.Coral.copy(alpha = 0.3f),
                shape = AccessShapes.Card,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(AccessShapes.Medium)
                .background(AccessDefaults.Coral.copy(alpha = 0.14f))
                .border(
                    width = 1.dp,
                    color = AccessDefaults.Coral.copy(alpha = 0.3f),
                    shape = AccessShapes.Medium,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = AccessDefaults.Coral,
                modifier = Modifier.size(18.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextPrimary,
                    fontWeight = FontWeight.Bold,
                ),
            )
            subtitle()
        }

        trailing()
    }
}
