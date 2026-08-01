package com.kaanf.core.designsystem.component.progressbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Yatay ilerleme çubuğu. [progress] 0f..1f aralığına kırpılır. */
@Composable
fun BaseProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 4.dp,
    trackColor: Color = AccessDefaults.SurfaceHigh,
    progressColor: Color = AccessDefaults.Accent,
) {
    val fraction = progress.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(
                    current = fraction,
                    range = 0f..1f,
                )
            }
            .background(color = trackColor, shape = AccessShapes.Pill),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .background(color = progressColor, shape = AccessShapes.Pill),
        )
    }
}

@Preview
@Composable
private fun BaseProgressBarPreview() {
    CrewTheme(isDarkTheme = true) {
        BaseProgressBar(progress = 0.42f)
    }
}
