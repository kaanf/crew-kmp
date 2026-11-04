package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import com.kaanf.core.presentation.util.haptics.rememberImpactHaptics
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.profile_board_edit
import org.jetbrains.compose.resources.stringResource

/** Standard long-press recognition cancels when a scroll starts; ordinary taps keep the board locked. */
@Composable
internal fun Modifier.boardEditOnLongPress(enabled: Boolean, onEdit: () -> Unit): Modifier {
    if (!enabled) return this
    val latestEdit = rememberUpdatedState(onEdit)
    val impact = rememberImpactHaptics()
    val label = stringResource(Res.string.profile_board_edit)
    return this.pointerInput(Unit) {
        detectTapGestures(onLongPress = {
            impact.perform()
            latestEdit.value()
        })
    }.semantics {
        onLongClick(label) { latestEdit.value(); true }
    }
}
