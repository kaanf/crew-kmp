package com.kaanf.game.presentation.quests

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.progressbar.BaseProgressBar
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.game.domain.model.Quest
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.quests_claim_action
import crew.feature.game.presentation.generated.resources.quests_claimed_label
import crew.feature.game.presentation.generated.resources.quests_points_unit
import crew.feature.game.presentation.generated.resources.quests_progress_short
import crew.feature.game.presentation.generated.resources.quests_reward_format
import crew.feature.game.presentation.generated.resources.quests_ready
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun QuestMapSection(
    group: QuestGroup,
    selectedQuestKey: String?,
    claimingKey: String?,
    onSelect: (Quest) -> Unit,
    onClaim: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(groupTitle(group), style = MaterialTheme.typography.headlineSmall, color = AccessDefaults.TextPrimary)
        group.levels.forEach { quest ->
            QuestRow(group, quest, selectedQuestKey, claimingKey, onSelect, onClaim)
        }
    }
}

@Composable
private fun QuestRow(
    group: QuestGroup,
    quest: Quest,
    selectedKey: String?,
    claimingKey: String?,
    onSelect: (Quest) -> Unit,
    onClaim: (String) -> Unit,
) {
    val isSelected = selectedKey == quest.key
    val canClaim = quest.completed && !quest.claimed
    val borderColor = when {
        isSelected -> AccessDefaults.Accent
        canClaim -> AccessDefaults.AccentFocusRing
        else -> AccessDefaults.BorderSoft
    }
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(AccessShapes.LargeCard)
            .background(AccessDefaults.Surface)
            .border(1.dp, borderColor, AccessShapes.LargeCard)
            .semantics { selected = isSelected }
            .clickable(role = Role.Button, onClick = { onSelect(quest) })
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        QuestNodeArt(group, quest)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = quest.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    letterSpacing = (-0.4).sp,
                ),
                color = AccessDefaults.TextPrimary,
            )
            if (!quest.completed) {
                BaseProgressBar(
                    progress = quest.progress.toFloat() / quest.target.coerceAtLeast(1),
                    height = 5.dp,
                    trackColor = AccessDefaults.SurfaceHigh,
                )
            }
            Text(
                text = when {
                    quest.claimed -> stringResource(Res.string.quests_claimed_label)
                    quest.completed -> stringResource(Res.string.quests_ready)
                    else -> stringResource(Res.string.quests_progress_short, quest.progress, quest.target)
                },
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                ),
                color = if (canClaim) AccessDefaults.Accent else AccessDefaults.TextMuted,
            )
        }
        QuestPointsReward(
            points = quest.points,
            claimed = quest.claimed,
            claimable = canClaim,
            isClaiming = claimingKey == quest.key,
            modifier = if (canClaim && claimingKey == null) {
                Modifier.clickable(role = Role.Button, onClick = { onClaim(quest.key) })
            } else {
                Modifier
            },
        )
    }
}

/** Puan madalyonu: toplanabilir ödül dolu accent, toplanmış ödül tikli ve soluk. */
@Composable
internal fun QuestPointsReward(
    points: Int,
    claimed: Boolean,
    modifier: Modifier = Modifier,
    claimable: Boolean = false,
    isClaiming: Boolean = false,
) {
    val ink = when {
        claimable -> AccessDefaults.OnAccent
        claimed -> AccessDefaults.TextMuted
        else -> AccessDefaults.Accent
    }
    Column(
        modifier = modifier.width(64.dp)
            .clip(AccessShapes.Medium)
            .background(if (claimable) AccessDefaults.Accent else AccessDefaults.SurfaceElevated)
            .border(1.dp, if (claimable) AccessDefaults.Accent else AccessDefaults.Border, AccessShapes.Medium)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (isClaiming) {
            CircularProgressIndicator(Modifier.size(20.dp), color = ink, strokeWidth = 2.dp)
        } else {
            Text(
                text = stringResource(Res.string.quests_reward_format, points),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    letterSpacing = (-0.8).sp,
                ),
                color = ink,
            )
        }
        Text(
            text = if (claimable) stringResource(Res.string.quests_claim_action) else stringResource(Res.string.quests_points_unit).uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                lineHeight = 10.sp,
                letterSpacing = 1.4.sp,
            ),
            color = ink.copy(alpha = if (claimable) 1f else 0.7f),
        )
    }
}

@Composable
private fun QuestNodeArt(group: QuestGroup, quest: Quest) {
    Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) {
        Image(
            painterResource(badgeFor(group, quest)), null,
            modifier = Modifier.size(64.dp).alpha(if (quest.completed || quest.claimed) 1f else 0.6f),
        )
        if (quest.claimed) {
            Box(
                Modifier.align(Alignment.BottomEnd).size(20.dp).background(AccessDefaults.Accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(painterResource(AccessIcons.Check), null, tint = AccessDefaults.OnAccent, modifier = Modifier.size(13.dp))
            }
        }
    }
}
