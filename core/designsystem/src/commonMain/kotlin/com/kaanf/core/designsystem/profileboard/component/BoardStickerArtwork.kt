package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.domain.model.profileboard.BoardStickerDesign
import crew.core.designsystem.generated.resources.*
import crew.core.designsystem.generated.resources.profile_sticker_crew
import crew.core.designsystem.generated.resources.profile_sticker_good_vibes
import crew.core.designsystem.generated.resources.profile_sticker_lightning
import crew.core.designsystem.generated.resources.profile_sticker_sparkle
import crew.core.designsystem.generated.resources.profile_sticker_star
import crew.core.designsystem.generated.resources.profile_sticker_trophy
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BoardStickerDesign.label(): String = stringResource(when (this) {
    BoardStickerDesign.CREW -> Res.string.profile_sticker_crew
    BoardStickerDesign.GOOD_VIBES -> Res.string.profile_sticker_good_vibes
    BoardStickerDesign.STAR -> Res.string.profile_sticker_star
    BoardStickerDesign.LIGHTNING -> Res.string.profile_sticker_lightning
    BoardStickerDesign.TROPHY -> Res.string.profile_sticker_trophy
    BoardStickerDesign.SPARKLE -> Res.string.profile_sticker_sparkle
    BoardStickerDesign.BOARD_GAMES -> Res.string.profile_sticker_board_games
    BoardStickerDesign.COFFEE -> Res.string.profile_sticker_coffee
    BoardStickerDesign.CYCLING -> Res.string.profile_sticker_cycling
    BoardStickerDesign.DANCING -> Res.string.profile_sticker_dancing
    BoardStickerDesign.MUSIC -> Res.string.profile_sticker_music
    BoardStickerDesign.COOKING -> Res.string.profile_sticker_cooking
    BoardStickerDesign.PHOTOGRAPHY -> Res.string.profile_sticker_photography
    BoardStickerDesign.GAMING -> Res.string.profile_sticker_gaming
    BoardStickerDesign.HIKING -> Res.string.profile_sticker_hiking
})

@Composable
internal fun BoardStickerArtwork(design: BoardStickerDesign, modifier: Modifier = Modifier) {
    val color = when (design) {
        BoardStickerDesign.CREW -> AccessDefaults.Accent
        BoardStickerDesign.GOOD_VIBES -> AccessDefaults.Rose
        BoardStickerDesign.STAR -> AccessDefaults.Gold
        BoardStickerDesign.LIGHTNING -> AccessDefaults.Sky
        BoardStickerDesign.TROPHY -> AccessDefaults.Coral
        BoardStickerDesign.SPARKLE -> AccessDefaults.Violet
        BoardStickerDesign.HIKING, BoardStickerDesign.CYCLING -> AccessDefaults.Accent
        BoardStickerDesign.GAMING, BoardStickerDesign.BOARD_GAMES -> AccessDefaults.Violet
        BoardStickerDesign.PHOTOGRAPHY, BoardStickerDesign.MUSIC -> AccessDefaults.Sky
        BoardStickerDesign.COOKING, BoardStickerDesign.COFFEE -> AccessDefaults.Gold
        BoardStickerDesign.DANCING -> AccessDefaults.Rose
    }
    val shape = if (design.round) CircleShape else RoundedCornerShape(50)
    Box(
        modifier = modifier.clearAndSetSemantics { }.background(color, shape).border(2.dp, AccessDefaults.TextPrimary, shape)
            .padding(horizontal = if (design.round) 12.dp else 6.dp, vertical = if (design.round) 12.dp else 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (design.round) {
            val icon = when (design) {
                BoardStickerDesign.STAR -> AccessIcons.Star
                BoardStickerDesign.LIGHTNING -> AccessIcons.Lightning
                BoardStickerDesign.TROPHY -> AccessIcons.Trophy
                else -> AccessIcons.Sparkle
            }
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = AccessDefaults.OnAccent,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text = design.label(),
                style = TextStyle.Default,
                fontFamily = BricolageGrotesque,
                fontWeight = FontWeight.ExtraBold,
                color = AccessDefaults.OnAccent,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(minFontSize = 8.sp, maxFontSize = 20.sp),
            )
        }
    }
}
