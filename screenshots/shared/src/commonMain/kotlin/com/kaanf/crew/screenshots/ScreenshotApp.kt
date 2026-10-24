package com.kaanf.crew.screenshots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
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
    // before the semantic gate; Argent additionally awaits accessibility-tree stability after this marker.
    LaunchedEffect(scene) { delay(1800); settled = true }
    Box(Modifier.fillMaxSize().then(if (settled) Modifier
        .testTag("screenshot-ready-${scene.route}")
        .semantics { contentDescription = "screenshot-ready-${scene.route}" } else Modifier)) {
        when (scene) {
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
