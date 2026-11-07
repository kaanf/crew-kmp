package com.kaanf.crew.screenshots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import com.kaanf.core.designsystem.profileboard.ProfileBoardTilesContent
import com.kaanf.core.designsystem.profileboard.ProfileBoardState
import com.kaanf.core.designsystem.profileboard.ProfileBoardEditor
import com.kaanf.core.designsystem.profileboard.ProfileBoardAction
import com.kaanf.core.domain.model.profileboard.BoardStickerDesign
import com.kaanf.core.domain.model.profileboard.BoardStickerPlacement
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Density
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import coil3.intercept.Interceptor
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.game.presentation.leaderboard.LeaderboardContent
import com.kaanf.game.presentation.memories.MemoriesStackScreen
import com.kaanf.game.presentation.session.MatchContainerScreen
import com.kaanf.game.presentation.session.component.GameBottomTab
import com.kaanf.home.presentation.dashboard.DashboardScreen
import com.kaanf.home.presentation.eventdetail.EventDetailScreen
import com.kaanf.home.presentation.eventdetail.EventShareButton
import crew.screenshots.generated.resources.Res
import kotlinx.coroutines.delay
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

/** Fail closed: every image must be bundled. No URL can reach a network fetcher. */
private class FixtureImages(private val assets: Map<String, ByteArray>) : Interceptor {
    override suspend fun intercept(chain: Interceptor.Chain): coil3.request.ImageResult {
        val bytes = assets[chain.request.data.toString()]
            ?: error("Unbundled screenshot image: ${chain.request.data}")
        return chain.withRequest(chain.request.newBuilder().data(bytes).build()).proceed()
    }
}

@OptIn(coil3.annotation.DelicateCoilApi::class)
@Composable
fun ScreenshotApp() {
    KoinApplication(application = { modules(screenshotModule) }) {
        val context = LocalPlatformContext.current
        var imagesReady by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            val files = listOf("event.png") + (1..8).map { "avatar-$it.png" } + (1..4).map { "memory-$it.png" }
            val assets = files.associate { "fixture://$it" to Res.readBytes("files/$it") }
            val loader = ImageLoader.Builder(context)
                .components { add(FixtureImages(assets)) }
                .build()
            SingletonImageLoader.setUnsafe(loader)
            // Decode every asset before exposing any scene or ready marker. Missing assets fail loudly.
            assets.keys.forEach { uri ->
                check(loader.execute(ImageRequest.Builder(context).data(uri).build()) is SuccessResult) {
                    "Cannot decode fixture $uri"
                }
            }
            imagesReady = true
        }
        val density = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 1f)) {
            CrewTheme(isDarkTheme = false) {
                Box(Modifier.fillMaxSize().background(AccessDefaults.Background)) {
                    if (imagesReady) key(ScreenshotRouter.revision) {
                        ScreenshotContent(ScreenshotRouter.scene)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenshotContent(scene: ScreenshotScene, fixtures: ScreenshotFixtures = koinInject()) {
    var settled by remember { mutableStateOf(false) }
    // The longest entrance is the four-card roll (600 ms). Fonts/layout and Coil settle
    // before exposing the semantic readiness marker.
    LaunchedEffect(scene) { delay(1800); settled = true }
    Box(Modifier.fillMaxSize().then(if (settled) Modifier
        .testTag("screenshot-ready-${scene.route}")
        .semantics { contentDescription = "screenshot-ready-${scene.route}" } else Modifier)) {
        when (scene) {
            ScreenshotScene.ProfileBoardLoading, ScreenshotScene.ProfileQrLoading,
            ScreenshotScene.ProfileBoard, ScreenshotScene.MatchBoard, ScreenshotScene.BoardEditor -> {
                val owner = scene != ScreenshotScene.MatchBoard
                var board by remember { mutableStateOf(ProfileBoardState(
                    publicCode = "ABC1234567", fullName = if (scene == ScreenshotScene.ProfileBoardLoading) "" else "Kaan Fırat", profilePictureUrl = "fixture://avatar-1.png",
                    isLoading = scene == ScreenshotScene.ProfileBoardLoading, canEdit = true,
                    qr = if (scene == ScreenshotScene.BoardEditor) com.kaanf.core.domain.model.profileboard.BoardQrPlacement()
                        else com.kaanf.core.domain.model.profileboard.BoardQrPlacement(.6f, .5f, .4f),
                    qrStyleId = when (scene) {
                        ScreenshotScene.ProfileQrLoading -> com.kaanf.core.domain.model.profileboard.BoardQrTheme.HAMSTER.id
                        ScreenshotScene.BoardEditor -> "dots"
                        else -> "rounded"
                    },
                    isEditingBoard = scene == ScreenshotScene.BoardEditor,
                    isEditorSheetOpen = scene == ScreenshotScene.BoardEditor,
                    stickers = listOf(BoardStickerDesign.HIKING, BoardStickerDesign.GAMING,
                        BoardStickerDesign.MUSIC, BoardStickerDesign.COFFEE).mapIndexed { slot, design ->
                        val sticker = BoardStickerPlacement.create(slot, design)
                        if (scene == ScreenshotScene.MatchBoard && slot < 2) sticker.copy(
                            position = com.kaanf.core.domain.model.profileboard.BoardPosition(if (slot == 0) .09f else .91f, .6f),
                            rotation = if (slot == 0) 90f else -90f,
                        ) else sticker
                    },
                )) }
                AppScaffold { padding ->
                    if (!owner) com.kaanf.game.presentation.session.phase.QrHomePhase(
                        state = com.kaanf.game.presentation.session.MatchSessionState(
                            publicCode = board.publicCode,
                            currentUserName = board.fullName,
                            currentUserPhotoUrl = board.profilePictureUrl,
                            currentUserScore = 120,
                            profileBoard = com.kaanf.core.domain.model.profileboard.ProfileBoard(
                                qrStyleId = "classic", stickers = board.stickers, qr = board.qr,
                            ),
                        ),
                        onAction = {}, modifier = Modifier.padding(padding),
                    ) else ProfileBoardTilesContent(
                        state = board, contentPadding = padding,
                        onBack = {}, onEditProfile = {},
                        onAction = { action -> board = if (action == ProfileBoardAction.CancelEditing) {
                            ProfileBoardEditor.reduce(board, ProfileBoardAction.FinishEditing)
                        } else ProfileBoardEditor.reduce(board, action) },
                    )
                }
            }
            ScreenshotScene.Dashboard -> {
                val list = rememberLazyListState()
                AppScaffold(topBar = {
                    AppTopBar(state = AppTopBarState.Dashboard(fixtures.dashboard.profilePictureUrl, fixtures.dashboard.userName), elevated = { list.canScrollBackward })
                }) { padding ->
                    DashboardScreen(Modifier.padding(padding).consumeWindowInsets(padding), fixtures.dashboard, {}, list)
                }
            }
            ScreenshotScene.EventDetail -> {
                val list = rememberLazyListState()
                AppScaffold(topBar = {
                    AppTopBar(state = AppTopBarState.EventDetail, elevated = { list.canScrollBackward }, transparentAtRest = false,
                        rightActions = { EventShareButton(enabled = true, onClick = {}) })
                }) { padding ->
                    EventDetailScreen(
                        modifier = Modifier.padding(bottom = padding.calculateBottomPadding()).consumeWindowInsets(padding),
                        listState = list, state = fixtures.detail, contentTopPadding = padding.calculateTopPadding(), onAction = {},
                    )
                }
            }
            else -> MatchContainerScreen(
                state = when (scene) {
                    ScreenshotScene.Rps -> fixtures.rps
                    ScreenshotScene.Quest -> fixtures.quest
                    else -> fixtures.ended
                },
                selectedTab = if (scene == ScreenshotScene.Rps || scene == ScreenshotScene.Quest) GameBottomTab.Play else GameBottomTab.Leaderboard,
                onTabSelected = {}, onAction = {},
                showMemories = scene == ScreenshotScene.TonightsRoll, hasMemories = true,
                leaderboardContent = { LeaderboardContent(fixtures.leaderboard, modifier = it) },
                memoriesContent = { MemoriesStackScreen(fixtures.memories, onLoadMore = {}, modifier = it) },
            )
        }
    }
}
