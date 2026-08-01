package com.kaanf.game.presentation.quests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.header.SectionHeader
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.modifier.surfaceCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.game.domain.model.Quest
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.quests_claim_format
import crew.feature.game.presentation.generated.resources.quests_claimed_label
import crew.feature.game.presentation.generated.resources.quests_completed_format
import crew.feature.game.presentation.generated.resources.quests_eyebrow
import crew.feature.game.presentation.generated.resources.quests_progress_format
import crew.feature.game.presentation.generated.resources.quests_reward_format
import crew.feature.game.presentation.generated.resources.quests_title_highlight
import crew.feature.game.presentation.generated.resources.quests_title_prefix
import crew.feature.game.presentation.generated.resources.quests_top_bar_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun QuestsRoot(
    onBack: () -> Unit,
    viewModel: QuestsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    QuestsScreen(
        state = state,
        onBack = onBack,
        onClaim = viewModel::claim,
    )
}

@Composable
fun QuestsScreen(
    state: QuestsState,
    onBack: () -> Unit,
    onClaim: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                state = AppTopBarState.GameLobby(
                    stringResource(Res.string.quests_top_bar_title),
                ),
                onBackClick = onBack,
            )
        },
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                item(key = "header") { QuestsHeader() }
                items(state.quests, key = { it.key }) { quest ->
                    QuestCard(
                        quest = quest,
                        isClaiming = state.claimingKey == quest.key,
                        onClaim = { onClaim(quest.key) },
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestsHeader(modifier: Modifier = Modifier) {
    SectionHeader(
        modifier = modifier.padding(top = 12.dp, bottom = 8.dp),
        title = buildAnnotatedString {
            append(stringResource(Res.string.quests_title_prefix))
            withStyle(
                style = SpanStyle(
                    color = AccessDefaults.Accent,
                    shadow = Shadow(color = AccessDefaults.AccentGlow, blurRadius = 24f),
                ),
            ) {
                append(stringResource(Res.string.quests_title_highlight))
            }
        },
        titleStyle = MaterialTheme.typography.displaySmall.copy(
            color = AccessDefaults.TextPrimary,
        ),
        verticalSpacing = 6.dp,
    )
}

@Composable
private fun QuestCard(
    quest: Quest,
    isClaiming: Boolean,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(22.dp)
    val cardModifier = modifier
        .fillMaxWidth()
        .alpha(if (quest.claimed) 0.62f else 1f)
        .surfaceCard(shape = cardShape)

    if (quest.completed) {
        Row(
            modifier = cardModifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(AccessDefaults.Accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(AccessIcons.Check),
                    contentDescription = null,
                    tint = AccessDefaults.Accent,
                    modifier = Modifier.size(15.dp),
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.TextPrimary,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text = quest.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = AccessDefaults.TextMuted),
                )
                Text(
                    text = stringResource(Res.string.quests_completed_format, quest.points),
                    style = MaterialTheme.typography.labelSmall.copy(color = AccessDefaults.TextFaint),
                )
            }
            when {
                quest.claimed -> Text(
                    text = stringResource(Res.string.quests_claimed_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.TextFaint,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )

                isClaiming -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = AccessDefaults.Accent,
                    strokeWidth = 2.dp,
                )

                else -> Text(
                    text = stringResource(Res.string.quests_claim_format, quest.points),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = AccessDefaults.OnAccent,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(AccessDefaults.Accent)
                        .clickable(onClick = onClaim)
                        .padding(horizontal = 15.dp, vertical = 9.dp),
                )
            }
        }
    } else {
        Column(modifier = cardModifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.TextPrimary,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(Res.string.quests_reward_format, quest.points),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.Accent,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier
                        .background(
                            color = AccessDefaults.Accent.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(999.dp),
                        )
                        .padding(horizontal = 9.dp, vertical = 3.dp),
                )
            }
            Text(
                text = quest.description,
                style = MaterialTheme.typography.bodySmall.copy(color = AccessDefaults.TextMuted),
                modifier = Modifier.padding(top = 6.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 9.dp)
                    .height(7.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(AccessDefaults.SurfaceElevated),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = quest.progress / quest.target.toFloat())
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(999.dp))
                        .background(AccessDefaults.Accent),
                )
            }
            Text(
                text = stringResource(
                    Res.string.quests_progress_format,
                    quest.progress,
                    quest.target,
                ),
                style = MaterialTheme.typography.labelMedium.copy(color = AccessDefaults.TextMuted),
            )
        }
    }
}
