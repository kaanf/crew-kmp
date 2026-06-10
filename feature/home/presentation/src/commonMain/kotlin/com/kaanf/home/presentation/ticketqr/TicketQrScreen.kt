package com.kaanf.home.presentation.ticketqr

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.info.InfoCard
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.home.presentation.eventcode.EventCodeContent
import com.kaanf.home.presentation.ticketqr.component.qr.TicketQrInfoCard
import com.kaanf.home.presentation.ticketqr.component.successcard.TicketSuccessCard
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.ticket_qr_info_action
import crew.feature.home.presentation.generated.resources.ticket_qr_info_prefix
import crew.feature.home.presentation.generated.resources.ticket_qr_info_suffix
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
    val qrScrollState: ScrollState = rememberScrollState()
    val eventCodeScrollState: ScrollState = rememberScrollState()

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
                    enabled = !state.isCheckingIn,
                    scrollState = eventCodeScrollState,
                    onCodeChanged = { onAction(TicketQrAction.OnCodeChanged(it)) },
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
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TicketSuccessCard(
                doorsAtEpochMillis = state.ticket?.doorsOpenAt ?: 0L,
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
                eventTitle = state.ticket?.eventTitle.orEmpty(),
                entryCode = state.ticket?.entryCode.orEmpty(),
                formattedDoorTime = state.ticket?.formattedDoorTime.orEmpty(),
                formattedVenueAddress = state.ticket?.formattedVenueAddress.orEmpty(),
                onEventCodeClicked = {
                    onAction(TicketQrAction.OnEventCodeClicked)
                },
            )
        }
    }
}
