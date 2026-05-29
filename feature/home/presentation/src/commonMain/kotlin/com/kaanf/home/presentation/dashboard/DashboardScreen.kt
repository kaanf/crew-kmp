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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.LoadingOverlayLayout
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.home.presentation.dashboard.component.EventRow
import com.kaanf.home.presentation.dashboard.component.challengecard.ChallengeCardUiModel
import com.kaanf.home.presentation.dashboard.component.challengecard.ChallengeCardVariant
import com.kaanf.home.presentation.dashboard.component.challengecard.GradientChallengeCard
import com.kaanf.home.presentation.dashboard.component.challengecard.MoreDeckCard
import com.kaanf.home.presentation.dashboard.component.eventinfo.DashboardEventInfoRow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardRoot(
    viewModel: DashboardViewModel = koinViewModel(),
    onEventClicked: (eventId: String) -> Unit = { },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
        }
    }

    SnackbarScaffold(snackbarHostState = snackbarHostState) { innerPadding ->
        LoadingOverlayLayout(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            isLoading = state.isLoading,
        ) {
            DashboardScreen(
                state = state,
                onAction = {},
                onEventClicked
            )
        }
    }
}

@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
    onEventClicked: (eventId: String) -> Unit = { },
) {
    val gradientCards = listOf(
        ChallengeCardUiModel(
            description = "Get two strangers to teach you the same word in their language. Both of them, same word.",
            variant = ChallengeCardVariant.Social,
            points = 20
        ),
        ChallengeCardUiModel(
            description = "Walk to the loudest table and convince one of them to teach you a dance move.",
            variant = ChallengeCardVariant.Bold,
            points = 35
        ),
        ChallengeCardUiModel(
            description = "Find someone wearing your favourite colour. Ask why they chose it tonight.",
            variant = ChallengeCardVariant.Icebreaker,
            points = 10
        ),
        ChallengeCardUiModel(
            description = "Sincerely compliment three different people on something they chose for tonight.",
            variant = ChallengeCardVariant.Flirty,
            points = 35
        ),
        ChallengeCardUiModel(
            description = "Find one more player. Together find a fourth. Selfie. Bring me proof.",
            variant = ChallengeCardVariant.Team,
            points = 20
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        AppTopBar(
            state = AppTopBarState.Dashboard,
            onRightClick = {  }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item(contentType = "header") {
                DashboardHeader()
            }

            item(contentType = "desk_card_info") {
                DashboardEventInfoRow(
                    leftText = "A TASTE OF THE GAME",
                    description = "Real cards from past nights · swipe →",
                    rightText = "120+ DECK",
                )
            }

            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = gradientCards,
                        key = { it.description },
                        contentType = { "challenge_card" },
                    ) { card ->
                        GradientChallengeCard(
                            card = card,
                        )
                     }

                    item(
                        key = "see_all_challenges",
                        contentType = "see_all_challenges"
                    ) {
                        MoreDeckCard()
                    }
                }
            }

            item(contentType = "divider") {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item(contentType = "event_info") {
                DashboardEventInfoRow(
                    leftText = "UPCOMING EVENTS",
                    rightText = "${state.events.size} events",
                )
            }

            items(
                items = state.events,
                key = { it.id },
                contentType = { "event" },
            ) { event ->
                EventRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    event = event,
                    onClicked = { onEventClicked(it) }
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "This week in the city",
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Live bar games. You show up solo, leave with a story.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextMuted,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
