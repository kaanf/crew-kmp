package com.kaanf.home.presentation.ticketqr

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.info.InfoCard
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.FullScreenLoader
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.KeepScreenOn
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.home.presentation.eventcode.EventCodeContent
import com.kaanf.home.presentation.ticketqr.component.qr.TicketQrInfoCard
import com.kaanf.home.presentation.ticketqr.component.successcard.TicketSuccessCard
import com.kaanf.home.presentation.component.EventVenueDirectionsCard
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.ticket_qr_getting_there
import crew.feature.home.presentation.generated.resources.ticket_qr_info_action
import crew.feature.home.presentation.generated.resources.ticket_qr_info_prefix
import crew.feature.home.presentation.generated.resources.ticket_qr_info_suffix
import crew.feature.home.presentation.generated.resources.ticket_qr_load_error_description
import crew.feature.home.presentation.generated.resources.ticket_qr_load_error_title
import crew.feature.home.presentation.generated.resources.ticket_qr_retry_action
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TicketQrRoot(
    viewModel: TicketQrViewModel = koinViewModel(),
    onCheckInSuccess: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            TicketQrEvent.CheckInSuccess -> onCheckInSuccess()
        }
    }

    TicketContainerScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TicketContainerScreen(
    state: TicketQrState,
    onAction: (TicketQrAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Kapı kuyruğunda ekran pasif gösterilirken kararmasın.
    KeepScreenOn()

    val qrScrollState: ScrollState = rememberScrollState()
    val eventCodeScrollState: ScrollState = rememberScrollState()

    val doorsLocked = rememberDoorsLocked(
        doorsOpenAtMillis = state.ticket?.doorsOpenAt ?: 0L,
        serverClockOffsetMillis = state.ticket?.serverClockOffsetMillis ?: 0L,
    )

    BackHandler(enabled = state.phase == TicketPhase.EventCode) {
        onAction(TicketQrAction.OnBackClick)
    }

    AppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                elevated = {
                    when (state.phase) {
                        TicketPhase.Qr -> qrScrollState.canScrollBackward
                        TicketPhase.EventCode -> eventCodeScrollState.canScrollBackward
                    }
                },
                state = when (state.phase) {
                    TicketPhase.Qr -> AppTopBarState.TicketQr
                    TicketPhase.EventCode -> AppTopBarState.EventCode
                },
                onBackClick = {
                    when (state.phase) {
                        TicketPhase.EventCode -> onAction(TicketQrAction.OnBackClick)
                        TicketPhase.Qr -> onBack()
                    }
                },
            )
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = state.phase,
            contentKey = { it },
            transitionSpec = {
                val forward = targetState.ordinal > initialState.ordinal
                val towards = if (forward) {
                    AnimatedContentTransitionScope.SlideDirection.Left
                } else {
                    AnimatedContentTransitionScope.SlideDirection.Right
                }
                slideIntoContainer(towards, tween(300)) togetherWith
                    slideOutOfContainer(towards, tween(300))
            },
            label = "ticket_phase",
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) { phase ->
            when (phase) {
                TicketPhase.Qr -> TicketQrContent(
                    state = state,
                    scrollState = qrScrollState,
                    onAction = onAction,
                    modifier = Modifier.fillMaxSize(),
                )

                TicketPhase.EventCode -> EventCodeContent(
                    eventCode = state.eventCode,
                    status = state.codeStatus,
                    enabled = !state.isCheckingIn && !doorsLocked,
                    doorsLocked = doorsLocked,
                    doorTime = state.ticket?.formattedDoorClock.orEmpty(),
                    scrollState = eventCodeScrollState,
                    onCodeChanged = { onAction(TicketQrAction.OnCodeChanged(it)) },
                    onClearClicked = { onAction(TicketQrAction.OnClearCode) },
                    onShowQrClicked = { onAction(TicketQrAction.OnBackClick) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun TicketQrContent(
    state: TicketQrState,
    scrollState: ScrollState,
    onAction: (TicketQrAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = state.ticket,
        modifier = modifier.fillMaxSize(),
        animationSpec = tween(durationMillis = 250),
    ) { ticket ->
        if (ticket == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                if (state.loadFailed) {
                    TicketLoadErrorContent(
                        onRetry = { onAction(TicketQrAction.OnRetryLoad) },
                    )
                } else {
                    FullScreenLoader()
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TicketSuccessCard(
                    doorsAtEpochMillis = ticket.doorsOpenAt,
                    serverClockOffsetMillis = ticket.serverClockOffsetMillis,
                )

                InfoCard(
                    text = buildAnnotatedString {
                        append(stringResource(Res.string.ticket_qr_info_prefix))

                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = AccessDefaults.TextPrimary,
                            ),
                        ) {
                            append(stringResource(Res.string.ticket_qr_info_action))
                        }

                        append(stringResource(Res.string.ticket_qr_info_suffix))
                    },
                )

                TicketQrInfoCard(
                    eventTitle = ticket.eventTitle,
                    entryCode = ticket.entryCode,
                    formattedDoorTime = ticket.formattedDoorTime,
                    formattedVenueAddress = ticket.formattedVenueAddress,
                    onEventCodeClicked = {
                        onAction(TicketQrAction.OnEventCodeClicked)
                    },
                )

                ticket.location?.let { location ->
                    Text(
                        text = stringResource(Res.string.ticket_qr_getting_there),
                        style = MaterialTheme.typography.labelSmall,
                        color = AccessDefaults.TextMuted,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    EventVenueDirectionsCard(location = location)
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TicketLoadErrorContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .widthIn(max = 320.dp)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.ticket_qr_load_error_title),
            style = MaterialTheme.typography.titleMedium,
            color = AccessDefaults.TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.ticket_qr_load_error_description),
            style = MaterialTheme.typography.bodyMedium,
            color = AccessDefaults.TextMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        BaseButton(
            text = stringResource(Res.string.ticket_qr_retry_action),
            onClick = onRetry,
            filled = true,
        )
    }
}
