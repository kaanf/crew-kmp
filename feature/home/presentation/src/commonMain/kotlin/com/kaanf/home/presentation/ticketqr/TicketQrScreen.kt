package com.kaanf.home.presentation.ticketqr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.info.InfoCard
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.LoadingOverlayLayout
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.home.presentation.ticketqr.component.qr.TicketQrInfoCard
import com.kaanf.home.presentation.ticketqr.component.successcard.TicketSuccessCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TicketQrRoot(
    viewModel: TicketQrViewModel = koinViewModel(),
    onEventCodeClicked: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val listState: LazyListState = rememberLazyListState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
        }
    }

    SnackbarScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.TicketQr,
                onBackClick = {},
            )
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        LoadingOverlayLayout(

            isLoading = false,
        ) {
            TicketQrScreen(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                listState = listState,
                state = state,
                onAction = {
                    when (it) {
                        TicketQrAction.OnEventCodeClicked -> onEventCodeClicked()
                    }
                },
            )
        }
    }
}

@Composable
fun TicketQrScreen(
    modifier: Modifier,
    listState: LazyListState,
    state: TicketQrState,
    onAction: (TicketQrAction) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(1.dp))
            }

            item(contentType = "success-card") {
                TicketSuccessCard()
            }

            item(contentType = "info-card") {
                InfoCard(
                    text = buildAnnotatedString {
                        append("Show the QR at the door — or if the camera's slow, tap ")

                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = AccessDefaults.TextPrimary,
                            ),
                        ) {
                            append("enter event code")
                        }

                        append(" and type the 4 letters on the welcome card.")
                    },
                )
            }

            item(contentType = "ticket-qr-card") {
                TicketQrInfoCard(
                    onEventCodeClicked = {
                        onAction(TicketQrAction.OnEventCodeClicked)
                    },
                )
            }
        }
    }
}
