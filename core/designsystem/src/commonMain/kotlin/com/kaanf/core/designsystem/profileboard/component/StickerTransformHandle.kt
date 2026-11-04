package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.presentation.util.haptics.rememberImpactHaptics
import com.kaanf.core.designsystem.profileboard.ProfileBoardAction
import com.kaanf.core.domain.model.profileboard.BoardSticker
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.profile_sticker_transform_handle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.atan2

/** One-finger alternative to pinching: drag the corner around the selected sticker's centre. */
@Composable
internal fun StickerTransformHandle(
    sticker: BoardSticker,
    footprintWidth: Dp,
    footprintHeight: Dp,
    onAction: (ProfileBoardAction) -> Unit,
    onTransforming: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val latest = rememberUpdatedState(sticker)
    val latestAction = rememberUpdatedState(onAction)
    val latestTransforming = rememberUpdatedState(onTransforming)
    val latestWidth = rememberUpdatedState(with(LocalDensity.current) { footprintWidth.toPx() })
    val latestHeight = rememberUpdatedState(with(LocalDensity.current) { footprintHeight.toPx() })
    val impact = rememberImpactHaptics()
    Box(
        modifier = modifier.size(20.dp).background(AccessDefaults.Accent, CircleShape)
            .pointerInput(sticker.slot) {
                var vector = Offset.Zero
                var feedback: StickerGestureFeedback? = null
                detectDragGestures(
                    onDragStart = { position ->
                        vector = Offset(
                            -latestWidth.value / 2f + position.x,
                            latestHeight.value / 2f - 20.dp.toPx() + position.y,
                        )
                        impact.perform()
                        feedback = null
                        onTransforming(true)
                    },
                    onDragEnd = {
                        latestAction.value(ProfileBoardAction.FinishStickerGesture(sticker.slot))
                        latestTransforming.value(false)
                    },
                    onDragCancel = {
                        latestAction.value(ProfileBoardAction.FinishStickerGesture(sticker.slot))
                        latestTransforming.value(false)
                    },
                ) { change, amount ->
                    change.consume()
                    if (feedback == null) feedback = StickerGestureFeedback(impact, latest.value, change.uptimeMillis)
                    val next = vector + amount
                    val previousLength = vector.getDistance().coerceAtLeast(8.dp.toPx())
                    val zoom = (next.getDistance() / previousLength).coerceIn(0.5f, 2f)
                    val rotation = (atan2(next.y, next.x) - atan2(vector.y, vector.x)) * 180f / PI.toFloat()
                    val angle = ((rotation + 180f) % 360f + 360f) % 360f - 180f
                    latestAction.value(ProfileBoardAction.TransformSticker(sticker.slot, 0f, 0f, zoom, angle, isGesture = true))
                    vector = next
                    feedback?.update(latest.value, change.uptimeMillis)
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(AccessIcons.ArrowUpRight),
            contentDescription = stringResource(Res.string.profile_sticker_transform_handle),
            tint = AccessDefaults.OnAccent,
            modifier = Modifier.size(14.dp),
        )
    }
}
