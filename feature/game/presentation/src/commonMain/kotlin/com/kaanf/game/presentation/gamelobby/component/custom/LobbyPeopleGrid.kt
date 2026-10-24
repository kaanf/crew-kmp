package com.kaanf.game.presentation.gamelobby.component.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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

@Composable
internal fun LobbyPeopleGrid(
    members: List<LobbyMember>, self: LobbyMember, photoHeight: Dp, wave: LobbyWave?,
) {
    val pages = remember(members, self) {
        val people = members.filterNot { it.id == self.id }.distinctBy { it.id }.toMutableList()
        // Place yourself in the lower middle on the first page, exactly once in the whole list.
        people.add(minOf(4, people.size), self)
        people.chunked(6)
    }
    val pager = rememberPagerState(pageCount = { pages.size })
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        HorizontalPager(state = pager, pageSpacing = 22.dp, verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()) { page ->
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                pages[page].chunked(3).forEachIndexed { rowIndex, row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                        row.forEachIndexed { columnIndex, person ->
                            val taller = (rowIndex == 0 && columnIndex != 1) || (rowIndex == 1 && columnIndex == 1)
                            PersonPortrait(person, person.id == self.id,
                                photoHeight + if (taller) 10.dp else 0.dp,
                                wave?.takeIf { it.sender.id == person.id },
                                Modifier.weight(1f))
                        }
                        repeat(if (row.size == 1) 1 else 3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
        if (pages.size > 1) {
            Text(stringResource(Res.string.lobby_people_page, pager.currentPage + 1, pages.size),
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp),
                color = AccessDefaults.TextSecondary, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun PersonPortrait(member: LobbyMember, isSelf: Boolean, height: Dp, wave: LobbyWave?, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(Modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(12.dp))
            .background(AccessDefaults.SurfaceHigh), contentAlignment = Alignment.Center) {
            Text(member.avatar.label.ifBlank { "?" }, color = AccessDefaults.TextSecondary,
                fontFamily = BricolageGrotesque, fontWeight = FontWeight.Bold, fontSize = 48.sp,
                maxLines = 1, modifier = Modifier.padding(12.dp),
                autoSize = TextAutoSize.StepBased(minFontSize = 20.sp, maxFontSize = 48.sp))
            member.avatar.imageUrl?.takeIf { it.isNotBlank() }?.let { url ->
                BaseImage(imageUrl = url, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
            }
            if (wave != null) key(wave.id) {
                WavingHand(Modifier.align(Alignment.BottomEnd).padding(10.dp))
            }
        }
        Text(
            text = if (isSelf) stringResource(Res.string.lobby_crowd_you)
                else member.fullName?.trim()?.substringBefore(" ")?.takeIf { it.isNotBlank() }
                    ?: member.avatar.label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelf) FontWeight.Bold else FontWeight.Normal,
            color = AccessDefaults.TextPrimary, textAlign = TextAlign.Center,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
