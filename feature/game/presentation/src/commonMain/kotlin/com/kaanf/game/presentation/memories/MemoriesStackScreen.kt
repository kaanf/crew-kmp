package com.kaanf.game.presentation.memories

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.domain.review.appReviewUrl
import com.kaanf.game.domain.model.EventMemory
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.memories_you_badge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.zIndex
import com.kaanf.core.designsystem.component.button.BaseMiniButton
import com.kaanf.core.designsystem.theme.AccessIcons
import crew.feature.game.presentation.generated.resources.memories_finished_rate
import crew.feature.game.presentation.generated.resources.memories_finished_replay
import crew.feature.game.presentation.generated.resources.memories_finished_subtitle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** Bir karenin yığındaki dağınıklığı: eğim (derece) ve merkezden kayma. */
private data class Scatter(val tilt: Float, val dx: Dp, val dy: Dp)

// Kare rulodaki sırasına göre sabit bir dağınıklık alır; üste çıkarken yeri değişmez.
// Kaymalar merkezin iki yanına dağıtıldı ki yığın bir bütün olarak ortada dursun.
private val Scatters = listOf(
    Scatter(tilt = -6f, dx = (-10).dp, dy = 4.dp),
    Scatter(tilt = 5f, dx = 12.dp, dy = (-8).dp),
    Scatter(tilt = -2.5f, dx = 4.dp, dy = 10.dp),
    Scatter(tilt = 8f, dx = (-14).dp, dy = (-4).dp),
    Scatter(tilt = -8.5f, dx = 10.dp, dy = 6.dp),
    Scatter(tilt = 3.5f, dx = (-6).dp, dy = (-10).dp),
)
private const val STACK_DEPTH = 4
private const val PREFETCH_DISTANCE = 3
private const val FLY_DURATION_MS = 280
private const val ENTRANCE_DURATION_MS = 360
private const val ENTRANCE_STAGGER_MS = 80L
private const val CARD_ASPECT = 1.25f
private val CardShape = RoundedCornerShape(20.dp)
private val SwipeThreshold = 80.dp
private val FlyDistance = 560.dp

/**
 * Etkinlik sonu foto rulosu: odanın fotoğrafları üst üste dağınık baskılar. Üstteki kare
 * herhangi bir yöne fırlatılınca alttaki açılır; sonuncudan sonra kapanış mesajı ve
 * desteyi yeniden dağıtan buton gelir.
 * Kendi top bar'ı yok: MatchContainerScreen içinde leaderboard'un yerine çizilir.
 */
@Composable
fun MemoriesStackRoot(
    modifier: Modifier = Modifier,
    viewModel: MemoriesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // İmzalı URL'ler kısa ömürlü; rulo her açıldığında taze liste.
    LaunchedEffect(Unit) { viewModel.refresh() }

    MemoriesStackScreen(
        state = state,
        onLoadMore = viewModel::loadNextPage,
        modifier = modifier,
    )
}

@Composable
fun MemoriesStackScreen(
    state: MemoriesState,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val memories = state.memories
    val count = memories.size
    var index by rememberSaveable { mutableIntStateOf(0) }

    val scope = rememberCoroutineScope()
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var isFlying by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val flyPx = with(density) { FlyDistance.toPx() }
    val thresholdPx = with(density) { SwipeThreshold.toPx() }
    // index == count: deste bitti, kapanış ekranı. Son karede sayfalar bitmediyse sonraki
    // sayfa gelene dek fırlatılamaz.
    val isFinished = count > 0 && index >= count
    val canAdvance by rememberUpdatedState(index < count - 1 || (index == count - 1 && state.endReached))

    // Tazelemede liste kısalırsa index dışarıda kalmasın.
    LaunchedEffect(count) {
        if (index > count) index = count
    }
    // Sona yaklaşınca sonraki sayfa; kullanıcı rulonun sonuna gelmeden yetişsin.
    LaunchedEffect(index, count, state.endReached) {
        if (!state.endReached && count > 0 && index >= count - PREFETCH_DISTANCE) onLoadMore()
    }

    fun settle() {
        val dragged = offset.value
        if (isFlying) return
        if (dragged.getDistance() <= thresholdPx || !canAdvance) {
            scope.launch { offset.animateTo(Offset.Zero) }
            return
        }
        scope.launch {
            isFlying = true
            // Bırakıldığı yönde ekrandan çıkar.
            offset.animateTo(dragged / dragged.getDistance() * flyPx, tween(FLY_DURATION_MS))
            index++
            offset.snapTo(Offset.Zero)
            isFlying = false
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    // Fırlatılan kare alttaki başlığın da üstünden geçsin.
                    .zIndex(1f),
                contentAlignment = Alignment.Center,
            ) {
                // Kareler alana göre büyür; dağınıklık için kenarda biraz boşluk kalır.
                val cardWidth = minOf(maxWidth * 0.82f, maxHeight * 0.86f / CARD_ASPECT)
                val cardHeight = cardWidth * CARD_ASPECT

                if (count == 0) {
                    if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(28.dp))
                } else if (!isFinished) {
                    val visible = memories.subList(index, minOf(index + STACK_DEPTH, count))
                    // Derindekinden üsttekine çiz ki üstteki kare en son (en üstte) çizilsin.
                    for (depth in visible.indices.reversed()) {
                        val memory = visible[depth]
                        val isTop = depth == 0
                        key(memory.id) {
                            StackCard(
                                memory = memory,
                                depth = depth,
                                // Deste alttan üste kurulur: derindeki kare önce düşer.
                                entranceDelayMillis = (STACK_DEPTH - 1 - depth) * ENTRANCE_STAGGER_MS,
                                scatter = Scatters[(index + depth) % Scatters.size],
                                width = cardWidth,
                                height = cardHeight,
                                dragOffset = { if (isTop) offset.value else Offset.Zero },
                                alpha = {
                                    if (isTop && isFlying) {
                                        1f - (offset.value.getDistance() / flyPx).coerceIn(0f, 1f)
                                    } else {
                                        1f
                                    }
                                },
                                modifier = if (isTop) {
                                    Modifier.pointerInput(memory.id) {
                                        detectDragGestures(
                                            onDragEnd = { settle() },
                                            onDragCancel = { scope.launch { offset.animateTo(Offset.Zero) } },
                                            onDrag = { change, dragAmount ->
                                                if (isFlying) return@detectDragGestures
                                                change.consume()
                                                scope.launch { offset.snapTo(offset.value + dragAmount) }
                                            },
                                        )
                                    }
                                } else {
                                    Modifier
                                },
                            )
                        }
                    }
                }
            }

            // Görevsiz (eski) fotoğraflarda alan boş kalır; yükseklik sabit ki yığın zıplamasın.
            TaskCaption(
                title = memories.getOrNull(index)?.taskTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(start = 28.dp, end = 28.dp, bottom = 16.dp),
            )
        }

        // Yığın alanının değil tüm ekranın ortası: altta boş duran caption payı mesajı yukarı itmesin.
        if (isFinished) RollFinished(onReplay = { index = 0 })
    }
}

@Composable
private fun StackCard(
    memory: EventMemory,
    depth: Int,
    entranceDelayMillis: Long,
    scatter: Scatter,
    width: Dp,
    height: Dp,
    dragOffset: () -> Offset,
    alpha: () -> Float,
    modifier: Modifier = Modifier,
) {
    // Üstteki kare gidince alttakiler yumuşakça bir kademe öne çıkar.
    val scale by animateFloatAsState(targetValue = 1f - depth * 0.03f)
    val dim by animateFloatAsState(targetValue = if (depth == 0) 0f else 0.28f)
    // Kare yığına ilk girdiğinde masaya düşer gibi gelir: açılışta ve "Look again"de tüm
    // deste kademeli kurulur, swipe'ta yalnız en alttaki yeni kare.
    val entrance = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(entranceDelayMillis)
        entrance.animateTo(1f, tween(ENTRANCE_DURATION_MS, easing = FastOutSlowInEasing))
    }

    // Sürükleme her karede yalnız graphicsLayer'ı günceller; recomposition yok.
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .graphicsLayer {
                val drag = dragOffset()
                translationX = scatter.dx.toPx() + drag.x
                val rise = 1f - entrance.value
                translationY = scatter.dy.toPx() + drag.y + rise * 48.dp.toPx()
                rotationZ = scatter.tilt + drag.x.toDp().value / 18f + rise * scatter.tilt
                scaleX = scale * (1f + rise * 0.12f)
                scaleY = scale * (1f + rise * 0.12f)
                this.alpha = alpha() * entrance.value
                // Yalnız üstteki kare gölge düşer; alttakilerin gölgesi desteyi bulanıklaştırır.
                if (depth == 0) {
                    shadowElevation = 16.dp.toPx()
                    shape = CardShape
                }
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CardShape)
                .background(AccessDefaults.SurfaceElevated)
                .border(width = 6.dp, color = AccessDefaults.TextPrimary, shape = CardShape),
        ) {
            BaseImage(
                imageUrl = memory.imageUrl,
                contentScale = ContentScale.Crop,
                cacheKey = memory.id,
                showLoadingIndicator = depth == 0,
                modifier = Modifier.fillMaxSize(),
            )
            if (dim > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = dim)),
                )
            }
        }

        if (memory.isMine) {
            Text(
                text = stringResource(Res.string.memories_you_badge),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.OnAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 12.dp, y = (-12).dp)
                    .rotate(8f)
                    .background(AccessDefaults.Accent, AccessShapes.Pill)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun RollFinished(
    onReplay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.memories_finished_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = AccessDefaults.TextSecondary,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
            ),
        )
        BaseMiniButton(
            text = stringResource(Res.string.memories_finished_replay),
            onClick = onReplay,
            leadingIcon = AccessIcons.Refresh,
            modifier = Modifier.padding(top = 24.dp),
        )
        val uriHandler = LocalUriHandler.current
        BaseMiniButton(
            text = stringResource(Res.string.memories_finished_rate),
            onClick = { uriHandler.openUri(appReviewUrl) },
            leadingIcon = AccessIcons.Star,
            borderColor = AccessDefaults.Gold,
            textColor = AccessDefaults.Gold,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun TaskCaption(
    title: String?,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = title,
        transitionSpec = {
            (fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 8 }) togetherWith fadeOut(tween(150))
        },
        contentAlignment = Alignment.TopCenter,
        modifier = modifier,
    ) { current ->
        Text(
            text = current.orEmpty(),
            style = MaterialTheme.typography.headlineSmall.copy(
                color = AccessDefaults.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
