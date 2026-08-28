package com.kaanf.home.presentation.dashboard

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.FullScreenLoader
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.permission.Permission
import com.kaanf.core.presentation.permission.rememberPermissionController
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.core.designsystem.component.card.GradientChallengeCard
import com.kaanf.core.designsystem.component.card.MoreDeckCard
import com.kaanf.home.presentation.dashboard.component.carousel.DashboardEventCarousel
import com.kaanf.home.presentation.dashboard.component.emptystate.DashboardEmptyState
import com.kaanf.home.presentation.dashboard.component.eventcard.DashboardEventCard
import com.kaanf.home.presentation.dashboard.component.eventinfo.DashboardEventInfoRow
import com.kaanf.home.presentation.model.EventDashboardUiModel
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.dashboard_featured_event_section_description
import crew.feature.home.presentation.generated.resources.dashboard_featured_event_section_title
import crew.feature.home.presentation.generated.resources.dashboard_game_preview_cta
import crew.feature.home.presentation.generated.resources.dashboard_game_preview_section_description
import crew.feature.home.presentation.generated.resources.dashboard_game_preview_section_title
import crew.feature.home.presentation.generated.resources.dashboard_header_description
import crew.feature.home.presentation.generated.resources.dashboard_header_title
import crew.feature.home.presentation.generated.resources.dashboard_doors_open_events_count
import crew.feature.home.presentation.generated.resources.dashboard_doors_open_events_title
import crew.feature.home.presentation.generated.resources.dashboard_your_events_count
import crew.feature.home.presentation.generated.resources.dashboard_your_events_title
import crew.feature.home.presentation.generated.resources.dashboard_upcoming_events_count
import crew.feature.home.presentation.generated.resources.dashboard_upcoming_events_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardRoot(
    viewModel: DashboardViewModel = koinViewModel(),
    onEventClicked: (eventId: String) -> Unit,
    onProfileClicked: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.onAction(DashboardAction.OnResume)
        onPauseOrDispose { }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is DashboardEvent.NavigateToEventDetail -> onEventClicked(event.eventId)
        }
    }

    RequestNotificationPermissionEffect(
        isReady = !state.isLoading && state.profilePictureUrl != null,
    )

    val listState = rememberLazyListState()

    AppScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.Dashboard(state.profilePictureUrl, state.userName),
                elevated = { listState.canScrollBackward },
                onRightClick = onProfileClicked,
            )
        },
    ) { innerPadding ->
        DashboardScreen(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            listState = listState,
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

/**
 * Bildirim iznini açılışta değil, kullanıcı login olup profil fotoğrafını da yükledikten sonra
 * dashboard'da ister. Sistem dialogu sınırlı sayıda gösterilebiliyor (Android'de iki red sonrası
 * bir daha hiç, iOS'ta tek hak), o yüzden kullanıcı uygulamanın ne olduğunu anlamadan harcanmıyor.
 *
 * ponytail: kalıcı "soruldu" bayrağı yok — izin verilmiş/reddedilmişse requestPermission zaten
 * dialog göstermeden dönüyor, tek maliyet bir suspend çağrısı.
 */
@Composable
private fun RequestNotificationPermissionEffect(isReady: Boolean) {
    val permissionController = rememberPermissionController()
    var requested by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isReady) {
        if (requested || !isReady) return@LaunchedEffect
        requested = true
        permissionController.requestPermission(Permission.REMOTE_NOTIFICATION)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    modifier: Modifier,
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
    listState: LazyListState,
) {
    Crossfade(
        targetState = state.isLoading,
        modifier = modifier.fillMaxSize(),
        animationSpec = tween(durationMillis = 250),
    ) { isLoading ->
        if (isLoading) {
            FullScreenLoader()
        } else {
            val pullToRefreshState = rememberPullToRefreshState()

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(DashboardAction.OnRefresh) },
                state = pullToRefreshState,
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        state = pullToRefreshState,
                        isRefreshing = state.isRefreshing,
                        containerColor = AccessDefaults.SurfaceElevated,
                        color = AccessDefaults.Accent,
                    )
                },
            ) {
                DashboardContent(
                    listState = listState,
                    state = state,
                    onAction = onAction,
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    listState: LazyListState,
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
) {
    val yourEventsTitle = stringResource(Res.string.dashboard_your_events_title)
    val yourEventsCount = stringResource(Res.string.dashboard_your_events_count, state.myEvents.size)
    val doorsOpenTitle = stringResource(Res.string.dashboard_doors_open_events_title)
    val doorsOpenCount = stringResource(Res.string.dashboard_doors_open_events_count, state.doorsOpenEvents.size)
    val upcomingTitle = stringResource(Res.string.dashboard_upcoming_events_title)
    val upcomingCount = stringResource(Res.string.dashboard_upcoming_events_count, state.upcomingEvents.size)

    val hasNoEvents = state.myEvents.isEmpty() &&
        state.doorsOpenEvents.isEmpty() &&
        state.upcomingEvents.isEmpty()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        if (hasNoEvents) {
            item(contentType = "empty-state") {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    DashboardEmptyState()
                }
            }
            return@LazyColumn
        }

        item(contentType = "header") {
            DashboardHeader()
        }

        item(contentType = "space-after-header") {
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.featuredEvents.isNotEmpty()) {
            item(contentType = "featured-carousel-section") {
                DashboardSection(
                    title = stringResource(Res.string.dashboard_featured_event_section_title),
                    description = stringResource(Res.string.dashboard_featured_event_section_description),
                    ctaText = "",
                    content = {
                        DashboardEventCarousel(
                            events = state.featuredEvents,
                            onEventClicked = { onAction(DashboardAction.OnEventClicked(it)) },
                        )
                    },
                )
            }

            item(contentType = "space-after-featured") {
                Spacer(modifier = Modifier.height(24.dp))
            }
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

        eventSection(
            title = yourEventsTitle,
            countText = yourEventsCount,
            events = state.myEvents,
            onEventClicked = { onAction(DashboardAction.OnEventClicked(it.id)) },
        )

        eventSection(
            title = doorsOpenTitle,
            countText = doorsOpenCount,
            events = state.doorsOpenEvents,
            onEventClicked = { onAction(DashboardAction.OnEventClicked(it.id)) },
        )

        eventSection(
            title = upcomingTitle,
            countText = upcomingCount,
            events = state.upcomingEvents,
            onEventClicked = { onAction(DashboardAction.OnEventClicked(it.id)) },
        )
    }
}

private fun LazyListScope.eventSection(
    title: String,
    countText: String,
    events: List<EventDashboardUiModel>,
    onEventClicked: (EventDashboardUiModel) -> Unit,
) {
    if (events.isEmpty()) return

    item(contentType = "events-header") {
        DashboardEventInfoRow(
            leftText = title,
            description = null,
            rightText = countText,
        )
    }

    item(contentType = "space-after-events-header") {
        Spacer(modifier = Modifier.height(12.dp))
    }

    items(
        items = events,
        key = { it.id },
        contentType = { "event_card" },
    ) { event ->
        DashboardEventCard(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            event = event,
            onClicked = { onEventClicked(event) },
        )
    }

    item(contentType = "space-after-events") {
        Spacer(modifier = Modifier.height(24.dp))
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
        Spacer(modifier = Modifier.height(2.dp))

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
