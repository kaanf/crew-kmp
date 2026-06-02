package com.kaanf.home.presentation.ticketqr

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.info.InfoCard
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.home.presentation.ticketqr.component.qr.TicketQrInfoCard
import com.kaanf.home.presentation.ticketqr.component.successcard.TicketSuccessCard
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.ticket_qr_info_action
import crew.feature.home.presentation.generated.resources.ticket_qr_info_prefix
import crew.feature.home.presentation.generated.resources.ticket_qr_info_suffix
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.emitter.Emitter
import io.github.vinceglb.confettikit.core.models.Shape
import io.github.vinceglb.confettikit.core.models.Size
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TicketQrRoot(
    viewModel: TicketQrViewModel = koinViewModel(),
    onEventCodeClicked: (eventId: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollState: ScrollState = rememberScrollState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            else -> Unit
        }
    }

    SnackbarScaffold(
        topBar = {
            AppTopBar(
                elevated = { scrollState.canScrollBackward },
                state = AppTopBarState.TicketQr,
                onBackClick = {},
            )
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        TicketQrScreen(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            scrollState = scrollState,
            state = state,
            onAction = {
                when (it) {
                    TicketQrAction.OnEventCodeClicked -> onEventCodeClicked(state.ticket?.eventId.orEmpty())
                }
            },
        )
    }
}

@Composable
fun TicketQrScreen(
    modifier: Modifier,
    scrollState: ScrollState,
    state: TicketQrState,
    onAction: (TicketQrAction) -> Unit,
) {
    var isConfettiVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isConfettiVisible = true
    }

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

        if (isConfettiVisible) {
            ConfettiKit(
                modifier = Modifier.fillMaxSize(),
                parties = remember { listOf(successConfetti()) },
                onParticleSystemEnded = { _, activeSystems ->
                    if (activeSystems == 0) {
                        isConfettiVisible = false
                    }
                },
            )
        }
    }
}

private fun successConfetti(): Party {
    return Party(
        speed = 0f,
        maxSpeed = 30f,
        damping = 0.9f,
        spread = 360,
        colors = listOf(
            0xC8FF3D,
            0xFF7A5C,
            0xFF5A7A,
            0x6FB7FF,
        ),
        shapes = listOf(
            Shape.Circle,
        ),
        size = listOf(
            Size.SMALL,
            Size.MEDIUM,
        ),
        emitter = Emitter(
            duration = 100.milliseconds,
        ).max(100),
        position = Position.Relative(
            x = 0.5,
            y = 0.05,
        ),
    )
}
