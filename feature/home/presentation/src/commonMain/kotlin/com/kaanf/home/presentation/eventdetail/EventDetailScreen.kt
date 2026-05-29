package com.kaanf.home.presentation.eventdetail

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil3.compose.AsyncImage
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.LoadingOverlayLayout
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.home.presentation.component.eventHeroBackground
import com.kaanf.home.presentation.eventdetail.component.EventDetailInformationCard
import com.kaanf.home.presentation.eventdetail.component.EventOnboarding
import com.kaanf.home.presentation.eventdetail.component.SafetyBadge
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun EventDetailRoot(
    viewModel: EventDetailViewModel = koinViewModel()
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
            isLoading = false,
        ) {
            EventDetailScreen(
                state = state,
                onAction = {},
            )
        }
    }
}

@Composable
fun EventDetailScreen(
    state: EventDetailState,
    onAction: (EventDetailAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AppTopBar(
            state = AppTopBarState.Title,
            title = "Event Detail",
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(1.dp))
            }

            item(contentType = "hero-card") {
                EventDetailHeroCard("", "")
            }

            item(contentType = "information-card") {
                EventDetailInformationCard()
            }

            item(contentType = "onboarding") {
                EventOnboarding()
            }

            item(contentType = "safety-card") {
                SafetyBadge()
            }
        }
    }
}

@Composable
private fun EventDetailHeroCard(
    title: String,
    date: String
) {
    EventHeroBackground(
        imageUrl = "https://www.booking.com/hotel/cz/hostel-florenc.en-gb.html",
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
                alignment = Alignment.Bottom
            )
        ) {
            Text(
                text = "SATURDAY - MAY 30",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 12.sp
                )
            )

            Text(
                text = "One night.\nOne bar.\n80 strangers.",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = AccessDefaults.TextPrimary
                ),
            )
        }
    }
}

@Composable
@Preview
fun EventDetailScreenPreview() {
    EventDetailScreen(
        state = EventDetailState(),
        onAction = {},
    )
}

@Composable
fun EventHeroBackground(
    imageUrl: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = shape
            )
    ) {
        AsyncImage(
            model = "https://hostel-drunken-monkey.praguehotelsweb.com/data/Photos/OriginalPhoto/16920/1692044/1692044305/drunken-monkey-hostel-prague-photo-15.JPEG",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.75f))
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
                                strokeWidth = stripeStroke
                            )
                            x += stripeStep
                        }
                    }
                }
        )

        content()
    }
}
