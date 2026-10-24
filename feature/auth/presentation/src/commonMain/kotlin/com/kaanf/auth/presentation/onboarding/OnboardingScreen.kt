package com.kaanf.auth.presentation.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.pager.PagerDots
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.onboarding_friends
import crew.feature.auth.presentation.generated.resources.onboarding_friends_body
import crew.feature.auth.presentation.generated.resources.onboarding_friends_title
import crew.feature.auth.presentation.generated.resources.onboarding_page
import crew.feature.auth.presentation.generated.resources.onboarding_play
import crew.feature.auth.presentation.generated.resources.onboarding_play_body
import crew.feature.auth.presentation.generated.resources.onboarding_play_title
import crew.feature.auth.presentation.generated.resources.onboarding_save_error
import crew.feature.auth.presentation.generated.resources.onboarding_start
import crew.feature.auth.presentation.generated.resources.onboarding_winner
import crew.feature.auth.presentation.generated.resources.onboarding_winner_body
import crew.feature.auth.presentation.generated.resources.onboarding_winner_title
import crew.feature.auth.presentation.generated.resources.welcome_skip
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private data class OnboardingPage(
    val artwork: DrawableResource,
    val title: StringResource,
    val body: StringResource,
)

private val pages = listOf(
    OnboardingPage(
        Res.drawable.onboarding_friends,
        Res.string.onboarding_friends_title,
        Res.string.onboarding_friends_body,
    ),
    OnboardingPage(
        Res.drawable.onboarding_play,
        Res.string.onboarding_play_title,
        Res.string.onboarding_play_body,
    ),
    OnboardingPage(
        Res.drawable.onboarding_winner,
        Res.string.onboarding_winner_title,
        Res.string.onboarding_winner_body,
    ),
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    isSaving: Boolean = false,
    hasError: Boolean = false,
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val titles = pages.map { stringResource(it.title) }
    val bodies = pages.map { stringResource(it.body) }
    val pageDescription = stringResource(Res.string.onboarding_page, pagerState.currentPage + 1, pages.size)

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("onboarding_screen")
            .background(AccessDefaults.Background)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(68.dp).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = onComplete,
                enabled = !isSaving,
                contentPadding = PaddingValues(horizontal = 0.dp),
                modifier = Modifier.widthIn(min = 36.dp).testTag("onboarding_skip"),
            ) {
                Text(
                    text = stringResource(Res.string.welcome_skip),
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp),
                    color = AccessDefaults.TextMuted,
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = !isSaving,
            modifier = Modifier.weight(1f).fillMaxWidth()
                .testTag("onboarding_pager")
                .semantics { stateDescription = pageDescription },
        ) { index ->
            val page = pages[index]
            BoxWithConstraints(Modifier.fillMaxSize()) {
                // Small screens and enlarged text can scroll vertically without hiding the footer.
                val artworkHeight = (maxHeight * 0.5f).coerceIn(120.dp, 280.dp)
                val contentWidth = (maxWidth - 56.dp).coerceAtLeast(1.dp)
                val titleStyle = MaterialTheme.typography.displaySmall
                val bodyStyle = MaterialTheme.typography.bodyLarge
                // Reserve the tallest text across all pages, including large font scales.
                val titleHeight = with(density) {
                    val width = minOf(contentWidth, 360.dp).roundToPx().coerceAtLeast(1)
                    titles.maxOf {
                        textMeasurer.measure(
                            AnnotatedString(it), titleStyle,
                            constraints = Constraints(maxWidth = width),
                        ).size.height
                    }.toDp()
                }
                val bodyHeight = with(density) {
                    val width = minOf(contentWidth, 320.dp).roundToPx().coerceAtLeast(1)
                    bodies.maxOf {
                        textMeasurer.measure(
                            AnnotatedString(it), bodyStyle,
                            constraints = Constraints(maxWidth = width),
                        ).size.height
                    }.toDp()
                }
                Column(
                    modifier = Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 28.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth().height(artworkHeight),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(page.artwork),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(artworkHeight).padding(8.dp),
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = titles[index],
                        style = titleStyle,
                        color = AccessDefaults.TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth()
                            .heightIn(min = titleHeight)
                            .testTag("onboarding_title_$index"),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = bodies[index],
                        style = bodyStyle,
                        color = AccessDefaults.TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(max = 320.dp).fillMaxWidth().heightIn(min = bodyHeight),
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val descriptions = pages.indices.map { index ->
                stringResource(Res.string.onboarding_page, index + 1, pages.size)
            }
            if (hasError) {
                Text(
                    text = stringResource(Res.string.onboarding_save_error),
                    style = MaterialTheme.typography.bodySmall,
                    color = AccessDefaults.Error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            // Keep the artwork and text stable when the final-page button appears.
            Box(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
                val isLastPage = pagerState.currentPage == pages.lastIndex
                // Fade only: the reserved slot stays fixed, without per-frame size changes.
                androidx.compose.animation.AnimatedVisibility(
                    visible = isLastPage,
                    enter = fadeIn(tween(durationMillis = 220, easing = FastOutSlowInEasing)),
                    exit = fadeOut(tween(durationMillis = 140)),
                ) {
                    BaseButton(
                        text = stringResource(Res.string.onboarding_start),
                        onClick = onComplete,
                        enabled = !isSaving && isLastPage,
                        isLoading = isSaving,
                        filled = true,
                        pill = true,
                        backgroundColor = Color.White,
                        borderColor = Color.White,
                        contentColor = AccessDefaults.Background,
                        modifier = Modifier.fillMaxWidth().then(
                            if (isLastPage) Modifier.testTag("onboarding_start")
                            else Modifier.clearAndSetSemantics {},
                        ),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            PagerDots(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                inactiveColor = AccessDefaults.TextFaint,
                enabled = !isSaving,
                onPageClick = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
                pageDescription = { descriptions[it] },
            )
        }
    }
}
