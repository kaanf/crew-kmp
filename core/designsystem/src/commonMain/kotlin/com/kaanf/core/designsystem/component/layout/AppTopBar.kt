package com.kaanf.core.designsystem.component.layout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
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
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.AvatarContent
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
import crew.core.designsystem.generated.resources.game_qr_home_title
import crew.core.designsystem.generated.resources.image_crop_title
import crew.core.designsystem.generated.resources.loser_accepts_title
import crew.core.designsystem.generated.resources.loser_active_task_title
import crew.core.designsystem.generated.resources.loser_waits_title
import crew.core.designsystem.generated.resources.login_text
import crew.core.designsystem.generated.resources.loser_active_task_skip
import crew.core.designsystem.generated.resources.profile_cancel
import crew.core.designsystem.generated.resources.profile_save_changes
import crew.core.designsystem.generated.resources.profile_sign_out
import crew.core.designsystem.generated.resources.profile_title
import crew.core.designsystem.generated.resources.register_text
import crew.core.designsystem.generated.resources.rps_ready_title
import crew.core.designsystem.generated.resources.scan_opponent_title
import crew.core.designsystem.generated.resources.sign_in_methods_title
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
    state: AppTopBarState = AppTopBarState.Dashboard(profileImageUrl = null),
    elevated: () -> Boolean = { false },
    /**
     * Bar en üstteyken zemini saydam bırakır ve [elevated] açılınca alt gölgesiyle *aynı*
     * alfadan kendi rengine döner. Altındaki içeriğin bar'ın arkasına kadar uzandığı
     * ekranlar için (ör. etkinlik detayında hero'nun renk alanı). Varsayılan kapalı:
     * [elevated] vermeyen ekranlarda bar kalıcı olarak saydam kalırdı.
     */
    transparentAtRest: Boolean = false,
    onBackClick: (() -> Unit) = {},
    onRightClick: (() -> Unit) = {},
    onLeftClick: (() -> Unit) = {},
    onPassportClick: (() -> Unit) = {},
) {
    val overlayAlpha by animateFloatAsState(
        targetValue = if (elevated()) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "AppTopBarOverlayAlpha",
    )
    val title = when (state) {
        is AppTopBarState.GameLobby -> state.title
        is AppTopBarState.Game -> state.title ?: stringResource(state.titleResource)
        else -> stringResource(state.titleResource)
    }
    val navigationIcon = state.navigationIcon

    val barColor = when (state) {
        AppTopBarState.ScanOpponent -> Color.Transparent
        else -> AccessDefaults.Background
    }.takeIf { it.alpha > 0f }

    Box(
        modifier = modifier
            .zIndex(1f)
            .fillMaxWidth()
            // Zemin artık ayrı bir background() modifier'ı değil, gölgeyle aynı çizim
            // bloğunda: ikisi de tek [overlayAlpha]'dan sürülüyor, dolayısıyla scroll'da
            // eş zamanlı geliyor/gidiyor. statusBarsPadding'ten *önce* duruyor ki dolgu
            // durum çubuğunu da kapsasın (background() da orada duruyordu); gölge yine
            // size.height'ten başlıyor, o da bar'ın alt kenarı.
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
                    // Alfa yalnız burada okunuyor: animasyon draw fazını geçersiz kılar,
                    // bar yeniden compose olmaz.
                    val backgroundAlpha = if (transparentAtRest) overlayAlpha else 1f
                    if (barColor != null && backgroundAlpha > 0f) {
                        drawRect(color = barColor, alpha = backgroundAlpha)
                    }

                    if (overlayAlpha <= 0f) return@onDrawBehind

                    drawRect(
                        brush = shadowBrush,
                        topLeft = Offset(0f, size.height),
                        size = Size(size.width, shadowHeight),
                        alpha = overlayAlpha,
                    )
                }
            }
            .statusBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(
                    horizontal = 16.dp,
                )
                .padding(
                    top = 6.dp,
                    bottom = 2.dp
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

            val profileEditing = state is AppTopBarState.Profile && state.hasUnsavedChanges

            if (profileEditing) {
                // In edit mode the back chevron becomes a Cancel action that discards changes.
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .widthIn(min = 36.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.profile_cancel),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = AccessDefaults.TextMuted,
                            fontSize = 12.sp,
                        ),
                    )
                }
            } else {
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
                            modifier = Modifier.size(24.dp),
                        )
                    }
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
                            .padding(
                                vertical = 8.dp,
                            ),
                    )
                }
            }

            val isRightIconVisible = when (state) {
                AppTopBarState.Register,
                AppTopBarState.Login,
                AppTopBarState.ProfilePicture,
                is AppTopBarState.Profile,
                is AppTopBarState.Dashboard,
                AppTopBarState.LoserActiveTask,
                    -> true

                else -> false
            }

            // QR home: solda (varsa) quest + pasaport ikonları.
            if (state is AppTopBarState.Game && (state.showQuestsAction || state.showPassportAction)) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (state.showQuestsAction) {
                        GameActionButton(emoji = "🎯", onClick = onLeftClick)
                    }
                    if (state.showPassportAction) {
                        GameActionButton(emoji = "📘", onClick = onPassportClick)
                    }
                }
            }

            // QR home: sol nav yok, kapatma çarpısı sağda durur.
            if (state is AppTopBarState.Game) {
                IconButton(
                    onClick = onRightClick,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
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
                        painter = painterResource(AccessIcons.Close),
                        contentDescription = null,
                        tint = AccessDefaults.TextPrimary,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            if (state is AppTopBarState.Profile && state.isSaving) {
                // Saving replaces the action label with an accent spinner until it settles.
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(20.dp),
                    color = AccessDefaults.Accent,
                    strokeWidth = 2.dp,
                )
            } else if (isRightIconVisible) {
                TextButton(
                    onClick = onRightClick,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .widthIn(min = 36.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp),
                ) {
                    if (profileEditing) {
                        Text(
                            text = stringResource(Res.string.profile_save_changes),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = AccessDefaults.Accent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                            ),
                        )
                    } else if (state is AppTopBarState.Dashboard) {
                        AvatarCircle(
                            content = state.profileImageUrl?.let { AvatarContent.Image(it) }
                                ?: AvatarContent.Initials(
                                    label = state.userName
                                        ?.trim()
                                        ?.firstOrNull()
                                        ?.uppercase()
                                        .orEmpty(),
                                    color = AccessDefaults.Accent,
                                ),
                            avatarSize = 40,
                            borderColor = AccessDefaults.Border,
                            borderSize = 1
                        )
                    } else {
                        Text(
                            text = stringResource(
                                when (state) {
                                    AppTopBarState.Register -> Res.string.login_text
                                    AppTopBarState.Login -> Res.string.register_text
                                    is AppTopBarState.Profile -> Res.string.profile_sign_out
                                    AppTopBarState.ProfilePicture,
                                    AppTopBarState.LoserActiveTask,
                                        -> Res.string.loser_active_task_skip

                                    else -> Res.string.empty
                                },
                            ),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = if (state is AppTopBarState.LoserActiveTask) {
                                    AccessDefaults.LeftArrowColor
                                } else {
                                    AccessDefaults.TextMuted
                                },
                                fontSize = 12.sp,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameActionButton(
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(CircleShape)
            .background(AccessDefaults.SurfaceElevated)
            .border(
                width = 1.dp,
                color = AccessDefaults.BorderSoft,
                shape = CircleShape,
            )
            .size(32.dp),
    ) {
        Text(text = emoji, fontSize = 16.sp)
    }
}

private val AppTopBarState.titleResource: StringResource
    get() = when (this) {
        AppTopBarState.Login,
        AppTopBarState.Register,
        AppTopBarState.ProfilePicture,
        is AppTopBarState.Dashboard,
        is AppTopBarState.GameLobby,
            -> Res.string.empty

        is AppTopBarState.Game -> Res.string.game_qr_home_title

        is AppTopBarState.Profile -> Res.string.profile_title
        AppTopBarState.SignInMethods -> Res.string.sign_in_methods_title
        AppTopBarState.EventDetail -> Res.string.event_detail_title
        AppTopBarState.ImageCrop -> Res.string.image_crop_title
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
        is AppTopBarState.Dashboard,
        AppTopBarState.ProfilePicture,
        // QR home çıkışı sağdaki çarpıdan; solda nav ikonu yok.
        is AppTopBarState.Game,
            -> null

        AppTopBarState.Login,
        AppTopBarState.Register,
        is AppTopBarState.Profile,
        AppTopBarState.SignInMethods,
        AppTopBarState.EventDetail,
        AppTopBarState.TicketQr,
        AppTopBarState.EventCode,
        AppTopBarState.ImageCrop,
        is AppTopBarState.GameLobby,
            -> AccessIcons.LeftChevron

        AppTopBarState.ScanOpponent,
        AppTopBarState.RpsReady,
        AppTopBarState.RpsConfirmation,
        AppTopBarState.WinnerPicks,
        AppTopBarState.WinnerConfirms,
        AppTopBarState.LoserWaits,
        AppTopBarState.LoserAccepts,
        AppTopBarState.LoserActiveTask,
            -> AccessIcons.Close
    }

@Composable
@Preview
private fun AppTopBarPreview() {
    CrewTheme {
        AppTopBar(
            state = AppTopBarState.Dashboard(profileImageUrl = null),
            onBackClick = {},
            onRightClick = {},
        )
    }
}
