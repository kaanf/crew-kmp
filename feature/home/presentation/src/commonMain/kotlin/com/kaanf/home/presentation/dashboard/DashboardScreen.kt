package com.kaanf.home.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.ColorFilter
import org.jetbrains.compose.resources.painterResource
import crew.feature.home.presentation.generated.resources.dashboard_rock_paper_scissors
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.kaanf.core.presentation.permission.PermissionState
import com.kaanf.core.presentation.permission.rememberPermissionController
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.core.presentation.util.ReportFullyDrawnWhen
import com.kaanf.core.designsystem.component.card.GradientChallengeCard
import com.kaanf.home.presentation.dashboard.component.profile.DashboardProfilePhotoReminder
import com.kaanf.home.presentation.dashboard.component.carousel.DashboardEventCarousel
import com.kaanf.home.presentation.dashboard.component.emptystate.DashboardEmptyState
import com.kaanf.home.presentation.dashboard.component.eventcard.DashboardEventCard
import com.kaanf.home.presentation.dashboard.component.eventinfo.DashboardEventInfoRow
import com.kaanf.home.presentation.dashboard.component.notification.NotificationPermissionSheet
import com.kaanf.home.presentation.dashboard.component.notification.NotificationSheetStep
import com.kaanf.home.presentation.model.EventDashboardUiModel
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.dashboard_featured_event_section_description
import crew.feature.home.presentation.generated.resources.dashboard_featured_event_section_title
import crew.feature.home.presentation.generated.resources.dashboard_game_preview_section_description
import crew.feature.home.presentation.generated.resources.dashboard_game_preview_section_title
import crew.feature.home.presentation.generated.resources.dashboard_header_title
import crew.feature.home.presentation.generated.resources.dashboard_doors_open_events_count
import crew.feature.home.presentation.generated.resources.dashboard_doors_open_events_title
import crew.feature.home.presentation.generated.resources.dashboard_your_events_count
import crew.feature.home.presentation.generated.resources.dashboard_your_events_title
import crew.feature.home.presentation.generated.resources.dashboard_upcoming_events_count
import crew.feature.home.presentation.generated.resources.dashboard_upcoming_events_title
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.launch
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
            DashboardEvent.NavigateToProfile -> onProfileClicked()
            is DashboardEvent.NavigateToEventDetail -> onEventClicked(event.eventId)
        }
    }

    // Platformun cold start ölçümü ilk karede (splash) duruyor; kullanıcının gerçekten
    // içerik gördüğü anı ayrıca bildiriyoruz. Koşul "veri geldi" değil "yükleme bitti":
    // hata durumunda da true olmazsa metrik hiç raporlanmaz.
    ReportFullyDrawnWhen { !state.isLoading }

    NotificationPermissionPrompt(
        isReady = !state.isLoading,
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
 * Bildirim iznini açılışta değil, dashboard yüklendikten sonra ister. Sistem dialogu sınırlı
 * sayıda gösterilebiliyor (Android'de iki red sonrası bir daha hiç, iOS'ta tek hak), o yüzden
 * önce açıklama sheet'i gösterilir; sistem dialogu ancak
 * kullanıcı "Turn on" derse harcanır.
 *
 * ponytail: kalıcı "soruldu" bayrağı yok — izin hâlâ sorulabilir durumdaysa her soğuk açılışta
 * sheet tekrar çıkar. Can sıkarsa "Not now" için DataStore'da bir bayrak tutulur.
 */
@Composable
private fun NotificationPermissionPrompt(isReady: Boolean) {
    val permissionController = rememberPermissionController()
    val scope = rememberCoroutineScope()
    var checked by rememberSaveable { mutableStateOf(false) }
    var step by rememberSaveable { mutableStateOf<NotificationSheetStep?>(null) }

    LaunchedEffect(isReady) {
        if (checked || !isReady) return@LaunchedEffect
        checked = true
        val permissionState = permissionController.checkPermission(Permission.REMOTE_NOTIFICATION)
        if (permissionState == PermissionState.NOT_DETERMINED || permissionState == PermissionState.DENIED) {
            step = NotificationSheetStep.Ask
        }
    }

    step?.let { currentStep ->
        NotificationPermissionSheet(
            step = currentStep,
            onEnableClick = {
                scope.launch {
                    val result = permissionController.requestPermission(Permission.REMOTE_NOTIFICATION)
                    step = if (result == PermissionState.GRANTED) {
                        NotificationSheetStep.Enabled
                    } else {
                        NotificationSheetStep.Skipped
                    }
                }
            },
            onSkipClick = { step = NotificationSheetStep.Skipped },
            onDismiss = { step = null },
        )
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
    // Tam ekran Crossfade kaldırıldı: iki durumu da compose edilmiş tutup ekran boyu bir
    // alpha katmanı (offscreen buffer) açıyordu. FullScreenLoader zaten 500 ms geciktiği
    // için kısa yüklemelerde spinner hiç çizilmiyor, geçiş de göze çarpmıyor.
    if (state.isLoading) {
        FullScreenLoader(modifier = modifier.fillMaxSize())
    } else {
        val pullToRefreshState = rememberPullToRefreshState()

        PullToRefreshBox(
            modifier = modifier.fillMaxSize(),
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
        if (!hasNoEvents) {
            item(contentType = "header") {
                DashboardHeader()
            }

            item(contentType = "space-after-header") {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (state.showProfilePhotoReminder) {
            item(key = "profile-photo-reminder", contentType = "profile-photo-reminder") {
                DashboardProfilePhotoReminder(
                    onClick = { onAction(DashboardAction.OnProfilePhotoReminderClicked) },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = if (hasNoEvents) 16.dp else 0.dp, bottom = 24.dp),
                )
            }
        }

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

        eventSection(
            title = yourEventsTitle,
            countText = yourEventsCount,
            events = state.myEvents,
            onEventClicked = { onAction(DashboardAction.OnEventClicked(it.id)) },
        )

        if (state.featuredEvents.isNotEmpty()) {
            item(contentType = "featured-carousel-section") {
                DashboardSection(
                    title = stringResource(Res.string.dashboard_featured_event_section_title),
                    description = stringResource(Res.string.dashboard_featured_event_section_description),
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
                            GradientChallengeCard(
                                card = card,
                                showPoints = false,
                                descriptionSpacing = 4.dp,
                                pressFeedbackEnabled = false,
                            )
                        }

                        item(
                            key = "see_all_challenges",
                            contentType = "see_all_challenges",
                        ) {
                            //MoreDeckCard()
                        }
                    }
                },
            )
        }

        item(contentType = "space-after-deck") {
            Spacer(modifier = Modifier.height(24.dp))
        }

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
            rightText = "",
        )

        content()
    }
}

@Composable
private fun DashboardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.dashboard_header_title),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.weight(1f),
        )
        Image(
            painter = painterResource(Res.drawable.dashboard_rock_paper_scissors),
            contentDescription = null,
            colorFilter = ColorFilter.tint(AccessDefaults.TextPrimary),
            modifier = Modifier.size(96.dp),
        )
    }
}
