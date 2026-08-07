package com.kaanf.home.presentation.dashboard.component.carousel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.kaanf.core.designsystem.component.badge.RoundedBadge
import com.kaanf.home.presentation.component.verticalGradientScrim
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.home.presentation.model.EventDashboardUiModel
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.abs
import kotlin.random.Random

private const val MinTiltDegrees = 5f
private const val MaxTiltDegrees = 11f
private const val NeighbourScale = 0.78f
private const val NeighbourAlpha = 0.55f
private val CardHeight = 290.dp

private val CardScrim = Brush.verticalGradient(
    0.35f to Color.Transparent,
    1f to Color.Black.copy(alpha = 0.85f),
)
private val NeighbourPull = 44.dp
private val CenterElevation = 16.dp
private val JitterX = 12.dp
private val JitterY = 6.dp
private const val JitterScale = 0.03f

private data class CardJitter(val tilt: Float, val dx: Float, val dy: Float)

private fun jitterFor(page: Int): CardJitter {
    val random = Random(page)
    val magnitude = MinTiltDegrees + random.nextFloat() * (MaxTiltDegrees - MinTiltDegrees)

    return CardJitter(
        tilt = if (random.nextBoolean()) magnitude else -magnitude,
        dx = random.nextFloat() * 2f - 1f,
        dy = random.nextFloat() * 2f - 1f,
    )
}

@Composable
fun DashboardEventCarousel(
    events: List<EventDashboardUiModel>,
    onEventClicked: (eventId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (events.isEmpty()) return

    val isLooping = events.size > 1
    val pageCount = if (isLooping) Int.MAX_VALUE else 1
    val pagerState = rememberPagerState(
        initialPage = if (isLooping) (Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2) % events.size else 0,
    ) { pageCount }
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val sidePadding = maxWidth * 0.16f

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = sidePadding),
            pageSpacing = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val event = events[page % events.size]
            val jitter = remember(page) { jitterFor(page) }

            CarouselEventCard(
                event = event,
                onClicked = {
                    if (page == pagerState.currentPage) {
                        onEventClicked(event.id)
                    } else {
                        scope.launch { pagerState.animateScrollToPage(page) }
                    }
                },
                modifier = Modifier
                    .zIndex(if (page == pagerState.currentPage) 1f else 0f)
                    .padding(vertical = 8.dp)
                    .graphicsLayer {
                        val offset = (
                            (page - pagerState.currentPage) - pagerState.currentPageOffsetFraction
                            ).coerceIn(-1f, 1f)
                        val distance = abs(offset)

                        rotationZ = distance * jitter.tilt
                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                        translationX = -offset * NeighbourPull.toPx() +
                            distance * jitter.dx * JitterX.toPx()
                        translationY = distance * jitter.dy * JitterY.toPx()

                        val scale = lerp(1f, NeighbourScale + jitter.dx * JitterScale, distance)
                        scaleX = scale
                        scaleY = scale
                        alpha = lerp(1f, NeighbourAlpha, distance)

                        shape = AccessShapes.Large
                        clip = true
                        shadowElevation = lerp(CenterElevation.toPx(), 0f, distance)
                    },
            )
        }
    }
}

@Composable
private fun CarouselEventCard(
    event: EventDashboardUiModel,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CardHeight)
            .clip(AccessShapes.Large)
            .background(AccessDefaults.Surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClicked,
            ),
    ) {
        if (event.imageUrl != null) {
            BaseImage(
                imageUrl = event.imageUrl,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        } else {
            Image(
                painter = painterResource(AccessIcons.PixelArt),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }

        Box(modifier = Modifier.matchParentSize().verticalGradientScrim(CardScrim))

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = event.date,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = AccessDefaults.Accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                ),
            )

            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = AccessDefaults.TextPrimary,
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = event.formattedPrice,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = AccessDefaults.TextPrimary,
                        fontSize = 14.sp,
                    ),
                )

                RoundedBadge(
                    text = if (event.hasMyTicket) "TICKET" else "%${event.percentage} Full",
                )
            }
        }
    }
}

@Preview
@Composable
private fun DashboardEventCarouselPreview() {
    CrewTheme {
        DashboardEventCarousel(
            events = List(3) { index ->
                EventDashboardUiModel(
                    id = index.toString(),
                    title = "Crew - Vol ${index + 12}.",
                    date = "FRI, 12 MAR - DOORS 20:00",
                    formattedPrice = "220 CZK",
                    percentage = 42,
                    isFeatured = false,
                    hasMyTicket = false,
                )
            },
            onEventClicked = {},
        )
    }
}
