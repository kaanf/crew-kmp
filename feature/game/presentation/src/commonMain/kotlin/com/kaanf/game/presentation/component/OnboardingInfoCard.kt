package com.kaanf.game.presentation.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.util.dottedBorder
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.how_to_play_step_1_subtitle
import crew.feature.game.presentation.generated.resources.how_to_play_step_1_title
import crew.feature.game.presentation.generated.resources.how_to_play_step_2_subtitle
import crew.feature.game.presentation.generated.resources.how_to_play_step_2_title
import crew.feature.game.presentation.generated.resources.how_to_play_step_3_subtitle
import crew.feature.game.presentation.generated.resources.how_to_play_step_3_title
import crew.feature.game.presentation.generated.resources.match_onboarding_info_text
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OnboardingInfoCard() {
    var showHowToPlayDialog by remember { mutableStateOf(false) }

    if (showHowToPlayDialog) {
        BaseDialog(onDismissRequest = { showHowToPlayDialog = false }) {
            HowToPlayDialogContent()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .dottedBorder(
                color = AccessDefaults.Border,
                shape = AccessShapes.Medium,
                strokeWidth = 1.dp,
                dotLength = 2.dp,
                gapLength = 4.dp,
            )
            .clip(AccessShapes.Medium)
            .clickable { showHowToPlayDialog = true },
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.match_onboarding_info_text),
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.TextSecondary,
                    ),
                )

                Icon(
                    painter = painterResource(AccessIcons.RightChevron),
                    contentDescription = null,
                    tint = AccessDefaults.BorderSoft,
                )
            }
        },
    )
}

@Composable
private fun HowToPlayDialogContent() {
    val steps = listOf(
        Res.string.how_to_play_step_1_title to Res.string.how_to_play_step_1_subtitle,
        Res.string.how_to_play_step_2_title to Res.string.how_to_play_step_2_subtitle,
        Res.string.how_to_play_step_3_title to Res.string.how_to_play_step_3_subtitle,
    )
    val pagerState = rememberPagerState { steps.size }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) { page ->
            val (title, subtitle) = steps[page]
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = AccessDefaults.TextPrimary,
                        textAlign = TextAlign.Center,
                    ),
                )

                Text(
                    text = stringResource(subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AccessDefaults.TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            steps.indices.forEach { index ->
                val isActive = index == pagerState.currentPage
                val width by animateDpAsState(if (isActive) 32.dp else 22.dp)
                val color = when {
                    isActive -> AccessDefaults.Accent
                    index < pagerState.currentPage -> AccessDefaults.Accent.copy(alpha = 0.5f)
                    else -> AccessDefaults.Border
                }

                Box(
                    modifier = Modifier
                        .width(width)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { scope.launch { pagerState.animateScrollToPage(index) } },
                )
            }
        }
    }
}

@Composable
@Preview
fun OnboardingInfoCardPreview() {
    CrewTheme {
        OnboardingInfoCard()
    }
}
