package com.kaanf.home.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.home.presentation.dashboard.component.eventcard.DashboardEventCard
import com.kaanf.home.presentation.dashboard.component.challengecard.GradientChallengeCard
import com.kaanf.home.presentation.dashboard.component.challengecard.MoreDeckCard
import com.kaanf.home.presentation.dashboard.component.eventinfo.DashboardEventInfoRow
import com.kaanf.home.presentation.dashboard.component.featuredevent.DashboardFeaturedEventCard
import com.kaanf.home.presentation.model.EventDashboardUiModel
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.dashboard_featured_event_date
import crew.feature.home.presentation.generated.resources.dashboard_featured_event_price
import crew.feature.home.presentation.generated.resources.dashboard_featured_event_section_title
import crew.feature.home.presentation.generated.resources.dashboard_featured_event_title
import crew.feature.home.presentation.generated.resources.dashboard_game_preview_cta
import crew.feature.home.presentation.generated.resources.dashboard_game_preview_section_description
import crew.feature.home.presentation.generated.resources.dashboard_game_preview_section_title
import crew.feature.home.presentation.generated.resources.dashboard_header_description
import crew.feature.home.presentation.generated.resources.dashboard_header_title
import crew.feature.home.presentation.generated.resources.dashboard_upcoming_events_count
import crew.feature.home.presentation.generated.resources.dashboard_upcoming_events_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardRoot(
    viewModel: DashboardViewModel = koinViewModel(),
    onEventClicked: (eventId: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            else -> Unit
        }
    }

    val listState = rememberLazyListState()

    SnackbarScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.Dashboard,
                elevated = { listState.canScrollBackward },
                onRightClick = {},
            )
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        DashboardScreen(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            listState = listState,
            state = state,
            onAction = { action ->
                when (action) {
                    is DashboardAction.OnEventClicked -> onEventClicked(action.id)
                }

                viewModel.onAction(action)
            },
        )
    }
}

@Composable
fun DashboardScreen(
    modifier: Modifier,
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
    listState: LazyListState,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
                .zIndex(0f),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item(contentType = "header") {
                DashboardHeader()
            }

            item(contentType = "space-after-header") {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item(contentType = "featured-card-section") {
                DashboardSection(
                    title = stringResource(Res.string.dashboard_featured_event_section_title),
                    description = null,
                    ctaText = "",
                    content = {
                        val featuredEvent = EventDashboardUiModel(
                            id = "1",
                            title = stringResource(Res.string.dashboard_featured_event_title),
                            date = stringResource(Res.string.dashboard_featured_event_date),
                            formattedPrice = stringResource(Res.string.dashboard_featured_event_price),
                            percentage = 42,
                            isFeatured = false,
                        )

                        DashboardFeaturedEventCard(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            event = featuredEvent,
                            onClicked = {
                                onAction(DashboardAction.OnEventClicked(featuredEvent.id))
                            },
                        )
                    },
                )
            }

            item(contentType = "space-after-featured") {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item(contentType = "deck-card-section") {
                DashboardSection(
                    title = stringResource(Res.string.dashboard_game_preview_section_title),
                    description = stringResource(Res.string.dashboard_game_preview_section_description),
                    ctaText = stringResource(Res.string.dashboard_game_preview_cta),
                    content = {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(
                                items = state.tasks,
                                key = { it.description }, // key'i task id yap.
                                contentType = { "challenge_card" },
                            ) { card ->
                                GradientChallengeCard(card = card)
                            }

                            item(
                                key = "see_all_challenges",
                                contentType = "see_all_challenges",
                            ) {
                                MoreDeckCard()
                            }
                        }
                    },
                )
            }

            item(contentType = "space-after-deck") {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item(contentType = "upcoming-events-header") {
                DashboardEventInfoRow(
                    leftText = stringResource(Res.string.dashboard_upcoming_events_title),
                    description = null,
                    rightText = stringResource(
                        Res.string.dashboard_upcoming_events_count,
                        state.events.size,
                    ),
                )
            }

            item(contentType = "space-after-upcoming-header") {
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(
                items = state.events,
                key = { it.id },
                contentType = { "event_card" },
            ) { event ->
                DashboardEventCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    event = event,
                    onClicked = {
                        onAction(DashboardAction.OnEventClicked(event.id))
                    },
                )
            }
        }
    }
}

@Composable
private fun DashboardSection(
    title: String,
    ctaText: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DashboardEventInfoRow(
            leftText = title,
            description = description,
            rightText = ctaText,
        )

        content()
    }
}

@Composable
private fun DashboardHeader() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.dashboard_header_title),
            style = MaterialTheme.typography.displayMedium,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(Res.string.dashboard_header_description),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextMuted,
            ),
        )
    }
}
