package com.kaanf.game.presentation.gamelobby.component.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.presentation.model.LobbyMember
import crew.feature.game.presentation.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlin.math.absoluteValue

@Composable
internal fun LobbyParticipantDeck(
    members: List<LobbyMember>,
    selfId: String,
    cardHeight: Dp,
    modifier: Modifier = Modifier,
) {
    // The caller always includes the current user, including before presence arrives.
    val pager = rememberPagerState(pageCount = { members.size })
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pager,
            key = { members[it].id },
            contentPadding = PaddingValues(horizontal = 30.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth().height(cardHeight + 24.dp),
        ) { page ->
            val member = members[page]
            Column(
                modifier = Modifier.fillMaxWidth().height(cardHeight)
                    .graphicsLayer {
                        val offset = (pager.currentPage - page) + pager.currentPageOffsetFraction
                        val distance = offset.absoluteValue.coerceIn(0f, 1f)
                        scaleX = 1f - distance * 0.06f
                        scaleY = 1f - distance * 0.06f
                        rotationZ = offset.coerceIn(-1f, 1f) * -4f
                    }
                    .clip(RoundedCornerShape(24.dp))
                    .background(AccessDefaults.TextPrimary),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                        .background(member.avatar.color),
                    contentAlignment = Alignment.Center,
                ) {
                    // This remains visible for missing/failed images, rather than an empty card.
                    Text(
                        text = member.avatar.label.ifBlank { "?" },
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = BricolageGrotesque,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 112.sp,
                        ),
                        color = AccessDefaults.OnAccent,
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(minFontSize = 40.sp, maxFontSize = 112.sp),
                    )
                    member.avatar.imageUrl?.takeIf { it.isNotBlank() }?.let { url ->
                        BaseImage(
                            imageUrl = url,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize(),
                        )
                    }
                    Text(
                        text = stringResource(if (member.id == selfId) Res.string.lobby_crowd_you
                            else Res.string.lobby_deck_say_hi),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = AccessDefaults.OnAccent,
                        modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                            .rotate(-5f).background(AccessDefaults.Accent, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = member.fullName?.takeIf { it.isNotBlank() } ?: member.avatar.label.ifBlank { "?" },
                        modifier = Modifier.fillMaxWidth().semantics { heading() },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = BricolageGrotesque, fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.6).sp,
                        ),
                        color = AccessDefaults.OnAccent,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(if (member.id == selfId) Res.string.lobby_crowd_you
                            else Res.string.lobby_deck_here_tonight),
                        style = MaterialTheme.typography.bodySmall,
                        color = AccessDefaults.OnAccent.copy(alpha = 0.7f),
                    )
                }
            }
        }
        if (members.size > 1) {
            Text(
                text = stringResource(Res.string.lobby_deck_swipe),
                style = MaterialTheme.typography.bodySmall,
                color = AccessDefaults.TextSecondary,
            )
            Text(
                text = "${pager.currentPage.coerceAtMost(members.lastIndex) + 1} / ${members.size}",
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = AccessDefaults.TextPrimary,
            )
        }
    }
}
