package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.domain.model.profileboard.BoardQrPlacement
import com.kaanf.core.domain.model.profileboard.BoardStickerPlacement
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.profile_board_loading
import crew.core.designsystem.generated.resources.profile_board_qr_loading
import org.jetbrains.compose.resources.stringResource

/** One quiet pulse shared by the placeholders; Compose's motion duration scale is respected. */
@Composable
private fun loadingOpacity(): Float {
    val transition = rememberInfiniteTransition(label = "board-loading")
    val opacity by transition.animateFloat(.4f, .85f,
        infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "placeholder-opacity")
    return opacity
}

@Composable
internal fun ProfileBoardQrLoading(modifier: Modifier = Modifier) {
    val description = stringResource(Res.string.profile_board_qr_loading)
    Box(
        modifier.background(AccessDefaults.Background, RoundedCornerShape(20.dp))
            .semantics(mergeDescendants = true) { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = AccessDefaults.TextSecondary,
            strokeWidth = 2.dp,
        )
    }
}

@Composable
internal fun ProfileBoardLoadingCanvas(modifier: Modifier = Modifier) {
    val description = stringResource(Res.string.profile_board_loading)
    BoxWithConstraints(modifier.fillMaxWidth().aspectRatio(BoardStickerPlacement.ASPECT_RATIO)
        .semantics { contentDescription = description; progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate }) {
        val width = maxWidth
        val qr = BoardQrPlacement()
        ProfileBoardQrLoading(Modifier.absoluteOffset(x = width * (qr.x - qr.size / 2),
            y = width * (qr.y - qr.size / 2)).size(width * qr.size))

    }
}

@Composable
internal fun ProfileBoardIdentityLoading(modifier: Modifier = Modifier) {
    val opacity = loadingOpacity()
    Column(modifier.alpha(opacity), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.size(96.dp).background(AccessDefaults.SurfaceElevated, CircleShape))
        Box(Modifier.width(148.dp).height(24.dp)
            .background(AccessDefaults.SurfaceElevated, RoundedCornerShape(8.dp)))
    }
}
