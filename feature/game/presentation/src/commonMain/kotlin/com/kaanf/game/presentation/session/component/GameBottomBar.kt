package com.kaanf.game.presentation.session.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.game_tab_history
import crew.feature.game.presentation.generated.resources.game_tab_leaderboard
import crew.feature.game.presentation.generated.resources.game_tab_play
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class GameBottomTab { Leaderboard, Play, History }

/**
 * Etkinlik içi ana ekranların (QR home, leaderboard, gece geçmişi) altında yüzen
 * tab bar. Faz ekranlarında (RPS, görev, skor tablosu...) gösterilmez.
 * Her tab yalnız ikon + yazının kapladığı kadar genişler; bar içeriğe sarılır.
 * Oyun sürerken leaderboard, oyun bitince Play kilitlidir (kilit ikonu + tıklanamaz).
 */
@Composable
fun GameBottomBar(
    activeTab: GameBottomTab,
    isGameEnded: Boolean,
    onTabClick: (GameBottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val barShape = RoundedCornerShape(26.dp)
    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            // ponytail: tasarımdaki backdrop blur atlandı (Skia'da pahalı, iOS scroll jank);
            // yerine yüksek alfa'lı düz zemin.
            .background(color = BarBackground, shape = barShape)
            .border(width = 1.dp, color = BarBorder, shape = barShape)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TabItem(
            icon = AccessIcons.Trophy,
            label = stringResource(Res.string.game_tab_leaderboard),
            isActive = activeTab == GameBottomTab.Leaderboard,
            isLocked = !isGameEnded,
            onClick = { onTabClick(GameBottomTab.Leaderboard) },
        )
        PlayItem(
            label = stringResource(Res.string.game_tab_play),
            isLocked = isGameEnded,
            onClick = { onTabClick(GameBottomTab.Play) },
        )
        TabItem(
            icon = AccessIcons.Clock,
            label = stringResource(Res.string.game_tab_history),
            isActive = activeTab == GameBottomTab.History,
            onClick = { onTabClick(GameBottomTab.History) },
        )
    }
}

@Composable
private fun TabItem(
    icon: DrawableResource,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    isLocked: Boolean = false,
) {
    val contentColor = when {
        isLocked -> AccessDefaults.TextFaint
        isActive -> AccessDefaults.TextPrimary
        else -> AccessDefaults.TextMuted
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (isActive) {
                    Modifier.background(AccessDefaults.SurfaceElevated)
                } else {
                    Modifier
                },
            )
            .clickable(enabled = !isLocked, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(if (isLocked) AccessIcons.Lock else icon),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            ),
            maxLines = 1,
        )
    }
}

@Composable
private fun PlayItem(
    label: String,
    onClick: () -> Unit,
    isLocked: Boolean = false,
) {
    val contentColor = if (isLocked) AccessDefaults.TextFaint else AccessDefaults.OnAccent
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isLocked) AccessDefaults.SurfaceElevated else AccessDefaults.Accent)
            .clickable(enabled = !isLocked, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(if (isLocked) AccessIcons.Lock else AccessIcons.QR),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            maxLines = 1,
        )
    }
}

private val BarBackground = Color(0xF014100C)
private val BarBorder = Color(0x0FFFFFFF)

@Composable
@Preview
private fun GameBottomBarPreview() {
    CrewTheme {
        GameBottomBar(
            activeTab = GameBottomTab.Play,
            isGameEnded = false,
            onTabClick = {},
        )
    }
}
