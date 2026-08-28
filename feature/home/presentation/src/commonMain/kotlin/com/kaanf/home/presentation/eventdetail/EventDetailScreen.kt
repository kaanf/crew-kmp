package com.kaanf.home.presentation.eventdetail

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.home.presentation.component.background.DynamicEventBackground
import com.kaanf.home.presentation.component.softShadow
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
                transparentAtRest = true,
                onBackClick = { onBackClick() },
            )
        },
    ) { innerPadding ->
        EventDetailScreen(
            // Üst boşluk bilerek uygulanmıyor: hero'nun renk alanı bar'ın arkasına kadar
            // uzansın ki saydam bar boyalı zemini göstersin. Kartı bar'ın altında tutan
            // boşluk hero öğesinin *içinde* (topInset), böylece arka plan yukarı taşıyor.
            modifier = Modifier
                .padding(bottom = innerPadding.calculateBottomPadding())
                .consumeWindowInsets(innerPadding),
            listState = listState,
            state = state,
            topInset = innerPadding.calculateTopPadding(),
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
    /** Üst bar'ın kapladığı yükseklik; hero içeriği bu kadar aşağıdan başlar. */
    topInset: Dp = 0.dp,
) {
    Crossfade(
        targetState = state.event,
        modifier = modifier.fillMaxSize(),
        animationSpec = tween(durationMillis = 250),
    ) { event ->
        if (event == null) {
            Box(
                // İçerik bar'ın arkasına uzanıyor; yükleyici/hata yine bar'ın altındaki
                // alanda ortalansın.
                modifier = Modifier.fillMaxSize().padding(top = topInset),
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
                topInset = topInset,
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
    topInset: Dp,
    onAction: (EventDetailAction) -> Unit,
) {
    // null = kapalı; saf UI durumu olduğu için ViewModel'e taşınmaz.
    var viewerPage by rememberSaveable { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Kapak görselinden türeyen yavaş renk alanı; gövde kartları üstünde opak
        // durduğu için okunabilirliği bozmuyor.
        // Draw fazında okunur; scroll'da recomposition tetiklemez.
        val scrollOffset: () -> Float = {
            if (listState.firstVisibleItemIndex == 0) {
                listState.firstVisibleItemScrollOffset.toFloat()
            } else {
                Float.MAX_VALUE
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Kart ve başlık tek öğe: renk alanı bu bloğun arkasında duruyor ve onunla
            // birlikte kayıyor. Ayrı bir sabit katmanı scroll ofsetiyle kaydırmak,
            // ofset ilk öğe ekrandan çıkınca süreksiz hale geldiği için zıplıyordu.
            item(contentType = "hero") {
                Box(modifier = Modifier.fillMaxWidth()) {
                    event.imageUrls.firstOrNull()?.let { coverUrl ->
                        DynamicEventBackground(
                            imageUrl = coverUrl,
                            modifier = Modifier.matchParentSize(),
                        )
                    }

                    // Dolgu Column'da, Box'ta değil: renk alanı (matchParentSize) bar'ın
                    // arkasını da boyasın, kart yine bar'ın altında kalsın.
                    Column(
                        modifier = Modifier.padding(top = topInset),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        EventDetailHero(
                            imageUrls = event.imageUrls,
                            scrollOffset = scrollOffset,
                            onImageClick = { page -> viewerPage = page },
                        )

                        EventDetailHeading(
                            title = event.title,
                            meta = listOfNotNull(
                                event.heroDate,
                                event.doorsOpenAt.toClockText(),
                                event.location?.name,
                            ).joinToString(META_SEPARATOR),
                            modifier = Modifier.padding(horizontal = BodyPadding),
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            item(contentType = "event-info") {
                EventDetailInfoSection(
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
    scrollOffset: () -> Float,
    onImageClick: (Int) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    // clipToBounds yok: hale kartın dışına taşan tek şey ve burada kırpılırdı.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeroImageSize + HeroImageMargin * 2),
    ) {
        // Kart sabit, parallax içerideki görselde: kart kendisi kaysaydı yuvarlak alt
        // köşeleri ve halesi kayarken kırpılırdı.
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(HeroImageSize)
                // Kartın clip'inden önce: hale kutunun dışına taşmalı.
                // Dar + koyu + aşağı kaymış hale: yayılım daralınca sönüm kartın kenarında
                // toplanıyor, koyuluk artınca kart zeminden ayrışıyor. offsetY hale'nin
                // kartın altına taşma payı — üstte hep `spread` kadar kalır, aşağıda
                // `spread + offsetY`; aradaki fark büyüdükçe ışık tepeden geliyormuş gibi
                // okunur ve kart daha yüksekte durur (12 üst / 28 alt).
                .softShadow(
                    cornerRadius = HeroImageCorner,
                    spread = 12.dp,
                    offsetY = 16.dp,
                    maxAlpha = 0.45f,
                )
                .graphicsLayer {
                    shadowElevation = HeroImageElevation.toPx()
                    shape = RoundedCornerShape(HeroImageCorner)
                    clip = true
                },
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

            // Scrim kartın içinde: kenar boşluklarının üstüne taşmasın.
            Box(modifier = Modifier.matchParentSize().verticalGradientScrim(HeroScrim))
        }

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

    }
}

/** Başlık artık görselin üstünde değil altında: kart tek parça kalıyor. */
@Composable
private fun EventDetailHeading(
    title: String,
    meta: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34.sp,
                lineHeight = 34.sp,
                letterSpacing = (-1.4).sp,
            ),
        )

        Text(
            text = meta,
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private const val META_SEPARATOR = " · "

private val HeroImageSize = 240.dp
private val HeroImageMargin = 18.dp
private val HeroImageCorner = 24.dp

// Yayılan gölgeyi softShadow veriyor; bu yalnız kartın kenarındaki temas gölgesi.
private val HeroImageElevation = 8.dp
private val BodyPadding = 22.dp

// Alt etek kaldırıldı: başlık artık kartın üstünde değil, orada karartacak bir şey yok.
// Üstteki tek durak PagerDots okunsun diye.
private val HeroScrim = Brush.verticalGradient(
    0f to AccessDefaults.Background.copy(alpha = 0.45f),
    0.24f to Color.Transparent,
    1f to Color.Transparent,
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
