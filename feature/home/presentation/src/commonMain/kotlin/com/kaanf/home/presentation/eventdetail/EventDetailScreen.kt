package com.kaanf.home.presentation.eventdetail

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.home.presentation.eventdetail.component.EventDetailInformationCard
import com.kaanf.home.presentation.eventdetail.component.EventOnboardingCard
import com.kaanf.home.presentation.eventdetail.component.SafetyBadge
import com.kaanf.home.presentation.model.EventDetailUiModel
import com.kaanf.home.presentation.util.toClockText
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.event_detail_free_ticket_cta
import crew.feature.home.presentation.generated.resources.event_detail_my_ticket_cta
import crew.feature.home.presentation.generated.resources.event_detail_ticket_cta
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
    val snackbarHostState = remember { SnackbarHostState() }

    val listState: LazyListState = rememberLazyListState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is EventDetailEvent.CheckoutSuccess -> {
                onCheckoutSuccess(event.eventId)
            }
        }
    }

    SnackbarScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.EventDetail,
                elevated = { listState.canScrollBackward },
                onBackClick = { onBackClick() },
            )
        },
        snackbarHostState = snackbarHostState,
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
                CircularProgressIndicator(color = AccessDefaults.Accent)
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
private fun EventDetailContent(
    listState: LazyListState,
    event: EventDetailUiModel,
    isCheckingOut: Boolean,
    onAction: (EventDetailAction) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(contentType = "hero-card") {
                EventDetailHeroCard(
                    date = event.heroDate,
                    title = event.title,
                )
            }

            item(contentType = "information-card") {
                EventDetailInformationCard(
                    doorsTime = event.doorsOpenAt.toClockText(),
                    gameTime = event.gameTime,
                    crew = event.crew,
                    price = event.formattedPrice,
                )
            }

            item(contentType = "onboarding-card") {
                EventOnboardingCard()
            }

            item(contentType = "safety-card") {
                SafetyBadge()
            }

            item(contentType = "space-after-last-card") {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        BaseButton(
            text = when {
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
            enabled = !isCheckingOut,
            filled = false,
            animatedBorder = true,
        )
    }
}

@Composable
private fun EventDetailHeroCard(
    date: String,
    title: String,
) {
    EventHeroBackground(
        imageUrl = "https://hostel-drunken-monkey.praguehotelsweb.com/data/Photos/OriginalPhoto/16920/1692044/1692044305/drunken-monkey-hostel-prague-photo-15.JPEG",
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.Bottom,
            ),
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 12.sp,
                ),
            )

            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall.copy(
                    color = AccessDefaults.TextPrimary,
                ),
            )
        }
    }
}

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

@Composable
fun EventHeroBackground(
    imageUrl: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = shape,
            ),
    ) {
        BaseImage(
            imageUrl = imageUrl,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.75f)),
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .drawWithCache {
                    val stripeColor = Color.White.copy(alpha = 0.035f)
                    val stripeStep = 18.dp.toPx()
                    val stripeStroke = 2.dp.toPx()

                    onDrawBehind {
                        var x = -size.height

                        while (x < size.width + size.height) {
                            drawLine(
                                color = stripeColor,
                                start = Offset(x, 0f),
                                end = Offset(x + size.height, size.height),
                                strokeWidth = stripeStroke,
                            )
                            x += stripeStep
                        }
                    }
                },
        )

        content()
    }
}
