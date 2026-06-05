package com.kaanf.core.designsystem.component.layout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.kaanf.core.designsystem.component.logo.LogoCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.empty
import crew.core.designsystem.generated.resources.event_code_title
import crew.core.designsystem.generated.resources.event_detail_title
import crew.core.designsystem.generated.resources.game_how_to_play
import crew.core.designsystem.generated.resources.loser_accepts_title
import crew.core.designsystem.generated.resources.loser_active_task_title
import crew.core.designsystem.generated.resources.loser_waits_title
import crew.core.designsystem.generated.resources.login_text
import crew.core.designsystem.generated.resources.loser_active_task_skip
import crew.core.designsystem.generated.resources.register_text
import crew.core.designsystem.generated.resources.rps_ready_title
import crew.core.designsystem.generated.resources.scan_opponent_title
import crew.core.designsystem.generated.resources.ticket_qr_title
import crew.core.designsystem.generated.resources.who_won_title
import crew.core.designsystem.generated.resources.winner_confirms_title
import crew.core.designsystem.generated.resources.winner_picks_title
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    state: AppTopBarState = AppTopBarState.Dashboard,
    elevated: () -> Boolean = { false },
    onBackClick: (() -> Unit) = {},
    onRightClick: (() -> Unit) = {},
) {
    val overlayAlpha by animateFloatAsState(
        targetValue = if (elevated()) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "AppTopBarOverlayAlpha",
    )
    val title = when (state) {
        is AppTopBarState.GameLobby -> state.title
        else -> stringResource(state.titleResource)
    }
    val navigationIcon = state.navigationIcon

    Box(
        modifier = modifier
            .zIndex(1f)
            .fillMaxWidth()
            .background(
                when (state) {
                    AppTopBarState.ScanOpponent -> Color.Transparent
                    else -> AccessDefaults.Background
                },
            )
            .statusBarsPadding()
            .drawWithCache {
                val shadowHeight = 24.dp.toPx()
                val shadowBrush = Brush.verticalGradient(
                    colors = listOf(
                        AccessDefaults.AppBarShadow.copy(alpha = 0.7f),
                        AccessDefaults.AppBarShadow.copy(alpha = 0.5f),
                        AccessDefaults.AppBarShadow.copy(alpha = 0.3f),
                        AccessDefaults.AppBarShadow.copy(alpha = 0.1f),
                        Color.Transparent,
                    ),
                    startY = size.height,
                    endY = size.height + shadowHeight,
                )
                onDrawBehind {
                    if (overlayAlpha <= 0f) return@onDrawBehind

                    drawRect(
                        brush = shadowBrush,
                        topLeft = Offset(0f, size.height),
                        size = Size(size.width, shadowHeight),
                        alpha = overlayAlpha,
                    )
                }
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                )
                .padding(
                    top = 20.dp,
                    bottom = 16.dp
                ),
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = AccessDefaults.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            navigationIcon?.let { icon ->
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clip(CircleShape)
                        .background(AccessDefaults.SurfaceElevated)
                        .border(
                            width = 1.dp,
                            color = AccessDefaults.BorderSoft,
                            shape = CircleShape,
                        )
                        .size(32.dp),
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = AccessDefaults.TextPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            if (state is AppTopBarState.Dashboard) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .align(Alignment.CenterStart),
                ) {
                    LogoCard(
                        modifier = Modifier
                            .matchParentSize()
                    )
                }
            }

            val isRightIconVisible = when (state) {
                AppTopBarState.Register,
                AppTopBarState.Login,
                AppTopBarState.Dashboard,
                AppTopBarState.Game,
                AppTopBarState.LoserActiveTask -> true

                else -> false
            }

            if (isRightIconVisible) {
                TextButton(
                    onClick = onRightClick,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .widthIn(min = 36.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp),
                ) {
                    if (state is AppTopBarState.Dashboard) {
                        IconButton(
                            onClick = onRightClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(AccessDefaults.SurfaceElevated)
                                .border(
                                    width = 1.dp,
                                    color = AccessDefaults.BorderSoft,
                                    shape = CircleShape,
                                )
                                .size(32.dp),
                        ) {
                            Icon(
                                painter = painterResource(AccessIcons.User),
                                contentDescription = "Back",
                                tint = AccessDefaults.TextPrimary,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(
                                when (state) {
                                    AppTopBarState.Register -> Res.string.register_text
                                    AppTopBarState.Login -> Res.string.login_text
                                    AppTopBarState.Game -> Res.string.game_how_to_play
                                    AppTopBarState.LoserActiveTask -> Res.string.loser_active_task_skip
                                    else -> Res.string.empty
                                }
                            ),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = if (state is AppTopBarState.LoserActiveTask) {
                                    AccessDefaults.LeftArrowColor
                                } else {
                                    AccessDefaults.TextMuted
                                },
                                fontSize = 12.sp
                            ),
                        )
                    }
                }
            }
        }
    }
}

private val AppTopBarState.titleResource: StringResource
    get() = when (this) {
        AppTopBarState.Login,
        AppTopBarState.Register,
        AppTopBarState.Dashboard,
        AppTopBarState.Game,
        is AppTopBarState.GameLobby,
            -> Res.string.empty

        AppTopBarState.EventDetail -> Res.string.event_detail_title
        AppTopBarState.TicketQr -> Res.string.ticket_qr_title
        AppTopBarState.EventCode -> Res.string.event_code_title
        AppTopBarState.ScanOpponent -> Res.string.scan_opponent_title
        AppTopBarState.RpsReady -> Res.string.rps_ready_title
        AppTopBarState.RpsConfirmation -> Res.string.who_won_title
        AppTopBarState.WinnerPicks -> Res.string.winner_picks_title
        AppTopBarState.WinnerConfirms -> Res.string.winner_confirms_title
        AppTopBarState.LoserWaits -> Res.string.loser_waits_title
        AppTopBarState.LoserAccepts -> Res.string.loser_accepts_title
        AppTopBarState.LoserActiveTask -> Res.string.loser_active_task_title
    }

private val AppTopBarState.navigationIcon: DrawableResource?
    get() = when (this) {
        AppTopBarState.Dashboard,
        AppTopBarState.RpsConfirmation,
        AppTopBarState.WinnerPicks,
        AppTopBarState.WinnerConfirms,
        AppTopBarState.LoserWaits,
        AppTopBarState.LoserAccepts,
        AppTopBarState.LoserActiveTask,
            -> null

        AppTopBarState.Login,
        AppTopBarState.Register,
        AppTopBarState.EventDetail,
        is AppTopBarState.GameLobby,
        AppTopBarState.TicketQr,
        AppTopBarState.EventCode,
            -> AccessIcons.LeftChevron

        AppTopBarState.Game,
        AppTopBarState.ScanOpponent,
        AppTopBarState.RpsReady,
            -> AccessIcons.Close
    }

@Composable
@Preview
private fun AppTopBarPreview() {
    CrewTheme {
        AppTopBar(
            state = AppTopBarState.Dashboard,
            onBackClick = {},
            onRightClick = {},
        )
    }
}
