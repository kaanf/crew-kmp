package com.kaanf.home.presentation.eventdetail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.width
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.FullScreenLoader
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.SpaceGrotesk
import com.kaanf.core.palette.CoverBackdrop
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.home.presentation.eventdetail.component.EventDetailInfoSection
import com.kaanf.home.presentation.component.EventLocationRow
import com.kaanf.home.presentation.eventdetail.component.EventImageViewer
import com.kaanf.home.presentation.eventdetail.component.EventOnboardingCard
import com.kaanf.home.presentation.eventdetail.component.GuestListCard
import com.kaanf.home.presentation.eventdetail.component.GuestListSheet
import com.kaanf.home.presentation.eventdetail.component.LockedGuestListCard
import com.kaanf.core.designsystem.component.pager.PagerDots
import com.kaanf.home.presentation.eventdetail.component.SafetyBadge
import com.kaanf.home.domain.model.EventGuest
import com.kaanf.home.presentation.model.EventDetailUiModel
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.event_detail_free_ticket_cta
import crew.feature.home.presentation.generated.resources.event_detail_locked_cta
import crew.feature.home.presentation.generated.resources.event_detail_ended_cta
import crew.feature.home.presentation.generated.resources.event_share_invitation
import crew.feature.home.presentation.generated.resources.event_detail_load_error_description
import crew.feature.home.presentation.generated.resources.event_detail_doors_open_at
import crew.feature.home.presentation.generated.resources.event_detail_load_error_title
import crew.feature.home.presentation.generated.resources.event_detail_my_ticket_cta
import crew.feature.home.presentation.generated.resources.event_detail_ticket_cta
import crew.feature.home.presentation.generated.resources.ticket_qr_retry_action
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

/** Backend'in paylaşım linki beklenen biçimde değilse paylaş butonu kapanır. */
private val ShareUrlPattern = Regex("https://share\\.crewishere\\.com/e/[a-z0-9]{10}")

@Composable
fun EventDetailRoot(
    viewModel: EventDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onCheckoutSuccess: (eventId: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val listState: LazyListState = rememberLazyListState()
    val shareEvent = rememberEventShareLauncher()
    val shareInvitation = stringResource(Res.string.event_share_invitation)

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is EventDetailEvent.CheckoutSuccess -> {
                onCheckoutSuccess(event.eventId)
            }
        }
    }

    val shareUrl = state.event?.shareUrl?.takeIf { it.matches(ShareUrlPattern) }

    // Zemin yalnızca AppScaffold'un containerColor'ı: burada ayrıca tam ekran bir Box
    // boyanıyordu ve ekran iki kez doluyordu.
    AppScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.EventDetail,
                elevated = { listState.canScrollBackward },
                transparentAtRest = false,
                rightActions = {
                    EventShareButton(
                        enabled = shareUrl != null,
                        onClick = {
                            state.event?.let { event ->
                                val url = shareUrl ?: return@let
                                shareEvent(
                                    EventShareContent(
                                        title = event.title,
                                        text = listOf(
                                            shareInvitation,
                                            listOfNotNull(
                                                event.title,
                                                event.heroDate,
                                                event.location?.name,
                                            ).joinToString(" · "),
                                            url,
                                        ).joinToString("\n\n"),
                                        url = url,
                                        imageUrl = event.imageUrls.firstOrNull(),
                                    ),
                                )
                            }
                        },
                    )
                },
                onBackClick = { onBackClick() },
            )
        },
    ) { innerPadding ->
        EventDetailScreen(
            modifier = Modifier
                .padding(bottom = innerPadding.calculateBottomPadding())
                .consumeWindowInsets(innerPadding),
            contentTopPadding = innerPadding.calculateTopPadding(),
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
    contentTopPadding: Dp = 0.dp,
    onAction: (EventDetailAction) -> Unit,
) {
    val typography = MaterialTheme.typography
    val spaceGrotesk = SpaceGrotesk
    // copy() her recomposition'da yeni bir Typography üretip LocalTypography'yi baştan
    // sağlıyordu; altındaki her tüketici boşuna invalidate oluyordu.
    val eventDetailTypography = remember(typography, spaceGrotesk) {
        typography.copy(
            labelSmall = typography.labelSmall.copy(fontFamily = spaceGrotesk),
        )
    }

    MaterialTheme(typography = eventDetailTypography) {
        val event = state.event

        // Tam ekran Crossfade kaldırıldı: iki durumu da compose edilmiş tutup 1440x3120'lik
        // alt ağaca alpha katmanı uyguluyordu, yani ekran boyu bir offscreen buffer.
        if (event == null) {
            Box(
                modifier = modifier.fillMaxSize(),
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
                modifier = modifier,
                listState = listState,
                contentTopPadding = contentTopPadding,
                event = event,
                guests = state.guests,
                isGuestsLoading = state.isGuestsLoading,
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
    modifier: Modifier,
    listState: LazyListState,
    contentTopPadding: Dp,
    event: EventDetailUiModel,
    guests: List<EventGuest>,
    isGuestsLoading: Boolean,
    isCheckingOut: Boolean,
    onAction: (EventDetailAction) -> Unit,
) {
    var viewerPage by rememberSaveable { mutableStateOf<Int?>(null) }
    var isGuestListOpen by rememberSaveable { mutableStateOf(false) }

    // Zemin AppScaffold'dan geliyor; burada tekrar boyanmıyor.
    Box(
        modifier = modifier.fillMaxSize().clipToBounds(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(contentTopPadding + HeroHeight + 160.dp)
                .graphicsLayer {
                    translationY = if (listState.firstVisibleItemIndex == 0) {
                        -listState.firstVisibleItemScrollOffset.toFloat()
                    } else {
                        -size.height
                    }
                }
                .drawWithCache {
                    val fade = Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.60f to Color.Transparent,
                        0.85f to AccessDefaults.Background.copy(alpha = 0.85f),
                        1f to AccessDefaults.Background,
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(fade)
                    }
                },
        ) {
            CoverBackdrop(
                palette = event.backdropPalette,
                overlayColor = AccessDefaults.Background,
                legibleTextColor = AccessDefaults.TextSecondary,
                modifier = Modifier.matchParentSize(),
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(top = contentTopPadding),
            verticalArrangement = Arrangement.spacedBy(SectionSpacing),
        ) {
            item(contentType = "hero") {
                EventDetailHero(
                    imageUrls = event.imageUrls,
                    onImageClick = { page -> viewerPage = page },
                )
            }

            item(contentType = "title-date") {
                Column(
                    modifier = Modifier.padding(horizontal = BodyPadding),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 34.sp,
                            lineHeight = 34.sp,
                            letterSpacing = (-1.4).sp,
                        ),
                    )

                    Text(
                        text = event.heroDate,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = AccessDefaults.TextSecondary,
                        ),
                    )

                    Text(
                        text = stringResource(Res.string.event_detail_doors_open_at, event.doorsTime),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = AccessDefaults.TextSecondary,
                        ),
                    )
                }
            }

            event.location?.let { location ->
                item(contentType = "location") {
                    EventLocationRow(
                        location = location,
                        modifier = Modifier.padding(horizontal = BodyPadding),
                    )
                }
            }

            if (!event.description.isNullOrBlank()) {
                item(contentType = "event-info") {
                    EventDetailInfoSection(
                        description = event.description,
                        modifier = Modifier.padding(horizontal = BodyPadding),
                    )
                }
            }

            if (!event.hasMyTicket) {
                item(contentType = "guest-list-locked") {
                    LockedGuestListCard(
                        modifier = Modifier.padding(horizontal = BodyPadding),
                    )
                }
            } else {
                item(contentType = "guest-list") {
                    GuestListCard(
                        guests = guests,
                        isLoading = isGuestsLoading,
                        onClick = { if (event.hasMyTicket) isGuestListOpen = true },
                        modifier = Modifier.padding(horizontal = BodyPadding),
                    )
                }
            }

            /*
            item(key = "plot-twist", contentType = "plot-twist") {
                EventPlotTwistCard(
                    eventId = event.id,
                    modifier = Modifier.padding(horizontal = BodyPadding),
                )
            }
             */

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

        val ctaLocked = event.isEnded || event.isLocked
        BoxWithConstraints(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = BodyPadding, end = BodyPadding, bottom = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            val ctaWidth by animateDpAsState(
                targetValue = if (isCheckingOut) 62.dp.coerceAtMost(maxWidth) else maxWidth,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "event_cta_width",
            )
            BaseButton(
                text = when {
                    event.isLocked -> stringResource(Res.string.event_detail_locked_cta)
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
                modifier = Modifier.width(ctaWidth),
                isLoading = isCheckingOut,
                enabled = !isCheckingOut && !ctaLocked,
                backgroundColor = Color.White,
                borderColor = Color.White,
                contentColor = AccessDefaults.Background,
                filled = true,
                pill = true,
            )
        }
    }

    viewerPage?.let { page ->
        EventImageViewer(
            imageUrls = event.imageUrls,
            initialPage = page,
            onDismiss = { viewerPage = null },
        )
    }

    if (isGuestListOpen && event.hasMyTicket) {
        GuestListSheet(
            guests = guests,
            onDismiss = { isGuestListOpen = false },
        )
    }
}

@Composable
private fun EventDetailHero(
    imageUrls: List<String>,
    onImageClick: (Int) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeroHeight)
            .padding(horizontal = HeroInset)
            .padding(top = 12.dp)
            .shadow(
                elevation = 28.dp,
                shape = HeroShape,
                ambientColor = Color.Black,
                spotColor = Color.Black,
            )
            .border(1.dp, HeroEdgeLight, HeroShape),
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

        if (imageUrls.size > 1) {
            PagerDots(
                pageCount = pagerState.pageCount,
                currentPage = pagerState.currentPage,
                activeColor = AccessDefaults.Accent,
                inactiveColor = AccessDefaults.TextMuted.copy(alpha = 0.4f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp),
            )
        }
    }
}

private val HeroHeight = 344.dp
private val HeroInset = 28.dp
private val BodyPadding = 22.dp
private val SectionSpacing = 20.dp

private val HeroShape = RoundedCornerShape(28.dp)

private val HeroEdgeLight = Brush.verticalGradient(
    0f to Color.White.copy(alpha = 0.18f),
    0.5f to Color.Transparent,
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
