package com.kaanf.home.presentation.eventdetail

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.FullScreenLoader
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.home.presentation.component.verticalGradientScrim
import com.kaanf.home.presentation.eventdetail.component.EventDetailInfoSection
import com.kaanf.home.presentation.eventdetail.component.EventImageViewer
import com.kaanf.home.presentation.eventdetail.component.EventOnboardingCard
import com.kaanf.home.presentation.eventdetail.component.PagerDots
import com.kaanf.home.presentation.eventdetail.component.SafetyBadge
import com.kaanf.home.presentation.model.EventDetailUiModel
import com.kaanf.home.presentation.util.toClockText
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.event_detail_free_ticket_cta
import crew.feature.home.presentation.generated.resources.event_detail_ended_cta
import crew.feature.home.presentation.generated.resources.event_detail_load_error_description
import crew.feature.home.presentation.generated.resources.event_detail_load_error_title
import crew.feature.home.presentation.generated.resources.event_detail_my_ticket_cta
import crew.feature.home.presentation.generated.resources.event_detail_ticket_cta
import crew.feature.home.presentation.generated.resources.ticket_qr_retry_action
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EventDetailRoot(
    viewModel: EventDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onCheckoutSuccess: (eventId: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val listState: LazyListState = rememberLazyListState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is EventDetailEvent.CheckoutSuccess -> {
                onCheckoutSuccess(event.eventId)
            }
        }
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.EventDetail,
                elevated = { listState.canScrollBackward },
                onBackClick = { onBackClick() },
            )
        },
    ) { innerPadding ->
        EventDetailScreen(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            listState = listState,
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
fun EventDetailScreen(
    modifier: Modifier,
    listState: LazyListState,
    state: EventDetailState,
    onAction: (EventDetailAction) -> Unit,
) {
    Crossfade(
        targetState = state.event,
        modifier = modifier.fillMaxSize(),
        animationSpec = tween(durationMillis = 250),
    ) { event ->
        if (event == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                if (state.loadFailed) {
                    EventDetailLoadErrorContent(
                        onRetry = { onAction(EventDetailAction.OnRetryLoad) },
                    )
                } else {
                    FullScreenLoader()
                }
            }
        } else {
            EventDetailContent(
                listState = listState,
                event = event,
                isCheckingOut = state.isCheckingOut,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun EventDetailLoadErrorContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.event_detail_load_error_title),
            style = MaterialTheme.typography.titleMedium,
            color = AccessDefaults.TextPrimary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.event_detail_load_error_description),
            style = MaterialTheme.typography.bodyMedium,
            color = AccessDefaults.TextMuted,
        )
        Spacer(Modifier.height(24.dp))
        BaseButton(
            text = stringResource(Res.string.ticket_qr_retry_action),
            onClick = onRetry,
            filled = true,
        )
    }
}

@Composable
private fun EventDetailContent(
    listState: LazyListState,
    event: EventDetailUiModel,
    isCheckingOut: Boolean,
    onAction: (EventDetailAction) -> Unit,
) {
    // null = kapalı; saf UI durumu olduğu için ViewModel'e taşınmaz.
    var viewerPage by rememberSaveable { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(contentType = "hero") {
                EventDetailHero(
                    imageUrls = event.imageUrls,
                    title = event.title,
                    // Draw fazında okunur; parallax scroll'da recomposition tetiklemez.
                    scrollOffset = {
                        if (listState.firstVisibleItemIndex == 0) {
                            listState.firstVisibleItemScrollOffset.toFloat()
                        } else {
                            Float.MAX_VALUE
                        }
                    },
                    onImageClick = { page -> viewerPage = page },
                )
            }

            item(contentType = "event-info") {
                EventDetailInfoSection(
                    date = event.heroDate,
                    doorsTime = event.doorsOpenAt.toClockText(),
                    goingCount = event.goingCount,
                    spotsLeft = event.spotsLeft,
                    description = event.description,
                    location = event.location,
                    modifier = Modifier.padding(horizontal = BodyPadding),
                )
            }

            item(contentType = "onboarding-card") {
                Box(modifier = Modifier.padding(horizontal = BodyPadding)) {
                    EventOnboardingCard()
                }
            }

            item(contentType = "safety-card") {
                Box(modifier = Modifier.padding(horizontal = BodyPadding)) {
                    SafetyBadge()
                }
            }

            item(contentType = "space-after-last-card") {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Bilet satışı Gameplay boyunca da açık (backend TICKETABLE_PHASES ile aynı kural);
        // kilit yalnız etkinlik bittiğinde.
        val ctaLocked = event.isEnded
        BaseButton(
            text = when {
                event.isEnded -> stringResource(Res.string.event_detail_ended_cta)
                event.hasMyTicket -> stringResource(Res.string.event_detail_my_ticket_cta)
                event.isFree -> stringResource(Res.string.event_detail_free_ticket_cta)
                else -> stringResource(Res.string.event_detail_ticket_cta, event.formattedPrice)
            },
            onClick = {
                if (!event.hasMyTicket) {
                    onAction(EventDetailAction.OnCheckoutClicked)
                } else {
                    onAction(EventDetailAction.GoToTicketQr)
                }
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp)
                    .align(Alignment.BottomCenter),
            isLoading = isCheckingOut,
            enabled = !isCheckingOut && !ctaLocked,
            filled = false,
            animatedBorder = !ctaLocked,
        )
    }

    viewerPage?.let { page ->
        EventImageViewer(
            imageUrls = event.imageUrls,
            initialPage = page,
            onDismiss = { viewerPage = null },
        )
    }
}

/**
 * Kenardan kenara hero: görsel scroll'da yavaşça kayar, üstündeki başlık sönerek gider.
 * Alttaki scrim görseli arka plan rengine bağlar, böylece gövde metni hero'nun devamı gibi okunur.
 */
@Composable
private fun EventDetailHero(
    imageUrls: List<String>,
    title: String,
    scrollOffset: () -> Float,
    onImageClick: (Int) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeroHeight)
            .clipToBounds(),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    translationY = scrollOffset().coerceIn(0f, size.height) * 0.35f
                },
        ) {
            if (imageUrls.size > 1) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.matchParentSize(),
                ) { page ->
                    BaseImage(
                        imageUrl = imageUrls[page],
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onImageClick(page) },
                        contentScale = ContentScale.Crop,
                    )
                }
            } else {
                BaseImage(
                    imageUrl = imageUrls.first(),
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { onImageClick(0) },
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Box(modifier = Modifier.matchParentSize().verticalGradientScrim(HeroScrim))

        if (imageUrls.size > 1) {
            PagerDots(
                pagerState = pagerState,
                activeColor = AccessDefaults.Accent,
                inactiveColor = AccessDefaults.TextMuted.copy(alpha = 0.4f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp),
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34.sp,
                lineHeight = 34.sp,
                letterSpacing = (-1.4).sp,
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .graphicsLayer {
                    val offset = scrollOffset()
                    alpha = (1f - offset / HeroTitleFadeDistance.toPx()).coerceIn(0f, 1f)
                    translationY = -offset * 0.12f
                }
                .padding(horizontal = BodyPadding)
                .padding(bottom = 22.dp),
        )
    }
}

private val HeroHeight = 396.dp
private val HeroTitleFadeDistance = 260.dp
private val BodyPadding = 22.dp

private val HeroScrim = Brush.verticalGradient(
    0f to AccessDefaults.Background.copy(alpha = 0.55f),
    0.22f to Color.Transparent,
    0.46f to Color.Transparent,
    0.74f to AccessDefaults.Background.copy(alpha = 0.42f),
    0.99f to AccessDefaults.Background,
)


@Composable
@Preview
fun EventDetailScreenPreview() {
    EventDetailScreen(
        modifier = Modifier,
        listState = rememberLazyListState(),
        state = EventDetailState(),
        onAction = {},
    )
}
